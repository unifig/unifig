package com.unifig.organ.service.impl;

import com.unifig.organ.dao.UmsSysOssDao;
import com.unifig.organ.model.SysOssEntity;
import com.unifig.organ.service.UmsSysOssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("sysOssService")
public class UmsUmsSysOssServiceImpl implements UmsSysOssService {
    @Autowired
    private UmsSysOssDao umsSysOssDao;

    @Override
    public SysOssEntity queryObject(Long id) {
        return umsSysOssDao.queryObject(id);
    }

    @Override
    public List<SysOssEntity> queryList(Map<String, Object> map) {
        return umsSysOssDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return umsSysOssDao.queryTotal(map);
    }

    @Override
    public void save(SysOssEntity sysOss) {
        umsSysOssDao.save(sysOss);
    }

    @Override
    public void update(SysOssEntity sysOss) {
        umsSysOssDao.update(sysOss);
    }

    @Override
    public void delete(Long id) {
        umsSysOssDao.delete(id);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        umsSysOssDao.deleteBatch(ids);
    }

}
