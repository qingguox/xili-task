package com.xlg.cms.api.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TemplateStoreFileUtil {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private static final Logger logger = LoggerFactory.getLogger(TemplateStoreFileUtil.class);

    /**
     * download file
     */
    public static final void download(HttpServletResponse response, byte[] datas, String fileName, String fileType)
            throws IOException {

//        String fileName2 = new String(fileName.getBytes("GB2312"), "ISO-8859-1");
        fileName = fileName + "-" + sdf.format(System.currentTimeMillis());
        String newFileName = URLEncoder.encode(fileName,"utf-8");
        response.setContentType("application/octet-stream");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition", "attachment;filename=" + newFileName + fileType);
        response.flushBuffer();

        System.out.println(response.getCharacterEncoding());
        OutputStream os = response.getOutputStream();
        os.write(datas);
        os.flush();
        os.close();
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String name = "nihao1";
        System.out.println(java.nio.charset.Charset.forName("GB2312").newEncoder().canEncode(name));
        String file = new String(name.getBytes("GB2312"), "utf-8");
        System.out.println(java.nio.charset.Charset.forName("ISO-8859-1").newEncoder().canEncode(file));
        System.out.println(java.nio.charset.Charset.forName("utf-8").newEncoder().canEncode(file));
    }

}
