package com.xlg.component.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.xlg.component.dao.XlgTaskUserProgressItemDAO;
import com.xlg.component.model.XlgTaskUserProgressItem;
import com.xlg.component.service.XlgTaskUserProgressItemService;


/*
 * 任务用户进度子项表 service实现
 */
@Lazy
@Service
public class XlgTaskUserProgressItemServiceImpl implements XlgTaskUserProgressItemService {
    @Resource
    private XlgTaskUserProgressItemDAO xlgTaskUserProgressItemDAO;

    @Override
    public int batchInsert(List<XlgTaskUserProgressItem> list) {
        return xlgTaskUserProgressItemDAO.batchInsert(list);
    }

}