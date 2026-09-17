package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.core.ArrayValuable;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.util.collection.CollectionUtils;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.minio.config.MinioProperties;
import cn.iocoder.power.framework.minio.core.IMinioService;
import cn.iocoder.power.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerChangeImportDataVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerImportDataVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerRevokeImportDataVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerImportDetailPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerImportDetailRespVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerImportTaskPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerImportTaskRespVO;
import cn.iocoder.power.module.carbon.convert.CarbonLedgerImportConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerFileDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerImportDetailDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerImportTaskDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerImportTaskWithFileDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonLedgerFileMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonLedgerImportDetailMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonLedgerImportTaskMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonUserInfoMapper;
import cn.iocoder.power.module.carbon.enums.*;
import cn.iocoder.power.module.carbon.service.CarbonLedgerImportService;
import cn.iocoder.power.module.carbon.util.LedgerDataPermissionHelper;
import cn.iocoder.power.module.carbon.util.UserCodeGenerator;
import cn.idev.excel.EasyExcel;
import cn.iocoder.power.module.system.api.area.AreaApi;
import cn.iocoder.power.module.system.api.area.dto.AreaRespDTO;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class CarbonLedgerImportServiceImpl implements CarbonLedgerImportService {

    @Resource
    private CarbonLedgerImportTaskMapper importTaskMapper;

    @Resource
    private CarbonLedgerImportDetailMapper importDetailMapper;

    @Resource
    private CarbonLedgerFileMapper ledgerFileMapper;

    @Resource
    private CarbonUserInfoMapper userInfoMapper;

    @Resource
    private IMinioService minioService;

    @Resource
    private MinioProperties minioProperties;

    @Resource
    private AreaApi areaApi;

    @Resource
    private UserCodeGenerator userCodeGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createImportTask(String uploadType, MultipartFile file, String cityCode, String districtCode) throws Exception {
        validateFileExt(file);

        // 上传文件到 MinIO
        String fileName = file.getOriginalFilename();
        String path = minioService.uploadFile(file, minioProperties.getBucketName(), fileName);

        // 解析 Excel 统计总行数
        FileParseResult parseResult = parseExcel(file, uploadType);

        // 创建台账文件记录
        CarbonLedgerFileDO ledgerFile = new CarbonLedgerFileDO();
        ledgerFile.setUploadType(uploadType);
        ledgerFile.setCityCode(cityCode);
        ledgerFile.setDistrictCode(districtCode);
        ledgerFile.setFileName(fileName);
        ledgerFile.setFileUrl(path);
        ledgerFile.setDataCount(parseResult.getDataCount());
        ledgerFile.setUploadStatus(parseResult.isSuccess() ? LedgerFileStatusEnum.SUCCESS.getType() : LedgerFileStatusEnum.FAIL.getType());
        ledgerFile.setAuditStatus(LedgerFileAuditStatusEnum.DRAFT.getType());
        ledgerFile.setUploadTime(LocalDateTime.now());
        ledgerFile.setUploader(SecurityFrameworkUtils.getLoginUserNickname());
        ledgerFileMapper.insert(ledgerFile);

        // 创建导入任务记录
        CarbonLedgerImportTaskDO task = new CarbonLedgerImportTaskDO();
        task.setLedgerFileId(ledgerFile.getId());
        task.setSuccessCount(0);
        task.setFailCount(0);
        task.setImportStatus(parseResult.isSuccess() ? LedgerImportStatusEnum.PENDING_CHECK.getType() : LedgerImportStatusEnum.IMPORT_FAIL.getType());
        task.setUploader(SecurityFrameworkUtils.getLoginUserNickname());
        task.setUploadTime(LocalDateTime.now());
        importTaskMapper.insert(task);
        return task.getId();
    }

    @Override
    public CarbonLedgerImportTaskRespVO getImportTask(Long id) {
        // 使用联表查询获取导入任务和文件信息
        CarbonLedgerImportTaskWithFileDO taskWithFile = importTaskMapper.selectOneWithFile(id);
        if (taskWithFile == null) {
            throw exception(LEDGER_IMPORT_TASK_NOT_EXISTS);
        }
        // 验证数据权限（通过关联的台账文件）
        validateDataPermissionWithFile(taskWithFile);
        return CarbonLedgerImportConvert.INSTANCE.convertFromWithFile(taskWithFile);
    }

    @Override
    public PageResult<CarbonLedgerImportTaskRespVO> getImportTaskPage(CarbonLedgerImportTaskPageReqVO pageReqVO) {
        // 使用联表分页查询获取导入任务和文件信息
        PageResult<CarbonLedgerImportTaskWithFileDO> pageResult = importTaskMapper.selectPageWithFile(pageReqVO);
        // 转换为 RespVO
        PageResult<CarbonLedgerImportTaskRespVO> result = CarbonLedgerImportConvert.INSTANCE.convertTaskPageFromWithFile(pageResult);
        // 填充城市和区县名称
        fillAreaNames(result.getList());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteImportTask(Long id) {
        CarbonLedgerImportTaskDO task = validateImportTaskExists(id);
        validateDataPermission(task);
        importDetailMapper.deleteByTaskId(id);
        importTaskMapper.deleteById(id);
    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void executeImport(Long taskId) throws Exception {
        CarbonLedgerImportTaskDO task = validateImportTaskExists(taskId);
        // 通过联表查询获取文件信息
        CarbonLedgerImportTaskWithFileDO taskWithFile = importTaskMapper.selectOneWithFile(taskId);
        if (taskWithFile == null) {
            throw exception(LEDGER_IMPORT_TASK_NOT_EXISTS);
        }
        validateDataPermissionWithFile(taskWithFile);


        // 更新为导入中
        CarbonLedgerImportTaskDO updating = new CarbonLedgerImportTaskDO();
        updating.setId(taskId);

        // 清空旧明细
        importDetailMapper.deleteByTaskId(taskId);

        // 从 MinIO 下载并解析 Excel
        String uploadType = taskWithFile.getUploadType();
        if ("2".equals(uploadType)) {
            // 变更户使用专用模板（多行表头、列序号映射）
            executeChangeImport(taskId, taskWithFile.getFileUrl());
            return;
        }
        if ("3".equals(uploadType)) {
            // 撤销户使用专用模板（多行表头、列序号映射）
            executeRevokeImport(taskId, taskWithFile.getFileUrl());
            return;
        }

        List<CarbonLedgerImportDataVO> dataList = readExcelFromMinio(taskWithFile.getFileUrl());
        int successCount = 0;
        int failCount = 0;
        List<CarbonLedgerImportDetailDO> detailDOs = new ArrayList<>();
        Long deptId =  taskWithFile.getDeptId();
        for (CarbonLedgerImportDataVO dataVO : dataList) {
            CarbonLedgerImportDetailDO detail = CarbonLedgerImportConvert.INSTANCE.convertDetail(dataVO);
            detail.setTaskId(taskId);

            try {
                String errorMsg = validateImportData(dataVO, uploadType);
                if (StrUtil.isNotBlank(errorMsg)) {
                    throw new RuntimeException(errorMsg);
                }

                // 根据上传类型处理用户信息，成功后用最终数据覆盖明细
                CarbonUserInfoDO userInfo = processUserInfo(dataVO, uploadType);
                userInfo.setDeptId(deptId);
                fillSuccessDetail(detail, userInfo);
                resolveAreaNameToDetail(detail, dataVO);
                successCount++;
            } catch (Exception e) {
                resolveAreaNameToDetail(detail, dataVO);
                detail.setImportStatus(LedgerImportDetailStatusEnum.FAIL.getType());
                detail.setErrorMsg(e.getMessage());
                failCount++;
            }
            detailDOs.add(detail);
        }

        // 批量插入明细
        if (CollUtil.isNotEmpty(detailDOs)) {
            importDetailMapper.insertBatch(detailDOs);
        }

        // 更新任务状态
        updating = new CarbonLedgerImportTaskDO();
        updating.setId(taskId);
        updating.setSuccessCount(successCount);
        updating.setFailCount(failCount);
        log.info("更新导入任务状态， taskId={}, successCount={}, failCount={}", taskId, successCount, failCount);
        if (failCount == 0) {
            updating.setImportStatus(LedgerImportStatusEnum.IMPORT_SUCCESS.getType());
        } else if (successCount == 0) {
            updating.setImportStatus(LedgerImportStatusEnum.IMPORT_FAIL.getType());
        } else {
            updating.setImportStatus(LedgerImportStatusEnum.PARTIAL_FAIL.getType());
        }
        importTaskMapper.updateById(updating);
    }

    /**
     * 根据上传类型处理用户信息
     */
    private CarbonUserInfoDO processUserInfo(CarbonLedgerImportDataVO dataVO, String uploadType) {
        // 上传类型：1-新增户 2-变更户 3-撤销户 4-改造类型变更
        switch (uploadType) {
            case "1": // 新增户
                return createUserInfo(dataVO);
            case "4": // 改造类型变更
                return updateReformType(dataVO);
            default:
                throw new RuntimeException("不支持的上传类型: " + uploadType);
        }
    }

    /**
     * 新增用户信息
     */
    private CarbonUserInfoDO createUserInfo(CarbonLedgerImportDataVO dataVO) {
        CarbonUserInfoDO userInfo = new CarbonUserInfoDO();
        userInfo.setUsername(dataVO.getUsername());
        userInfo.setIdCard(dataVO.getIdCard());
        userInfo.setPhone(dataVO.getPhone());
        userInfo.setAddress(dataVO.getAddress());
        userInfo.setHeatingArea(dataVO.getHeatingArea());
        userInfo.setElectricityId(dataVO.getElectricityId());
        userInfo.setGasId(dataVO.getGasId());
        userInfo.setReformYear(dataVO.getReformYear());
        userInfo.setGasUserCode(dataVO.getGasUserCode());
        userInfo.setRemark(dataVO.getRemark());

        // 根据字典中文名称反查编码
        userInfo.setReformType(resolveDictCode(ReformTypeEnum.values(), dataVO.getReformType()));
        userInfo.setReformMode(resolveDictCode(ReformModeEnum.values(), dataVO.getReformMode()));
        userInfo.setDataSource(resolveDictCode(UserDataSourceEnum.values(), dataVO.getDataSource()));
        userInfo.setReformBatch(resolveDictCode(ReformBatchEnum.values(), dataVO.getReformBatch()));
        userInfo.setSubsidyMethod(resolveDictCode(SubsidyMethodEnum.values(), dataVO.getSubsidyMethod()));
        userInfo.setHouseUsage(resolveDictCode(HouseUsageEnum.values(), dataVO.getHouseUsage()));
        userInfo.setUserCategory(resolveDictCode(UserCategoryEnum.values(), dataVO.getUserCategory()));
        userInfo.setUseStatus(resolveDictCode(UseStatusEnum.values(), dataVO.getUseStatus()));

        // 根据行政区中文名称反查编码
        resolveAndSetAreaCodes(userInfo, dataVO);

        // 解析完区域编码与数据来源后再生成用户编码
        userInfo.setUserCode(userCodeGenerator.generate(userInfo.getDataSource(), userInfo.getProvinceCode(),
                userInfo.getCityCode(), userInfo.getDistrictCode()));

        userInfoMapper.insert(userInfo);
        return userInfo;
    }

    /**
     * 变更用户信息（根据身份证号和变更前地址匹配，更新变更后的字段）
     */
    private CarbonUserInfoDO updateUserInfo(CarbonLedgerChangeImportDataVO dataVO) {
        CarbonUserInfoDO existingUser = findChangeUserByIdCardAndAddress(dataVO);
        if (existingUser == null) {
            throw new RuntimeException("未找到匹配的用户信息（身份证号: " + dataVO.getIdCard()
                    + ", 地址: " + dataVO.getAddress() + "）");
        }

        // 更新变更后的基本信息
        if (StrUtil.isNotBlank(dataVO.getChangedUsername())) {
            existingUser.setUsername(dataVO.getChangedUsername());
        }
        if (StrUtil.isNotBlank(dataVO.getChangedIdCard())) {
            existingUser.setIdCard(dataVO.getChangedIdCard());
        }
        if (StrUtil.isNotBlank(dataVO.getChangedPhone())) {
            existingUser.setPhone(dataVO.getChangedPhone());
        }
        if (StrUtil.isNotBlank(dataVO.getChangedAddress())) {
            existingUser.setAddress(dataVO.getChangedAddress());
        }
        if (dataVO.getChangedHeatingArea() != null) {
            existingUser.setHeatingArea(dataVO.getChangedHeatingArea());
        }
        if (StrUtil.isNotBlank(dataVO.getRemark())) {
            existingUser.setRemark(dataVO.getRemark());
        }

        userInfoMapper.updateById(existingUser);
        return existingUser;
    }

    /**
     * 撤销用户（只更新使用状态，根据身份证号和详细地址匹配）
     */
    private CarbonUserInfoDO revokeUserInfo(CarbonLedgerRevokeImportDataVO dataVO) {
        CarbonUserInfoDO existingUser = findRevokeUserByIdCardAndAddress(dataVO);
        if (existingUser == null) {
            throw new RuntimeException("未找到匹配的用户信息（身份证号: " + dataVO.getIdCard() + ", 详细地址: " + dataVO.getAddress() + "）");
        }

        existingUser.setUseStatus(resolveDictCode(UseStatusEnum.values(), dataVO.getUseStatus()));
        userInfoMapper.updateById(existingUser);
        return existingUser;
    }

    /**
     * 成功后用最终用户数据覆盖明细（省市区乡村名称从Excel原始数据写入）
     */
    private void fillSuccessDetail(CarbonLedgerImportDetailDO detail, CarbonUserInfoDO userInfo) {
        detail.setUsername(userInfo.getUsername());
        detail.setIdCard(userInfo.getIdCard());
        detail.setPhone(userInfo.getPhone());
        detail.setAddress(userInfo.getAddress());
        detail.setHeatingArea(userInfo.getHeatingArea());
        detail.setReformType(userInfo.getReformType());
        detail.setDataSource(userInfo.getDataSource());
        detail.setElectricityId(userInfo.getElectricityId());
        detail.setGasId(userInfo.getGasId());
        detail.setReformYear(userInfo.getReformYear());
        detail.setReformBatch(userInfo.getReformBatch());
        detail.setSubsidyMethod(userInfo.getSubsidyMethod());
        detail.setHouseUsage(userInfo.getHouseUsage());
        detail.setUserCategory(userInfo.getUserCategory());
        detail.setGasUserCode(userInfo.getGasUserCode());
        detail.setUseStatus(userInfo.getUseStatus());
        detail.setRemark(userInfo.getRemark());
        detail.setImportStatus(LedgerImportDetailStatusEnum.SUCCESS.getType());
    }

    /**
     * 根据撤销户VO的身份证号和详细地址查找用户
     */
    private CarbonUserInfoDO findRevokeUserByIdCardAndAddress(CarbonLedgerRevokeImportDataVO dataVO) {
        if (StrUtil.isBlank(dataVO.getIdCard())) {
            throw new RuntimeException("身份证号不能为空");
        }
        if (StrUtil.isBlank(dataVO.getAddress())) {
            throw new RuntimeException("详细地址不能为空");
        }
        return userInfoMapper.selectByIdCardAndAddress(dataVO.getIdCard(), dataVO.getAddress());
    }

    /**
     * 执行撤销户导入（使用专用模板：多行表头、列序号映射）
     */
    private void executeRevokeImport(Long taskId, String fileUrl) throws Exception {
        List<CarbonLedgerRevokeImportDataVO> dataList = readRevokeExcelFromMinio(fileUrl);
        int successCount = 0;
        int failCount = 0;
        List<CarbonLedgerImportDetailDO> detailDOs = new ArrayList<>();

        for (CarbonLedgerRevokeImportDataVO dataVO : dataList) {
            CarbonLedgerImportDetailDO detail = CarbonLedgerImportConvert.INSTANCE.convertDetail(dataVO);
            detail.setTaskId(taskId);

            try {
                String errorMsg = validateRevokeImportData(dataVO);
                if (StrUtil.isNotBlank(errorMsg)) {
                    throw new RuntimeException(errorMsg);
                }
                CarbonUserInfoDO userInfo = revokeUserInfo(dataVO);
                fillSuccessDetail(detail, userInfo);
                resolveAreaNameToDetail(detail, dataVO);
                successCount++;
            } catch (Exception e) {
                resolveAreaNameToDetail(detail, dataVO);
                detail.setImportStatus(LedgerImportDetailStatusEnum.FAIL.getType());
                detail.setErrorMsg(e.getMessage());
                failCount++;
            }
            detailDOs.add(detail);
        }

        if (CollUtil.isNotEmpty(detailDOs)) {
            importDetailMapper.insertBatch(detailDOs);
        }

        CarbonLedgerImportTaskDO updating = new CarbonLedgerImportTaskDO();
        updating.setId(taskId);
        updating.setSuccessCount(successCount);
        updating.setFailCount(failCount);
        if (failCount == 0) {
            updating.setImportStatus(LedgerImportStatusEnum.IMPORT_SUCCESS.getType());
        } else if (successCount == 0) {
            updating.setImportStatus(LedgerImportStatusEnum.IMPORT_FAIL.getType());
        } else {
            updating.setImportStatus(LedgerImportStatusEnum.PARTIAL_FAIL.getType());
        }
        importTaskMapper.updateById(updating);
    }

    /**
     * 从 MinIO 读取撤销户 Excel（headRowNumber=3，跳过标题行和分组行）
     */
    private List<CarbonLedgerRevokeImportDataVO> readRevokeExcelFromMinio(String filePath) throws Exception {
        String downloadUrl = minioService.getFileDownloadUrl(minioProperties.getBucketName(), filePath);
        URL url = new URL(downloadUrl);
        URI uri = new URI(url.getProtocol(), url.getAuthority(), url.getPath(), url.getQuery(), url.getRef());
        try (InputStream is = uri.toURL().openStream()) {
            return EasyExcel.read(is, CarbonLedgerRevokeImportDataVO.class, null)
                    .sheet()
                    .headRowNumber(3)
                    .doReadSync();
        }
    }

    /**
     * 校验撤销户导入数据
     */
    private String validateRevokeImportData(CarbonLedgerRevokeImportDataVO data) {
        List<String> errors = new ArrayList<>();
        if (StrUtil.isBlank(data.getCityName())) {
            errors.add("市名称不能为空");
        }
        if (StrUtil.isBlank(data.getDistrictName())) {
            errors.add("区县名称不能为空");
        }
        if (StrUtil.isBlank(data.getTownName())) {
            errors.add("乡镇名称不能为空");
        }
//        if (StrUtil.isBlank(data.getVillageName())) {
//            errors.add("村名称不能为空");
//        }
        if (StrUtil.isBlank(data.getAddress())) {
            errors.add("详细地址不能为空");
        }
        if (StrUtil.isBlank(data.getIdCard())) {
            errors.add("身份证号不能为空");
        } else if (data.getIdCard().length() != 18 && data.getIdCard().length() != 15) {
            errors.add("身份证号格式不正确");
        }
        if (!errors.isEmpty()) {
            return String.join("; ", errors);
        }
        CarbonUserInfoDO existingUser = findRevokeUserByIdCardAndAddress(data);
        if (existingUser == null) {
            return "未找到匹配的用户信息（身份证号: " + data.getIdCard() + ", 详细地址: " + data.getAddress() + "）";
        }
        return null;
    }

    /**
     * 执行变更户导入（使用专用模板：多行表头、列序号映射）
     */
    private void executeChangeImport(Long taskId, String fileUrl) throws Exception {
        List<CarbonLedgerChangeImportDataVO> dataList = readChangeExcelFromMinio(fileUrl);
        int successCount = 0;
        int failCount = 0;
        List<CarbonLedgerImportDetailDO> detailDOs = new ArrayList<>();

        for (CarbonLedgerChangeImportDataVO dataVO : dataList) {
            CarbonLedgerImportDetailDO detail = CarbonLedgerImportConvert.INSTANCE.convertDetail(dataVO);
            detail.setTaskId(taskId);

            try {
                String errorMsg = validateChangeImportData(dataVO);
                if (StrUtil.isNotBlank(errorMsg)) {
                    throw new RuntimeException(errorMsg);
                }
                CarbonUserInfoDO userInfo = updateUserInfo(dataVO);
                fillSuccessDetail(detail, userInfo);
                resolveAreaNameToDetail(detail, dataVO);
                successCount++;
            } catch (Exception e) {
                resolveAreaNameToDetail(detail, dataVO);
                detail.setImportStatus(LedgerImportDetailStatusEnum.FAIL.getType());
                detail.setErrorMsg(e.getMessage());
                failCount++;
            }
            detailDOs.add(detail);
        }

        if (CollUtil.isNotEmpty(detailDOs)) {
            importDetailMapper.insertBatch(detailDOs);
        }

        CarbonLedgerImportTaskDO updating = new CarbonLedgerImportTaskDO();
        updating.setId(taskId);
        updating.setSuccessCount(successCount);
        updating.setFailCount(failCount);
        if (failCount == 0) {
            updating.setImportStatus(LedgerImportStatusEnum.IMPORT_SUCCESS.getType());
        } else if (successCount == 0) {
            updating.setImportStatus(LedgerImportStatusEnum.IMPORT_FAIL.getType());
        } else {
            updating.setImportStatus(LedgerImportStatusEnum.PARTIAL_FAIL.getType());
        }
        importTaskMapper.updateById(updating);
    }

    /**
     * 从 MinIO 读取变更户 Excel（headRowNumber=3，跳过标题行和分组行）
     */
    private List<CarbonLedgerChangeImportDataVO> readChangeExcelFromMinio(String filePath) throws Exception {
        String downloadUrl = minioService.getFileDownloadUrl(minioProperties.getBucketName(), filePath);
        URL url = new URL(downloadUrl);
        URI uri = new URI(url.getProtocol(), url.getAuthority(), url.getPath(), url.getQuery(), url.getRef());
        try (InputStream is = uri.toURL().openStream()) {
            return EasyExcel.read(is, CarbonLedgerChangeImportDataVO.class, null)
                    .sheet()
                    .headRowNumber(3)
                    .doReadSync();
        }
    }

    /**
     * 校验变更户导入数据
     */
    private String validateChangeImportData(CarbonLedgerChangeImportDataVO data) {
        List<String> errors = new ArrayList<>();
        if (StrUtil.isBlank(data.getCityName())) {
            errors.add("市名称不能为空");
        }
        if (StrUtil.isBlank(data.getDistrictName())) {
            errors.add("区县名称不能为空");
        }
        if (StrUtil.isBlank(data.getTownName())) {
            errors.add("乡镇名称不能为空");
        }
//        if (StrUtil.isBlank(data.getVillageName())) {
//            errors.add("村名称不能为空");
//        }
        if (StrUtil.isBlank(data.getAddress())) {
            errors.add("原详细地址不能为空");
        }
        if (StrUtil.isBlank(data.getIdCard())) {
            errors.add("身份证号不能为空");
        } else if (data.getIdCard().length() != 18 && data.getIdCard().length() != 15) {
            errors.add("身份证号格式不正确");
        }
        if (!errors.isEmpty()) {
            return String.join("; ", errors);
        }
        CarbonUserInfoDO existingUser = findChangeUserByIdCardAndAddress(data);
        if (existingUser == null) {
            return "未找到匹配的用户信息（身份证号: " + data.getIdCard() + ", 地址: " + data.getAddress() + "）";
        }
        return null;
    }

    /**
     * 根据变更户VO的身份证号和原详细地址查找用户
     */
    private CarbonUserInfoDO findChangeUserByIdCardAndAddress(CarbonLedgerChangeImportDataVO dataVO) {
        if (StrUtil.isBlank(dataVO.getIdCard())) {
            throw new RuntimeException("身份证号不能为空");
        }
        if (StrUtil.isBlank(dataVO.getAddress())) {
            throw new RuntimeException("详细地址不能为空");
        }
        return userInfoMapper.selectByIdCardAndAddress(dataVO.getIdCard(), dataVO.getAddress());
    }

    /**
     * 更新改造类型（只更新改造类别和改造类型）
     * 查询用户逻辑：根据身份证号+地址查询
     */
    private CarbonUserInfoDO updateReformType(CarbonLedgerImportDataVO dataVO) {
        if (StrUtil.isBlank(dataVO.getIdCard())) {
            throw new RuntimeException("身份证号不能为空");
        }
        if (StrUtil.isBlank(dataVO.getAddress())) {
            throw new RuntimeException("地址不能为空");
        }

        // 根据身份证号+地址查找用户
        CarbonUserInfoDO existingUser = userInfoMapper.selectByIdCardAndAddress(dataVO.getIdCard(), dataVO.getAddress());
        if (existingUser == null) {
            throw new RuntimeException("未找到匹配的用户信息（身份证号: " + dataVO.getIdCard() + "，地址: " + dataVO.getAddress() + "）");
        }

        existingUser.setReformType(resolveDictCode(ReformTypeEnum.values(), dataVO.getReformType()));
        existingUser.setReformMode(resolveDictCode(ReformModeEnum.values(), dataVO.getReformMode()));
        userInfoMapper.updateById(existingUser);
        return existingUser;
    }

    /**
     * 根据身份证号和（气号或电表号）查找用户
     */
    private CarbonUserInfoDO findUserByIdCardAndMeter(CarbonLedgerImportDataVO dataVO) {
        if (StrUtil.isBlank(dataVO.getIdCard())) {
            throw new RuntimeException("身份证号不能为空");
        }

        // 先根据身份证号查找
        CarbonUserInfoDO user = userInfoMapper.selectByIdCard(dataVO.getIdCard());
        if (user != null) {
            return user;
        }

        // 如果有电表号，根据电表号查找
        if (StrUtil.isNotBlank(dataVO.getElectricityId())) {
            user = userInfoMapper.selectByElectricityId(dataVO.getElectricityId());
            if (user != null) {
                return user;
            }
        }

        // 如果有气号，根据气号查找
        if (StrUtil.isNotBlank(dataVO.getGasId())) {
            user = userInfoMapper.selectByGasId(dataVO.getGasId());
            return user;
        }

        return null;
    }

    /**
     * 默认省份：河北省，编码 13
     */
    private static final Long DEFAULT_PROVINCE_CODE = 13L;

    /**
     * 根据行政区中文名称反查编码并设置到用户信息
     * 模板不含省份字段，默认归属河北省（13），以河北省为父节点查找市→区→乡→村
     */
    private void resolveAndSetAreaCodes(CarbonUserInfoDO userInfo, CarbonLedgerImportDataVO dataVO) {
        Long provinceCode = DEFAULT_PROVINCE_CODE;
        Long cityCode     = resolveAreaCode(provinceCode, dataVO.getCityName());
        Long districtCode = resolveAreaCode(cityCode, dataVO.getDistrictName());
        Long townCode     = resolveAreaCode(districtCode, dataVO.getTownName());
        Long villageCode  = resolveAreaCode(townCode, dataVO.getVillageName());

        userInfo.setProvinceCode(provinceCode);
        userInfo.setCityCode(cityCode);
        userInfo.setDistrictCode(districtCode);
        userInfo.setTownCode(townCode);
        userInfo.setVillageCode(villageCode);
    }

    /**
     * 将Excel中的省市区乡村名称直接写入明细
     */
    private void resolveAreaNameToDetail(CarbonLedgerImportDetailDO detail, CarbonLedgerImportDataVO dataVO) {
        detail.setProvinceName("河北省");
        detail.setCityName(dataVO.getCityName());
        detail.setDistrictName(dataVO.getDistrictName());
        detail.setTownName(dataVO.getTownName());
        detail.setVillageName(dataVO.getVillageName());
    }

    private void resolveAreaNameToDetail(CarbonLedgerImportDetailDO detail, CarbonLedgerRevokeImportDataVO dataVO) {
        detail.setProvinceName("河北省");
        detail.setCityName(dataVO.getCityName());
        detail.setDistrictName(dataVO.getDistrictName());
        detail.setTownName(dataVO.getTownName());
        detail.setVillageName(dataVO.getVillageName());
    }

    private void resolveAreaNameToDetail(CarbonLedgerImportDetailDO detail, CarbonLedgerChangeImportDataVO dataVO) {
        detail.setProvinceName("河北省");
        detail.setCityName(dataVO.getCityName());
        detail.setDistrictName(dataVO.getDistrictName());
        detail.setTownName(dataVO.getTownName());
        detail.setVillageName(dataVO.getVillageName());
    }

    /**
     * 根据父级编码和行政区名称解析编码
     */
    private Long resolveAreaCode(Long parentId, String name) {
        if (parentId == null || StrUtil.isBlank(name)) {
            return null;
        }
        AreaRespDTO area = areaApi.getAreaByName(parentId, name).getData();
        return area != null ? area.getId() : null;
    }

    /**
     * 根据字典名称或编码反查编码
     * 支持两种输入：
     *   1. 中文名称（如"电代煤"）→ 反查对应 type 编码
     *   2. 直接输入编码（如"1"）→ 校验后原样返回
     */
    private String resolveDictCode(ArrayValuable<String>[] enumValues, String name) {
        if (StrUtil.isBlank(name)) {
            return null;
        }
        for (ArrayValuable<String> enumValue : enumValues) {
            try {
                java.lang.reflect.Field typeField  = enumValue.getClass().getDeclaredField("type");
                java.lang.reflect.Field nameField  = enumValue.getClass().getDeclaredField("name");
                typeField.setAccessible(true);
                nameField.setAccessible(true);
                String typeCode  = (String) typeField.get(enumValue);
                String enumName  = (String) nameField.get(enumValue);
                // 输入的是编码，直接返回
                if (name.equals(typeCode)) {
                    return typeCode;
                }
                // 输入的是中文名称，返回对应编码
                if (name.equals(enumName)) {
                    return typeCode;
                }
            } catch (Exception e) {
                log.warn("解析字典编码失败: {}", enumValue, e);
            }
        }
        return name; // 找不到匹配的枚举则返回原值
    }

    @Override
    public PageResult<CarbonLedgerImportDetailRespVO> getImportDetailPage(CarbonLedgerImportDetailPageReqVO pageReqVO) {
        return CarbonLedgerImportConvert.INSTANCE.convertDetailPage(importDetailMapper.selectPage(pageReqVO));
    }

    @Override
    public List<CarbonLedgerImportDetailRespVO> getImportDetailList(Long taskId) {
        CarbonLedgerImportTaskDO task = validateImportTaskExists(taskId);
        validateDataPermission(task);
        return CarbonLedgerImportConvert.INSTANCE.convertDetailList(importDetailMapper.selectListByTaskId(taskId));
    }

    @Override
    public List<CarbonLedgerImportDetailRespVO> getFailDetailList(Long taskId) {
        CarbonLedgerImportTaskDO task = validateImportTaskExists(taskId);
        validateDataPermission(task);
        return CarbonLedgerImportConvert.INSTANCE.convertDetailList(importDetailMapper.selectFailListByTaskId(taskId));
    }

    private void validateFileExt(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (StrUtil.isBlank(fileName)) {
            throw exception(LEDGER_IMPORT_FILE_FORMAT_ERROR);
        }
        String lowerName = fileName.toLowerCase();
        if (!lowerName.endsWith(".xls") && !lowerName.endsWith(".xlsx")) {
            throw exception(LEDGER_IMPORT_FILE_FORMAT_ERROR);
        }
    }

    private FileParseResult parseExcel(MultipartFile file, String uploadType) {
        try {
            int dataCount;
            if ("2".equals(uploadType) || "3".equals(uploadType)) {
                // 变更户/撤销户模板有2行标题/分组头，从第3行开始读取数据
                Class<?> voClass = "2".equals(uploadType)
                        ? CarbonLedgerChangeImportDataVO.class
                        : CarbonLedgerRevokeImportDataVO.class;
                List<?> list = EasyExcel.read(file.getInputStream(), voClass, null)
                        .sheet()
                        .headRowNumber(3)
                        .doReadSync();
                dataCount = list.size();
            } else {
                List<CarbonLedgerImportDataVO> list = ExcelUtils.read(file, CarbonLedgerImportDataVO.class);
                dataCount = list.size();
            }
            return new FileParseResult(dataCount, true);
        } catch (Exception e) {
            log.warn("导入文件解析失败: {}", file.getOriginalFilename(), e);
            return new FileParseResult(0, false);
        }
    }

    private List<CarbonLedgerImportDataVO> readExcelFromMinio(String downloadUrl) throws Exception {
//        log.info("文件路径filePath：{}",filePath);
//        String downloadUrl = minioService.getFileDownloadUrl(minioProperties.getBucketName(), filePath);
        // 对 URL 中的中文及特殊字符（如空格）进行百分号编码，避免服务器返回 400
        URL url = new URL(downloadUrl);
        URI uri = new URI(url.getProtocol(), url.getAuthority(), url.getPath(), url.getQuery(), url.getRef());
        List<CarbonLedgerImportDataVO> result = new ArrayList<>();
        try (InputStream is = uri.toURL().openStream()) {
            List<CarbonLedgerImportDataVO> list = EasyExcel.read(is, CarbonLedgerImportDataVO.class, null).sheet().doReadSync();
            result.addAll(list);
        }
        return result;
    }

    private String validateImportData(CarbonLedgerImportDataVO data, String uploadType) {
        List<String> errors = new ArrayList<>();
        if (StrUtil.isBlank(data.getUsername())) {
            errors.add("用户姓名不能为空");
        }
        if (StrUtil.isBlank(data.getIdCard())) {
            errors.add("身份证号不能为空");
        } else if (data.getIdCard().length() != 18 && data.getIdCard().length() != 15) {
            errors.add("身份证号格式不正确");
        }
        if (StrUtil.isBlank(data.getCityName())) {
            errors.add("市名称不能为空");
        }
        if (StrUtil.isBlank(data.getDistrictName())) {
            errors.add("区县名称不能为空");
        }
        if (StrUtil.isBlank(data.getTownName())) {
            errors.add("乡镇名称不能为空");
        }
//        if (StrUtil.isBlank(data.getVillageName())) {
//            errors.add("村名称不能为空");
//        }
        if (StrUtil.isBlank(data.getAddress())) {
            errors.add("详细地址不能为空");
        }
        if (!errors.isEmpty()) {
            return String.join("; ", errors);
        }

        // 新增户需要校验组合唯一性：身份证号单独可重复，但与电表号/气号/地址的组合不能重复
        if ("1".equals(uploadType)) {
            if (StrUtil.isNotBlank(data.getElectricityId())) {
                CarbonUserInfoDO existingByIdCardAndElectricity = userInfoMapper.selectByIdCardAndElectricityId(data.getIdCard(), data.getElectricityId());
                if (existingByIdCardAndElectricity != null) {
                    return "身份证号与电表号组合已存在: 身份证号=" + data.getIdCard() + ", 电表号=" + data.getElectricityId();
                }
            }
            if (StrUtil.isNotBlank(data.getGasId())) {
                CarbonUserInfoDO existingByIdCardAndGas = userInfoMapper.selectByIdCardAndGasId(data.getIdCard(), data.getGasId());
                if (existingByIdCardAndGas != null) {
                    return "身份证号与气号组合已存在: 身份证号=" + data.getIdCard() + ", 气号=" + data.getGasId();
                }
            }
            CarbonUserInfoDO existingByIdCardAndAddress = userInfoMapper.selectByIdCardAndAddress(data.getIdCard(), data.getAddress());
            if (existingByIdCardAndAddress != null) {
                return "身份证号与地址组合已存在: 身份证号=" + data.getIdCard() + ", 地址=" + data.getAddress();
            }
        }

        // 改造类型变更使用身份证号+气号/电表号匹配
        if ("4".equals(uploadType)) {
            CarbonUserInfoDO existingUser = findUserByIdCardAndMeter(data);
            if (existingUser == null) {
                return "未找到匹配的用户信息（身份证号: " + data.getIdCard() + "）";
            }
        }

        return null;
    }

    @VisibleForTesting
    CarbonLedgerImportTaskDO validateImportTaskExists(Long id) {
        if (id == null) {
            return null;
        }
        CarbonLedgerImportTaskDO task = importTaskMapper.selectById(id);
        if (task == null) {
            throw exception(LEDGER_IMPORT_TASK_NOT_EXISTS);
        }
        return task;
    }

    private void validateDataPermission(CarbonLedgerImportTaskDO task) {
        // 注意：由于CarbonLedgerImportTaskDO不再包含地区字段，需要通过联表查询验证
        // 这里暂时保留原逻辑，但建议使用validateDataPermissionWithFile方法
        // 实际上应该通过ledger_file_id关联查询台账文件表来验证权限
    }

    private void validateDataPermissionWithFile(CarbonLedgerImportTaskWithFileDO taskWithFile) {
        LedgerDataPermissionHelper.UserAreaContext context = LedgerDataPermissionHelper.getCurrentUserAreaContext();
        if (context.getScope() == LedgerDataPermissionHelper.DataScope.DISTRICT
                && !context.getDistrictCode().equals(taskWithFile.getDistrictCode())) {
            throw exception(LEDGER_IMPORT_TASK_NOT_EXISTS);
        }
        if (context.getScope() == LedgerDataPermissionHelper.DataScope.CITY
                && !context.getCityCode().equals(taskWithFile.getCityCode())) {
            throw exception(LEDGER_IMPORT_TASK_NOT_EXISTS);
        }
    }

    /**
     * 批量填充导入任务的城市和区县名称
     */
    private void fillAreaNames(List<CarbonLedgerImportTaskRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        // 收集所有出现的城市和区县编码
        Set<Long> areaIds = new HashSet<>();
        list.forEach(item -> {
            if (StrUtil.isNotBlank(item.getCityCode())) {
                try {
                    areaIds.add(Long.parseLong(item.getCityCode()));
                } catch (NumberFormatException ignored) {
                }
            }
            if (StrUtil.isNotBlank(item.getDistrictCode())) {
                try {
                    areaIds.add(Long.parseLong(item.getDistrictCode()));
                } catch (NumberFormatException ignored) {
                }
            }
        });
        if (areaIds.isEmpty()) {
            return;
        }
        CommonResult<List<AreaRespDTO>> areaDtoResult = areaApi.getAreaList(areaIds);
        List<AreaRespDTO> areaRespDTOS = areaDtoResult.getData();
        Map<Long, String> areaMap = CollectionUtils.convertMap(areaRespDTOS, AreaRespDTO::getId, AreaRespDTO::getName);
        list.forEach(item -> {
            if (StrUtil.isNotBlank(item.getCityCode())) {
                try {
                    Long cityId = Long.parseLong(item.getCityCode());
                    item.setCityName(areaMap.get(cityId));
                } catch (NumberFormatException ignored) {
                }
            }
            if (StrUtil.isNotBlank(item.getDistrictCode())) {
                try {
                    Long districtId = Long.parseLong(item.getDistrictCode());
                    item.setDistrictName(areaMap.get(districtId));
                } catch (NumberFormatException ignored) {
                }
            }
        });
    }

    private static class FileParseResult {
        private final Integer dataCount;
        private final boolean success;

        FileParseResult(Integer dataCount, boolean success) {
            this.dataCount = dataCount;
            this.success = success;
        }

        Integer getDataCount() {
            return dataCount;
        }

        boolean isSuccess() {
            return success;
        }
    }
}
