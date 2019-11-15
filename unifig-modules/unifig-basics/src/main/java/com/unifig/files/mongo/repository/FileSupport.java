package com.unifig.files.mongo.repository;

import com.mongodb.BasicDBObject;
import com.unifig.files.mongo.domain.File;
import com.unifig.mongo.MongoSupport;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FileSupport extends MongoSupport<File> {

    public File findById(String id) {
        Criteria criteria = Criteria.where("id").is(id);
        return findOne(criteria);
    }


    public List<File> listPage(int page, int size) {
        BasicDBObject fieldsObject = new BasicDBObject();
        fieldsObject.put("content", 0);
        BasicDBObject fieldsObjectNew = new BasicDBObject();
        BasicQuery query = new BasicQuery(fieldsObjectNew,fieldsObject);
        List<File> filesList = mongoTemplate.find(query.skip((page - 1) * size).limit(size), entityMsg.getCls());
        return filesList;
    }
}
