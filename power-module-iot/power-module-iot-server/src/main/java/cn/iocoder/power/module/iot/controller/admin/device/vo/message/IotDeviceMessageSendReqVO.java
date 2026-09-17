package cn.iocoder.power.module.iot.controller.admin.device.vo.message;

import cn.iocoder.power.framework.common.validation.InEnum;
import cn.iocoder.power.module.iot.core.enums.IotDeviceMessageMethodEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Schema(description = "管理后台 - IoT 设备消息发送 Request VO") // 属性上报、事件上报、状态变更等
@Data
@Accessors(chain = true)
public class IotDeviceMessageSendReqVO {

    @Schema(description = "请求方法", requiredMode = Schema.RequiredMode.REQUIRED, example = "report")
    @NotEmpty(message = "请求方法不能为空")
    @InEnum(IotDeviceMessageMethodEnum.class)
    private String method;

    @Schema(description = "请求参数")
    private Object params; // 例如说：属性上报的 properties、事件上报的 params

    @Schema(description = "设备编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "177")
    @NotNull(message = "设备编号不能为空")
    private Long deviceId;

}
