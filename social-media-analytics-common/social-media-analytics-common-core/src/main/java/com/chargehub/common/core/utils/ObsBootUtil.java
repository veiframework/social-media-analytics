package com.chargehub.common.core.utils;

import com.chargehub.common.core.properties.HuaweiObs;
import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.internal.Constants;
import com.obs.services.internal.utils.AbstractAuthentication;
import com.obs.services.model.*;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.chargehub.common.core.utils.file.FileUtils.SINGLE_SLASH;

/**
 * @author zhangshengli
 * @version 1.0
 * @description: TODO
 * @date 2023/9/6 0006 10:17
 */

public class ObsBootUtil {
    private static final Logger log = LoggerFactory.getLogger(ObsBootUtil.class);


    /**
     * 文件名 正则字符串
     * 文件名支持的字符串：字母数字中文.-_()（） 除此之外的字符将被删除
     */
    private static final String FILE_NAME_REGEX = "[^A-Za-z\\.\\(\\)\\-（）\\_0-9\\u4e00-\\u9fa5]";

    private ObsBootUtil() {
    }


    /**
     * oss 工具客户端
     */
    private static ObsClient ossClient = null;

    private static String endPoint;
    private static String accessKeyId;
    private static String accessKeySecret;
    private static String bucketName;

    private static String privateBucketName;
    private static String privateStaticDomain;

    private static String qrCodeHost;

    public static void setQrCodeHost(String qrCodeHost) {
        ObsBootUtil.qrCodeHost = qrCodeHost;
    }

    public static void setOssClient(HuaweiObs huaweiObs) {
        if (huaweiObs == null) {
            return;
        }
        ObsBootUtil.endPoint = huaweiObs.getEndpoint();
        ObsBootUtil.accessKeyId = huaweiObs.getAccessKeyId();
        ObsBootUtil.accessKeySecret = huaweiObs.getAccessKeySecret();
        ObsBootUtil.bucketName = huaweiObs.getBucketName();
        ObsBootUtil.privateBucketName = huaweiObs.getPrivateBucketName();
        ObsBootUtil.privateStaticDomain = huaweiObs.getPrivateStaticDomain();
    }


    public static ObsClient getOssClient() {
        return ossClient;
    }

