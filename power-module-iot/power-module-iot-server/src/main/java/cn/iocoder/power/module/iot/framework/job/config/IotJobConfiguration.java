package cn.iocoder.power.module.iot.framework.job.config;

import cn.iocoder.power.module.iot.framework.job.core.IotSchedulerManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * IoT 模块的 Job 自动配置类
 *
 * 
 */
@Configuration
public class IotJobConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public IotSchedulerManager iotSchedulerManager(ApplicationContext applicationContext) {
        return new IotSchedulerManager(applicationContext);
    }

}
