package com.ak47007.core.controller;

import com.ak47007.core.ObjectStorage;
import com.ak47007.core.exception.ObjectStorageException;
import com.ak47007.core.vo.ObjectStorageVO;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Author AK47007
 * @Date 2023/4/10 14:09
 * @Description: 文件操作的一些常用接口
 */
@RestController
@RequestMapping(value = "/file")
public class ObjectStorageController {

    private final ObjectStorage objectStorage;

    public ObjectStorageController(ObjectStorage objectStorage) {
        this.objectStorage = objectStorage;
    }


    /**
     * 文件上传
     *
     * @param file       文件
     * @param moduleName 模块名称
     * @param dataId     数据ID
     */
    @PostMapping("/upload")
    public HttpEntity<ObjectStorageVO> fileUpload(@RequestParam("file") MultipartFile file, @RequestParam("moduleName") String moduleName, @RequestParam(value = "dataId", required = false) String dataId) {
        try {
            ObjectStorageVO objectStorageVO = objectStorage.saveFiles(file, moduleName, dataId, false);
            return new HttpEntity<>(objectStorageVO);
        } catch (IOException e) {
            throw new ObjectStorageException(e.getMessage());
        }
    }

    /**
     * 文件列表
     *
     * @param moduleName 模块名称
     * @param dataId     数据ID
     * @return
     */
    @GetMapping("/list")
    public HttpEntity<List<ObjectStorageVO>> fileList(
            @RequestParam("moduleName") String moduleName,
            @RequestParam("dataId") String dataId,
            @RequestParam(value = "readOnly", required = false, defaultValue = "false") Boolean readOnly
    ) {
        List<ObjectStorageVO> list = objectStorage.list(moduleName, dataId, readOnly);
        return new HttpEntity<>(list);
    }

    /**
     * 批量删除
     */
    @PostMapping("/delete/batch")
    public HttpEntity<String> deleteBatch(@RequestBody List<String> ids) {
        objectStorage.deleteBatch(ids);
        return new HttpEntity<>("success");
    }

    /**
     * 文件预览
     *
     * @param response
     * @param id
     * @throws IOException
     */
    @GetMapping("/view/{id}")
    public void view(HttpServletResponse response, @PathVariable("id") String id) {
        objectStorage.download(response, id, false);
    }

    /**
     * 文件下载
     *
     * @param response
     * @param id
     * @throws IOException
     */
    @GetMapping("/download/{id}")
    public void download(HttpServletResponse response, @PathVariable("id") String id) {
        objectStorage.download(response, id, true);
    }

    /**
     * 文件下载合并为zip
     *
     * @param response
     * @throws IOException
     */
    @PostMapping("/download/zip")
    public void mergeDownload(@RequestBody List<String> ids, HttpServletResponse response) {
        objectStorage.zipDownload(ids, response);
    }

    /**
     * 文件外链
     *
     * @param id
     */
    @GetMapping("/share/{id}")
    public HttpEntity<String> share(@PathVariable("id") String id) {
        return new HttpEntity<>(objectStorage.share(id));
    }
}
