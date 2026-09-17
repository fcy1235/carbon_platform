package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 排放源创建/修改 Request VO")
@Data
public class CarbonEmissionSourceSaveReqVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "排放源编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "排放源1")
    @NotBlank(message = "排放源不能为空")
    private String sourceCode;

    @Schema(description = "排放源名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "排放源1")
    @NotBlank(message = "排放源不能为空")
    private String sourceName;

    @Schema(description = "排放范围", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String scope;
}
