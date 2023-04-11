package com.ak47007.core.support;

import cn.hutool.core.io.IoUtil;
import com.ak47007.core.ObjectStorage;
import com.ak47007.core.dao.ObjectStorageDAO;
import com.ak47007.core.exception.ObjectStorageException;
import com.ak47007.core.vo.ObjectStorageVO;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author AK47007
 * @Date 2023/4/10 21:48
 * @Description: 默认实现的一些文件基本操作
 */

public abstract class DefaultObjectStorage implements ObjectStorage {

    private ObjectStorageDAO objectStorageDAO;

    public DefaultObjectStorage(ObjectStorageDAO dao) {
        this.objectStorageDAO = dao;
    }

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY/MM/dd");

    /**
     * 生成时间格式的目录
     * 格式: 2023/01/01
     */
    public String dateRuleDir() {
        return dateTimeFormatter.format(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
    }

    @Override
    public void append(List<String> ids, String dataId) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        List<ObjectStorageEntity> objectStorages = objectStorageDAO.selectByIds(ids);
        for (ObjectStorageEntity objectStorage : objectStorages) {
            objectStorage.setDataId(dataId);
            objectStorageDAO.updateById(objectStorage);
        }
    }

    @Override
    public void bindDataId(String module, List<String> ids, String dataId, Boolean readOnly) {
        List<ObjectStorageEntity> oldObjectStorages = selectFileList(module, dataId, readOnly);
        for (ObjectStorageEntity oldObjectStorage : oldObjectStorages) {
            oldObjectStorage.setIsDelete(Boolean.TRUE);
            objectStorageDAO.updateById(oldObjectStorage);
        }
        if (!ids.isEmpty()) {
            List<ObjectStorageEntity> objectStorages = objectStorageDAO.selectByIds(ids);
            if (!objectStorages.isEmpty() && objectStorages.size() != ids.size()) {
                throw new ObjectStorageException("Failed to find file, expected " + ids.size() + ", actual " + objectStorages.size());
            }
            for (ObjectStorageEntity objectStorage : objectStorages) {
                objectStorage.setDataId(dataId);
                objectStorage.setIsDelete(Boolean.FALSE);
                objectStorageDAO.updateById(objectStorage);
            }
        }
    }


    /**
     * 查询文件列表
     *
     * @param moduleName     模块名称
     * @param dataId         数据ID
     * @param searchReadOnly 查询是否为只读的
     */
    public List<ObjectStorageEntity> selectFileList(String moduleName, String dataId, Boolean searchReadOnly) {
        return objectStorageDAO.selectFileList(moduleName, dataId, searchReadOnly);
    }

    @Override
    public void deleteBatch(List<String> ids) {
        List<ObjectStorageEntity> objectStorages = objectStorageDAO.selectByIds(ids);
        for (ObjectStorageEntity objectStorage : objectStorages) {
            objectStorage.setIsDelete(Boolean.TRUE);
            objectStorageDAO.updateById(objectStorage);
        }
    }

    @Override
    public ObjectStorageEntity getFileInfo(String fileId) {
        ObjectStorageEntity entity = objectStorageDAO.selectById(fileId);
        if (entity == null) {
            throw new ObjectStorageException("File not found");
        }
        return entity;
    }

    /**
     * 获取实际文件
     */
    @Override
    public InputStream getFileStream(String fileId) {
        return getFileStream(getFileInfo(fileId));
    }

    @Override
    public void download(HttpServletResponse response, String id, Boolean isDown) {
        ObjectStorageEntity objectStorage = getFileInfo(id);
        InputStream fileInputStream = getFileStream(objectStorage);

        if (isDown) {
            if (objectStorage.getReadOnly()) {
                throw new ObjectStorageException("File is readOnly, forbid download");
            }
            response.setContentType(objectStorage.getFileType());
            try {
                response.setHeader("Content-Disposition", "fileStorage;filename=" + URLEncoder.encode(objectStorage.getFileName(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new ObjectStorageException("File not found");
            }
        }
        try {
            IoUtil.copy(fileInputStream, response.getOutputStream());
        } catch (IOException e) {
            throw new ObjectStorageException("File not found");
        }
    }

    @Override
    public List<ObjectStorageVO> list(String moduleName, String dataId, Boolean searchReadOnly) {
        List<ObjectStorageEntity> objectStorages = objectStorageDAO.selectFileList(moduleName, dataId, searchReadOnly);
        return objectStorages.stream().map(it -> {
            ObjectStorageVO vo = new ObjectStorageVO();
            BeanUtils.copyProperties(it, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
