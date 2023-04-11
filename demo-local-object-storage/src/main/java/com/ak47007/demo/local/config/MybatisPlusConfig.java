package com.ak47007.demo.local.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * @Author AK47007
 * @Date 2023/4/10 23:03
 * @Description: MybatisPlus 配置
 */
@Configuration
public class MybatisPlusConfig implements MetaObjectHandler {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页配置
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 乐观锁 Version配置
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 防全表修改/删除配置
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        String userName = "ak47007";
        setFieldValByName("version", 0, metaObject);
        setFieldValByName("createdTime", now, metaObject);
        setFieldValByName("createdUser", userName, metaObject);
        setFieldValByName("modifiedTime", now, metaObject);
        setFieldValByName("modifiedUser", userName, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        setFieldValByName("modifiedTime", LocalDateTime.now(), metaObject);
        String userName = "ak47007";
        setFieldValByName("modifiedUser", userName, metaObject);
    }
}
