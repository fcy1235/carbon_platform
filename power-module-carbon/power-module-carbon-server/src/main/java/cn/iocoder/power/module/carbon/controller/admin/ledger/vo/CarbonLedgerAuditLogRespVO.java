package cn.iocoder.power.module.carbon.controller.admin.ledger.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 台账审核轨迹 Response VO")
@Data
public class CarbonLedgerAuditLogRespVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "上报ID", example = "1024")
    private Long reportId;

    @Schema(description = "状态", example = "2")
    private String status;

    @Schema(description = "状态变更原因", example = "提交审核")
    private String reason;

    @Schema(description = "操作人", example = "admin")
    private String operator;

    @Schema(description = "状态变更时间")
    private LocalDateTime changeTime;
}
