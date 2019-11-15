package com.unifig.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class EntityMsg<T> {

    private Class<T> cls;

    private String collect;

    private String keyName;

    private Field keyField;

    private Map<Field, String> mapping;

    public EntityMsg() {
    }

    public EntityMsg(Class<T> cls) {
        this.cls = cls;
        if (!cls.isAnnotationPresent(Document.class)) {
            throw new RuntimeException("");
        }
        this.collect = cls.getAnnotation(Document.class).collection();
        this.mapping = new HashMap<>();
        for (Field field : cls.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Id.class)) {
                this.keyName = field.getName();
                this.keyField = field;
            } else {
                mapping.put(field, field.getName());
            }
        }
    }


    public Class<T> getCls() {
        return cls;
    }

    public void setCls(Class<T> cls) {
        this.cls = cls;
    }

    public String getCollect() {
        return collect;
    }

    public void setCollect(String collect) {
        this.collect = collect;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public Field getKeyField() {
        return keyField;
    }

    public void setKeyField(Field keyField) {
        this.keyField = keyField;
    }

    public Map<Field, String> getMapping() {
        return mapping;
    }

    public void setMapping(Map<Field, String> mapping) {
        this.mapping = mapping;
    }
}
