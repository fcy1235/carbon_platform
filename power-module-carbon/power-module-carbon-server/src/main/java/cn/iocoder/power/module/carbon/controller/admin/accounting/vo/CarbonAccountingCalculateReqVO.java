package cn.iocoder.power.module.carbon.controller.admin.accounting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 碳排放核算试算 Request VO")
@Data
public class CarbonAccountingCalculateReqVO {

    @Schema(description = "供暖面积", requiredMode = Schema.RequiredMode.REQUIRED, example = "90")
    private BigDecimal heatingArea;

    @Schema(description = "基准线排放强度", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.45")
    private BigDecimal baselineIntensity;

    @Schema(description = "活动数据列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    private List<CarbonAccountingActivitySaveReqVO> activities;
}
