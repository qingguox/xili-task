package com.xlg.cms.api.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-03
 */
public class UploadFile {
    @SuppressWarnings("checkstyle:VisibilityModifier")
    public String name;
    @SuppressWarnings("checkstyle:VisibilityModifier")
    public String url;
    @SuppressWarnings("checkstyle:VisibilityModifier")
    public String file;

    public UploadFile() {
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
