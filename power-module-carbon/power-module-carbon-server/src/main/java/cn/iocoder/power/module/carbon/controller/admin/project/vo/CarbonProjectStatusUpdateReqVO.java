package cn.iocoder.power.module.carbon.controller.admin.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 项目状态变更 Request VO")
@Data
public class CarbonProjectStatusUpdateReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "编号不能为空")
    private Long id;

    @Schema(description = "项目状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    @NotNull(message = "项目状态不能为空")
    private String projectStatus;

    @Schema(description = "变更原因", example = "项目审批通过")
    private String reason;
}
