package com.ak47007.demo.local.mapper;

import cn.hutool.core.util.StrUtil;
import com.ak47007.core.dao.ObjectStorageDAO;
import com.ak47007.core.support.ObjectStorageEntity;
import com.ak47007.demo.local.model.SysObjectStorage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author AK47007
 * @Date 2023/4/10 23:09
 * @Description: 数据库操作实现（必需）
 */
@Service
public class ObjectStorageDaoImpl implements ObjectStorageDAO {

    @Resource
    private SysStorageObjectMapper sysStorageObjectMapper;

    private List<ObjectStorageEntity> transformList(List<SysObjectStorage> sysObjectStorages) {
        return sysObjectStorages.stream().map(it -> transformObject(it)).collect(Collectors.toList());
    }

    private ObjectStorageEntity transformObject(SysObjectStorage sysObjectStorage) {
        ObjectStorageEntity objectStorage = new ObjectStorageEntity();
        BeanUtils.copyProperties(sysObjectStorage, objectStorage);
        return objectStorage;
    }

    @Override
    public List<ObjectStorageEntity> selectByIds(List<String> ids) {
        return transformList(sysStorageObjectMapper.selectBatchIds(ids));

    }

    @Override
    @Transactional
    public void updateById(ObjectStorageEntity objectStorage) {
        SysObjectStorage sysObjectStorage = new SysObjectStorage();
        sysObjectStorage.setId(objectStorage.getId());
        sysStorageObjectMapper.deleteById(sysObjectStorage.getId());
    }

    @Override
    public List<ObjectStorageEntity> selectFileList(String moduleName, String dataId, Boolean searchReadOnly) {
        LambdaQueryWrapper<SysObjectStorage> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysObjectStorage::getModuleName, moduleName);
        if (StrUtil.isNotBlank(dataId)) {
            wrapper.eq(SysObjectStorage::getDataId, dataId);
        }
        return transformList(sysStorageObjectMapper.selectList(wrapper));
    }

    @Override
    public ObjectStorageEntity selectById(String fileId) {
        return transformObject(sysStorageObjectMapper.selectById(fileId));
    }

    @Override
    @Transactional
    public int insertFile(ObjectStorageEntity objectStorage) {
        SysObjectStorage sysObjectStorage = new SysObjectStorage();
        BeanUtils.copyProperties(objectStorage, sysObjectStorage);
        int i = sysStorageObjectMapper.insert(sysObjectStorage);
        if (i > 0) {
            objectStorage.setId(sysObjectStorage.getId());
        }
        return i;
    }
}
