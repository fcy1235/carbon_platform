package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.minio.config.MinioProperties;
import cn.iocoder.power.framework.minio.core.IMinioService;
import cn.iocoder.power.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.power.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonBaselinePageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonBaselineRespVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonFileMinioUploadVO;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonUserInfoRespVO;
import cn.iocoder.power.module.carbon.controller.admin.project.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonProjectConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.*;
import cn.iocoder.power.module.carbon.dal.mysql.*;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonProjectWithContactDTO;
import cn.iocoder.power.module.carbon.enums.ProjectStageEnum;
import cn.iocoder.power.module.carbon.enums.ProjectStatusEnum;
import cn.iocoder.power.module.carbon.service.CarbonBaselineService;
import cn.iocoder.power.module.carbon.service.CarbonProjectService;
import cn.iocoder.power.module.carbon.util.CarbonUserInfoHelper;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonFileMinioUploadVO.URL_TEMPLATE;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.PROJECT_NOT_EXISTS;

@Service("carbonProjectService")
@Validated
@Slf4j
public class CarbonProjectServiceImpl implements CarbonProjectService {

    @Resource
    private CarbonProjectMapper projectMapper;
    @Resource
    private CarbonProjectUserMapper projectUserMapper;
    @Resource
    private CarbonProjectDocMapper projectDocMapper;
    @Resource
    private CarbonProjectProgressMapper projectProgressMapper;

    @Resource
    private CarbonContactMapper contactMapper;

    @Resource
    private CarbonAccountingMapper carbonAccountingMapper;

    @Resource
    private CarbonBaselineService carbonBaselineService;

    @Resource
    private CarbonUserInfoHelper carbonUserInfoHelper;

    @Resource
    private IMinioService minioService;

