package cn.iocoder.power.module.iot.core.topic.service;

import cn.iocoder.power.module.iot.core.enums.IotDeviceMessageMethodEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * IoT 设备服务调用 Request DTO
 * <p>
 * 用于 {@link IotDeviceMessageMethodEnum#SERVICE_INVOKE} 下行消息的 params 参数
 *
 * 
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class IotDeviceServiceInvokeReqDTO {

    /**
     * 服务标识符
     */
    private String identifier;

    /**
     * 服务输入参数
     */
    private Map<String, Object> inputParams;

}
