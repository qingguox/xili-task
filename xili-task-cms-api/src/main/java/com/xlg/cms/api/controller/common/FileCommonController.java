package com.xlg.cms.api.controller.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.xlg.cms.api.model.Result;
import com.xlg.cms.api.model.UploadFile;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-03
 */
@Controller
@RequestMapping("/file")
public class FileCommonController {

    @Resource
    private RestTemplate restTemplate;

    @Value("${web.upload-path}")
    private String uploadPath;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * 文件上传
     */
    @RequestMapping(value = "/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Result upload(@RequestParam MultipartFile file, HttpServletRequest request) throws Exception {
        String format = sdf.format(new Date());
        File folder = new File(uploadPath + format);
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }
        String s = new String(file.getBytes());
        // 对上传的文件重命名，避免文件重名
        String oldName = file.getOriginalFilename();
        String newName = UUID.randomUUID().toString()
                + oldName.substring(oldName.lastIndexOf("."), oldName.length());
        try {
            // 文件保存å
            file.transferTo(new File(folder, newName));

            // 返回上传文件的访问路径
            String filePath = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort() + "/file/download/" + format + "/" + newName;
            UploadFile resFile = new UploadFile();
            resFile.setName(oldName);
            resFile.setUrl(filePath);
            resFile.setFile(s);
            System.out.println(filePath);
            return Result.ok(Lists.newArrayList(resFile));
        } catch (IOException e) {
            throw new Exception(e);
        }
    }

    @RequestMapping(value = "/download/{date}/{path}")
    public String download(HttpServletRequest request, @PathVariable String date, @PathVariable String path, HttpServletResponse response)
            throws IOException {

        System.out.println(path);

        String filename = date + "/" + path;
        String filePath = uploadPath;
        File file = new File(filePath + "/" + filename);
        if (file.exists()) { //判断文件父目录是否存在
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            // response.setContentType("application/force-download");
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + java.net.URLEncoder.encode(filename, "UTF-8"));
            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;

            OutputStream os = null; //输出流
            try {
                os = response.getOutputStream();
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer);
                    i = bis.read(buffer);
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("----------file download---" + filename);
            try {
                bis.close();
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

}
