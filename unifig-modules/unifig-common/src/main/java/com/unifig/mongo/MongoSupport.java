package com.unifig.mongo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

/**
 * mongo 接口
 *
 *
 */
public abstract class MongoSupport<T> implements MongoBase<T> {

    @Resource
    protected MongoTemplate mongoTemplate;

    protected EntityMsg<T> entityMsg;

    public final static Criteria EMPTY_CRITERIA = new Criteria();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public MongoSupport() {
        this.entityMsg = new EntityMsg((Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    public MongoSupport(MongoTemplate mongoTemplate) {
        this.entityMsg = new EntityMsg((Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void insert(T model) {
        mongoTemplate.insert(model);
    }

    @Override
    public void insert(Collection<T> list) {
        mongoTemplate.insertAll(list);
    }

    @Override
    public void save(T model) {
        mongoTemplate.save(model);
    }

    @Override
    public void delete(Serializable key) {
        mongoTemplate.remove(new Query(Criteria.where(entityMsg.getKeyName()).is(key)), entityMsg.getCls());
    }

    @Override
    public void update(T model) {
        try {
            Query query = new Query(Criteria.where(entityMsg.getKeyName()).is(entityMsg.getKeyField().get(model)));
            Update update = new Update();
            for (Entry<Field, String> entry : this.entityMsg.getMapping().entrySet()) {
                update.set(entry.getValue(), entry.getKey().get(model));
            }
            mongoTemplate.updateFirst(query, update, entityMsg.getCls());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateMulti(Criteria criteria, Update update) {
        mongoTemplate.updateMulti(new Query(criteria), update, entityMsg.getCls());
    }

    @Override
    public boolean updateFirst(Criteria criteria, Update update) {
        return mongoTemplate.updateFirst(new Query(criteria), update, entityMsg.getCls()).getN() == 1;
    }

    @Override
    public T select(Serializable key) {
        return mongoTemplate.findById(key, entityMsg.getCls());
    }

    @Override
    public T findOne(Criteria criteria) {
        return mongoTemplate.findOne(new Query(criteria), entityMsg.getCls());
    }

    @Override
    public JSONObject findOne(Criteria criteria, Class cls) {
        return JSONObject.parseObject(JSON.toJSONString(mongoTemplate.findOne(new Query(criteria), cls, entityMsg.getCollect())));
    }

    @Override
    public T findOne(Criteria criteria, Sort sort) {
        return mongoTemplate.findOne(new Query(criteria).with(sort), entityMsg.getCls());
    }

    @Override
    public List<T> list() {
        return mongoTemplate.findAll(entityMsg.getCls());
    }

    @Override
    public List<T> list(Criteria criteria) {
        return mongoTemplate.find(new Query(criteria), entityMsg.getCls());
    }

    @Override
    public List<T> list(Criteria criteria, Sort sort) {
        return mongoTemplate.find(new Query(criteria).with(sort), entityMsg.getCls());
    }

    @Override
    public List<T> list(Integer start, Integer limit) {
        return mongoTemplate.find(new Query().skip(start).limit(limit), entityMsg.getCls());
    }

    @Override
    public List<T> list(Integer limit) {
        return mongoTemplate.find(new Query().limit(limit), entityMsg.getCls());
    }

    @Override
    public List<T> list(Sort sort) {
        return mongoTemplate.find(new Query().with(sort), entityMsg.getCls());
    }

    @Override
    public List<T> list(Sort sort, Integer start, Integer limit) {
        return mongoTemplate.find(new Query().with(sort).skip(start).limit(limit), entityMsg.getCls());
    }

    @Override
    public List<T> list(Criteria criteria, Integer start, Integer limit) {
        return mongoTemplate.find(new Query(criteria).skip(start).limit(limit), entityMsg.getCls());
    }

    @Override
    public List<T> list(Criteria criteria, Sort sort, Integer start, Integer limit) {
        return mongoTemplate.find(new Query(criteria).with(sort).skip(start).limit(limit), entityMsg.getCls());
    }

    @Override
    public JSONArray list(Criteria criteria, Sort sort, Integer start, Integer limit, Class cls) {
        return JSONArray.parseArray(JSON.toJSONString(mongoTemplate.find(new Query(criteria).with(sort).skip(start).limit(limit), cls, entityMsg.getCollect())));
    }

    @Override
    public long count() {
        return mongoTemplate.count(new Query(), entityMsg.getCls());
    }

    @Override
    public long count(Criteria criteria) {
        return mongoTemplate.count(new Query(criteria), entityMsg.getCls());
    }

    @Override
    public T findAndModify(Criteria criteria, Update update) {
        return mongoTemplate.findAndModify(new Query(criteria), update, entityMsg.getCls());
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}
