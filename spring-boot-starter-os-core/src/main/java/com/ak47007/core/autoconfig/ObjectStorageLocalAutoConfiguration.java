package com.ak47007.core.autoconfig;

import com.ak47007.core.EnableObjectStorage;
import com.ak47007.core.ObjectStorage;
import com.ak47007.core.dao.ObjectStorageDAO;
import com.ak47007.core.local.LocalObjectStorage;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author AK47007
 * @Date 2023/4/10 22:38
 * @Description: 自动装配，同时注入属性配置与数据库操作
 */
@Configuration
@EnableConfigurationProperties(ObjectStorageLocalProperties.class)
@ConditionalOnProperty(prefix = "object-storage", name = "type", havingValue = "local")
@ConditionalOnBean(annotation = EnableObjectStorage.class)
@AllArgsConstructor
public class ObjectStorageLocalAutoConfiguration {

    private final ObjectStorageDAO objectStorageDAO;

    private final ObjectStorageLocalProperties objectStorageLocalProperties;

    @Bean
    @ConditionalOnMissingBean
    public ObjectStorage objectStorage() {
        objectStorageLocalProperties.checkConfig();
        return new LocalObjectStorage(objectStorageDAO, objectStorageLocalProperties);
    }

}
