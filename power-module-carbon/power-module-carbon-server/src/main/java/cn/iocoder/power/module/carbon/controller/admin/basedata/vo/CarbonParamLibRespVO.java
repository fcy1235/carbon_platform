package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 参数库 Response VO")
@Data
public class CarbonParamLibRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "参数编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "PAR20260101001")
    private String paramCode;

    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "参数1")
    private String paramName;

    @Schema(description = "参数值", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.0")
    private BigDecimal paramValue;

    @Schema(description = "单位", requiredMode = Schema.RequiredMode.REQUIRED, example = "kg")
    private String unit;

    @Schema(description = "分类", requiredMode = Schema.RequiredMode.REQUIRED, example = "通用参数")
    private String category;

    @Schema(description = "备注", example = "")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;
}
