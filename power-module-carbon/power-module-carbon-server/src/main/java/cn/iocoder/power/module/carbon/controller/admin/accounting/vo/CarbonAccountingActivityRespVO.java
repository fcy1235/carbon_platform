package cn.iocoder.power.module.carbon.controller.admin.accounting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 碳排放核算活动数据 Response VO")
@Data
public class CarbonAccountingActivityRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "核算编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long accountingId;

    @Schema(description = "活动名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "电力消耗")
    private String activityNameCode;

    @Schema(description = "活动水平", requiredMode = Schema.RequiredMode.REQUIRED, example = "120")
    private BigDecimal activityLevel;

    @Schema(description = "排放因子", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.5")
    private BigDecimal emissionFactor;

    @Schema(description = "排放量", requiredMode = Schema.RequiredMode.REQUIRED, example = "60")
    private BigDecimal emission;

    @Schema(description = "碳设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long carbonDeviceId;

    @Schema(description = "碳设备名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "碳设备名称")
    private String carbonDeviceName;
}
