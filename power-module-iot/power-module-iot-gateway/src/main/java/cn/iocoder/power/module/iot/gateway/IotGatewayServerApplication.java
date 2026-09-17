package cn.iocoder.power.module.iot.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IotGatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotGatewayServerApplication.class, args);
        System.out.println("启动完成");
    }

}