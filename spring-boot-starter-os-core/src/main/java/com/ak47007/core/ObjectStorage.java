package com.ak47007.core;

import cn.hutool.core.util.ZipUtil;
import com.ak47007.core.exception.ObjectStorageException;
import com.ak47007.core.support.ObjectStorageEntity;
import com.ak47007.core.vo.ObjectStorageVO;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author AK47007
 * @Date 2023/4/10 14:24
 * @Description: 文件操作接口
 */
public interface ObjectStorage {

    /**
     * 文件下载
     *
     * @param response 响应对象
     * @param id       附件id
     * @param isDown   true 下载 false 预览
     */
    void download(HttpServletResponse response, String id, Boolean isDown);

    /**
     * 批量删除文件
     *
     * @param ids 文件id集合
     */
    void deleteBatch(List<String> ids);

    /**
     * 文件分享，非必须实现
     *
     * @param fileId 文件ID
     * @return 文件链接
     */
    default String share(String fileId) {
        // TODO("Not yet implemented")
        return null;
    }

    /**
     * 文件列表
     *
     * @param moduleName 模块名称
     * @param dataId     数据ID
     * @param readOnly   是否只读
     * @return
     */
    List<ObjectStorageVO> list(String moduleName, String dataId, Boolean readOnly);

    /**
     * 获取文件流 无本地存储的文件必须实现此接口
     *
     * @param entity 文件信息
     */
    InputStream getFileStream(ObjectStorageEntity entity);


    /**
     * 获取文件信息
     *
     * @param fileId 文件ID
     */
    ObjectStorageEntity getFileInfo(String fileId);

    /**
     * 获取文件流
     *
     * @param fileId 文件ID
     */
    InputStream getFileStream(String fileId);

    /**
     * 文件下载合并为zip
     *
     * @param ids      附件id数组
     * @param response 响应对象
     */
    default void zipDownload(List<String> ids, HttpServletResponse response) {
        if (ids.isEmpty()) {
            throw new ObjectStorageException("file list is empty");
        }
        List<ObjectStorageEntity> objectStorages = ids.stream().distinct().map(it -> getFileInfo(it)).collect(Collectors.toList());
        List<String> fileNames = new ArrayList<>(objectStorages.size());
        List<InputStream> files = new ArrayList<>(objectStorages.size());
        objectStorages.forEach(it -> {
            fileNames.add(it.getFileName());
            files.add(getFileStream(it));
        });
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipUtil.zip(baos, fileNames.toArray(new String[fileNames.size()]), files.toArray(new InputStream[files.size()]));
        try {
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(System.nanoTime() + ".zip", "UTF-8"));
            response.setContentType("application/octet-stream");
            ServletOutputStream outputStream = response.getOutputStream();//通过response的输出流返回文件
            outputStream.write(baos.toByteArray());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ObjectStorageException("Files zip download failed");
        }
    }

    /**
     * 附件存储(最终方法)
     *
     * @param file            文件流
     * @param moduleName      业务模块
     * @param dataId          业务键
     * @param fileName        文件名
     * @param fileContentType 文件媒体类型
     * @param fileSize        文件大小(字节)
     * @param readOnly        附件是否只读 仅能预览 不能下载
     */
    ObjectStorageVO saveFile(InputStream file, String moduleName, String dataId, String fileName, String fileContentType, Long fileSize, Boolean readOnly);

    /**
     * 文件存储(File处理流程)
     *
     * @param srcFile    需要存储的文件
     * @param moduleName 业务模块
     * @param dataId     业务键
     * @param readOnly   附件是否只读 仅能预览 不能下载
     */
    default ObjectStorageVO saveFile(File srcFile, String moduleName, String dataId, Boolean readOnly) throws Exception {
        InputStream inputStream = new FileInputStream(srcFile);
        return saveFile(inputStream, moduleName, dataId, srcFile.getName(), new Tika().detect(srcFile), srcFile.length(), readOnly);
    }

    /**
     * 文件存储(MultipartFile处理流程)
     *
     * @param file       需要存储的文件
     * @param moduleName 业务模块
     * @param dataId     业务键
     * @param readOnly   附件是否只读 仅能预览 不能下载
     */
    default ObjectStorageVO saveFiles(MultipartFile file, String moduleName, String dataId, Boolean readOnly) throws IOException {
        InputStream inputStream = file.getInputStream();
        return saveFile(inputStream, moduleName, dataId, file.getOriginalFilename(), file.getContentType(), file.getSize(), readOnly);
    }

    /**
     * 绑定文件
     *
     * @param moduleName 模块名
     * @param id         文件ID
     * @param dataId     数据ID
     * @param readOnly   是否只读
     */
    default void bindDataId(String moduleName, String id, String dataId, Boolean readOnly) {
        bindDataId(moduleName, Arrays.asList(id), dataId, readOnly);
    }

    /**
     * 绑定文件
     *
     * @param moduleName 模块名称
     * @param ids        文件ID集合
     * @param dataId     数据ID
     * @param readOnly   是否只读
     */
    void bindDataId(String moduleName, List<String> ids, String dataId, Boolean readOnly);

    /**
     * 追加文件
     *
     * @param id     文件ID
     * @param dataId 数据ID
     */
    default void append(String id, String dataId) {
        append(Arrays.asList(id), dataId);
    }

    /**
     * 追加文件
     *
     * @param ids    文件ID集合
     * @param dataId 数据ID
     */
    void append(List<String> ids, String dataId);
}
