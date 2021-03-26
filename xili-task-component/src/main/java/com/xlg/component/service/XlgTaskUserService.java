package com.xlg.component.service;

import java.util.List;

import com.xlg.component.model.XlgTaskUser;

/*
 * 任务用户表 service接口
 */
public interface XlgTaskUserService {

    int batchInsert(List<XlgTaskUser> list);

}