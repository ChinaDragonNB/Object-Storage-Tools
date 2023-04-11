package com.ak47007.core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author AK47007
 * @Date 2023/4/10 22:04
 * @Description: 文件存储异常类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectStorageException extends RuntimeException {

    private String message;
    private Throwable cause;
    private Boolean printStack;

    public ObjectStorageException(String message) {
        this.message = message;
        this.printStack = true;
    }


}
