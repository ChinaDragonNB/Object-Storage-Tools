package com.ak47007.core.dao;

import com.ak47007.core.support.ObjectStorageEntity;

import java.util.List;

/**
 * @Author AK47007
 * @Date 2023/4/10 21:43
 * @Description: 数据库存储文件信息操作
 */
public interface ObjectStorageDAO {

    /**
     * 根据ID集合查询多个文件
     *
     * @param ids 文件ID集合
     */
    List<ObjectStorageEntity> selectByIds(List<String> ids);

    /**
     * 根据ID修改文件信息
     *
     * @param entity 文件信息，需要文件ID
     */
    void updateById(ObjectStorageEntity entity);

    /**
     * 查询文件列表
     *
     * @param moduleName     模块名称
     * @param dataId         数据ID
     * @param searchReadOnly 是否只读
     */
    List<ObjectStorageEntity> selectFileList(String moduleName, String dataId, Boolean searchReadOnly);

    /**
     * 根据文件ID查询文件信息
     *
     * @param fileId 文件ID
     */
    ObjectStorageEntity selectById(String fileId);

    /**
     * 插入文件数据
     *
     * @param objectStorage 文件信息
     */
    int insertFile(ObjectStorageEntity objectStorage);
}
