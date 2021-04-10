package com.xlg.cms.api.controller;

import javax.annotation.Resource;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.xlg.component.common.RocketContants;
import com.xlg.component.dto.MessageDTO;
import com.xlg.component.enums.TaskType;
import com.xlg.component.utils.ProducerUtils;

@RestController
public class ProducerController {

    private static final Logger logger = LoggerFactory.getLogger(ProducerController.class);

    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private ProducerUtils producerUtils;

    @GetMapping("/rmqsend")
    public String send(String msg) {
        rocketMQTemplate.convertAndSend(RocketContants.TEST_TOPIC,msg);
        return "success";
    }

    @GetMapping("/rmqsend2")
    public String send2(@RequestParam("type") int type, @RequestParam("mills") long mills) {
        MessageDTO dto = new MessageDTO();
        dto.setTaskId(11);
        dto.setTaskType(TaskType.fromValue(type));
        dto.setTargetMills(mills);
        logger.info("[ProducerController] send message start , dto={}", JSON.toJSONString(dto));
        producerUtils.sendMessage(dto);
        return "success";
    }

}