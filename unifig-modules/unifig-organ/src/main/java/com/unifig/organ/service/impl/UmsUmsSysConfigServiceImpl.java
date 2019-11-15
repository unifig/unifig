package com.unifig.organ.service.impl;

import com.alibaba.fastjson.JSON;
import com.unifig.organ.dao.UmsSysConfigDao;
import com.unifig.organ.model.SysConfigEntity;
import com.unifig.organ.service.UmsSysConfigService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("sysConfigService")
public class UmsUmsSysConfigServiceImpl implements UmsSysConfigService {
    @Autowired
    private UmsSysConfigDao umsSysConfigDao;

    @Override
    public void save(SysConfigEntity config) {
        umsSysConfigDao.save(config);
    }

    @Override
    public void update(SysConfigEntity config) {
        umsSysConfigDao.update(config);
    }

    @Override
    public void updateValueByKey(String key, String value) {
        umsSysConfigDao.updateValueByKey(key, value);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        umsSysConfigDao.deleteBatch(ids);
    }

    @Override
    public List<SysConfigEntity> queryList(Map<String, Object> map) {
        return umsSysConfigDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return umsSysConfigDao.queryTotal(map);
    }

    @Override
    public SysConfigEntity queryObject(Long id) {
        return umsSysConfigDao.queryObject(id);
    }

    @Override
    public String getValue(String key, String defaultValue) {
        String value = umsSysConfigDao.queryByKey(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return value;
    }

    @Override
    public <T> T getConfigObject(String key, Class<T> clazz) {
        String value = getValue(key, null);
        if (StringUtils.isNotBlank(value)) {
            return JSON.parseObject(value, clazz);
        }

        try {
            return clazz.newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
