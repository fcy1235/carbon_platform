package cn.iocoder.power.module.carbon.controller.admin.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 设备状态变更 Request VO")
@Data
public class CarbonDeviceStatusUpdateReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "编号不能为空")
    private Long id;

    @Schema(description = "设备状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "正常")
    @NotBlank(message = "设备状态不能为空")
    private String status;

    @Schema(description = "变更原因", example = "设备检修")
    private String reason;
}
