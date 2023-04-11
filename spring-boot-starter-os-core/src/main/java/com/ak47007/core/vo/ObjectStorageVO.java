package com.ak47007.core.vo;

import com.ak47007.core.support.ObjectStorageEntity;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @Author AK47007
 * @Date 2023/4/10 20:59
 * @Description:
 */
@Data
public class ObjectStorageVO {

    private String id;

    private Integer version = 0;

    /**
     * 业务模块名称，文件父级目录
     */
    private String moduleName;

    /**
     * 新文件名
     */
    private String fileName;

    /**
     * 资源服务器文件相对路径
     */
    private String filePath;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小 单位 字节
     */
    private Long fileSize;

    /**
     * 是否只读
     * 只读文件不可下载 仅可通过图片在线预览
     */
    private Boolean readOnly = false;


    public static ObjectStorageVO transform(ObjectStorageEntity entity) {
        ObjectStorageVO vo = new ObjectStorageVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
