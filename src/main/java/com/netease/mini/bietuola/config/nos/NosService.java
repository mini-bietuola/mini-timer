package com.netease.mini.bietuola.config.nos;

import com.netease.cloud.ClientConfiguration;
import com.netease.cloud.ClientException;
import com.netease.cloud.Protocol;
import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Auther ctl
 * @Date 2019/5/6
 */
@Service
public class NosService {

    private static final Logger LOG = LoggerFactory.getLogger(NosService.class);

    private static final String DEFAULT_SUFFIX = ContentType.BIN.getSuffix();// 默认的文件后缀

    private static final int MIN_LARGE_OBJECT_SIZE = 50 * 1024 * 1024;
    private static final int MIN_PART_SIZE = MIN_LARGE_OBJECT_SIZE / 2;
    private static final int MAX_PART_SIZE = 100 * 1024 * 1024;
    private static final int DEFAULT_BUFFER_SIZE = 5 * 1024 * 1024;

    private static final int MAX_CONNECTIONS = 200;
    private static final int CONNECTION_TIMEOUT = 2000;
    private static final int SOCKET_TIMEOUT = 5000;

    private static final String ACCESS_KEY = "ed10c92cc8844686923747b39116e209";
    private static final String SECRET_KEY = "c52c6d63ab9e4eb69ea2193e04e8877b";

    private String bucketName;
    private String nosUrlPrefix;

    private NosClient nosClient;

    public NosService() {
        ClientConfiguration config = new ClientConfiguration();
        config.setMaxConnections(MAX_CONNECTIONS);
        config.setConnectionTimeout(CONNECTION_TIMEOUT);
        config.setSocketTimeout(SOCKET_TIMEOUT);
        config.setProtocol(Protocol.HTTPS);
        this.bucketName = "bietuola";
        this.nosUrlPrefix = String.format("https://%s.nos-eastchina1.126.net/", this.bucketName);
        this.nosClient = new NosClient(new BasicCredentials(ACCESS_KEY, SECRET_KEY), config);
        this.nosClient.setEndpoint("nos-eastchina1.126.net");
    }

    public String getNosUrlPrefix() {
        return nosUrlPrefix;
    }

    /**
     * 上传流到nos，默认文件后缀bin
     *
     * @param input 要上传的文件流
     * @param size  文件大小
     * @return 保存到数据库中的字符型文件<code>ID</code>
     * 错误时返回 null
     */
    public String uploadStream(InputStream input, long size, String objectName) throws NOSException {
        String resCode;
        try {
            resCode = upload(input, size, objectName, DEFAULT_SUFFIX);
        } catch (Exception e) {
            throw new NOSException("upload nos error! ", e);
        }
        return resCode;
    }

    public String uploadStream(InputStream input, long size, String objectName, String suffix) throws NOSException {
        String resCode;
        ContentType ct = ContentType.getContentTypeBySuffix(suffix);
        try {
            resCode = upload(input, size, objectName, ct.getSuffix());
        } catch (Exception e) {
            throw new NOSException("upload nos error! ", e);
        }
        return resCode;
    }

    /**
     * 上传图片文件，默认后缀为jpg
     *
     * @param input 要上传的图片流
     * @param size  文件大小
     * @return 保存到数据库中的字符型文件<code>ID</code>
     * 错误时返回 null
     */
    public String uploadImage(InputStream input, long size, String objectName, String suffix) throws NOSException {
        String resCode;
        ContentType ct = ContentType.getContentTypeBySuffix(suffix);
        if (ct.equals(ContentType.BIN)) {
            ct = ContentType.JPG;
        }
        try {
            resCode = upload(input, size, objectName, ct.getSuffix());
        } catch (Exception e) {
            throw new NOSException("upload nos error! ", e);
        }
        return resCode;
    }

    /**
     * 上传文件
     *
     * @param input  要上传的文件流对象
     * @param size   文件大小
     * @param suffix 后缀，如图片后缀“jpg”
     * @return 保存到数据库中的文件字符型<code>ID</code>
     * @throws Exception
     */
    private String upload(InputStream input, long size, String objectName, String suffix) throws Exception {
        // 设置上传文件的Content-type
        ObjectMetadata metadata = new ObjectMetadata();
        // 只有contentType为空时才set值
        if (StringUtils.isBlank(metadata.getContentType())) {
            ContentType ct = ContentType.getContentTypeBySuffix(suffix);
            metadata.setContentType(ct.getCt());
            metadata.setContentLength(input.available());
        }
        // nos服务器返回对象
        PutObjectResult result;
        // nos要求文件大小大于100M的需要分块上传
        if (size <= MAX_PART_SIZE) {
            // 新文件上传获取id;旧文件覆盖原桶中的内容
            if (objectName != null) {
                result = nosClient.putObject(bucketName, objectName, input, metadata);
            } else {
                result = nosClient.putObject(bucketName, null, input, metadata);
            }
            objectName = result.getObjectName();
            // 分块上传
        } else {
            if (objectName == null) {
                //先上传一个空的流文件，获取系统默认的key
                byte[] buf = new byte[0];
                InputStream in = new ByteArrayInputStream(buf);//创建一个空的流文件
                PutObjectResult res = nosClient.putObject(bucketName, null, in, null);
                IOUtils.closeQuietly(in);
                objectName = res.getObjectName();
            }
            partsUpload(input, objectName, suffix);
        }
        //关闭inputStream
        if (input != null) {
            IOUtils.closeQuietly(input);
        }
        return objectName;
    }

