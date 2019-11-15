package com.unifig.files.mongo.controller;


import com.unifig.files.mongo.domain.File;
import com.unifig.files.mongo.service.FileService;
import com.unifig.files.mongo.service.GridFSFileService;
import com.unifig.files.utils.ImageSizeUtil;
import com.unifig.files.utils.MD5Util;
import com.unifig.result.ResultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "*", maxAge = 3600)  // 允许所有域名访问
@Controller
@RequestMapping("/file")
public class FileController {

    private final static Logger logger = LoggerFactory.getLogger(FileController.class);


    @Autowired
    private FileService fileService;

    @Autowired
    private GridFSFileService gridFSFileService;

    @Value("${db.name}")
    private String dbName;

    @Value("${db.file.name.image}")
    private String fileNameImage;

    @Value("${db.file.name.html}")
    private String fileNameHtml;

    @Value("${db.file.name}")
    private String fileName;

    /**
     * 分页查询文件
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/fileList")
    @ResponseBody
    public ResultData listFilesByPage(Integer pageNum, Integer pageSize) {
        if (null == pageNum) {
            pageNum = 1;
        }
        if (null == pageSize) {
            pageSize = 10;
        }
        List<File> files = fileService.listFilesByPage(pageNum, pageSize);
        int count = fileService.countFiles();
        return ResultData.result(true).setData(files).setCount(count);
    }

    /**
     * 下载文件
     *
     * @param id
     * @return
     */
    @GetMapping("/download/{id}")
    @ResponseBody
    public ResponseEntity serveFile(@PathVariable String id) {

        // File file = fileService.getFileById(id);
        File file = gridFSFileService.getFileById(id, dbName, fileName);
        if (file == null) {
            file = fileService.getFileById(id);
        }
        if (file != null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + file.getName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .header(HttpHeaders.CONTENT_LENGTH, file.getSize() + "").header("Connection", "close")
                    .body(file.getContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("文件不存在");
        }

    }

    /**
     * 在线显示文件(图片和txt可直接预览)
     *
     * @param id
     * @return
     */
    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseEntity serveFileOnline(@PathVariable String id, String type) {

        File file = gridFSFileService.getFileById(id, dbName, fileName);
        if (file == null) {
            file = fileService.getFileById(id);
        }
        if (file != null) {
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "fileName=\"" + file.getName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                    .header(HttpHeaders.CONTENT_LENGTH, file.getSize() + "").header("Connection", "close")
                    .body(file.getContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("文件不存在");
        }

    }

    /**
     * 在线显示图片
     *
     * @param id
     * @return
     */
    @GetMapping("/view/image/{id}")
    @ResponseBody
    public ResponseEntity serveFileImage(@PathVariable String id) {
        File file = gridFSFileService.getFileById(id, dbName, fileNameImage);
        if (file == null) {
            file = fileService.getFileById(id);
        }
        if (file != null) {
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "fileName=\"" + file.getName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                    .header(HttpHeaders.CONTENT_LENGTH, file.getSize() + "").header("Connection", "close")
                    .body(file.getContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("文件不存在");
        }

    }

    /**
     * 在线显示网页
     *
     * @param id
     * @return
     */
    @GetMapping("/view/html/{id}")
    @ResponseBody
    public ResponseEntity serveFileHtml(@PathVariable String id) {
        File file = gridFSFileService.getFileById(id, dbName, fileNameHtml);
        if (file == null) {
            file = fileService.getFileById(id);
        }
        if (file != null) {
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "fileName=\"" + file.getName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                    .header(HttpHeaders.CONTENT_LENGTH, file.getSize() + "").header("Connection", "close")
                    .body(file.getContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("文件不存在");
        }

    }


    /**
     * 上传接口
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        File returnFile = null;
        try {
            File f = new File(file.getOriginalFilename(), file.getContentType(), file.getSize(), file.getBytes());
            logger.info("FileController --- > handleFileUpload originalFilename:{},contentType:{},size:{},", file.getOriginalFilename(), file.getContentType(), file.getSize());
            f.setMd5(MD5Util.getMD5(file.getInputStream()));
            returnFile = fileService.saveFile(f);
            String path = returnFile.getId();
            return ResponseEntity.status(HttpStatus.OK).body(path);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }

    }

    /**
     * 上传接口 压缩
     *
     * @param file
     * @return
     */
    @PostMapping("/upload/image")
    @ResponseBody
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            file = ImageSizeUtil.byte2Base64StringFun(file);

            return handleFileUpload(file);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }

    }

    /**
     * 删除文件
     *
     * @param id
     * @return
     */
    @GetMapping("/delete")
    @ResponseBody
    public Map<String, Object> deleteFile(String id) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            fileService.removeFile(id);
            map.put("code", "200");
            map.put("msg", "删除文件成功");
            return map;
        } catch (Exception e) {
            map.put("code", "500");
            map.put("msg", "删除文件失败");
            return map;
        }
    }

}
