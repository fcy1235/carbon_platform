package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 文档管理参数说明 Response VO")
@Data
public class CarbonDocumentParamRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "参数名称", example = "AD")
    private String paramName;

    @Schema(description = "参数说明", example = "活动数据")
    private String paramDescription;
}
