package com.xlg.component.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.xlg.component.dto.MessageDTO;
import com.xlg.component.enums.TaskType;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-30
 */
@Service
public class XlgTaskNoneProcessor implements XlgTaskStatusChangedProcessor {

    private static final Logger logger = LoggerFactory.getLogger(XlgTaskNoneProcessor.class);

    @Override
    public boolean support(TaskType taskType) {
        return false;
    }

    @Override
    public void process(MessageDTO dto) {
        logger.info("[XlgTaskNoneProcessor] warn not to process !");
    }
}
