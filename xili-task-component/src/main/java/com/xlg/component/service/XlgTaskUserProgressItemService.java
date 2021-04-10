package com.xlg.component.service;

import java.util.List;

import com.xlg.component.model.XlgTaskUserProgressItem;

/*
 * 任务用户进度子项表 service接口
 */
public interface XlgTaskUserProgressItemService {

    int batchInsert(List<XlgTaskUserProgressItem> list);

    List<XlgTaskUserProgressItem> getByProgressIdAndUserId(long progressId, long userId);

    int batchUpdate(List<XlgTaskUserProgressItem> xlgTaskUserProgressItemList);
}