package com.chaobk.util.upload.channel;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.chaobk.config.properties.AliyunProperties;
import com.chaobk.util.upload.UploadUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.UUID;

/**
 * 阿里云OSS存储上传
 */
@Lazy
@Component
public class AliyunChannel implements FileUploadChannel {

    private AliyunProperties aliyunProperties;

    private OSS ossClient;

    public AliyunChannel(AliyunProperties aliyunProperties) {
        this.aliyunProperties = aliyunProperties;
        this.ossClient = new OSSClientBuilder().build(aliyunProperties.getEndpoint(), aliyunProperties.getAccessKeyId(), aliyunProperties.getSecretAccessKey());
    }

    @Override
    public String upload(UploadUtils.ImageResource image) throws Exception {
        String uploadName = aliyunProperties.getPath() + "/" + UUID.randomUUID() + "." + image.getType();
        PutObjectRequest putObjectRequest = new PutObjectRequest(aliyunProperties.getBucketName(), uploadName, new ByteArrayInputStream(image.getData()));
        try {
            ossClient.putObject(putObjectRequest);
            return String.format("https://%s.%s/%s", aliyunProperties.getBucketName(), aliyunProperties.getEndpoint(), uploadName);
        } catch (Exception e) {
            throw new RuntimeException("阿里云OSS上传失败");
        } finally {
            putObjectRequest.getInputStream().close();
        }
    }
}
