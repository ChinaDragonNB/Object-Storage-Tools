package com.ak47007.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author AK47007
 * @Date 2023/4/11 17:01
 * @Description: 是否启用文件存储功能注解，需在Application类上加上该注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableObjectStorage {
}
