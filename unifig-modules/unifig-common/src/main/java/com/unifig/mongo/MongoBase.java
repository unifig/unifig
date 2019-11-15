package com.unifig.mongo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * mongo 公共接口
 *
 *
 */
public interface MongoBase<T> {

    void insert(T model);

    void insert(Collection<T> list);

    void save(T model);

    void delete(Serializable key);

    void update(T model);

    /**
     * 更新查询出的所有数据
     *
     * @param criteria
     * @param update
     */
    void updateMulti(Criteria criteria, Update update);

    /**
     * 只更新查询出的第一条数据
     *
     * @param criteria
     * @param update
     * @return
     */
    boolean updateFirst(Criteria criteria, Update update);

    T select(Serializable key);

    T findOne(Criteria criteria);

    JSONObject findOne(Criteria criteria, Class cls);

    T findOne(Criteria criteria, Sort sort);

    List<T> list();

    List<T> list(Criteria criteria);

    List<T> list(Criteria criteria, Sort sort);

    List<T> list(Integer limit);

    List<T> list(Integer start, Integer limit);

    List<T> list(Sort sort);

    List<T> list(Sort sort, Integer start, Integer limit);

    List<T> list(Criteria criteria, Integer start, Integer limit);

    JSONArray list(Criteria criteria, Sort sort, Integer start, Integer limit, Class cls);

    long count();

    long count(Criteria criteria);

    T findAndModify(Criteria criteria, Update update);

    List<T> list(Criteria criteria, Sort sort, Integer start, Integer limit);

}
