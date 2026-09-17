package cn.iocoder.power.module.iot.core.topic.auth;

import cn.iocoder.power.module.iot.core.enums.IotDeviceMessageMethodEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.NoArgsConstructor;

/**
 * IoT 子设备动态注册 Response DTO
 * <p>
 * 用于 {@link IotDeviceMessageMethodEnum#SUB_DEVICE_REGISTER} 响应的设备信息
 *
 * 
 * @see <a href="https://help.aliyun.com/zh/iot/user-guide/register-devices">阿里云 - 动态注册子设备</a>
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class IotSubDeviceRegisterRespDTO {

    /**
     * 子设备 ProductKey
     */
    private String productKey;

    /**
     * 子设备 DeviceName
     */
    private String deviceName;

    /**
     * 分配的 DeviceSecret
     */
    private String deviceSecret;

}
