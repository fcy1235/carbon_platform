package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 文档管理详细信息 Response VO")
@Data
public class CarbonDocumentDetailRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "名称", example = "排放因子")
    private String name;

    @Schema(description = "描述", example = "描述信息")
    private String description;

    @Schema(description = "计算公式", example = "E=AD×EF")
    private String formula;
}
