package cn.iocoder.power.framework.apilog.config;

import cn.iocoder.power.framework.common.biz.infra.logger.ApiAccessLogCommonApi;
import cn.iocoder.power.framework.common.biz.infra.logger.ApiErrorLogCommonApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * API 日志使用到 Feign 的配置项
 *
 * 
 */
@AutoConfiguration
@EnableFeignClients(clients = {ApiAccessLogCommonApi.class, ApiErrorLogCommonApi.class}) // 主要是引入相关的 API 服务
public class ApiLogRpcAutoConfiguration {
}
