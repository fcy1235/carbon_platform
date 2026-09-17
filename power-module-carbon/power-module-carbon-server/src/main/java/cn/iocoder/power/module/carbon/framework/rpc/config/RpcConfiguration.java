package cn.iocoder.power.module.carbon.framework.rpc.config;


import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "carbonRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(basePackages = { "cn.iocoder.power.module.system.api"} )
public class RpcConfiguration {
}
