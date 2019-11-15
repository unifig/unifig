package com.unifig.mall.util;

import com.alibaba.fastjson.JSONObject;
import com.unifig.context.Constants;
import com.unifig.mall.bean.model.CmsFile;
import com.unifig.mall.service.CmsFileService;
import com.unifig.utils.SpringContextUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//import org.springframework.web.multipart.MultipartFile;
@Component
//@PropertySource("classpath:application.properties")
public class UploadUtils {

    private String realPath="E:\\";

    @Value("${url.path}")
    private String backPath;
    //String realPath = "/usr/static/images/";


//	@Autowired
//	private Environment env;

    public String uploadPic(MultipartFile file, HttpServletRequest request) throws IllegalStateException, IOException {
        if (null != file) {
            String[] split = file.getOriginalFilename().split("\\.");
            String ext = split[split.length - 1];
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String filename = UUID.randomUUID().toString() + "." + ext;
            String path = simpleDateFormat.format(new Date()) + "/";
            String url = realPath + path + filename;
            //此图片保存到指定位置
            File dir = new File(realPath + path);
            if (!dir.exists() && !dir.isDirectory()) {
                dir.mkdirs();
            }
            file.transferTo(new File(url));
            CmsFileService cmsFileService = SpringContextUtils.getBean(CmsFileService.class);
            CmsFile cmsFile = new CmsFile();
            cmsFile.setAbsolutePath(path + filename);
            cmsFile.setUrl(backPath + path + filename);
            cmsFile.setUploadTime(new Date());
            cmsFile.setName(filename);
            cmsFile.setSuffix(ext);
            cmsFile.setEnable(Constants.DEFAULT_VAULE_ONE);
            cmsFileService.insert(cmsFile);
            return backPath + path + filename;
        } else {
            return null;
        }
    }






    /**
     * @param imageFile  图片文件
     * @return
     * @Description:保存图片并且生成缩略图
     */
    public String uploadPic(MultipartFile imageFile,int width, int height, double scale) {



        String uuid = UUID.randomUUID().toString();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String fileDirectory = simpleDateFormat.format(new Date()) + "/";


        //拼接后台文件名称
        String[] split = imageFile.getOriginalFilename().split("\\.");
        String ext = split[split.length - 1];
        String pathName = fileDirectory + File.separator + uuid + "."
                + ext;
        //构建保存文件路劲
        //2016-5-6 yangkang 修改上传路径为服务器上
        //获取服务器绝对路径 linux 服务器地址  获取当前使用的配置文件配置
        //String urlString=PropertiesUtil.getInstance().getSysPro("uploadPath");
        //拼接文件路劲
        String filePathName = realPath + File.separator + pathName;
        //判断文件保存是否存在
        File file = new File(filePathName);
        if (file.getParentFile() != null || !file.getParentFile().isDirectory()) {
            //创建文件
            file.getParentFile().mkdirs();
        }

        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = imageFile.getInputStream();
            fileOutputStream = new FileOutputStream(file);
            //写出文件
            //2016-05-12 yangkang 改为增加缓存
//            IOUtils.copy(inputStream, fileOutputStream);
            IOUtils.copyLarge(inputStream, fileOutputStream);

        } catch (IOException e) {
            filePathName = null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                filePathName = null;
            }
        }


        //        String fileId = FastDFSClient.uploadFile(file, filePathName);

        /**
         * 缩略图begin
         */

        //拼接后台文件名称
        String thumbnailPathName = fileDirectory  + uuid + "small."
                + ext;
        //added by yangkang 2016-3-30 去掉后缀中包含的.png字符串
        if (thumbnailPathName.contains(".png")) {
            thumbnailPathName = thumbnailPathName.replace(".png", ".jpg");
        }
        //long size = imageFile.getSize();

        //scale = 0.7;

        //拼接文件路劲
        String thumbnailFilePathName = realPath  + thumbnailPathName;
        try {
            if (width == 0 && height == 0) {
                Thumbnails.of(filePathName).scale(1f).outputQuality(scale).outputFormat("jpg").toFile(thumbnailFilePathName);
            } else {
                Thumbnails.of(filePathName).size(width,height).outputQuality(scale).outputFormat("jpg").toFile(thumbnailFilePathName);
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        /**
         * 缩略图end
         */

        Map<String, Object> map = new HashMap<String, Object>();
        //原图地址
        map.put("originalUrl", pathName);
        //缩略图地址
        return backPath+ thumbnailPathName;

    }


}
