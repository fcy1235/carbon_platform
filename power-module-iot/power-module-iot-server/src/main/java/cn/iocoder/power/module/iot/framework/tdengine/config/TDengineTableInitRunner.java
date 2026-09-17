package cn.iocoder.power.module.iot.framework.tdengine.config;

import cn.iocoder.power.module.iot.service.device.message.IotDeviceMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * TDengine 表初始化的 Configuration
 *
 * @author alwayssuper
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TDengineTableInitRunner implements ApplicationRunner {

    private final IotDeviceMessageService deviceMessageService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            // 初始化设备消息表
            deviceMessageService.defineDeviceMessageStable();
            log.info("[run][TDengine 初始化设备消息表结构成功]");
        } catch (Exception ex) {
            // TDengine 未配置时跳过初始化，不影响系统启动
            log.warn("[run][TDengine 初始化设备消息表结构失败，跳过初始化。如需使用 TDengine 请配置 taos 数据源]", ex);
        }
    }

}
