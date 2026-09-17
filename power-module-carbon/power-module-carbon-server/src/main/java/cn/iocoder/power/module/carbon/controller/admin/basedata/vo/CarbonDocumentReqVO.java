package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "管理后台 - 文档管理分页 Request VO")
@Data
public class CarbonDocumentReqVO  implements Serializable {

    @Schema(description = "文档标题", example = "文档标题1")
    private String docTitle;

    @Schema(description = "文档名称", example = "文档1.pdf")
    private String docName;
}
