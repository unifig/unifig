package com.unifig.mall.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.unifig.context.Constants;
import com.unifig.mall.bean.model.CmsFile;
import com.unifig.mall.service.CmsFileService;
import com.unifig.mall.util.UploadUtils;
import com.unifig.result.ResultData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上传图片
 *

 * @date 2018-10-24
 */
@RestController
@Api(tags = "文件", description = "文件")
@RequestMapping("/cms/feil")
@ApiIgnore
public class CmsFileController {

    @Autowired
    private UploadUtils uploadUtils;

    @Autowired
    private CmsFileService cmsFileService;

    /**
     * 图片上传
     */
    // @PostMapping("/upload")
//    public String upload(MultipartFile file) {
//        //上传文件
//        String url = null;
//        try {
//            url = OSSFactory.build().upload(file);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return url;
//    }


    /**
     * 文件列表
     *
     * @param page
     * @param size
     * @return
     */
    @ApiOperation("文件列表")
    @RequestMapping("/listPage")
    public ResultData listPage(@RequestParam(required = false, defaultValue = "1") Integer page,
                               @RequestParam(required = false, defaultValue = "10") Integer size) {
        try {
            EntityWrapper<CmsFile> cmsFileEntityWrapper = new EntityWrapper<CmsFile>();
            cmsFileEntityWrapper.eq("enable", Constants.DEFAULT_VAULE_ONE);
            Page<CmsFile> cmsFilePage = cmsFileService.selectPage(new Page<CmsFile>(page,size), cmsFileEntityWrapper);
            int count = cmsFileService.selectCount(cmsFileEntityWrapper);
            return ResultData.result(true).setData(cmsFilePage.getRecords()).setCount(count);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @param file
     * @param request
     * @return ResultData
     * @throws
     * @Title: uploadMonofile
     * @Description: 单文件上传
     */
    @ApiOperation("单文件文件上传")
    @RequestMapping("/uploadMonofile")
    public String uploadMonofile(MultipartFile file, HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        try {
           // String url = uploadUtils.uploadPic(file, request);
            String   url = uploadUtils.uploadPic(file, 0,0,0);
            String Filename = file.getOriginalFilename();
            map.put("fileName", Filename);
            map.put("url", url);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @ApiOperation("多文件文件上传")
    @PostMapping("/uploadFiles")
    public List<Map<String, String>> uploadFiles(HttpServletRequest request, @RequestParam(name = "files", required = false) MultipartFile[] files) {

        List<Map<String, String>> list = new ArrayList<>();
        try {
            if (files != null && files.length > 0) {
                for (MultipartFile file : files) {
                    Map<String, String> map = new HashMap<>();
                    String url = uploadUtils.uploadPic(file, request);
                    String Filename = file.getOriginalFilename();
                    map.put("fileName", Filename);
                    map.put("url", url);
                    if (url == null && !url.equals("")) {
                        continue;
                    }
                    list.add(map);
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value = "/eitor")
    @ResponseBody
    public ResultData imgUpdate(HttpServletRequest request, MultipartFile file) {
        if (file.isEmpty()) {
            return ResultData.result(false);
        }
        try {
            String url = uploadUtils.uploadPic(file, request);
            return ResultData.result(true).setData("http://admin.unifigInfo.com/images/20190125/53f14b89-0310-4e1b-9597-43aa6b9e7086.jpg");
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultData.result(false);

    }

}
