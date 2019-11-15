package com.unifig.organ.service.impl;

import com.unifig.organ.constant.RedisConstants;
import com.unifig.organ.dao.UmsSysMacroDao;
import com.unifig.organ.service.UmsSysMacroService;
import com.unifig.organ.model.SysMacroEntity;
import com.unifig.utils.CacheRedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用字典表Service实现类
 *

 * @date 2018-10-24
 */
@Service("sysMacroService")
public class UmsUmsSysMacroServiceImpl implements UmsSysMacroService {
    @Autowired
    private UmsSysMacroDao umsSysMacroDao;
    @Autowired
    private CacheRedisUtils cacheRedisUtils;

    @Override
    public SysMacroEntity queryObject(Long macroId) {
        return umsSysMacroDao.queryObject(macroId);
    }

    @Override
    public List<SysMacroEntity> queryList(Map<String, Object> map) {
        return umsSysMacroDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return umsSysMacroDao.queryTotal(map);
    }

    @Override
    public int save(SysMacroEntity sysMacro) {
        sysMacro.setGmtCreate(new Date());
        umsSysMacroDao.save(sysMacro);
        cacheRedisUtils.set(RedisConstants.unifig_SYS + "macroList", queryList(new HashMap<>()));
        return 1;
    }

    @Override
    public int update(SysMacroEntity sysMacro) {
        sysMacro.setGmtModified(new Date());
        umsSysMacroDao.update(sysMacro);
        cacheRedisUtils.set(RedisConstants.unifig_SYS + "macroList", queryList(new HashMap<>()));
        return 1;
    }

    @Override
    public int delete(Long macroId) {
        umsSysMacroDao.delete(macroId);
        cacheRedisUtils.set(RedisConstants.unifig_SYS + "macroList", queryList(new HashMap<>()));
        return 1;
    }

    @Override
    public int deleteBatch(Long[] macroIds) {
        umsSysMacroDao.deleteBatch(macroIds);
        cacheRedisUtils.set(RedisConstants.unifig_SYS + "macroList", queryList(new HashMap<>()));
        return 1;
    }

    @Override
    public List<SysMacroEntity> queryMacrosByValue(String value) {
        return umsSysMacroDao.queryMacrosByValue(value);
    }
}
