package com.xlg.component.service;

import java.util.List;

import com.xlg.component.common.Page;
import com.xlg.component.enums.AllStatusEnum;
import com.xlg.component.model.XlgUser;

/*
 * 用户表 service接口
 */
public interface XlgUserService {

    int batchInsert(List<XlgUser> list);

    // createId 工号，姓名 转换器
    String format(long createId);

    List<XlgUser> getAllTaskByPage(Page page, XlgUser request);

    int delete(long id);

    int update(XlgUser user);

    long formatNameToCreateId(String creator);

    AllStatusEnum hasUser(long userId, String passwordFromMd5);
}