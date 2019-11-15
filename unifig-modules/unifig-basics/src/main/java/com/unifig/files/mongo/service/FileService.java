package com.unifig.files.mongo.service;

import java.util.List;

import com.unifig.files.mongo.domain.File;


/**
 * File 服务接口.
 */
public interface FileService {
    /**
     * 保存文件
     *
     * @return
     */
    File saveFile(File file);

    /**
     * 删除文件
     *
     * @return
     */
    void removeFile(String id);

    /**
     * 根据id获取文件
     *
     * @return
     */
    File getFileById(String id);

    /**
     * 分页查询，按上传时间降序
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<File> listFilesByPage(int pageIndex, int pageSize);

    int countFiles();
}
