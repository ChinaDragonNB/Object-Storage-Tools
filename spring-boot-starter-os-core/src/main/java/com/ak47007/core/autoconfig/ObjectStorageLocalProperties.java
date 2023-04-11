package com.ak47007.core.autoconfig;

import cn.hutool.core.util.StrUtil;
import com.ak47007.core.exception.ObjectStorageException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author AK47007
 * @Date 2023/4/10 22:21
 * @Description: 本地文件存储属性配置
 */
@ConfigurationProperties(prefix = "object-storage.local")
@Configuration
@Data
public class ObjectStorageLocalProperties {

    /**
     * 本地存储路径
     */
    private String storagePath;

    public void checkConfig() {
        if (StrUtil.isBlank(storagePath)) {
            throw new ObjectStorageException("Please check storage path");
        }
    }

}