    /**
     * 上传文件至华为云 OBS
     * 文件上传成功,返回文件完整访问路径
     * 文件上传失败,返回 null
     *
     * @param file    待上传文件
     * @param fileDir 文件保存目录
     * @return oss 中的相对文件路径
     */
    public static String upload(MultipartFile file, String fileDir, String customBucket) throws Exception {
        //update-begin-author:liusq date:20210809 for: 过滤上传文件类型
        FileTypeFilter.fileTypeFilter(file);
        //update-end-author:liusq date:20210809 for: 过滤上传文件类型

        String filePath;
        initOss(endPoint, accessKeyId, accessKeySecret);
        StringBuilder fileUrl = new StringBuilder();
        String newBucket = bucketName;
        if (StringUtils.isNotEmpty(customBucket)) {
            newBucket = customBucket;
        }
        try {
            //判断桶是否存在,不存在则创建桶
            if (!ossClient.headBucket(newBucket)) {
                ossClient.createBucket(newBucket);
            }
            // 获取文件名
            String orgName = file.getOriginalFilename();
            if ("".equals(orgName) || orgName == null) {
                orgName = file.getName();
            }
            orgName = getFileName(orgName);
            String fileName = !orgName.contains(".")
                    ? orgName + "_" + System.currentTimeMillis()
                    : orgName.substring(0, orgName.lastIndexOf("."))
                    + "_" + System.currentTimeMillis()
                    + orgName.substring(orgName.lastIndexOf("."));
            if (!fileDir.endsWith(SINGLE_SLASH)) {
                fileDir = fileDir.concat(SINGLE_SLASH);
            }
            //update-begin-author:wangshuai date:20201012 for: 过滤上传文件夹名特殊字符，防止攻击
            fileDir = filter(fileDir);
            //update-end-author:wangshuai date:20201012 for: 过滤上传文件夹名特殊字符，防止攻击
            fileUrl.append(fileDir).append(fileName);

            filePath = "https://" + newBucket + "." + endPoint + SINGLE_SLASH + fileUrl;

            PutObjectResult result = ossClient.putObject(newBucket, fileUrl.toString(), file.getInputStream());
            // 设置权限(公开读)
//            ossClient.setBucketAcl(newBucket, CannedAccessControlList.PublicRead);
            if (result != null) {
                log.info("------OSS文件上传成功------" + fileUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return filePath;
    }

    /**
     * 文件上传
     *
     * @param file    文件
     * @param fileDir fileDir
     * @return 路径
     */
    public static String upload(MultipartFile file, String fileDir) throws Exception {
        return upload(file, fileDir, null);
    }

    /**
     * 上传文件至华为云 OBS
     * 文件上传成功,返回文件完整访问路径
     * 文件上传失败,返回 null
     *
     * @param file    待上传文件
     * @param fileDir 文件保存目录
     * @return oss 中的相对文件路径
     */
    public static String upload(FileItemStream file, String fileDir) {
        String filePath;
        initOss(endPoint, accessKeyId, accessKeySecret);
        StringBuilder fileUrl = new StringBuilder();
        try {
            String suffix = file.getName().substring(file.getName().lastIndexOf('.'));
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
            if (!fileDir.endsWith(SINGLE_SLASH)) {
                fileDir = fileDir.concat(SINGLE_SLASH);
            }
            fileDir = filter(fileDir);
            fileUrl.append(fileDir).append(fileName);

            filePath = "https://" + bucketName + "." + endPoint + SINGLE_SLASH + fileUrl;

            PutObjectResult result = ossClient.putObject(bucketName, fileUrl.toString(), file.openStream());
            // 设置权限(公开读)
//            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
            if (result != null) {
                log.info("------OSS文件上传成功------" + fileUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return filePath;
    }

    /**
     * 删除文件
     *
     * @param url 路径
     */
    public static void deleteUrl(String url) {
        deleteUrl(url, null);
    }

    /**
     * 删除文件
     *
     * @param url 路径
     */
    public static void deleteUrl(String url, String bucket) {
        String newBucket = bucketName;
        if (StringUtils.isNotEmpty(bucket)) {
            newBucket = bucket;
        }
        String bucketUrl = "https://" + newBucket + "." + endPoint + SINGLE_SLASH;

        //TODO 暂时不允许删除云存储的文件
        //initOss(endPoint, accessKeyId, accessKeySecret);
        url = url.replace(bucketUrl, "");
        ossClient.deleteObject(newBucket, url);
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名称
     */
    public static void delete(String fileName) {
        ossClient.deleteObject(bucketName, fileName);
    }

    public static void delete(String fileName, Boolean isPrivate) {
        if(isPrivate){
            ossClient.deleteObject(privateBucketName, fileName);
        }else{
            ossClient.deleteObject(bucketName, fileName);
        }
    }

    /**
     * 获取文件流
     *
     * @param objectName 对象名
     * @param bucket     桶
     * @return 文件流
     */
    public static InputStream getOssFile(String objectName, String bucket) {
        InputStream inputStream = null;
        try {
            String newBucket = bucketName;
            if (StringUtils.isNotEmpty(bucket)) {
                newBucket = bucket;
            }
            initOss(endPoint, accessKeyId, accessKeySecret);
            //update-begin---author:liusq  Date:20220120  for：替换objectName前缀，防止key不一致导致获取不到文件----
            objectName = ObsBootUtil.replacePrefix(objectName, bucket);
            //update-end---author:liusq  Date:20220120  for：替换objectName前缀，防止key不一致导致获取不到文件----
            ObsObject ossObject = ossClient.getObject(newBucket, objectName);
            inputStream = new BufferedInputStream(ossObject.getObjectContent());
        } catch (Exception e) {
            log.info("文件获取失败" + e.getMessage());
        }
        return inputStream;
    }

    /**
     * 获取文件外链
     *
     * @param bucketName 桶名称
     * @param objectName 对项名
     * @param expires    日期
     * @return 外链
     */
    public static String getObjectUrl(String bucketName, String objectName, Date expires) {
        initOss(endPoint, accessKeyId, accessKeySecret);
        try {
            //update-begin---author:liusq  Date:20220120  for：替换objectName前缀，防止key不一致导致获取不到文件----
            objectName = ObsBootUtil.replacePrefix(objectName, bucketName);
            //update-end---author:liusq  Date:20220120  for：替换objectName前缀，防止key不一致导致获取不到文件----
            if (ossClient.doesObjectExist(bucketName, objectName)) {
                //URL url = ossClient.generatePresignedUrl(bucketName, objectName, expires);
                //log.info("原始url : {}", url.toString());
                //log.info("decode url : {}", URLDecoder.decode(url.toString(), "UTF-8"));
                //【issues/4023】问题 oss外链经过转编码后，部分无效，大概在三分一；无需转编码直接返回即可 #4023
                //return url.toString();
                return "";
            }
        } catch (Exception e) {
            log.info("文件路径获取失败" + e.getMessage());
        }
        return null;
    }

    /**
     * 初始化 oss 客户端
     */
    private static void initOss(String endpoint, String accessKeyId, String accessKeySecret) {
        if (ossClient == null) {
            ossClient = new ObsClient(accessKeyId, accessKeySecret, endpoint);
        }
    }


    /**
     * 上传文件到oss
     *
     * @param stream       文件流
     * @param relativePath 相对路径
     * @return 文件路径
     */
    public static String upload(InputStream stream, String relativePath) {
        String filePath = "https://" + bucketName + "." + endPoint + SINGLE_SLASH + relativePath;
        initOss(endPoint, accessKeyId, accessKeySecret);
        PutObjectResult result = ossClient.putObject(bucketName, relativePath, stream);
        // 设置权限(公开读)
        //ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        if (result != null) {
            log.info("------OSS文件上传成功------" + relativePath);
        }
        return filePath;
    }

    /**
     * 替换前缀，防止key不一致导致获取不到文件
     *
     * @param objectName   文件上传路径 key
     * @param customBucket 自定义桶
     * @return 对象名
     * @date 2022-01-20
     * @author lsq
     */
    private static String replacePrefix(String objectName, String customBucket) {
        log.info("------replacePrefix---替换前---objectName:{}", objectName);

        String newBucket = bucketName;
        if (StringUtils.isNotEmpty(customBucket)) {
            newBucket = customBucket;
        }
        String path = "https://" + newBucket + "." + endPoint + SINGLE_SLASH;

        objectName = objectName.replace(path, "");

        log.info("------replacePrefix---替换后---objectName:{}", objectName);
        return objectName;
    }

    public static String getOriginalUrl(String url) {
        return url;
    }


    public static String getFileName(String fileName) {
        //判断是否带有盘符信息
        // Check for Unix-style path
        int unixSep = fileName.lastIndexOf('/');
        // Check for Windows-style path
        int winSep = fileName.lastIndexOf('\\');
        // Cut off at latest possible point
        int pos = (winSep > unixSep ? winSep : unixSep);
        if (pos != -1) {
            // Any sort of path separator found...
            fileName = fileName.substring(pos + 1);
        }
        //替换上传文件名字的特殊字符
        fileName = fileName.replace("=", "").replace(",", "").replace("&", "")
                .replace("#", "").replace("“", "").replace("”", "");
        //替换上传文件名字中的空格
        fileName = fileName.replaceAll("\\s", "");
        //update-beign-author:taoyan date:20220302 for: /issues/3381 online 在线表单 使用文件组件时，上传文件名中含%，下载异常
        fileName = fileName.replaceAll(FILE_NAME_REGEX, "");
        //update-end-author:taoyan date:20220302 for: /issues/3381 online 在线表单 使用文件组件时，上传文件名中含%，下载异常
        return fileName;
    }

    public static String filter(String str) throws PatternSyntaxException {
        // 清除掉所有特殊字符
        String regEx = "[`_《》~!@#$%^&*()+=|{}':;',\\[\\].<>?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 读取静态文本内容
     *
     * @param url
     * @return
     */
    public static String readStatic(String url) {
        String json = "";
        try {
            //换个写法，解决springboot读取jar包中文件的问题
            InputStream stream = StringUtils.class.getClassLoader().getResourceAsStream(url.replace("classpath:", ""));
            json = IOUtils.toString(stream, "UTF-8");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return json;
    }

    public static String getQREncodeUrl(String pileCode, String gunCode, Integer type) {
        try {
            MultipartFile qrCodeMultipartFile = QREncode.getQRCodeMultipartFile(qrCodeHost + "?pileCode=" + pileCode + "&gunCode=" + gunCode + "&temporaryUser=" + type, null);
            LocalDate now = LocalDate.now();
            long epochMilli = now.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
            return ObsBootUtil.upload(qrCodeMultipartFile, epochMilli + "");
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }


    /**
     * 创建临时上传访问url
     * @throws ObsException
     */
    public static Map doCreateObjectUrl(String objectKey, boolean isPrivate) throws ObsException {

        initOss(endPoint, accessKeyId, accessKeySecret);

        // Map<String, String> headers = new HashMap<String, String>();
        // String contentType = "text/plain";
        // headers.put("Content-Type", contentType);

        long expireSeconds = 3600L;
        TemporarySignatureRequest req = new TemporarySignatureRequest(HttpMethodEnum.PUT, expireSeconds);
        String otherBucketName = bucketName;
        if(isPrivate){
            otherBucketName = privateBucketName;
        }
        if (StringUtils.isEmpty(otherBucketName)){
            otherBucketName = bucketName;
        }
        req.setBucketName(otherBucketName);
        req.setObjectKey(objectKey);
        // req.setHeaders(headers);

        TemporarySignatureResponse res = ossClient.createTemporarySignature(req);

        System.out.println("Createing object using temporary signature url:");
        System.out.println("\t" + res.getSignedUrl());

        HashMap map = new HashMap();
        map.put("url", res.getSignedUrl());
        map.put("header", res.getActualSignedRequestHeaders().entrySet());
        return map;
    }

    /**
     * 获取临时下载URL
     * @throws ObsException
     */
    public static Map doDownloadUrl(String objectKey, String otherBucketName) throws ObsException {

        initOss(endPoint, accessKeyId, accessKeySecret);

        long expireSeconds = 3600L;
        TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, expireSeconds);
        if (StringUtils.isEmpty(otherBucketName)){
            otherBucketName = bucketName;
        }
        request.setBucketName(otherBucketName);
        request.setObjectKey(objectKey);
        TemporarySignatureResponse response = ossClient.createTemporarySignature(request);
        System.out.println("Getting object using temporary signature url:");
        System.out.println("SignedUrl:" + response.getSignedUrl());

        HashMap map = new HashMap();
        map.put("url", response.getSignedUrl());
        map.put("header", response.getActualSignedRequestHeaders());
        return map;
    }

    public static Map createToken(String method, String path, String contentType, boolean isPrivate) throws Exception{
        String host = bucketName + "." + endPoint;
        String tmpbucketName = bucketName;
        if(isPrivate){
            host = privateBucketName + "." + endPoint;
            tmpbucketName = privateBucketName;
        }

        if(path.startsWith("/")){
            path = path.substring(1);
        }
        // path = URLEncoder.encode(path, "UTF-8");

        String url = "https://" + host + "/" + path;
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType);
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        ZoneId zoneId = ZoneId.ofOffset("UTC", ZoneOffset.ofHours(8));
        TimeZone timeZone = TimeZone.getTimeZone(zoneId);
        dateFormat.setTimeZone(timeZone);
        String now = dateFormat.format(new Date(System.currentTimeMillis()));
        headers.put("x-obs-date", now);
        sign(method, "/" + tmpbucketName + "/" + path, headers,accessKeyId, accessKeySecret);
        // headers.put("Host",  host);
        Map<String, Object> result = new HashMap<>();
        result.put("headers", headers);
        result.put("url", url);
        return result;
    }

    public static void sign(String method, String url, Map headers, String ak, String sk) throws Exception {
        AbstractAuthentication authentication =
                Constants.AUTHTICATION_MAP.get(AuthTypeEnum.OBS);
        String stringToSign = authentication.makeServiceCanonicalString(method, url, headers, null, Constants.ALLOWED_RESOURCE_PARAMTER_NAMES);
        System.out.println("stringToSign: " + stringToSign);
        String auth = "OBS " + ak + ":" + AbstractAuthentication.caculateSignature(stringToSign, sk);
        headers.put("Authorization", auth);

    }

}


