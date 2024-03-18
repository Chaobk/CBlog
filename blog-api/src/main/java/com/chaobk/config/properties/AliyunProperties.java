package com.chaobk.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "upload.aliyun")
public class AliyunProperties {
    private String endpoint;
    private String bucketName;
    private String path;
    private String accessKeyId;
    private String secretAccessKey;
    private String backupPath;
}
