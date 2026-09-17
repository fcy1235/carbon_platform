package cn.iocoder.power.module.carbon.controller.admin.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 项目文档 Response VO")
@Data
public class CarbonProjectDocRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "项目编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long projectId;

    @Schema(description = "文档名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "方案.pdf")
    private String docName;

    @Schema(description = "文档描述", example = "")
    private String docDesc;

    @Schema(description = "文档大小", example = "2MB")
    private String docSize;

    @Schema(description = "文档路径", example = "/carbon/project/方案.pdf")
    private String docPath;

    @Schema(description = "上传人", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    private String uploader;

    @Schema(description = "上传时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime uploadTime;
}