    /**
     * 文件分块上传方法
     *
     * @param input      要上传的文件流对象
     * @param objectName 文件名
     * @param suffix     后缀，如图片后缀“jpg”
     * @throws Exception
     */
    private void partsUpload(InputStream input, String objectName, String suffix) throws Exception {
        InitiateMultipartUploadRequest initRequest =
                new InitiateMultipartUploadRequest(bucketName, objectName);
        // 设置上传文件的Content-type
        ObjectMetadata metadata = new ObjectMetadata();
        // 只有contentType为空时才set值
        if (StringUtils.isBlank(metadata.getContentType())) {
            ContentType ct = ContentType.getContentTypeBySuffix(suffix);
            metadata.setContentType(ct.getCt());
        }
        initRequest.setObjectMetadata(metadata);
        InitiateMultipartUploadResult initResult = nosClient.initiateMultipartUpload(initRequest);
        String uploadId = initResult.getUploadId();
        int offset = 0;
        //每次读取25M数据，作为上传的一块数据大小
        byte[] buff = new byte[MIN_PART_SIZE];
        int readLen;
        int index = 1;
        while ((readLen = input.read(buff, offset, MIN_PART_SIZE)) != -1) {
            // 每次从流中读取25M数据，并作为分块上传
            InputStream subInputStream = new ByteArrayInputStream(buff);
            nosClient.uploadPart(new UploadPartRequest()
                    .withBucketName(bucketName)
                    .withUploadId(uploadId)
                    .withInputStream(subInputStream)
                    .withKey(objectName)
                    .withPartSize(readLen)
                    .withPartNumber(index));
            index++;
        }
        /*
         * 列出所有上传的分块
         */
        int nextMarker = 0;
        List<PartETag> partETags = new ArrayList<>();
        while (true) {
            ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, objectName, uploadId);
            listPartsRequest.setPartNumberMarker(nextMarker);
            PartListing parts = nosClient.listParts(listPartsRequest);
            for (PartSummary par : parts.getParts()) {
                nextMarker++;
                partETags.add(new PartETag(par.getPartNumber(), par.getETag()));
            }

            if (!parts.isTruncated()) {
                break;
            }
        }
        /*
         * 完成分块上传
         */
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partETags);
        nosClient.completeMultipartUpload(completeMultipartUploadRequest);
    }

    public boolean deleteObject(String objectName) {
        try {
            nosClient.deleteObject(bucketName, objectName);
        } catch (ClientException e) {
            LOG.error("delete nos file fail, objectName:{}", objectName, e);
            return false;
        }
        return true;
    }

    /**
     * 字符串转字节数组
     *
     * @param text    输入字符串
     * @param charset 编码
     * @return
     */
    private byte[] stringToBytes(String text, String charset) {
        try {
            return text.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            return text.getBytes();
        }
    }

    /**
     * 数组转字符串
     *
     * @param bytes
     * @param charset
     * @return
     */
    private String bytesToString(byte[] bytes, String charset) {
        try {
            if (charset == null) {
                charset = "UTF-8";
            }
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(bytes);
        }
    }

    /**
     * 获得NosMetadata对象，用于下载文件
     *
     * @param objectName 文件<code>ID</code>
     * @return 成功返回NosDownloadMetadata对象，否则返回null
     * @throws Exception 失败抛出异常
     */
    public NosMetadata getNosMetadata(String objectName) throws Exception {
        InputStream in = null;
        try {
            NOSObject nosObject = nosClient.getObject(bucketName, objectName);
            // 流式获取文件内容
            in = nosObject.getObjectContent();
            NosMetadata md = new NosMetadata();
            ByteArrayOutputStream out = new ByteArrayOutputStream((int) Math.max(DEFAULT_BUFFER_SIZE, nosObject.getObjectMetadata().getContentLength()));
            int bs;
            bs = (int) Math.min(Math.max(0, nosObject.getObjectMetadata().getContentLength()), DEFAULT_BUFFER_SIZE);
            byte[] b = new byte[bs];
            int len;
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            md.setObjectName(objectName);
            md.setInputStream(new ByteArrayInputStream(out.toByteArray()));
            md.setSize(nosObject.getObjectMetadata().getContentLength());
            return md;
        } finally {
            if (in != null) {
                IOUtils.closeQuietly(in);
            }
        }

    }

    /**
     * 从NOS服务器获取文本
     *
     * @param objectName 文件<code>ID</code>
     * @param charset    编码
     * @return 文件id对应的文本
     */
    public String getText(String objectName, String charset) {
        NosMetadata metadata;
        InputStream in = null;
        try {
            metadata = getNosMetadata(objectName);
            in = metadata.getInputStream();
            if (in != null) {
                return IOUtils.toString(in, charset);
            }
        } catch (Exception e) {
            LOG.error("get text error & objName: " + objectName, e);
        } finally {
            IOUtils.closeQuietly(in);
        }
        return "";
    }

    /**
     * 把一篇文本上传到nos服务器
     *
     * @param content 文本
     * @param charset 编码
     * @return 字符型文件<code>ID</code>，
     */
    public String uploadText(String content, String objectName, String charset) throws NOSException {
        String resCode;
        InputStream input = new ByteArrayInputStream(stringToBytes(content, charset));
        try {
            resCode = upload(input, input.available(), objectName, DEFAULT_SUFFIX);
        } catch (Exception e) {
            throw new NOSException("upload nos error! ", e);
        }
        return resCode;
    }

}
