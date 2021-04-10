package com.xlg.component.service;

import com.xlg.component.dto.XlgTaskUserProgressDTO;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-04-10
 * 进度处理
 */
public interface XlgTaskUserProgressProcessService {

    /**
     *  任务进度处理
     */
    void processProgress(XlgTaskUserProgressDTO xlgTaskUserProgressDTO);

}
