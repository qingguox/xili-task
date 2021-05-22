package com.xlg.component.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xlg.component.dto.MessageDTO;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-30
 */
@Service
public class XlgTaskStatusChangedProcessorStrategyFactory {

    private final List<XlgTaskStatusChangedProcessor> processors;

    @Autowired
    public XlgTaskStatusChangedProcessorStrategyFactory(List<XlgTaskStatusChangedProcessor> processors) {
        this.processors = processors;
    }

    public void process(MessageDTO dto) {
        processors.stream()
                .filter(processor -> processor.support(dto.getTaskType()))
                .findFirst()
                .orElseGet(XlgTaskNoneProcessor::new)
                .process(dto);
    }
}
