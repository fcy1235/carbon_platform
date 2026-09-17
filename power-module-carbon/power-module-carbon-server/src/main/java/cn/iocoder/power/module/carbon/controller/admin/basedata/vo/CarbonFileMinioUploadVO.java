package cn.iocoder.power.module.carbon.controller.admin.basedata.vo;


import cn.iocoder.power.framework.minio.config.MinioProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件上传
 *
 * @author xuweizhi
 * @since 2024-08-13 18:23
 */
@Data
@Schema(description = "文件上传")
public class CarbonFileMinioUploadVO {

    /**
     * http 地址模板
     */
    public static final String URL_TEMPLATE = "http://%s:%d/%s/%s";

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "文件地址")
    private String url;

    @Schema(description = "桶名称")
    private String bucketName;

    @Schema(description = "文件路径")
    private String filePath;


    /**
     * 文件返回对象
     *
     * @param minioProperties minio 配置属性
     * @param fileName        文件名称
     * @param filePath        文件路径
     * @return //
     */
    public static CarbonFileMinioUploadVO of(MinioProperties minioProperties, String fileName, String filePath) {
        CarbonFileMinioUploadVO uploadFile = new CarbonFileMinioUploadVO();
        uploadFile.setFileName(fileName);
        String url = String.format(URL_TEMPLATE, minioProperties.getIp(), minioProperties.getPort(), minioProperties.getBucketName(), filePath);
        uploadFile.setUrl(url);
        uploadFile.setBucketName(minioProperties.getBucketName());
        uploadFile.setFilePath(filePath);
        return uploadFile;
    }
}
