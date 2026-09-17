package cn.iocoder.power.module.carbon.controller.admin.ledger.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 台账文件审核状态修改 Request VO")
@Data
public class CarbonLedgerFileAuditReqVO {

    @Schema(description = "台账文件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "台账文件ID不能为空")
    private Long id;

    @Schema(description = "目标审核状态：1-待提交 2-审核中 3-已通过 4-已驳回", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotBlank(message = "审核状态不能为空")
    private String auditStatus;

    @Schema(description = "驳回原因（驳回时必填）", example = "数据不完整")
    private String rejectReason;
}
