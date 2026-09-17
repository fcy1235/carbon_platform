package cn.iocoder.power.module.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;

/**
 * 项目的启动类 (exclude = {QuartzAutoConfiguration.class})
 * 
 */
@SpringBootApplication
public class IoTServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(IoTServerApplication.class, args);
        System.out.println("启动完成");
        
    }

}
