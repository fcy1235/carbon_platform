package cn.iocoder.power.module.iot.controller.admin.device.vo.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Schema(description = "管理后台 - IoT 设备消息对 Response VO")
@Data
@Accessors(chain = true)
public class IotDeviceMessageRespPairVO {

    @Schema(description = "请求消息", requiredMode = Schema.RequiredMode.REQUIRED)
    private IotDeviceMessageRespVO request;

    @Schema(description = "响应消息")
    private IotDeviceMessageRespVO reply; // 通过 requestId 配对

}
