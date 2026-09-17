package cn.iocoder.power.module.carbon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.framework.common.util.validation.ValidationUtils;
import cn.iocoder.power.framework.excel.core.util.ExcelUtils;
import cn.iocoder.power.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.power.framework.minio.config.MinioProperties;
import cn.iocoder.power.framework.minio.core.IMinioService;
import cn.idev.excel.EasyExcel;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerChangeImportDataVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerFilePageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerFileRespVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerImportDataVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerRevokeImportDataVO;
import cn.iocoder.power.module.carbon.convert.CarbonLedgerFileConvert;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerAuditLogDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerFileDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerImportTaskDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonLedgerReportDO;
import cn.iocoder.power.module.carbon.dal.dataobject.CarbonUserInfoDO;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonLedgerAuditLogMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonLedgerFileMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonLedgerImportTaskMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonUserInfoMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonLedgerReportFileMapper;
import cn.iocoder.power.module.carbon.dal.mysql.CarbonLedgerReportMapper;
import cn.iocoder.power.module.carbon.enums.LedgerFileAuditStatusEnum;
import cn.iocoder.power.module.carbon.enums.LedgerFileStatusEnum;
import cn.iocoder.power.module.carbon.enums.LedgerImportStatusEnum;
import cn.iocoder.power.module.carbon.service.CarbonLedgerFileService;
import cn.iocoder.power.module.carbon.util.LedgerDataPermissionHelper;
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
public class CarbonLedgerFileServiceImpl implements CarbonLedgerFileService {

    @Resource
    private CarbonLedgerFileMapper ledgerFileMapper;

    @Resource
    private CarbonLedgerReportFileMapper ledgerReportFileMapper;


    @Resource
    private CarbonLedgerAuditLogMapper ledgerAuditLogMapper;

    @Resource
    private CarbonLedgerImportTaskMapper importTaskMapper;

    @Resource
    private IMinioService minioService;

    @Resource
    private MinioProperties minioProperties;

    @Resource
    private AreaApi areaApi;

