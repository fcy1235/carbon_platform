package cn.iocoder.power.module.carbon.controller.admin.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 项目进度记录创建 Request VO")
@Data
public class CarbonProjectProgressSaveReqVO {

    @Schema(description = "项目编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "项目编号不能为空")
    private Long projectId;

    @Schema(description = "进度备注内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "验收通过，准备结项")
    @NotBlank(message = "进度备注内容不能为空")
    private String content;

}
