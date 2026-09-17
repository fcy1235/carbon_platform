package cn.iocoder.power.framework.minio.config;

import cn.iocoder.power.framework.minio.config.MinioProperties;
import cn.iocoder.power.framework.minio.core.MinioService;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Minio 自动配置类
 *
 * @author gitlab
 * @since 2025-03-11 11:47
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioAutoConfiguration {

    @Resource
    private MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        String scheme = Boolean.TRUE.equals(minioProperties.getSecure()) ? "https" : "http";
        String url = scheme + "://" + minioProperties.getIp() + ":" + minioProperties.getPort();
        return MinioClient.builder()
                .endpoint(url)
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

    @Bean
    public MinioService minioService(MinioProperties minioProperties, MinioClient minioClient) {
        return new MinioService(minioProperties, minioClient);
    }

}
