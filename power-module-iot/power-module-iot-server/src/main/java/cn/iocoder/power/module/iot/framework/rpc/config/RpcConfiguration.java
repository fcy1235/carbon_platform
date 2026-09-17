package cn.iocoder.power.module.iot.framework.rpc.config;

import cn.iocoder.power.module.system.api.mail.MailSendApi;
import cn.iocoder.power.module.system.api.notify.NotifyMessageSendApi;
import cn.iocoder.power.module.system.api.sms.SmsSendApi;
import cn.iocoder.power.module.system.api.user.AdminUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "iotRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {
        AdminUserApi.class, SmsSendApi.class, MailSendApi.class, NotifyMessageSendApi.class
})
public class RpcConfiguration {
}
