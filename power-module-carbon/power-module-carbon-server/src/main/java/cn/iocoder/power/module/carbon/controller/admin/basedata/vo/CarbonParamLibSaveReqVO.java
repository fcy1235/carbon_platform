package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 参数库创建/修改 Request VO")
@Data
public class CarbonParamLibSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "参数1")
    @NotBlank(message = "参数名称不能为空")
    private String paramName;

    @Schema(description = "参数值", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.0")
    private BigDecimal paramValue;

    @Schema(description = "单位", requiredMode = Schema.RequiredMode.REQUIRED, example = "kg")
    @NotBlank(message = "单位不能为空")
    private String unit;

    @Schema(description = "分类", example = "通用参数")
    private String category;

    @Schema(description = "备注", example = "")
    private String remark;
}
