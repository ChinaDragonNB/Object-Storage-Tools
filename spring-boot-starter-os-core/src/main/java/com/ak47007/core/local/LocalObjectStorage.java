package com.ak47007.core.local;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.ak47007.core.autoconfig.ObjectStorageLocalProperties;
import com.ak47007.core.dao.ObjectStorageDAO;
import com.ak47007.core.exception.ObjectStorageException;
import com.ak47007.core.support.DefaultObjectStorage;
import com.ak47007.core.support.ObjectStorageEntity;
import com.ak47007.core.vo.ObjectStorageVO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @Author AK47007
 * @Date 2023/4/10 21:37
 * @Description: 本地存储文件实现，默认
 */
public class LocalObjectStorage extends DefaultObjectStorage {

    private final ObjectStorageDAO objectStorageDAO;

    private final ObjectStorageLocalProperties objectStorageLocalProperties;

    public LocalObjectStorage(ObjectStorageDAO dao, ObjectStorageLocalProperties properties) {
        super(dao);
        this.objectStorageDAO = dao;
        this.objectStorageLocalProperties = properties;
    }

    @Override
    public InputStream getFileStream(ObjectStorageEntity entity) {
        File file = Paths.get(objectStorageLocalProperties.getStoragePath(), entity.getFilePath()).toFile();
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ObjectStorageException(e.getMessage());
        }
    }

    @Override
    public ObjectStorageVO saveFile(InputStream file, String moduleName, String dataId, String originalFileName, String fileContentType, Long fileSize, Boolean readOnly) {
        if (StrUtil.isBlank(originalFileName)) {
            throw new ObjectStorageException("FileName not be null");
        }
        String[] temp = originalFileName.split("\\.");
        String dateDir = dateRuleDir();

        Path storagePath = Paths.get(objectStorageLocalProperties.getStoragePath(), moduleName, dateDir).toAbsolutePath();
        if (!storagePath.toFile().exists() && !storagePath.toFile().mkdirs()) {
            throw new ObjectStorageException(String.format("%s path create failed", storagePath.toAbsolutePath().toAbsolutePath()));
        }
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + temp[temp.length - 1];

        // 自动关闭流
        FileUtil.writeFromStream(file, Paths.get(storagePath.toString(), fileName).toFile());

        ObjectStorageEntity entity = new ObjectStorageEntity();
        entity.setReadOnly(readOnly);
        entity.setModuleName(moduleName);
        entity.setDataId(dataId);
        entity.setFileName(originalFileName);
        entity.setFileType(fileContentType);
        entity.setFilePath(String.format("/%s/%s/%s", moduleName, dateDir.replace("\\\\", "/"), fileName));
        entity.setFileSize(fileSize);
        objectStorageDAO.insertFile(entity);
        if (StrUtil.isNotBlank(entity.getId())) {
            return ObjectStorageVO.transform(entity);
        } else {
            throw new ObjectStorageException("File save failed!");
        }
    }
}