    @Resource
    private MinioProperties minioProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProject(CarbonProjectSaveReqVO createReqVO) {
        CarbonProjectDO project = CarbonProjectConvert.INSTANCE.convert(createReqVO);
        project.setProjectCode(generateProjectCode());
        project.setProjectCycle(calcCycle(createReqVO.getPlanStartDate(), createReqVO.getPlanEndDate()));
        project.setTotalReduction(calcTotalReduction(createReqVO.getUserList()));
        projectMapper.insert(project);

        List<CarbonProjectUserDO> users = CarbonProjectConvert.INSTANCE.convertUserList(createReqVO.getUserList());
        users.forEach(user -> user.setProjectId(project.getId()));
        if (CollUtil.isNotEmpty(users)) {
            projectUserMapper.insertBatch(users);
        }

        return project.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProject(CarbonProjectSaveReqVO updateReqVO) {
        validateProjectExists(updateReqVO.getId());

        CarbonProjectDO project = CarbonProjectConvert.INSTANCE.convert(updateReqVO);
        project.setProjectCycle(calcCycle(updateReqVO.getPlanStartDate(), updateReqVO.getPlanEndDate()));
        project.setTotalReduction(calcTotalReduction(updateReqVO.getUserList()));
        projectMapper.updateById(project);

        projectUserMapper.deleteByProjectId(updateReqVO.getId());
        List<CarbonProjectUserDO> users = CarbonProjectConvert.INSTANCE.convertUserList(updateReqVO.getUserList());
        users.forEach(user -> user.setProjectId(updateReqVO.getId()));
        if (CollUtil.isNotEmpty(users)) {
            projectUserMapper.insertBatch(users);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProject(Long id) {
        validateProjectExists(id);
        projectUserMapper.deleteByProjectId(id);
        projectDocMapper.deleteByProjectId(id);
        projectProgressMapper.deleteByProjectId(id);
        projectMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProjectStatus(CarbonProjectStatusUpdateReqVO reqVO) {
        validateProjectExists(reqVO.getId());

        CarbonProjectDO updateObj = new CarbonProjectDO();
        updateObj.setId(reqVO.getId());
        updateObj.setProjectStatus(reqVO.getProjectStatus());
        projectMapper.updateById(updateObj);

        CarbonProjectProgressDO progress = new CarbonProjectProgressDO();
        progress.setProjectId(reqVO.getId());
        progress.setStage(reqVO.getProjectStatus());
        progress.setStatus(ProjectStatusEnum.IN_PROGRESS.getType());
        progress.setUpdateTime(LocalDateTime.now());
        progress.setUpdater(SecurityFrameworkUtils.getLoginUserNickname());
        progress.setContent(reqVO.getReason() != null ? reqVO.getReason() : "项目状态更新为" + ProjectStageEnum.getNameByType(reqVO.getProjectStatus()));
        projectProgressMapper.insert(progress);
    }

    @Override
    public CarbonProjectRespVO getProject(Long id) {
        // 1. 查询项目基本信息
        CarbonProjectDO project = projectMapper.selectById(id);
        if (project == null) {
            return null;
        }

        // 2. 并行查询关联数据（使用 CompletableFuture 或手动合并）
        // 由于是单个查询，直接顺序查询即可
        List<CarbonProjectUserDO> users = projectUserMapper.selectListByProjectId(id);
        List<CarbonProjectDocDO> docs = projectDocMapper.selectListByProjectId(id);
        List<CarbonProjectProgressDO> progressList = projectProgressMapper.selectListByProjectId(id);

        // 3. 构建 RespVO
        CarbonProjectRespVO respVO = CarbonProjectConvert.INSTANCE.convert(project);
        respVO.setDocs(CarbonProjectConvert.INSTANCE.convertDocRespList(docs));
        respVO.setProgressList(CarbonProjectConvert.INSTANCE.convertProgressRespList(progressList));

        // 4. 填充联系人信息（直接查询，不走 fillContactInfo）
        if (project.getContactId() != null) {
            CarbonContactDO contact = contactMapper.selectById(project.getContactId());
            if (contact != null) {
                respVO.setContactName(contact.getName());
                respVO.setContactPhone(contact.getPhone());
            }
        }

        // 5. 填充用户列表（如果有的话）
        if (CollUtil.isNotEmpty(users)) {
            respVO.setUserList(fillProjectUserRespList(users));
        } else {
            respVO.setUserList(Collections.emptyList());
        }

        return respVO;
    }

    @Override
    public PageResult<CarbonProjectRespVO> getProjectPage(CarbonProjectPageReqVO pageReqVO) {
        // 连表查询：项目 + 联系人
        PageResult<CarbonProjectWithContactDTO> pageResult = projectMapper.selectPageWithContact(pageReqVO);
        if (pageResult.getList().isEmpty()) {
            return PageResult.empty();
        }
        // 转换为 RespVO
        List<CarbonProjectRespVO> list = pageResult.getList().stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public List<CarbonProjectRespVO> getProjectList(CarbonProjectPageReqVO reqVO) {
        // 连表查询：项目 + 联系人
        List<CarbonProjectWithContactDTO> dtoList = projectMapper.selectListWithContact(reqVO);
        if (dtoList.isEmpty()) {
            return Collections.emptyList();
        }
        // 转换为 RespVO
        return dtoList.stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarbonProjectSimpleRespVO> getProjectSimpleList() {
        List<CarbonProjectDO> projects = projectMapper.selectList(new LambdaQueryWrapperX<CarbonProjectDO>()
                .select(CarbonProjectDO::getId, CarbonProjectDO::getProjectName)
                .orderByDesc(CarbonProjectDO::getId));
        return projects.stream().map(project -> {
            CarbonProjectSimpleRespVO respVO = new CarbonProjectSimpleRespVO();
            respVO.setProjectId(project.getId());
            respVO.setProjectName(project.getProjectName());
            return respVO;
        }).collect(Collectors.toList());
    }

    /**
     * 将联表查询结果转换为 RespVO
     */
    private CarbonProjectRespVO convertToRespVO(CarbonProjectWithContactDTO dto) {
        CarbonProjectRespVO respVO = new CarbonProjectRespVO();
        respVO.setId(dto.getId());
        respVO.setProjectCode(dto.getProjectCode());
        respVO.setProjectName(dto.getProjectName());
        respVO.setContactId(dto.getContactId());
        respVO.setProjectDesc(dto.getProjectDesc());
        respVO.setProvinceCode(dto.getProvinceCode());
        respVO.setCityCode(dto.getCityCode());
        respVO.setDistrictCode(dto.getDistrictCode());
        respVO.setPlanStartDate(dto.getPlanStartDate());
        respVO.setPlanEndDate(dto.getPlanEndDate());
        respVO.setProjectCycle(dto.getProjectCycle());
        respVO.setProjectStatus(dto.getProjectStatus());
        respVO.setTotalReduction(dto.getTotalReduction());
        respVO.setCreateTime(dto.getCreateTime());
        // 联系人信息
        respVO.setContactName(dto.getContactName());
        respVO.setContactPhone(dto.getContactPhone());
        return respVO;
    }

    @Override
    public List<CarbonProjectUserDO> getProjectUsers(Long projectId) {
        return projectUserMapper.selectListByProjectId(projectId);
    }

    @Override
    public List<CarbonProjectDocDO> getProjectDocs(Long projectId) {
        return projectDocMapper.selectListByProjectId(projectId);
    }

    @Override
    public List<CarbonProjectProgressDO> getProjectProgressList(Long projectId) {
        return projectProgressMapper.selectListByProjectId(projectId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProjectDoc(Long projectId, String docName) {
        projectDocMapper.deleteByProjectIdAndDocName(projectId, docName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CarbonProjectDocRespVO uploadProjectDoc(Long projectId, MultipartFile file, String docDesc) throws Exception {
        validateProjectExists(projectId);

        String fileName = file.getOriginalFilename();
        String path = minioService.uploadFile(file, minioProperties.getBucketName(), fileName);
        String urlPath = String.format(URL_TEMPLATE, minioProperties.getIp(), minioProperties.getPort(), minioProperties.getBucketName(), path);
        CarbonProjectDocDO doc = new CarbonProjectDocDO();
        doc.setProjectId(projectId);
        doc.setDocName(fileName);
        doc.setDocDesc(docDesc);
        doc.setDocSize(formatFileSize(file.getSize()));
        doc.setDocPath(urlPath);
        doc.setUploader(SecurityFrameworkUtils.getLoginUserNickname());
        doc.setUploadTime(LocalDateTime.now());
        projectDocMapper.insert(doc);

        return CarbonProjectConvert.INSTANCE.convert(doc);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProgressRecord(CarbonProjectProgressSaveReqVO reqVO) {
        validateProjectExists(reqVO.getProjectId());

        CarbonProjectDO project = projectMapper.selectById(reqVO.getProjectId());

        CarbonProjectProgressDO progress = new CarbonProjectProgressDO();
        progress.setProjectId(reqVO.getProjectId());
        progress.setStage(project.getProjectStatus());
        progress.setStatus(ProjectStatusEnum.IN_PROGRESS.getType());
        progress.setUpdateTime(LocalDateTime.now());
        progress.setUpdater(SecurityFrameworkUtils.getLoginUserNickname());
        progress.setContent(reqVO.getContent());
        projectProgressMapper.insert(progress);
    }

    private List<CarbonProjectUserRespVO> fillProjectUserRespList(List<CarbonProjectUserDO> userDOList) {
        if (CollUtil.isEmpty(userDOList)) {
            return Collections.emptyList();
        }

        // 1. 批量查询用户信息和核算数据
        Set<Long> userInfoIds = userDOList.stream()
                .map(CarbonProjectUserDO::getCarbonUserInfoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> accountingIds = userDOList.stream()
                .map(CarbonProjectUserDO::getAccountingId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, CarbonUserInfoRespVO> userInfoMap = Collections.emptyMap();
        if (CollUtil.isNotEmpty(userInfoIds)) {
            userInfoMap = carbonUserInfoHelper.queryUserInfoMap(userInfoIds);
        }

        Map<Long, CarbonAccountingDO> accountingMap = Collections.emptyMap();
        if (CollUtil.isNotEmpty(accountingIds)) {
            accountingMap = carbonAccountingMapper.selectBatchIds(accountingIds).stream()
                    .collect(Collectors.toMap(CarbonAccountingDO::getId, a -> a, (a, b) -> a));
        }

        // 2. 批量查询区域强度
        Map<String, CarbonBaselineRespVO> baselineMap = Collections.emptyMap();
        if (CollUtil.isNotEmpty(userInfoMap)) {
            Set<Long> provinceCodes = new HashSet<>();
            Set<Long> cityCodes = new HashSet<>();
            Set<Long> districtCodes = new HashSet<>();
            for (CarbonUserInfoRespVO userInfo : userInfoMap.values()) {
                if (userInfo.getProvinceCode() != null && userInfo.getCityCode() != null && userInfo.getDistrictCode() != null) {
                    provinceCodes.add(userInfo.getProvinceCode());
                    cityCodes.add(userInfo.getCityCode());
                    districtCodes.add(userInfo.getDistrictCode());
                }
            }
            if (CollUtil.isNotEmpty(provinceCodes)) {
                CarbonBaselinePageReqVO baselineReq = new CarbonBaselinePageReqVO();
                baselineReq.setProvinceCodes(provinceCodes);
                baselineReq.setCityCodes(cityCodes);
                baselineReq.setDistrictCodes(districtCodes);
                List<CarbonBaselineRespVO> baselineList = carbonBaselineService.getBaselineList(baselineReq);
                baselineMap = baselineList.stream()
                        .collect(Collectors.toMap(
                                b -> b.getProvinceCode() + "-" + b.getCityCode() + "-" + b.getDistrictCode(),
                                b -> b,
                                (a, b) -> a
                        ));
            }
        }

        // 3. 组装 RespVO
        List<CarbonProjectUserRespVO> result = new ArrayList<>(userDOList.size());
        for (CarbonProjectUserDO userDO : userDOList) {
            CarbonProjectUserRespVO respVO = new CarbonProjectUserRespVO();
            respVO.setId(userDO.getId());
            respVO.setProjectId(userDO.getProjectId());
            respVO.setCarbonUserInfoId(userDO.getCarbonUserInfoId());
            respVO.setAccountingId(userDO.getAccountingId());

            CarbonUserInfoRespVO userInfo = userInfoMap.get(userDO.getCarbonUserInfoId());
            if (userInfo != null) {
                respVO.setUsername(userInfo.getUsername());
                respVO.setIdCard(userInfo.getIdCard());
                respVO.setPhone(userInfo.getPhone());
                respVO.setDivision(userInfo.getDivision());
                respVO.setAddress(userInfo.getAddress());
                respVO.setReformType(userInfo.getReformType());
                respVO.setElectricityId(userInfo.getElectricityId());
                respVO.setGasId(userInfo.getGasId());
            }

            CarbonAccountingDO accounting = accountingMap.get(userDO.getAccountingId());
            if (accounting != null) {
                respVO.setActualEmission(accounting.getActualEmission());
                respVO.setReduction(accounting.getReduction());
                respVO.setAccountingPeriodStart(accounting.getAccountingPeriodStart());
                respVO.setAccountingPeriodEnd(accounting.getAccountingPeriodEnd());
            }

            // 计算基准线排放量
            if (userInfo != null && userInfo.getProvinceCode() != null && userInfo.getCityCode() != null && userInfo.getDistrictCode() != null) {
                String key = userInfo.getProvinceCode() + "-" + userInfo.getCityCode() + "-" + userInfo.getDistrictCode();
                CarbonBaselineRespVO baseline = baselineMap.get(key);
                if (baseline != null) {
                    BigDecimal heatingArea = userInfo.getHeatingArea() != null ? userInfo.getHeatingArea() : BigDecimal.ZERO;
                    respVO.setBaselineEmission(heatingArea.multiply(baseline.getIntensity()));
                }
            }

            result.add(respVO);
        }
        return result;
    }



    @VisibleForTesting
    void validateProjectExists(Long id) {
        if (id == null) {
            return;
        }
        CarbonProjectDO project = projectMapper.selectById(id);
        if (project == null) {
            throw exception(PROJECT_NOT_EXISTS);
        }
    }

    private String generateProjectCode() {
        return "PRJ" + System.currentTimeMillis();
    }

    private String calcCycle(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return "";
        }
        Period period = Period.between(start, end);
        int years = period.getYears();
        int months = period.getMonths();
        if (months < 0) {
            years--;
            months += 12;
        }
        return years + "年" + months + "月";
    }

    private BigDecimal calcTotalReduction(List<CarbonProjectUserSaveReqVO> userList) {
        if (CollUtil.isEmpty(userList)) {
            return BigDecimal.ZERO;
        }
        List<Long> accountingIds = userList.stream()
                .map(CarbonProjectUserSaveReqVO::getAccountingId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(accountingIds)) {
            return BigDecimal.ZERO;
        }
        List<CarbonAccountingDO> accountingList = carbonAccountingMapper.selectBatchIds(accountingIds);
        return accountingList.stream()
                .map(CarbonAccountingDO::getReduction)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        }
        BigDecimal sizeKb = BigDecimal.valueOf(size).divide(BigDecimal.valueOf(1024), 2, RoundingMode.HALF_UP);
        if (sizeKb.compareTo(BigDecimal.valueOf(1024)) < 0) {
            return sizeKb.stripTrailingZeros().toPlainString() + " KB";
        }
        BigDecimal sizeMb = sizeKb.divide(BigDecimal.valueOf(1024), 2, RoundingMode.HALF_UP);
        if (sizeMb.compareTo(BigDecimal.valueOf(1024)) < 0) {
            return sizeMb.stripTrailingZeros().toPlainString() + " MB";
        }
        BigDecimal sizeGb = sizeMb.divide(BigDecimal.valueOf(1024), 2, RoundingMode.HALF_UP);
        return sizeGb.stripTrailingZeros().toPlainString() + " GB";
    }
}
