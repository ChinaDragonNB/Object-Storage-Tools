package com.ak47007.minio.autoconfig;

import cn.hutool.core.util.StrUtil;
import com.ak47007.core.exception.ObjectStorageException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author AK47007
 * @Date 2023/4/12 22:23
 * @Description: Minio属性配置
 */
@ConfigurationProperties(prefix = "object-storage.minio")
@Configuration
@Data
public class ObjectStorageMinioProperties {

    /**
     * Bucket名称
     */
    private String bucket;

    /**
     * 域名
     */
    private String endpoint;

    /**
     * Key,新版本为username
     */
    private String accessKey;

    /**
     * 密钥,新版本为password
     */
    private String secretKey;

    /**
     * 分享链接过期时间,单位为秒,默认1分钟
     */
    private Integer shareTime = 60;

    /**
     * 检查配置
     */
    public void checkConfig() {
        if (StrUtil.isBlank(bucket)) {
            throw new ObjectStorageException("minio bucket not be null");
        }
        if (StrUtil.isBlank(endpoint)) {
            throw new ObjectStorageException("minio endpoint not be null");
        }
        if (StrUtil.isBlank(accessKey)) {
            throw new ObjectStorageException("minio accessKey not be null");
        }
        if (StrUtil.isBlank(secretKey)) {
            throw new ObjectStorageException("minio secretKey not be null");
        }
    }

}
