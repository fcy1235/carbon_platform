package cn.iocoder.power.module.carbon.controller.admin.ledger.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 导入明细分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonLedgerImportDetailPageReqVO extends PageParam {

    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long taskId;

    @Schema(description = "导入状态：1-成功 2-失败", example = "2")
    private String importStatus;
}
