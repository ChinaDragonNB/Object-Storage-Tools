package com.ak47007.minio.support;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.ak47007.core.dao.ObjectStorageDAO;
import com.ak47007.core.exception.ObjectStorageException;
import com.ak47007.core.support.DefaultObjectStorage;
import com.ak47007.core.support.ObjectStorageEntity;
import com.ak47007.core.vo.ObjectStorageVO;
import com.ak47007.minio.autoconfig.ObjectStorageMinioProperties;
import io.minio.*;
import io.minio.http.Method;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * @Author AK47007
 * @Date 2023/4/12 22:31
 * @Description:
 */
public class MinioObjectStorage extends DefaultObjectStorage {

    private final ObjectStorageDAO objectStorageDAO;

    private final ObjectStorageMinioProperties objectStorageMinioProperties;

    private final MinioClient minioClient;

    public MinioObjectStorage(ObjectStorageDAO dao, ObjectStorageMinioProperties properties, MinioClient client) {
        super(dao);
        this.objectStorageDAO = dao;
        this.objectStorageMinioProperties = properties;
        this.minioClient = client;
    }

    @Override
    public String share(String attachmentId) {
        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(objectStorageMinioProperties.getBucket())
                .object(getFileInfo(attachmentId).getFilePath())
                .expiry(objectStorageMinioProperties.getShareTime(), TimeUnit.SECONDS).build();
        try {
            return minioClient.getPresignedObjectUrl(args);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ObjectStorageException("File not exist");
        }
    }


    @Override
    public InputStream getFileStream(ObjectStorageEntity entity) {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(objectStorageMinioProperties.getBucket()).object(entity.getFilePath()).build());
        } catch (Exception e) {
            throw new ObjectStorageException("File not found");
        }
    }

    @Override
    public ObjectStorageVO saveFile(InputStream file, String moduleName, String dataId, String fileName, String fileContentType, Long fileSize, Boolean readOnly) {
        FileNameUtil.extName(fileName);
        String suffix = RandomUtil.randomString("abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ", 5);
        String mainName = FileNameUtil.mainName(fileName);
        String extName = FileNameUtil.extName(fileName);
        String dateDir = dateRuleDir();
        String newFileName = mainName + "-" + suffix + "." + extName;
        // 使用minio时, 统一使用/作为目录分隔符
        String storagePath = moduleName + "/" + dateDir + "/" + newFileName;
        try {
            // 上传文件到Minio
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(objectStorageMinioProperties.getBucket())
                    .object(storagePath)
                    .stream(file, file.available(), -1)
                    .contentType(fileContentType).build();
            minioClient.putObject(args);

            ObjectStorageEntity entity = new ObjectStorageEntity();

            entity.setModuleName(moduleName);
            entity.setReadOnly(true);
            entity.setDataId(dataId);
            entity.setFileName(newFileName);
            entity.setFileType(fileContentType);
            entity.setFilePath(storagePath);
            entity.setFileSize(fileSize);
            objectStorageDAO.saveFileInfo(entity);
            if (StrUtil.isNotBlank(entity.getId())) {
                return ObjectStorageVO.transform(entity);
            } else {
                throw new ObjectStorageException("File save failed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ObjectStorageException("File save failed!");
        }
    }
}
