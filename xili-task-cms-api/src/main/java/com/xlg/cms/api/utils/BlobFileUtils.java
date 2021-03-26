package com.xlg.cms.api.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-06
 */
public class BlobFileUtils {

    private static final Logger logger = LoggerFactory.getLogger(BlobFileUtils.class);

    public static <T> Set<T> getSetFromFile(Class<T> c, String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            logger.error("readFile error, fileUrl is null");
            return Collections.emptySet();
        }
        byte[] buffer = new byte[1024];
        Set<T> result = Sets.newHashSet();
        StringBuffer stringBuffer = new StringBuffer();
        BufferedInputStream bis = null;
        try {
            URL oracle = new URL(fileUrl);
            URLConnection conn = oracle.openConnection();
            bis = new BufferedInputStream(conn.getInputStream());
            int i = bis.read(buffer);
            while (i != -1) {
                System.out.println(new String(buffer));
                stringBuffer.append(new String(buffer));
                i = bis.read(buffer);
            }
            byte[] bytes = stringBuffer.toString().getBytes();
            result = getSetFromBytes(c, bytes);
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static <T> Set<T> getSetFromBytes(Class<T> c, byte[] bytes) {
        Set<T> result;
        String strFile = new String(bytes).trim();
        result = Splitter.on("\n").splitToList(strFile)
                .stream().filter(StringUtils::isNotBlank)
                .map(str -> transformObject(c, str)).collect(Collectors.toSet());
        return result;
    }

    private static <T> T transformObject(Class<T> c, String str) {
        str = str.replaceAll("\r|\n", "").trim();
        if (c == Long.class) {
            return (T) Long.valueOf(str);
        } else if (c == Integer.class) {
            return (T) Integer.valueOf(str);
        } else {
            return (T) str;
        }
    }
}
