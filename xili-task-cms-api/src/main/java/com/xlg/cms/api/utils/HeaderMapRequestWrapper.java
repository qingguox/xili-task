package com.xlg.cms.api.utils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class HeaderMapRequestWrapper extends HttpServletRequestWrapper {
    /**
     * construct a wrapper for this request
     */

    public HeaderMapRequestWrapper(HttpServletRequest request) {
        super(request);

    }

    private Map headerMap = new HashMap();

    /**
     * add a header with given name and value
     */

    public void addHeader(String name, String value) {
        headerMap.put(name, value);

    }

    @Override
    public String getHeader(String name) {
        String headerValue = super.getHeader(name);
        if (headerMap.containsKey(name)) {
            headerValue = (String) headerMap.get(name);
        }
        return headerValue;
    }

    /**
     * get the Header names
     */

    @Override
    public Enumeration getHeaderNames() {
        List names = Collections.list(super.getHeaderNames());
        for (Object name : headerMap.keySet()) {
            names.add(name);
        }
        return Collections.enumeration(names);
    }

    @Override
    public Enumeration getHeaders(String name) {
        List values = Collections.list(super.getHeaders(name));
        if (headerMap.containsKey(name)) {
            values.add(headerMap.get(name));
        }
        return Collections.enumeration(values);
    }
}