    @Resource
    private CarbonUserInfoMapper userInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createLedgerFile(String uploadType, MultipartFile file, String cityCode, String districtCode, String dataLevel) throws Exception {
        validateFileExt(file);

        // 上传文件到 MinIO
        String fileName = file.getOriginalFilename();
        String path = minioService.uploadFile(file, minioProperties.getBucketName(), fileName);

        // 解析并验证 Excel 数据（参考执行导入的验证逻辑）
        ExcelValidateResult validateResult = validateExcel(file, uploadType);

        CarbonLedgerFileDO ledgerFile = new CarbonLedgerFileDO();
        ledgerFile.setUploadType(uploadType);
        ledgerFile.setCityCode(cityCode);
        ledgerFile.setDistrictCode(districtCode);
        ledgerFile.setDataLevel(dataLevel);
        ledgerFile.setFileName(fileName);
        ledgerFile.setFileUrl(minioService.getFileDownloadUrl(minioProperties.getBucketName(),path));
        ledgerFile.setDataCount(validateResult.getDataCount());
        ledgerFile.setUploadStatus(validateResult.isSuccess() ? LedgerFileStatusEnum.SUCCESS.getType() : LedgerFileStatusEnum.FAIL.getType());
        // 验证失败时记录错误信息
        if (StrUtil.isNotBlank(validateResult.getErrorMsg())) {
            ledgerFile.setRemark(validateResult.getErrorMsg());
        }
        // 根据数据层级设置审核状态：市级直接通过，县级待提交
        boolean isCityLevel = "2".equals(dataLevel);
        if (isCityLevel) {
            ledgerFile.setAuditStatus(LedgerFileAuditStatusEnum.APPROVED.getType());
        } else {
            ledgerFile.setAuditStatus(LedgerFileAuditStatusEnum.WAIT_AUDIT.getType());
        }
        ledgerFile.setUploadTime(LocalDateTime.now());
        ledgerFile.setUploader(SecurityFrameworkUtils.getLoginUserNickname());
        ledgerFileMapper.insert(ledgerFile);

        // 市级直接审核通过，自动创建导入任务
        if (isCityLevel && LedgerFileStatusEnum.isSuccess(ledgerFile.getUploadStatus())) {
            createImportTaskFromFile(ledgerFile);
        }

        return ledgerFile.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLedgerFile(Long id, String uploadType, MultipartFile file) throws Exception {
        CarbonLedgerFileDO existing = validateLedgerFileExists(id);
        validateDataPermission(existing);
        // 状态为0(待审定)、1(待上报)、4(已驳回) 都可以修改
        if (!LedgerFileAuditStatusEnum.isWait(existing.getAuditStatus()) &&
                !LedgerFileAuditStatusEnum.isDraft(existing.getAuditStatus()) &&
                !LedgerFileAuditStatusEnum.isRejected(existing.getAuditStatus())){
            log.info("文件{}当前审核状态为{}，不允许修改", id, existing.getAuditStatus());
            throw exception(LEDGER_FILE_NOT_DRAFT);
        }

        if (isReferenced(id)) {
            throw exception(LEDGER_FILE_REFERENCED);
        }

        CarbonLedgerFileDO updateObj = new CarbonLedgerFileDO();
        updateObj.setId(id);
        updateObj.setUploadType(uploadType);

        if (file != null && !file.isEmpty()) {
            validateFileExt(file);
            String fileName = file.getOriginalFilename();
            String path = minioService.uploadFile(file, minioProperties.getBucketName(), fileName);

            // 解析并验证 Excel 数据（参考执行导入的验证逻辑）
            ExcelValidateResult validateResult = validateExcel(file, uploadType);
            updateObj.setFileName(fileName);
            updateObj.setFileUrl(minioService.getFileDownloadUrl(minioProperties.getBucketName(), path));
            updateObj.setDataCount(validateResult.getDataCount());
            updateObj.setUploadStatus(validateResult.isSuccess() ? LedgerFileStatusEnum.SUCCESS.getType() : LedgerFileStatusEnum.FAIL.getType());
            // 验证失败时记录错误信息
            if (StrUtil.isNotBlank(validateResult.getErrorMsg())) {
                updateObj.setRemark(validateResult.getErrorMsg());
            }
        }
        updateObj.setAuditStatus(LedgerFileAuditStatusEnum.WAIT_AUDIT.getType());
        ledgerFileMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteLedgerFile(Long id) {
        CarbonLedgerFileDO file = validateLedgerFileExists(id);
        validateDataPermission(file);
        if (LedgerFileAuditStatusEnum.isDraft(file.getAuditStatus()) && !LedgerFileAuditStatusEnum.isRejected(file.getAuditStatus())) {
            throw exception(LEDGER_FILE_NOT_DRAFT);
        }
        if (isReferenced(id)) {
            throw exception(LEDGER_FILE_REFERENCED);
        }
        ledgerFileMapper.deleteById(id);
    }

    @Override
    public CarbonLedgerFileRespVO getLedgerFile(Long id) {
        CarbonLedgerFileDO file = validateLedgerFileExists(id);
        validateDataPermission(file);
        CarbonLedgerFileRespVO respVO = CarbonLedgerFileConvert.INSTANCE.convert(file);
        fillAreaName(respVO);
        return respVO;
    }

    @Override
    public PageResult<CarbonLedgerFileRespVO> getLedgerFilePage(CarbonLedgerFilePageReqVO pageReqVO) {

        log.info("地址信息:{}",SecurityFrameworkUtils.getLoginUserAddress());
        PageResult<CarbonLedgerFileRespVO> result = CarbonLedgerFileConvert.INSTANCE.convertPage(ledgerFileMapper.selectPage(pageReqVO));
        fillAreaNames(result.getList());
        return result;
    }

    @Override
    public List<CarbonLedgerFileRespVO> getLedgerFileList(CarbonLedgerFilePageReqVO reqVO) {
        List<CarbonLedgerFileRespVO> list = CarbonLedgerFileConvert.INSTANCE.convertList(ledgerFileMapper.selectList(reqVO));
        fillAreaNames(list);
        return list;
    }

    @Override
    public List<CarbonLedgerFileRespVO> getAvailableFilesForReport(String uploadType, String districtCode) {
        CarbonLedgerFilePageReqVO reqVO = new CarbonLedgerFilePageReqVO();
        reqVO.setUploadType(uploadType);
        reqVO.setDistrictCode(districtCode);
        reqVO.setUploadStatus(LedgerFileStatusEnum.SUCCESS.getType());
        List<CarbonLedgerFileDO> list = ledgerFileMapper.selectList(reqVO);
        List<CarbonLedgerFileRespVO> result = CarbonLedgerFileConvert.INSTANCE.convertList(list.stream()
                .filter(file -> !isReferenced(file.getId()))
                .toList());
        fillAreaNames(result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAuditStatus(Long id, String auditStatus, String rejectReason) {
        CarbonLedgerFileDO existing = validateLedgerFileExists(id);
        validateDataPermission(existing);

        String currentStatus = existing.getAuditStatus();

        // 验证状态流转合法性
        validateStatusTransition(currentStatus, auditStatus, rejectReason);

        CarbonLedgerFileDO updateObj = new CarbonLedgerFileDO();
        updateObj.setId(id);
        updateObj.setAuditStatus(auditStatus);

        String operator = SecurityFrameworkUtils.getLoginUserNickname();

        if(LedgerFileAuditStatusEnum.AUDITING.getType().equals(auditStatus)){
            updateObj.setSubmitTime(LocalDateTime.now());
        }
        // 设置审核人和审核时间
        if(LedgerFileAuditStatusEnum.APPROVED.getType().equals(auditStatus)){
            updateObj.setReviewer(operator);
            updateObj.setReviewTime(LocalDateTime.now());
        }
        // 驳回时记录驳回原因
        if (LedgerFileAuditStatusEnum.REJECTED.getType().equals(auditStatus)) {
            updateObj.setRejectReason(rejectReason);
        }

        ledgerFileMapper.updateById(updateObj);

        // 写入审核日志
        createAuditLog(id, auditStatus,currentStatus, rejectReason, operator);

        // 审核通过后自动创建导入任务
        if (LedgerFileAuditStatusEnum.isApproved(auditStatus)) {
            createImportTaskFromFile(existing);
        }
    }

    /**
     * 写入审核日志
     */
    private void createAuditLog(Long fileId, String status,String currentStatus, String rejectReason, String operator) {
        String operatorName = StrUtil.isBlank(operator) ? SecurityFrameworkUtils.getLoginUserNickname() : operator;

        log.info("文件{}审核日志：{} -> {}，原因：{}，审核人：{}", fileId, currentStatus, status, rejectReason, operatorName);
        // 根据状态生成描述
        String reason;
        if (LedgerFileAuditStatusEnum.AUDITING.getType().equals(status)) {
            reason = operatorName + "已提交";
        } else if (LedgerFileAuditStatusEnum.APPROVED.getType().equals(status)) {
            reason = operatorName + "审核通过";
        } else if (LedgerFileAuditStatusEnum.REJECTED.getType().equals(status)) {
            reason = StrUtil.isNotBlank(rejectReason) ? operatorName + "审核驳回：" + rejectReason : operatorName + "审核驳回";
        } else if (LedgerFileAuditStatusEnum.DRAFT.getType().equals(status) && LedgerFileAuditStatusEnum.AUDITING.getType().equals(currentStatus)) {
            reason = operatorName + "撤回提交";
        }else if(LedgerFileAuditStatusEnum.DRAFT.getType().equals(status) && LedgerFileAuditStatusEnum.WAIT_AUDIT.getType().equals(currentStatus)){
            reason = operatorName + "提交审定";
        }else {
            reason = rejectReason;
        }

        // 查询关联的上报记录


        CarbonLedgerAuditLogDO auditLog = new CarbonLedgerAuditLogDO();

        auditLog.setReportId(fileId);
        auditLog.setStatus(status);
        auditLog.setReason(reason);
        auditLog.setOperator(operatorName);
        auditLog.setChangeTime(LocalDateTime.now());
        ledgerAuditLogMapper.insert(auditLog);
    }

    /**
     * 验证状态流转合法性
     * @targetStatus 改变状态
     * @currentStatus 数据库状态
     */
    private void validateStatusTransition(String currentStatus, String targetStatus, String rejectReason) {

        // 审核中 -> 待提交 0-1 4-1 2-1
        if (LedgerFileAuditStatusEnum.DRAFT.getType().equals(targetStatus)) {
            if (LedgerFileAuditStatusEnum.isWait(currentStatus) || LedgerFileAuditStatusEnum.isRejected(currentStatus) || LedgerFileAuditStatusEnum.isAuditing(currentStatus)) {
                return;
            }
            throw exception(LEDGER_FILE_CANNOT_WITHDRAW);
        }

        // 待提交/已驳回 -> 审核中（提交审核）
        if (LedgerFileAuditStatusEnum.AUDITING.getType().equals(targetStatus)) {
            if (LedgerFileAuditStatusEnum.isDraft(currentStatus)) {
                return;
            }
            throw exception(LEDGER_FILE_NOT_DRAFT);
        }


        // 审核中 -> 已通过/已驳回（审核）
        if (LedgerFileAuditStatusEnum.APPROVED.getType().equals(targetStatus) || LedgerFileAuditStatusEnum.REJECTED.getType().equals(targetStatus)) {
            if (!LedgerFileAuditStatusEnum.isAuditing(currentStatus)) {
                throw exception(LEDGER_FILE_CANNOT_REVIEW);
            }
            // 驳回时必须填写驳回原因
            if (LedgerFileAuditStatusEnum.REJECTED.getType().equals(targetStatus)
                    && StrUtil.isBlank(rejectReason)) {
                throw exception(LEDGER_FILE_REJECT_REASON_REQUIRED);
            }
            return;
        }

        // 其他状态流转不允许
        throw exception(LEDGER_FILE_NOT_DRAFT);
    }

    /**
     * 根据台账文件创建导入任务
     */
    private void createImportTaskFromFile(CarbonLedgerFileDO ledgerFile) {
        CarbonLedgerImportTaskDO importTask = new CarbonLedgerImportTaskDO();
        importTask.setLedgerFileId(ledgerFile.getId());
        importTask.setImportStatus(LedgerImportStatusEnum.PENDING_CHECK.getType());
        importTask.setUploader(ledgerFile.getUploader());
        importTask.setUploadTime(LocalDateTime.now());
        importTask.setDeptId(ledgerFile.getDeptId());
        importTaskMapper.insert(importTask);
        log.info("台账文件审核通过，自动创建导入任务，台账文件ID: {}, 导入任务ID: {}", ledgerFile.getId(), importTask.getId());
    }

    private void validateFileExt(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (StrUtil.isBlank(fileName)) {
            throw exception(LEDGER_FILE_FORMAT_ERROR);
        }
        String lowerName = fileName.toLowerCase();
        if (!lowerName.endsWith(".xls") && !lowerName.endsWith(".xlsx")) {
            throw exception(LEDGER_FILE_FORMAT_ERROR);
        }
    }

    /**
     * 解析并验证 Excel 数据（参考执行导入的验证逻辑）
     *
     * @param file       上传的 Excel 文件
     * @param uploadType 上传类型：1-新增户 2-变更户 3-撤销户 4-改造类型变更
     * @return 验证结果
     */
    private ExcelValidateResult validateExcel(MultipartFile file, String uploadType) {
        try {
            List<String> errors = new ArrayList<>();
            int dataCount;
            if ("2".equals(uploadType)) {
                // 变更户使用专用模板（多行表头），从第3行开始读取数据
                List<CarbonLedgerChangeImportDataVO> list = EasyExcel.read(file.getInputStream(), CarbonLedgerChangeImportDataVO.class, null)
                        .sheet()
                        .headRowNumber(3)
                        .doReadSync();
                dataCount = list.size();
                for (int i = 0; i < list.size(); i++) {
                    String errorMsg = validateChangeImportData(list.get(i));
                    if (StrUtil.isNotBlank(errorMsg)) {
                        errors.add("第" + (i + 4) + "行：" + errorMsg);
                    }
                }
            } else if ("3".equals(uploadType)) {
                // 撤销户使用专用模板（多行表头），从第3行开始读取数据
                List<CarbonLedgerRevokeImportDataVO> list = EasyExcel.read(file.getInputStream(), CarbonLedgerRevokeImportDataVO.class, null)
                        .sheet()
                        .headRowNumber(3)
                        .doReadSync();
                dataCount = list.size();
                for (int i = 0; i < list.size(); i++) {
                    String errorMsg = validateRevokeImportData(list.get(i));
                    if (StrUtil.isNotBlank(errorMsg)) {
                        errors.add("第" + (i + 4) + "行：" + errorMsg);
                    }
                }
            } else {
                // 新增户/改造类型变更
                List<CarbonLedgerImportDataVO> list = ExcelUtils.read(file, CarbonLedgerImportDataVO.class);
                dataCount = list.size();
                for (int i = 0; i < list.size(); i++) {
                    String errorMsg = validateImportData(list.get(i), uploadType);
                    if (StrUtil.isNotBlank(errorMsg)) {
                        errors.add("第" + (i + 1) + "行：" + errorMsg);
                    }
                }
            }

            boolean success = errors.isEmpty();
            String errorMsg = null;
            if (!success) {
                errorMsg = errors.size() <= 10
                        ? String.join("；", errors)
                        : String.join("；", errors.subList(0, 10)) + "；等共" + errors.size() + "条错误";
                if (errorMsg.length() > 500) {
                    errorMsg = errorMsg.substring(0, 500);
                }
            }
            return new ExcelValidateResult(dataCount, success, errorMsg);
        } catch (Exception e) {
            log.warn("台账文件解析失败: {}", file.getOriginalFilename(), e);
            return new ExcelValidateResult(0, false, "文件解析失败：" + e.getMessage());
        }
    }

    /**
     * 校验新增户/改造类型变更导入数据（参考执行导入的验证逻辑）
     */
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
        if(!ValidationUtils.isMobile(data.getPhone())){
            errors.add("手机号格式不正确");
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
            if (StrUtil.isNotBlank(data.getAddress())) {
                CarbonUserInfoDO existingByIdCardAndElectricity = userInfoMapper.selectByIdCardAndAddress(data.getIdCard(), data.getAddress());
                if (existingByIdCardAndElectricity != null) {
                    return "该地址已存在用户: 身份证号=" + data.getIdCard() + ", 地址=" + data.getAddress()+", 用户名=" + existingByIdCardAndElectricity.getUsername();
                }
            }
        }

        // 改造类型变更使用身份证号+气号/电表号匹配
        if ("4".equals(uploadType) || "2".equals(uploadType) || "3".equals(uploadType)) {
            CarbonUserInfoDO existingUser = userInfoMapper.selectByIdCardAndAddress(data.getIdCard(), data.getAddress());
            if (existingUser == null) {
                return "未找到匹配的用户信息（身份证号: " + data.getIdCard() + "）,地址：" + data.getAddress();
            }
        }
        return null;
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
     * 校验变更户导入数据（参考执行导入的验证逻辑）
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
        CarbonUserInfoDO existingUser = userInfoMapper.selectByIdCardAndAddress(data.getIdCard(), data.getAddress());
        if (existingUser == null) {
            return "未找到匹配的用户信息（身份证号: " + data.getIdCard() + ", 地址: " + data.getAddress() + "）";
        }
        return null;
    }

    /**
     * 校验撤销户导入数据（参考执行导入的验证逻辑）
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
        CarbonUserInfoDO existingUser = userInfoMapper.selectByIdCardAndAddress(data.getIdCard(), data.getAddress());
        if (existingUser == null) {
            return "未找到匹配的用户信息（身份证号: " + data.getIdCard() + ", 详细地址: " + data.getAddress() + "）";
        }
        return null;
    }

    private boolean isReferenced(Long fileId) {
        Long count = ledgerReportFileMapper.countByFileId(fileId);
        return count != null && count > 0;
    }

    @VisibleForTesting
    CarbonLedgerFileDO validateLedgerFileExists(Long id) {
        if (id == null) {
            return null;
        }
        CarbonLedgerFileDO ledgerFile = ledgerFileMapper.selectById(id);
        if (ledgerFile == null) {
            throw exception(LEDGER_FILE_NOT_EXISTS);
        }
        return ledgerFile;
    }

    private void validateDataPermission(CarbonLedgerFileDO file) {
        LedgerDataPermissionHelper.UserAreaContext context = LedgerDataPermissionHelper.getCurrentUserAreaContext();
        if (context.getScope() == LedgerDataPermissionHelper.DataScope.DISTRICT
                && !context.getDistrictCode().equals(file.getDistrictCode())) {
            throw exception(LEDGER_FILE_NOT_EXISTS);
        }
        if (context.getScope() == LedgerDataPermissionHelper.DataScope.CITY
                && !context.getCityCode().equals(file.getCityCode())) {
            throw exception(LEDGER_FILE_NOT_EXISTS);
        }
    }

    /**
     * 填充单个台账文件的城市和区县名称
     */
    private void fillAreaName(CarbonLedgerFileRespVO item) {
        if (item == null) {
            return;
        }
        fillAreaNames(Collections.singletonList(item));
    }

    /**
     * 批量填充台账文件的城市和区县名称
     */
    private void fillAreaNames(List<CarbonLedgerFileRespVO> list) {
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

    /**
     * Excel 验证结果
     */
    private static class ExcelValidateResult {
        private final Integer dataCount;
        private final boolean success;
        private final String errorMsg;

        ExcelValidateResult(Integer dataCount, boolean success, String errorMsg) {
            this.dataCount = dataCount;
            this.success = success;
            this.errorMsg = errorMsg;
        }

        Integer getDataCount() {
            return dataCount;
        }

        boolean isSuccess() {
            return success;
        }

        String getErrorMsg() {
            return errorMsg;
        }
    }
}
