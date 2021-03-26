package com.xlg.component.common;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-06
 */
public class Page {

    public int page;
    public int count;

    public Page() {
    }

    public Page(int page, int count) {
        this.page = page;
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
