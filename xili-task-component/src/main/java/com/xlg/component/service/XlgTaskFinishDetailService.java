package com.xlg.component.service;

import java.util.List;

import com.xlg.component.model.XlgTaskFinishDetail;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-04-10
 */
public interface XlgTaskFinishDetailService {

    int batchInsert(List<XlgTaskFinishDetail> list);

    XlgTaskFinishDetail getTaskIdAndUserId(long taskId, long userId, long indicator);

    int updateExtParams(XlgTaskFinishDetail xlgTaskFinishDetail);
}
