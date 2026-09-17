package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 气体信息创建/修改 Request VO")
@Data
public class CarbonGasInfoSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "气体编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "G001")

    private String gasCode;

    @Schema(description = "气体名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "二氧化碳(CO₂)")
    private String gasName;

    @Schema(description = "GWP值", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private BigDecimal gwp;

    @Schema(description = "分类", requiredMode = Schema.RequiredMode.REQUIRED, example = "Kyoto温室气体")
    private String category;
}
