package com.xlg.cms.api.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TemplateStoreFileUtil {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private static final Logger logger = LoggerFactory.getLogger(TemplateStoreFileUtil.class);

    /**
     * 功能描述: 下载文件
     * @throws IOException
     */
    public static final void download(HttpServletResponse response, byte[] datas, String fileName, String fileType)
            throws IOException {
        response.setContentType("application/vnd.ms-excel;charset=" +  StandardCharsets.ISO_8859_1.name());
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        String newFileName = fileName + sdf.format(System.currentTimeMillis()) + "." + fileType;
//        String gb2312 = new String(newFileName.getBytes());
        //java.net.URLEncoder.encode(newFileName, "ISO-8859-1")
        response.setHeader("Content-Disposition",
                "attachment;filename=" + new String(newFileName.getBytes(), StandardCharsets.ISO_8859_1.name()));

        logger.info("filenameType={}", response.getContentType());
        System.out.println(java.nio.charset.Charset.forName("GB2312").newEncoder().canEncode(newFileName));

        System.out.println(java.nio.charset.Charset.forName("GB2312").newEncoder().canEncode("汉字"));
        System.out.println(java.nio.charset.Charset.forName("ISO-8859-1").newEncoder().canEncode(""));
        OutputStream os = response.getOutputStream();
        os.write(datas);
        os.flush();
        os.close();
    }

    public static void main(String[] args) {
        System.out.println(java.nio.charset.Charset.forName("GB2312").newEncoder().canEncode("汉字"));
        System.out.println(java.nio.charset.Charset.forName("ISO-8859-1").newEncoder().canEncode("汉字"));
    }
}
