package cn.iocoder.power.framework.minio.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author yg
 */
@Component
@Data
@ConfigurationProperties(prefix = MinioProperties.PREFIX)
public class MinioProperties {

    public static final String PREFIX = "minio.config";

    /**
     * 环境变量
     */
    @Resource
    private Environment environment;


    /**
     * API调用地址ip
     */
    private String ip;

    /**
     * API调用地址端口
     */
    private Integer port;

    /**
     * 连接账号
     */
    private String accessKey;

    /**
     * 连接秘钥
     */
    private String secretKey;

    /**
     * minio存储桶的名称
     */
    private String bucketName;

    /**
     * 文件下载到本地的路径
     */
    private String downloadDir;

    /**
     * #如果是true，则用的是https而不是http,默认值是true
     */
    private Boolean secure;


    @PostConstruct
    public void init() {
        this.overrideFromEnv();
    }

    private void overrideFromEnv() {
        if (environment == null) {
            return;
        }
        if (!StringUtils.hasLength(this.getBucketName())) {
            this.setBucketName(environment.resolvePlaceholders("${spring.application.name:}"));
        }
    }

}