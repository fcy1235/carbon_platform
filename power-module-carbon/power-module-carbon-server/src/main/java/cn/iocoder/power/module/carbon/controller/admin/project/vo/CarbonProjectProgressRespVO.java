package cn.iocoder.power.module.carbon.controller.admin.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 项目进度 Response VO")
@Data
public class CarbonProjectProgressRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "项目编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long projectId;

    @Schema(description = "阶段", requiredMode = Schema.RequiredMode.REQUIRED, example = "申报")
    private String stage;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "已完成")
    private String status;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

    @Schema(description = "更新人", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    private String updater;

    @Schema(description = "内容", example = "已提交申报")
    private String content;
}
