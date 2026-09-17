package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "管理后台 - 排放因子库 Response VO")
@Data
public class CarbonFactorLibRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "排放因子编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "EF20260101001")
    private String factorCode;

    @Schema(description = "排放因子名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "排放因子1")
    private String factorName;

    @Schema(description = "关联文档", example = "标准文档A")
    private String relatedDoc;

    @Schema(description = "排放源", example = "xx1")
    private String emissionSource;

    @Schema(description = "排放源名称", example = "排放源1")
    private String emissionSourceName;

    @Schema(description = "单位", requiredMode = Schema.RequiredMode.REQUIRED, example = "kgCO₂/kWh")
    private String unit;

    @Schema(description = "因子值", requiredMode = Schema.RequiredMode.REQUIRED, example = "0.5")
    private BigDecimal factorValue;

    @Schema(description = "关联气体列表")
    private List<CarbonFactorLibGasRespVO> gasList;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}
