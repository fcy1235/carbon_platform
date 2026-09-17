package cn.iocoder.power.gateway.filter.cors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 跨域 Filter
 *
 * 统一在 Gateway 层处理跨域，下游微服务通过 power.web.cors.enable=false 禁用自身的 CORS 配置
 */
@Component
public class CorsFilter implements WebFilter {

    private static final String ALL = "*";
    private static final String MAX_AGE = "86400";

    private static final List<HttpMethod> ALLOWED_METHODS = Arrays.asList(
            HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
            HttpMethod.DELETE, HttpMethod.OPTIONS, HttpMethod.PATCH
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 非跨域请求，直接放行
        ServerHttpRequest request = exchange.getRequest();
        if (!CorsUtils.isCorsRequest(request)) {
            return chain.filter(exchange);
        }

        // 设置跨域响应头
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();

        // 读取请求的 Origin，支持 credentials（不能用 *）
        String origin = request.getHeaders().getOrigin();
        headers.setAccessControlAllowOrigin(Objects.requireNonNullElse(origin, ALL));
        headers.setAccessControlAllowMethods(ALLOWED_METHODS);
        headers.setAccessControlAllowHeaders(List.of("*"));
        headers.setAccessControlMaxAge(Long.parseLong(MAX_AGE));
        headers.setAccessControlAllowCredentials(true);
        headers.setAccessControlExposeHeaders(List.of("*"));

        // 预检请求直接返回 200
        if (request.getMethod() == HttpMethod.OPTIONS) {
            response.setStatusCode(HttpStatus.OK);
            return Mono.empty();
        }
        return chain.filter(exchange);
    }

}
