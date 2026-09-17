package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 基准线碳排放强度创建/修改 Request VO")
@Data
public class CarbonBaselineSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "行政区划-省", requiredMode = Schema.RequiredMode.REQUIRED, example = "11")
    private Long provinceCode;

    @Schema(description = "行政区划-市", requiredMode = Schema.RequiredMode.REQUIRED, example = "1111")
    private Long cityCode;

    @Schema(description = "行政区划-区", example = "111111")
    private Long districtCode;

    @Schema(description = "改造类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "气代煤")
    private String reformType;

    @Schema(description = "所属气候子区", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String climateZone;

    @Schema(description = "排放强度", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.45")
    private BigDecimal intensity;

    @Schema(description = "单位", requiredMode = Schema.RequiredMode.REQUIRED, example = "tCO₂/㎡")
    @NotBlank(message = "单位不能为空")
    private String unit;
}
