package com.xlg.component.processor;

import com.xlg.component.dto.MessageDTO;
import com.xlg.component.enums.TaskType;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-30
 */
public interface XlgTaskStatusChangedProcessor {

    boolean support(TaskType taskType);

    void process(MessageDTO dto);
}
