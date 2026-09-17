package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.common.util.collection.CollectionUtils;
import cn.iocoder.power.framework.minio.config.MinioProperties;
import cn.iocoder.power.framework.minio.core.IMinioService;
import cn.iocoder.power.module.carbon.controller.admin.basedata.vo.CarbonFileMinioUploadVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasSafetyOfficerPageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasSafetyOfficerRespVO;
import cn.iocoder.power.module.carbon.controller.admin.maintenance.vo.CarbonGasSafetyOfficerSaveReqVO;
import cn.iocoder.power.module.carbon.convert.CarbonGasSafetyOfficerConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonGasSafetyOfficerDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonMaintenanceEnterpriseDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonGasSafetyOfficerMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonMaintenanceEnterpriseMapper;
import cn.iocoder.power.module.carbon.service.CarbonGasSafetyOfficerService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.*;

@Service
@Slf4j
public class CarbonGasSafetyOfficerServiceImpl implements CarbonGasSafetyOfficerService {

    @Resource
    private CarbonGasSafetyOfficerMapper officerMapper;

    @Resource
    private CarbonMaintenanceEnterpriseMapper enterpriseMapper;

    @Resource
    private IMinioService minioService;

    @Resource
    private MinioProperties minioProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOfficer(CarbonGasSafetyOfficerSaveReqVO createReqVO,
                              MultipartFile idCardFrontImage) throws Exception {
        CarbonGasSafetyOfficerDO officer = CarbonGasSafetyOfficerConvert.INSTANCE.convert(createReqVO);
        // 上传身份证正面图片到MinIO
        uploadIdCardImages(officer, idCardFrontImage);
        officerMapper.insert(officer);
        return officer.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOfficer(CarbonGasSafetyOfficerSaveReqVO updateReqVO,
                              MultipartFile idCardFrontImage) throws Exception {
        validateOfficerExists(updateReqVO.getId());
        CarbonGasSafetyOfficerDO updateObj = CarbonGasSafetyOfficerConvert.INSTANCE.convert(updateReqVO);
        // 上传身份证正面图片到MinIO
        uploadIdCardImages(updateObj, idCardFrontImage);
        officerMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOfficer(Long id) {
        CarbonGasSafetyOfficerDO officer = officerMapper.selectById(id);
        if (officer == null) {
            throw exception(GAS_SAFETY_OFFICER_NOT_EXISTS);
        }
        officerMapper.deleteById(id);
    }

    @Override
    public CarbonGasSafetyOfficerRespVO getOfficer(Long id) {
        CarbonGasSafetyOfficerDO officer = officerMapper.selectById(id);
        if (officer == null) {
            return null;
        }
        CarbonGasSafetyOfficerRespVO respVO = CarbonGasSafetyOfficerConvert.INSTANCE.convert(officer);
        // 填充企业名称
        fillEnterpriseName(respVO);
        return respVO;
    }

    @Override
    public PageResult<CarbonGasSafetyOfficerRespVO> getOfficerPage(CarbonGasSafetyOfficerPageReqVO pageReqVO) {
        PageResult<CarbonGasSafetyOfficerDO> pageResult = officerMapper.selectPage(pageReqVO);
        PageResult<CarbonGasSafetyOfficerRespVO> resultVo = CarbonGasSafetyOfficerConvert.INSTANCE.convertPage(pageResult);
        if (CollUtil.isNotEmpty(resultVo.getList())) {

            // 批量填充企业名称
            fillEnterpriseNames(resultVo.getList());
        }
        return resultVo;
    }

    @Override
    public List<CarbonGasSafetyOfficerRespVO> getOfficerListByEnterpriseId(Long enterpriseId) {
        List<CarbonGasSafetyOfficerDO> officers = officerMapper.selectByEnterpriseId(enterpriseId);
        List<CarbonGasSafetyOfficerRespVO> list = CarbonGasSafetyOfficerConvert.INSTANCE.convertList(officers);

        // 批量填充企业名称
        fillEnterpriseNames(list);
        return list;
    }

    @Override
    public List<CarbonGasSafetyOfficerRespVO> getOfficerList(CarbonGasSafetyOfficerPageReqVO pageReqVO) {
        List<CarbonGasSafetyOfficerDO> list = officerMapper.selectList(pageReqVO);
        List<CarbonGasSafetyOfficerRespVO> result = CarbonGasSafetyOfficerConvert.INSTANCE.convertList(list);
        if (CollUtil.isNotEmpty(result)) {
            fillEnterpriseNames(result);
            fillAreaLabels(result);
        }
        return result;
    }

    @VisibleForTesting
    void validateOfficerExists(Long id) {
        if (id == null) {
            return;
        }
        CarbonGasSafetyOfficerDO officer = officerMapper.selectById(id);
        if (officer == null) {
            throw exception(GAS_SAFETY_OFFICER_NOT_EXISTS);
        }
    }

    /**
     * 填充单个安全员的企业名称和统一社会信用代码
     */
    private void fillEnterpriseName(CarbonGasSafetyOfficerRespVO respVO) {
        if (respVO.getEnterpriseId() == null) {
            return;
        }
        CarbonMaintenanceEnterpriseDO enterprise = enterpriseMapper.selectById(respVO.getEnterpriseId());
        if (enterprise != null) {
            respVO.setEnterpriseName(enterprise.getEnterpriseName());
            respVO.setUnifiedSocialCreditCode(enterprise.getUnifiedSocialCreditCode());
        }
    }


    /**
     * 批量填充安全员的企业名称和统一社会信用代码
     */
    private void fillEnterpriseNames(List<CarbonGasSafetyOfficerRespVO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Set<Long> enterpriseIds = list.stream()
                .map(CarbonGasSafetyOfficerRespVO::getEnterpriseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (enterpriseIds.isEmpty()) {
            return;
        }
        List<CarbonMaintenanceEnterpriseDO> enterprises = enterpriseMapper.selectBatchIds(enterpriseIds);
        Map<Long, String> enterpriseMap = CollectionUtils.convertMap(enterprises,
                CarbonMaintenanceEnterpriseDO::getId, CarbonMaintenanceEnterpriseDO::getEnterpriseName);
        Map<Long, String> creditCodeMap = CollectionUtils.convertMap(enterprises,
                CarbonMaintenanceEnterpriseDO::getId, CarbonMaintenanceEnterpriseDO::getUnifiedSocialCreditCode);
        list.forEach(item -> {
            item.setEnterpriseName(enterpriseMap.get(item.getEnterpriseId()));
            item.setUnifiedSocialCreditCode(creditCodeMap.get(item.getEnterpriseId()));
        });
    }

    /**
     * 从 area JSON 中提取 label 填充到 areaNName 字段
     */
    private void fillAreaLabels(List<CarbonGasSafetyOfficerRespVO> list) {
        list.forEach(item -> {
            item.setArea1Name(parseAreaLabel(item.getArea1()));
            item.setArea2Name(parseAreaLabel(item.getArea2()));
            item.setArea3Name(parseAreaLabel(item.getArea3()));
            item.setArea4Name(parseAreaLabel(item.getArea4()));
            item.setArea5Name(parseAreaLabel(item.getArea5()));
        });
    }

    private String parseAreaLabel(String areaJson) {
        if (StrUtil.isBlank(areaJson)) {
            return null;
        }
        try {
            JSONObject obj = JSONUtil.parseObj(areaJson);
            return obj.getStr("label");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 上传身份证正面图片到MinIO，并将URL设置到DO对象
     * 如果文件为空则跳过上传
     */
    private void uploadIdCardImages(CarbonGasSafetyOfficerDO officer,
                                    MultipartFile idCardFrontImage) throws Exception {
        if (idCardFrontImage != null && !idCardFrontImage.isEmpty()) {
            String fileName = idCardFrontImage.getOriginalFilename();
            String path = minioService.uploadFile(idCardFrontImage, minioProperties.getBucketName(), fileName);
            String urlPath = String.format(CarbonFileMinioUploadVO.URL_TEMPLATE,
                    minioProperties.getIp(), minioProperties.getPort(), minioProperties.getBucketName(), path);
            log.info("[uploadIdCardImages][正面] fileName={}, urlPath={}", fileName, urlPath);
            officer.setIdCardFrontImage(urlPath);
        } else {
            log.warn("[uploadIdCardImages][正面] 文件为空，跳过上传");
        }
    }
}
