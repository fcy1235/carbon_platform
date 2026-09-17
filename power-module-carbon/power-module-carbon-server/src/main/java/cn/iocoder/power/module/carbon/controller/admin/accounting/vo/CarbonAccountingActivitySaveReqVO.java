package cn.iocoder.power.module.carbon.controller.admin.accounting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 碳排放核算活动数据 Request VO")
@Data
public class CarbonAccountingActivitySaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "活动名称", example = "电力消耗")
    private String activityNameCode;

    @Schema(description = "活动水平", example = "120")
    private BigDecimal activityLevel;

    @Schema(description = "排放因子", example = "0.5")
    private BigDecimal emissionFactor;

    @Schema(description = "核算编号",example = "1")
    private Long accountingId;
    @Schema(description = "碳设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long carbonDeviceId;
}
