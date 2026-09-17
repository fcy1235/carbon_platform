package cn.iocoder.power.module.carbon.controller.admin.accounting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "管理后台 - 碳排放核算初始化数据 Request VO")
@Data
public class CarbonAccountingInitReqVO {

    @Schema(description = "用户信息编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1024, 1025]")
    @NotEmpty(message = "用户信息编号列表不能为空")
    private List<Long> carbonUserInfoIds;

    @Schema(description = "核算周期开始日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-11-15")
    @NotNull(message = "核算周期开始日期不能为空")
    private LocalDate accountingPeriodStart;

    @Schema(description = "核算周期结束日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-03-15")
    @NotNull(message = "核算周期结束日期不能为空")
    private LocalDate accountingPeriodEnd;
}
