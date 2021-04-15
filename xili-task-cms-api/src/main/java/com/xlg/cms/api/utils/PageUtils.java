package com.xlg.cms.api.utils;

import java.util.List;

import com.google.common.collect.Lists;
import com.xlg.component.common.Page;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-04-15
 */
public class PageUtils {

    public static <T> List<T> getTaskListByPage(List<T> taskList, Page page) {
        int start = (page.getPage() - 1) * page.getCount();
        int end = start + page.getCount();
        List<T> result = Lists.newArrayList();
        for (int i = start; i < taskList.size() && i <= end; i++) {
            result.add(taskList.get(i));
        }
        return result;
    }

}
