package cn.iocoder.power.module.carbon.service;

import cn.iocoder.power.framework.common.pojo.PageResult;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerFilePageReqVO;
import cn.iocoder.power.module.carbon.controller.admin.ledger.vo.CarbonLedgerFileRespVO;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CarbonLedgerFileService {

    Long createLedgerFile(String uploadType, MultipartFile file, String cityCode, String districtCode, String dataLevel) throws Exception;

    void updateLedgerFile(Long id, String uploadType, MultipartFile file) throws Exception;

    void deleteLedgerFile(Long id);

    CarbonLedgerFileRespVO getLedgerFile(Long id);

    PageResult<CarbonLedgerFileRespVO> getLedgerFilePage(CarbonLedgerFilePageReqVO pageReqVO);

    List<CarbonLedgerFileRespVO> getLedgerFileList(CarbonLedgerFilePageReqVO reqVO);

    /**
     * 查询可用于上报的台账文件（同类型、上传成功、未被引用）
     *
     * @param uploadType   上传类型
     * @param districtCode 所属区县编码
     * @return 可选台账文件列表
     */
    List<CarbonLedgerFileRespVO> getAvailableFilesForReport(String uploadType, String districtCode);

    /**
     * 修改台账文件审核状态
     * <p>
     * 状态流转：
     * - 待提交(1) -> 审核中(2)：提交审核
     * - 审核中(2) -> 待提交(1)：撤回
     * - 审核中(2) -> 已通过(3)：审核通过，自动创建导入任务
     * - 审核中(2) -> 已驳回(4)：审核驳回
     *
     * @param id          台账文件ID
     * @param auditStatus 目标审核状态
     * @param rejectReason 驳回原因（驳回时必填）
     */
    void updateAuditStatus(Long id, String auditStatus, String rejectReason);
}
