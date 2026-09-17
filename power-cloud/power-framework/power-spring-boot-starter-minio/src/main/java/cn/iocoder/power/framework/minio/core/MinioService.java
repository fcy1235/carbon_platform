package cn.iocoder.power.framework.minio.core;

import cn.iocoder.power.framework.minio.config.MinioProperties;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author gitlab
 */
@Slf4j
public class MinioService implements IMinioService {

    private final MinioProperties minioProperties;

    private final MinioClient minioClient;

    public MinioService(MinioProperties minioProperties, MinioClient minioClient) {
        this.minioProperties = minioProperties;
        this.minioClient = minioClient;
    }

    @Override
    public String getBaseUrl() {
        return "http://" + minioProperties.getIp() + ":" + minioProperties.getPort() + "/" + minioProperties.getBucketName() + "/";
    }

    /**
     * 创建桶
     *
     * @param bucketName 桶名称
     */
    @Override
    public void createBucket(String bucketName) throws Exception {
        if (!StringUtils.hasLength(bucketName)) {
            throw new RuntimeException("创建桶的时候，桶名不能为空！");
        }

        // Create bucket with default region.
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());

        String policyJson = getPolicyJson(bucketName);
        minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policyJson).build());
        log.info("minio auto create bucket:{}", bucketName);
    }

    /**
     * 创建桶,固定minio容器
     *
     * @param bucketName 桶名称
     */
    @Override
    public void createBucketByRegion(String bucketName, String region) throws Exception {
        if (!StringUtils.hasLength(bucketName)) {
            throw new RuntimeException("创建桶的时候，桶名不能为空！");
        }

        // Create bucket with specific region.
        minioClient.makeBucket(MakeBucketArgs.builder()
                .bucket(bucketName)
                .region(region)
                .build());
    }

    /**
     * 修改桶名
     * (minio不支持直接修改桶名，但是可以通过复制到一个新的桶里面，然后删除老的桶)
     *
     * @param oldBucketName 桶名称
     * @param newBucketName 桶名称
     */
    @Override
    public void renameBucket(String oldBucketName, String newBucketName) throws Exception {
        if (!StringUtils.hasLength(oldBucketName) || !StringUtils.hasLength(newBucketName)) {
            throw new RuntimeException("修改桶名的时候，桶名不能为空！");
        }

    }

    /**
     * 删除桶
     *
     * @param bucketName 桶名称
     */
    @Override
    public void deleteBucket(String bucketName) throws Exception {
        if (!StringUtils.hasLength(bucketName)) {
            throw new RuntimeException("删除桶的时候，桶名不能为空！");
        }

        minioClient.removeBucket(
                RemoveBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
    }

    /**
     * 检查桶是否存在
     *
     * @param bucketName 桶名称
     * @return boolean true-存在 false-不存在
     */
    @Override
    public boolean checkBucketExist(String bucketName) throws Exception {
        if (!StringUtils.hasLength(bucketName)) {
            throw new RuntimeException("检测桶的时候，桶名不能为空！");
        }

        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 列出所有的桶
     *
     * @return 所有桶名的集合
     */
    @Override
    public List<Bucket> getAllBucketInfo() throws Exception {

        // 列出所有桶
        return minioClient.listBuckets();
    }

    /**
     * 列出某个桶中的所有文件名
     * 文件夹名为空时，则直接查询桶下面的数据，否则就查询当前桶下对于文件夹里面的数据
     *
     * @param bucketName 桶名称
     * @param folderName 文件夹名
     * @param isDeep     是否递归查询
     */
    @Override
    public Iterable<Result<Item>> getBucketAllFile(String bucketName, String folderName, Boolean isDeep) throws Exception {
        if (!StringUtils.hasLength(bucketName)) {
            throw new RuntimeException("获取桶中文件列表的时候，桶名不能为空！");
        }
        if (!StringUtils.hasLength(folderName)) {
            folderName = "";
        }

        return minioClient.listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(bucketName)
                        .prefix(folderName + "/")
                        .recursive(isDeep)
                        .build());
    }

    /**
     * 删除文件夹
     *
     * @param bucketName 桶名
     * @param objectName 文件夹名
     * @return
     */
    @Override
    public Boolean deleteBucketFile(String bucketName, String objectName) {
        if (!StringUtils.hasLength(bucketName) || !StringUtils.hasLength(objectName)) {
            throw new RuntimeException("删除文件的时候，桶名或文件名不能为空！");
        }
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return true;
        } catch (Exception e) {
            log.info("删除文件失败");
            return false;
        }
    }

    /**
     * 删除文件夹
     *
     * @param bucketName 桶名
     * @param objectName 文件夹名
     * @param isDeep     是否递归删除
     * @return
     */
    @Override
    public Boolean deleteBucketFolder(String bucketName, String objectName, Boolean isDeep) {
        if (!StringUtils.hasLength(bucketName) || !StringUtils.hasLength(objectName)) {
            throw new RuntimeException("删除文件夹的时候，桶名或文件名不能为空！");
        }
        try {
            ListObjectsArgs args = ListObjectsArgs.builder().bucket(bucketName).prefix(objectName + "/").recursive(isDeep).build();
            Iterable<Result<Item>> listObjects = minioClient.listObjects(args);
            listObjects.forEach(objectResult -> {
                try {
                    Item item = objectResult.get();
                    minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(item.objectName()).build());
                } catch (Exception e) {
                    log.info("删除文件夹中的文件异常", e);
                }
            });
            return true;
        } catch (Exception e) {
            log.info("删除文件夹失败");
            return false;
        }
    }

    /**
     * 获取文件下载地址
     *
     * @param bucketName 桶名
     * @param objectName 文件名
     * @param expires    过期时间,默认秒
     * @return
     * @throws Exception
     */
    @Override
    public String getFileDownloadUrl(String bucketName, String objectName, Integer expires) throws Exception {

        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(objectName)
                .expiry(expires, TimeUnit.SECONDS)
                .build();
        return minioClient.getPresignedObjectUrl(args);
    }

    /**
     * 获取文件下载地址
     *
     * @param filePath 文件上传后返回的地址
     * @return 请求地址
     */
    @Override
    public String getFileDownloadUrl(String filePath) {
        return this.getFileDownloadUrl(minioProperties.getBucketName(), filePath);
    }

    /**
     * 获取文件下载地址
     *
     * @param bucketName 桶名
     * @param filePath   文件上传后返回的地址
     * @return 请求地址
     */
    @Override
    public String getFileDownloadUrl(String bucketName, String filePath) {

        return String.format("http://%s:%d/%s/%s",
                minioProperties.getIp(),
                minioProperties.getPort(),
                Optional.ofNullable(bucketName).orElse(minioProperties.getBucketName()),
                filePath
        );
    }

    /**
     * 获取文件上传地址(暂时还未实现)
     *
     * @param bucketName 桶名
     * @param objectName 文件名
     * @param expires    过期时间,默认秒
     * @return
     * @throws Exception
     */
    @Override
    public String getFileUploadUrl(String bucketName, String objectName, Integer expires) throws Exception {

        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .method(Method.POST)
                        // 预签名的 URL 有效期为 1 小时
                        .expiry(expires)
                        .build());
    }

    /**
     * 创建文件夹
     *
     * @param bucketName 桶名
     * @param folderName 文件夹名称
     * @return
     * @throws Exception
     */
    @Override
    public ObjectWriteResponse createBucketFolder(String bucketName, String folderName) throws Exception {

        if (!checkBucketExist(bucketName)) {
            throw new RuntimeException("必须在桶存在的情况下才能创建文件夹");
        }
        if (!StringUtils.hasLength(folderName)) {
            throw new RuntimeException("创建的文件夹名不能为空");
        }
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(folderName + "/")
                .stream(new ByteArrayInputStream(new byte[0]), 0, 0)
                .build();


        return minioClient.putObject(putObjectArgs);
    }

    /**
     * 检测某个桶内是否存在某个文件
     *
     * @param objectName 文件名称
     * @param bucketName 桶名称
     */
    @Override
    public boolean getBucketFileExist(String objectName, String bucketName) throws Exception {
        if (!StringUtils.hasLength(objectName) || !StringUtils.hasLength(bucketName)) {
            throw new RuntimeException("检测文件的时候，文件名和桶名不能为空！");
        }

        try {
            // 判断文件是否存在
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build()) &&
                    minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build()) != null;
        } catch (ErrorResponseException e) {
            log.info("文件不存在 ! Object does not exist");
            return false;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * 判断桶中是否存在文件夹
     *
     * @param bucketName 同名称
     * @param objectName 文件夹名称
     * @param isDeep     是否递归查询(暂不支持)
     * @return
     */
    @Override
    public Boolean checkBucketFolderExist(String bucketName, String objectName, Boolean isDeep) {

        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).prefix(objectName).recursive(isDeep).build());

        // 文件夹下存在文件
        return results.iterator().hasNext();
    }


    /**
     * 根据MultipartFile file上传文件
     *
     * @param file 上传的文件
     */
    @Override
    public String uploadFile(MultipartFile file) {
        try {
            return uploadFile(file, minioProperties.getBucketName(), false, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据MultipartFile file上传文件
     *
     * @param file 上传的文件
     */
    @Override
    public String uploadFile(MultipartFile file, String fileName) {
        try {
            return uploadFile(file, minioProperties.getBucketName(), false, fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据MultipartFile file上传文件
     *
     * @param file       上传的文件
     * @param bucketName 上传至服务器的桶名称
     */
    @Override
    public String uploadFile(MultipartFile file, String bucketName, String fileName) throws Exception {
        return this.uploadFile(file, bucketName, true, fileName);
    }

    /**
     * 根据MultipartFile file上传文件
     *
     * @param file       上传的文件
     * @param bucketName 上传至服务器的桶名称
     */
    @Override
    public String uploadFile(MultipartFile file, String bucketName, Boolean randomPath) throws Exception {
        return this.uploadFile(file, bucketName, randomPath, null);
    }


    /**
     * 根据MultipartFile file上传文件
     * security 采用文件流上传，可以换成下面的文件上传
     *
     * @param file       上传的文件
     * @param bucketName 上传至服务器的桶名称
     * @param randomPath 是否随机生成路径
     */
    @Override
    public String uploadFile(MultipartFile file, String bucketName, Boolean randomPath, String fileName) throws Exception {

        if (file == null || file.getSize() == 0 || file.isEmpty()) {
            throw new RuntimeException("上传文件为空，请重新上传");
        }

        bucketName = bucketIsExist(bucketName);

        // 获取上传的文件名
        String filename = file.getOriginalFilename();
        assert filename != null;

        // 可以选择生成一个minio中存储的文件名称
        String minioFilename = Objects.equals(randomPath, true) ? UUID.randomUUID() + "_" + filename : filename;
        if (org.apache.commons.lang3.StringUtils.isNotBlank(fileName)) {
            minioFilename = fileName;
        }

        InputStream inputStream = file.getInputStream();
        long size = file.getSize();
        String contentType = file.getContentType();

        // Upload known sized input stream.
        minioClient.putObject(
                PutObjectArgs.builder()
                        // 上传到指定桶里面
                        .bucket(bucketName)
                        // 文件在minio中存储的名字
                        .object(minioFilename)
                        // p1:上传的文件流；p2:上传文件总大小；p3：上传的分片大小  上传分片文件流大小，如果分文件上传可以采用这种形式
                        .stream(inputStream, size, -1)
                        // 文件的类型
                        .contentType(contentType)
                        .build());

        // 校验是否上传成功
        boolean bucketFileExist = this.getBucketFileExist(minioFilename, bucketName);

        if (bucketFileExist) {
            return minioFilename;
        }
        return null;
    }


    /**
     * 根据MultipartFile file上传文件
     * security 采用文件流上传，可以换成下面的文件上传
     *
     * @param fileName    上传的文件
     * @param inputStream 文件流
     * @param bucketName  上传至服务器的桶名称
     * @param contentType 上传至服务器的桶名称
     */
    public String uploadFile(String bucketName, String fileName, InputStream inputStream, String contentType) throws Exception {

        if (fileName == null) {
            throw new RuntimeException("上传文件为空，请重新上传");
        }

        bucketName = this.bucketIsExist(bucketName);

        // 可以选择生成一个minio中存储的文件名称
        String minioFilename = UUID.randomUUID() + "_" + fileName;

        // Upload known sized input stream.
        minioClient.putObject(
                PutObjectArgs.builder()
                        // 上传到指定桶里面
                        .bucket(bucketName)
                        // 文件在minio中存储的名字
                        .object(minioFilename)
                        // p1:上传的文件流；p2:上传文件总大小；p3：上传的分片大小  上传分片文件流大小，如果分文件上传可以采用这种形式
                        .stream(inputStream, -1, 1024 * 1024 * 10)
                        // 文件的类型
                        .contentType(contentType)
                        .build());

        // 校验是否上传成功
        boolean bucketFileExist = this.getBucketFileExist(minioFilename, bucketName);

        if (bucketFileExist) {
            return minioFilename;
        }
        return null;
    }


    /**
     * 根据MultipartFile file上传文件
     * security 采用文件流上传，可以换成下面的文件上传
     *
     * @param files      上传的文件
     * @param bucketName 上传至服务器的桶名称
     */
    public Map<String, String> uploadFiles(MultipartFile[] files, String bucketName) throws Exception {

        if (files == null) {
            throw new RuntimeException("上传文件为空，请重新上传");
        }

        for (MultipartFile file : files) {
            if (file == null || file.getSize() == 0 || file.isEmpty()) {
                throw new RuntimeException("上传文件为空，请重新上传");
            }
        }

        bucketName = bucketIsExist(bucketName);

        Map<String, String> uploadResult = new HashMap<>();

        for (MultipartFile file : files) {

            // 获取上传的文件名
            String filename = file.getOriginalFilename();

            // 可以选择生成一个minio中存储的文件名称
            String minioFilename = UUID.randomUUID() + "_" + filename;

            // Upload known sized input stream.
            minioClient.putObject(
                    PutObjectArgs.builder()
                            // 上传到指定桶里面
                            .bucket(bucketName)
                            // 文件在minio中存储的名字
                            .object(minioFilename)
                            // p1:上传的文件流；p2:上传文件总大小；p3：上传的分片大小  上传分片文件流大小，如果分文件上传可以采用这种形式
                            .stream(file.getInputStream(), file.getSize(), -1)
                            // 文件的类型
                            .contentType(file.getContentType())
                            .build());
            // 搞不懂 minio 不提供批量上传 api
            uploadResult.put(filename, minioFilename);
        }
        return uploadResult;
    }

    /**
     * 校验 bucket 是否存在
     *
     * @param bucketName //
     * @return //
     * @throws Exception //
     */
    private String bucketIsExist(String bucketName) throws Exception {
        if (!StringUtils.hasLength(bucketName)) {
            bucketName = minioProperties.getBucketName();
        }

        if (!this.checkBucketExist(bucketName)) {
            this.createBucket(bucketName);
        }
        return bucketName;
    }

    private static @NotNull String getPolicyJson(String bucketName) {
        String policyJson = "{\n" +
                "    \"Version\": \"2012-10-17\",\n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Principal\": {\n" +
                "                \"AWS\": [\n" +
                "                    \"*\"\n" +
                "                ]\n" +
                "            },\n" +
                "            \"Action\": [\n" +
                "                \"s3:ListBucketMultipartUploads\",\n" +
                "                \"s3:GetBucketLocation\",\n" +
                "                \"s3:ListBucket\"\n" +
                "            ],\n" +
                "            \"Resource\": [\n" +
                "                \"arn:aws:s3:::" + bucketName + "\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Principal\": {\n" +
                "                \"AWS\": [\n" +
                "                    \"*\"\n" +
                "                ]\n" +
                "            },\n" +
                "            \"Action\": [\n" +
                "                \"s3:ListMultipartUploadParts\",\n" +
                "                \"s3:PutObject\",\n" +
                "                \"s3:AbortMultipartUpload\",\n" +
                "                \"s3:DeleteObject\",\n" +
                "                \"s3:GetObject\"\n" +
                "            ],\n" +
                "            \"Resource\": [\n" +
                "                \"arn:aws:s3:::" + bucketName + "/*\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        return policyJson;
    }

    /**
     * 上传本地文件，根据路径上传
     * security 采用文件内容上传，可以换成上面的流上传
     *
     * @param filePath 上传本地文件路径
     * @Param bucketName 上传至服务器的桶名称
     */
    @Override
    public boolean uploadPath(String filePath, String bucketName) throws Exception {

        File file = new File(filePath);
        if (!file.isFile()) {
            throw new RuntimeException("上传文件为空，请重新上传");
        }

        bucketName = bucketIsExist(bucketName);

        // 获取文件名称
        String minioFilename = UUID.randomUUID().toString() + "_" + file.getName();
        String fileType = minioFilename.substring(minioFilename.lastIndexOf(".") + 1);

        minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucketName)
                        // 文件存储在minio中的名字
                        .object(minioFilename)
                        // 上传本地文件存储的路径
                        .filename(filePath)
                        // 文件类型
                        .contentType(fileType)
                        .build());

        return this.getBucketFileExist(minioFilename, bucketName);
    }

    /**
     * 文件下载,通过http返回，即在浏览器下载
     *
     * @param response   http请求的响应对象
     * @param bucketName 下载指定服务器的桶名称
     * @param objectName 下载的文件名称
     */
    @Override
    public void downloadFile(HttpServletResponse response, String bucketName, String objectName) throws Exception {
        if (response == null || !StringUtils.hasLength(bucketName) || !StringUtils.hasLength(objectName)) {
            throw new RuntimeException("下载文件参数不全！");
        }

        if (!this.checkBucketExist(bucketName)) {
            throw new RuntimeException("当前操作的桶不存在！");
        }

        // 获取一个下载的文件输入流操作
        GetObjectResponse objectResponse = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());

        OutputStream outputStream = response.getOutputStream();
        int len = 0;
        byte[] buf = new byte[1024 * 8];
        while ((len = objectResponse.read(buf)) != -1) {
            outputStream.write(buf, 0, len);
        }
        if (outputStream != null) {
            outputStream.close();
            outputStream.flush();
        }
        objectResponse.close();
    }

    /**
     * 文件下载到指定路径
     *
     * @param downloadPath 下载到本地路径
     * @param bucketName   下载指定服务器的桶名称
     * @param objectName   下载的文件名称
     */
    @Override
    public void downloadPath(String downloadPath, String bucketName, String objectName) throws Exception {
        if (downloadPath.isEmpty() || !StringUtils.hasLength(bucketName) || !StringUtils.hasLength(objectName)) {
            throw new RuntimeException("下载文件参数不全！");
        }

        if (!new File(downloadPath).isDirectory()) {
            throw new RuntimeException("本地下载路径必须是一个文件夹或者文件路径！");
        }

        if (!this.checkBucketExist(bucketName)) {
            throw new RuntimeException("当前操作的桶不存在！");
        }

        downloadPath += objectName;

        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        // 指定是在哪一个桶下载
                        .bucket(bucketName)
                        // 是minio中文件存储的名字;本地上传的文件是user.xlsx到minio中存储的是user-security,那么这里就是user-security
                        .object(objectName)
                        // 需要下载到本地的路径，一定是带上保存的文件名；如 d:\\security\\user.xlsx
                        .filename(downloadPath)
                        .build());
    }


}