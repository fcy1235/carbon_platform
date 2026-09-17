package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "管理后台 - 排放因子库创建/修改 Request VO")
@Data
public class CarbonFactorLibSaveReqVO implements Serializable {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "排放因子编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "排放因子1")
    private String factorCode;

    @Schema(description = "排放因子名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "排放因子1")
    private String factorName;

    @Schema(description = "关联文档", example = "标准文档A")
    private String relatedDoc;

    @Schema(description = "排放源", example = "排放源1")
    private String emissionSource;

    @Schema(description = "单位", requiredMode = Schema.RequiredMode.REQUIRED, example = "kgCO₂/kWh")
    @NotBlank(message = "单位不能为空")
    private String unit;

    @Schema(description = "因子值", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.5")
    private BigDecimal factorValue;

    @Schema(description = "关联气体列表")
    private List<CarbonFactorLibGasSaveReqVO> gasList;
}
