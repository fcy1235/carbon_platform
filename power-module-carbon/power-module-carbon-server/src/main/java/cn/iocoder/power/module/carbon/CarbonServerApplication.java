package cn.iocoder.power.module.carbon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"cn.iocoder.power.module.carbon", "cn.iocoder.power.framework"})
@EnableAsync
public class CarbonServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarbonServerApplication.class, args);
    }
}
