package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;

import cn.iocoder.power.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Schema(description = "管理后台 - 文档管理分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class CarbonDocumentPageReqVO extends PageParam {

    @Schema(description = "文档标题", example = "文档标题1")
    private String docTitle;

    @Schema(description = "文档名称", example = "文档1.pdf")
    private String docName;

    @Schema(description = "上传人", example = "文档1.pdf")
    private String uploader;  // 上传人

    @Schema(description = "选中的ID列表，用于选中导出", example = "1,2,3")
    private List<Long> ids;
}
