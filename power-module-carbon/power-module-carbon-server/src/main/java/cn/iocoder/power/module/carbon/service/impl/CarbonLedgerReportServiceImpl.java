package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.*;
import cn.iocoder.power.module.carbon.convert.CarbonLedgerReportConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerAuditLogDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerFileDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerReportDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerReportWithFileDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonLedgerAuditLogMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonLedgerFileMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonLedgerReportMapper;
import cn.iocoder.power.module.carbon.enums.LedgerFileAuditStatusEnum;
import cn.iocoder.power.module.carbon.service.CarbonLedgerFileService;
import cn.iocoder.power.module.carbon.service.CarbonLedgerReportService;
import cn.iocoder.power.module.carbon.util.LedgerDataPermissionHelper;
import cn.iocoder.power.framework.minio.config.MinioProperties;
import cn.iocoder.power.framework.minio.core.IMinioService;
import cn.iocoder.power.module.system.api.area.AreaApi;
import cn.iocoder.power.module.system.api.area.dto.AreaRespDTO;
import cn.iocoder.power.framework.common.pojo.CommonResult;
import cn.iocoder.power.framework.common.util.collection.CollectionUtils;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static cn.iocoder.power.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.power.module.carbon.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class CarbonLedgerReportServiceImpl implements CarbonLedgerReportService {

    @Resource
    private CarbonLedgerReportMapper ledgerReportMapper;

    @Resource
    private CarbonLedgerFileMapper ledgerFileMapper;

    @Resource
    private CarbonLedgerFileService ledgerFileService;

    @Resource
    private CarbonLedgerAuditLogMapper ledgerAuditLogMapper;

    @Resource
    private IMinioService minioService;

    @Resource
    private MinioProperties minioProperties;

    @Resource
    private AreaApi areaApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createLedgerReport(CarbonLedgerReportSaveReqVO createReqVO, MultipartFile stampedReportFile) throws Exception {
        // 验证关联的台账文件是否存在
        if (createReqVO.getLedgerFileId() != null) {
            CarbonLedgerFileDO ledgerFile = ledgerFileMapper.selectById(createReqVO.getLedgerFileId());
            if (ledgerFile == null) {
                throw exception(LEDGER_FILE_NOT_EXISTS);
            }
        }

        // 处理盖章报告文件上传
        if (stampedReportFile != null && !stampedReportFile.isEmpty()) {
            String fileName = stampedReportFile.getOriginalFilename();
            if (StrUtil.isBlank(fileName) || !fileName.toLowerCase().endsWith(".pdf")) {
                throw exception(LEDGER_STAMPED_REPORT_FORMAT_ERROR);
            }
            String path = minioService.uploadFile(stampedReportFile, minioProperties.getBucketName(), fileName);
            createReqVO.setStampedReportUrl(minioService.getFileDownloadUrl(minioProperties.getBucketName(),path));
            createReqVO.setStampedReportName(fileName);
        }

        CarbonLedgerReportDO report = CarbonLedgerReportConvert.INSTANCE.convert(createReqVO);
        // 状态由基表 carbon_ledger_file.audit_status 统一管理

        ledgerReportMapper.insert(report);

        // 创建审核轨迹：待提交
        createAuditLog(report.getId(), LedgerFileAuditStatusEnum.DRAFT.getType(), "创建上报", null);

        return report.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLedgerReport(CarbonLedgerReportSaveReqVO updateReqVO, MultipartFile stampedReportFile) throws Exception {
        CarbonLedgerReportDO existing = validateLedgerReportExists(updateReqVO.getId());
        validateDataPermission(existing);
        // 状态由基表管理，检查基表状态
        CarbonLedgerFileDO ledgerFile = ledgerFileMapper.selectById(existing.getLedgerFileId());
        if (!LedgerFileAuditStatusEnum.isDraft(ledgerFile.getAuditStatus())) {
            throw exception(LEDGER_REPORT_NOT_DRAFT);
        }

        // 处理盖章报告文件上传
        if (stampedReportFile != null && !stampedReportFile.isEmpty()) {
            String fileName = stampedReportFile.getOriginalFilename();
            if (StrUtil.isBlank(fileName) || !fileName.toLowerCase().endsWith(".pdf")) {
                throw exception(LEDGER_STAMPED_REPORT_FORMAT_ERROR);
            }
            String path = minioService.uploadFile(stampedReportFile, minioProperties.getBucketName(), fileName);
            updateReqVO.setStampedReportUrl(path);
            updateReqVO.setStampedReportName(fileName);
        }

        CarbonLedgerReportDO updateObj = CarbonLedgerReportConvert.INSTANCE.convert(updateReqVO);
        ledgerReportMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLedgerReport(Long id) {
        CarbonLedgerReportDO existing = validateLedgerReportExists(id);
        validateDataPermission(existing);
        // 状态由基表管理，检查基表状态
        CarbonLedgerFileDO ledgerFile = ledgerFileMapper.selectById(existing.getLedgerFileId());
        if (!LedgerFileAuditStatusEnum.isDraft(ledgerFile.getAuditStatus())) {
            throw exception(LEDGER_REPORT_NOT_DRAFT);
        }
        ledgerReportMapper.deleteById(id);
    }

    @Override
    public CarbonLedgerReportRespVO getLedgerReport(Long id) {
        // 使用联表查询获取上报和文件信息
        CarbonLedgerReportWithFileDO reportWithFile = ledgerReportMapper.selectOneWithFile(id);
        if (reportWithFile == null) {
            throw exception(LEDGER_REPORT_NOT_EXISTS);
        }
        // 验证数据权限（通过关联的台账文件）
        validateDataPermissionWithFile(reportWithFile);
        
        // 转换为 RespVO
        CarbonLedgerReportRespVO respVO = CarbonLedgerReportConvert.INSTANCE.convertFromWithFile(reportWithFile);
        // 填充审核轨迹
        respVO.setAuditLogs(CarbonLedgerReportConvert.INSTANCE.convertAuditLogList(ledgerAuditLogMapper.selectListByReportId(reportWithFile.getLedgerFileId())));
        // 填充城市和区县名称
        fillAreaNames(respVO);
        return respVO;
    }

    @Override
    public PageResult<CarbonLedgerReportRespVO> getLedgerReportPage(CarbonLedgerReportPageReqVO pageReqVO) {
        // 使用联表查询获取上报和文件信息
        PageResult<CarbonLedgerReportWithFileDO> pageResult = ledgerReportMapper.selectPageWithFile(pageReqVO);
        // 转换为 RespVO
        PageResult<CarbonLedgerReportRespVO> result = CarbonLedgerReportConvert.INSTANCE.convertPageFromWithFile(pageResult);
        // 批量填充城市和区县名称
        fillAreaNames(result.getList());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitLedgerReport(Long id) {
        CarbonLedgerReportDO existing = validateLedgerReportExists(id);
        validateDataPermission(existing);

        // 调用台账文件服务修改状态为审核中
        ledgerFileService.updateAuditStatus(existing.getLedgerFileId(),
                LedgerFileAuditStatusEnum.AUDITING.getType(), null);

        // 更新提交时间
        CarbonLedgerReportDO updateObj = new CarbonLedgerReportDO();
        updateObj.setId(id);
        ledgerReportMapper.updateById(updateObj);

        createAuditLog(id, LedgerFileAuditStatusEnum.AUDITING.getType(), "提交审核", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewLedgerReport(CarbonLedgerReportReviewReqVO reviewReqVO) {
        CarbonLedgerReportDO existing = validateLedgerReportExists(reviewReqVO.getId());
        validateDataPermission(existing);

        String operator = SecurityFrameworkUtils.getLoginUserNickname();

        if (reviewReqVO.getApproved()) {
            // 调用台账文件服务修改状态为已通过（审核人、审核时间由updateAuditStatus内部设置）
            ledgerFileService.updateAuditStatus(existing.getLedgerFileId(),
                    LedgerFileAuditStatusEnum.APPROVED.getType(), null);
            createAuditLog(reviewReqVO.getId(), LedgerFileAuditStatusEnum.APPROVED.getType(), "审核通过", operator);
        } else {
            if (StrUtil.isBlank(reviewReqVO.getRejectReason())) {
                throw exception(LEDGER_REPORT_REJECT_REASON_REQUIRED);
            }
            // 调用台账文件服务修改状态为已驳回（审核人、审核时间、驳回原因由updateAuditStatus内部设置）
            ledgerFileService.updateAuditStatus(existing.getLedgerFileId(),
                    LedgerFileAuditStatusEnum.REJECTED.getType(), reviewReqVO.getRejectReason());
            createAuditLog(reviewReqVO.getId(), LedgerFileAuditStatusEnum.REJECTED.getType(),
                    "审核驳回：" + reviewReqVO.getRejectReason(), operator);
        }
    }



    private void createAuditLog(Long reportId, String status, String reason, String operator) {
        CarbonLedgerAuditLogDO auditLog = new CarbonLedgerAuditLogDO();
        auditLog.setReportId(reportId);
        auditLog.setStatus(status);
        auditLog.setReason(reason);
        auditLog.setOperator(StrUtil.isBlank(operator) ? SecurityFrameworkUtils.getLoginUserNickname() : operator);
        auditLog.setChangeTime(LocalDateTime.now());
        ledgerAuditLogMapper.insert(auditLog);
    }

    /**
     * 验证数据权限（基于联表查询结果）
     */
    private void validateDataPermissionWithFile(CarbonLedgerReportWithFileDO reportWithFile) {
        if (reportWithFile.getCityCode() == null && reportWithFile.getDistrictCode() == null) {
            return;
        }
        LedgerDataPermissionHelper.UserAreaContext context = LedgerDataPermissionHelper.getCurrentUserAreaContext();
        if (context.getScope() == LedgerDataPermissionHelper.DataScope.DISTRICT
                && !context.getDistrictCode().equals(reportWithFile.getDistrictCode())) {
            throw exception(LEDGER_REPORT_NOT_EXISTS);
        }
        if (context.getScope() == LedgerDataPermissionHelper.DataScope.CITY
                && !context.getCityCode().equals(reportWithFile.getCityCode())) {
            throw exception(LEDGER_REPORT_NOT_EXISTS);
        }
    }

    /**
     * 填充单个 RespVO 的城市和区县名称
     */
    private void fillAreaNames(CarbonLedgerReportRespVO respVO) {
        if (respVO == null) {
            return;
        }
        fillAreaNames(Collections.singletonList(respVO));
    }

    /**
     * 批量填充 RespVO 列表的城市和区县名称
     */
    private void fillAreaNames(List<CarbonLedgerReportRespVO> list) {
        if (list == null || list.isEmpty()) {
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

    @VisibleForTesting
    CarbonLedgerReportDO validateLedgerReportExists(Long id) {
        if (id == null) {
            return null;
        }
        CarbonLedgerReportDO report = ledgerReportMapper.selectById(id);
        if (report == null) {
            throw exception(LEDGER_REPORT_NOT_EXISTS);
        }
        return report;
    }

    private void validateDataPermission(CarbonLedgerReportDO report) {
        // 通过关联的台账文件验证数据权限
        if (report.getLedgerFileId() == null) {
            return;
        }
        CarbonLedgerFileDO ledgerFile = ledgerFileMapper.selectById(report.getLedgerFileId());
        if (ledgerFile != null) {
            LedgerDataPermissionHelper.UserAreaContext context = LedgerDataPermissionHelper.getCurrentUserAreaContext();
            if (context.getScope() == LedgerDataPermissionHelper.DataScope.DISTRICT
                    && !context.getDistrictCode().equals(ledgerFile.getDistrictCode())) {
                throw exception(LEDGER_REPORT_NOT_EXISTS);
            }
            if (context.getScope() == LedgerDataPermissionHelper.DataScope.CITY
                    && !context.getCityCode().equals(ledgerFile.getCityCode())) {
                throw exception(LEDGER_REPORT_NOT_EXISTS);
            }
        }
    }
}
