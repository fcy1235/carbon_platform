package cn.iocoder.power.module.carbon.controller.admin.ledger.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List; // 保留，因为 auditLogs 仍然使用 List

@Schema(description = "管理后台 - 台账上报 Response VO")
@Data
public class CarbonLedgerReportRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "上报说明", example = "2026年第一季度新增户台账上报")
    private String reportDesc;

    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    @Schema(description = "盖章报告PDF地址", example = "/uploads/report/stamped.pdf")
    private String stampedReportUrl;

    @Schema(description = "盖章报告PDF文件名", example = "盖章报告.pdf")
    private String stampedReportName;

    @Schema(description = "备注", example = "")
    private String remark;

    @Schema(description = "审核轨迹列表")
    private List<CarbonLedgerAuditLogRespVO> auditLogs;

    @Schema(description = "关联台账文件ID")
    private Long ledgerFileId;

    @Schema(description = "台账文件名称")
    private String ledgerFileName;

    @Schema(description = "台账文件地址")
    private String ledgerFileUrl;

    @Schema(description = "所属市编码")
    private String cityCode;

    @Schema(description = "所属市名称")
    private String cityName;

    @Schema(description = "所属区县编码")
    private String districtCode;

    @Schema(description = "所属区县名称")
    private String districtName;

    @Schema(description = "上传类型：1-新增户 2-变更户 3-撤销户 4-改造类型变更")
    private String uploadType;

    @Schema(description = "数据量")
    private Integer dataCount;

    @Schema(description = "审核状态：1-待提交 2-审核中 3-已通过 4-已驳回")
    private String auditStatus;

    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;

    @Schema(description = "上传人")
    private String uploader;

    @Schema(description = "审核人（来自台账文件表）")
    private String reviewer;

    @Schema(description = "审核时间（来自台账文件表）")
    private LocalDateTime reviewTime;

    @Schema(description = "驳回原因（来自台账文件表）")
    private String rejectReason;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}
