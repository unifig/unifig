package com.unifig.organ.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.unifig.organ.dao.UmsSysRegionDao;
import com.unifig.organ.model.SysRegionEntity;
import com.unifig.organ.service.UmsSysRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service实现类
 *

 * @date 2018-10-24
 */
@Service("regionService")
public class UmsUmsSysRegionServiceImpl extends ServiceImpl<UmsSysRegionDao, SysRegionEntity> implements UmsSysRegionService {
    @Autowired
    private UmsSysRegionDao umsSysRegionDao;

    @Override
    public SysRegionEntity queryObject(Integer id) {
        return umsSysRegionDao.queryObject(id);
    }

    @Override
    public List<SysRegionEntity> queryList(Map<String, Object> map) {
        return umsSysRegionDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return umsSysRegionDao.queryTotal(map);
    }

    @Override
    public int save(SysRegionEntity region) {
        return umsSysRegionDao.save(region);
    }

    @Override
    public int update(SysRegionEntity region) {
        return umsSysRegionDao.updateById(region);
    }

    @Override
    public int delete(Integer id) {
        return umsSysRegionDao.deleteById(id);
    }

    @Override
    public int deleteBatch(Integer[] ids) {
        return umsSysRegionDao.deleteBatch(ids);
    }

    @Override
    public List<SysRegionEntity> selectByType(int type) {
        EntityWrapper<SysRegionEntity> sysRegion = new EntityWrapper<SysRegionEntity>();
        sysRegion.eq("type", type);
        List<SysRegionEntity> sysRegionEntities = selectList(sysRegion);
        return sysRegionEntities;
    }
}
