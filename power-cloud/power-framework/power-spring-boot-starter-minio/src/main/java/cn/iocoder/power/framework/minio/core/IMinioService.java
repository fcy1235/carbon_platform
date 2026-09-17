package cn.iocoder.power.framework.minio.core;


import io.minio.ObjectWriteResponse;
import io.minio.Result;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author enjoyi
 */
public interface IMinioService {

    /**
     * 创建桶
     *
     * @param bucketName 桶名称
     */
    void createBucket(String bucketName) throws Exception;

    /**
     * 创建桶,固定minio容器
     *
     * @param bucketName 桶名称
     */
    void createBucketByRegion(String bucketName, String region) throws Exception;

    /**
     * 修改桶名
     * (minio不支持直接修改桶名，但是可以通过复制到一个新的桶里面，然后删除老的桶)
     *
     * @param oldBucketName 桶名称
     * @param newBucketName 桶名称
     */
    void renameBucket(String oldBucketName, String newBucketName) throws Exception;

    /**
     * 删除桶
     *
     * @param bucketName 桶名称
     */
    void deleteBucket(String bucketName) throws Exception;

    /**
     * 检查桶是否存在
     *
     * @param bucketName 桶名称
     * @return boolean true-存在 false-不存在
     */
    boolean checkBucketExist(String bucketName) throws Exception;

    /**
     * 列出所有的桶
     *
     * @return 所有桶名的集合
     */
    List<Bucket> getAllBucketInfo() throws Exception;

    /**
     * 列出某个桶中的所有文件名
     * 文件夹名为空时，则直接查询桶下面的数据，否则就查询当前桶下对于文件夹里面的数据
     *
     * @param bucketName 桶名称
     * @param folderName 文件夹名
     * @param isDeep     是否递归查询
     */
    Iterable<Result<Item>> getBucketAllFile(String bucketName, String folderName, Boolean isDeep) throws Exception;

    /**
     * 删除文件夹
     *
     * @param bucketName 桶名
     * @param objectName 文件夹名
     * @return
     */
    Boolean deleteBucketFile(String bucketName, String objectName);

    /**
     * 删除文件夹
     *
     * @param bucketName 桶名
     * @param objectName 文件夹名
     * @param isDeep     是否递归删除
     * @return
     */
    Boolean deleteBucketFolder(String bucketName, String objectName, Boolean isDeep);

    /**
     * 获取文件下载地址
     *
     * @param bucketName 桶名
     * @param objectName 文件名
     * @param expires    过期时间,默认秒
     * @return
     * @throws Exception
     */
    String getFileDownloadUrl(String bucketName, String objectName, Integer expires) throws Exception;

    /**
     * 获取文件下载地址
     *
     * @param filePath 文件上传后返回的地址
     * @return 请求地址
     */
    String getFileDownloadUrl(String filePath);

    /**
     * 获取文件下载地址
     *
     * @param bucketName 桶名
     * @param filePath   文件上传后返回的地址
     * @return 请求地址
     */
    String getFileDownloadUrl(String bucketName, String filePath);

    /**
     * 获取文件上传地址(暂时还未实现)
     *
     * @param bucketName 桶名
     * @param objectName 文件名
     * @param expires    过期时间,默认秒
     * @return
     * @throws Exception
     */
    String getFileUploadUrl(String bucketName, String objectName, Integer expires) throws Exception;

    /**
     * 创建文件夹
     *
     * @param bucketName 桶名
     * @param folderName 文件夹名称
     * @return
     * @throws Exception
     */
    ObjectWriteResponse createBucketFolder(String bucketName, String folderName) throws Exception;

    /**
     * 检测某个桶内是否存在某个文件
     *
     * @param objectName 文件名称
     * @param bucketName 桶名称
     */
    boolean getBucketFileExist(String objectName, String bucketName) throws Exception;

    /**
     * 判断桶中是否存在文件夹
     *
     * @param bucketName 同名称
     * @param objectName 文件夹名称
     * @param isDeep     是否递归查询(暂不支持)
     * @return
     */
    Boolean checkBucketFolderExist(String bucketName, String objectName, Boolean isDeep);

    /**
     * 根据MultipartFile file上传文件
     * minio 采用文件流上传，可以换成下面的文件上传
     *
     * @param file 上传的文件
     */
    String uploadFile(MultipartFile file) throws Exception;

    /**
     * 根据MultipartFile file上传文件
     *
     * @param file 上传的文件
     */
    String uploadFile(MultipartFile file, String fileName);

    /**
     * 根据MultipartFile file上传文件
     *
     * @param file       上传的文件
     * @param bucketName 上传至服务器的桶名称
     */
    String uploadFile(MultipartFile file, String bucketName, String fileName) throws Exception;

    /**
     * 根据MultipartFile file上传文件
     *
     * @param file       上传的文件
     * @param bucketName 上传至服务器的桶名称
     */
    String uploadFile(MultipartFile file, String bucketName, Boolean randomPath) throws Exception;

    /**
     * 根据MultipartFile file上传文件
     * minio 采用文件流上传，可以换成下面的文件上传
     *
     * @param file       上传的文件
     * @param bucketName 上传至服务器的桶名称
     */
    String uploadFile(MultipartFile file, String bucketName, Boolean randomPath, String fileName) throws Exception;

    /**
     * 上传本地文件，根据路径上传
     * minio 采用文件内容上传，可以换成上面的流上传
     *
     * @param filePath 上传本地文件路径
     * @Param bucketName 上传至服务器的桶名称
     */
    boolean uploadPath(String filePath, String bucketName) throws Exception;

    /**
     * 文件下载,通过http返回，即在浏览器下载
     *
     * @param response   http请求的响应对象
     * @param bucketName 下载指定服务器的桶名称
     * @param objectName 下载的文件名称
     */
    void downloadFile(HttpServletResponse response, String bucketName, String objectName) throws Exception;

    /**
     * 文件下载到指定路径
     *
     * @param downloadPath 下载到本地路径
     * @param bucketName   下载指定服务器的桶名称
     * @param objectName   下载的文件名称
     */
    void downloadPath(String downloadPath, String bucketName, String objectName) throws Exception;

    /**
     * 获取默认的文件基础下载地址
     *
     * @return
     */
    String getBaseUrl();
}
