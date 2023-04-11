package com.ak47007.core.support;

import lombok.Data;

/**
 * @Author AK47007
 * @Date 2023/4/11 15:05
 * @Description: 文件操作实体类，数据库实体类可继承该类
 */
@Data
public class ObjectStorageEntity {

    /**
     * 主键
     */
    private String id;

    /**
     * 删除标记
     */
    private Boolean isDelete = Boolean.FALSE;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 资源服务器文件相对路径
     */
    private String filePath;

    /**
     * 文件大小 单位 字节
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件所属业务模块，作为文件父级目录
     */
    private String moduleName;

    /**
     * 与该文件关联的数据ID
     */
    private String dataId;

    /**
     * 是否只读
     */
    private Boolean readOnly;
}
