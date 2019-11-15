package com.unifig.files.mongo.service;

import com.unifig.files.mongo.domain.File;
import com.unifig.files.mongo.repository.FileSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FileServiceImpl implements FileService {


    @Autowired
    public FileSupport fileSupport;


    @Override
    public File saveFile(File file) {
         fileSupport.save(file);
        return file;
    }

    @Override
    public void removeFile(String id) {

        fileSupport.delete(id);
    }

    @Override
    public File getFileById(String id) {
        return fileSupport.findById(id);
    }

    @Override
    public List<File> listFilesByPage(int pageIndex, int pageSize) {
        List<File> files = fileSupport.listPage(pageIndex, pageSize);
        return files;
    }

    @Override
    public int countFiles() {
        long count = fileSupport.count();
        return (int)count;
    }
}
