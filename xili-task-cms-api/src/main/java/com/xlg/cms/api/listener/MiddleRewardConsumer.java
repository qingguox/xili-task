package com.xlg.cms.api.listener;


import javax.annotation.Resource;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.xlg.component.common.RocketContants;
import com.xlg.component.dto.MessageDTO;
import com.xlg.component.processor.XlgTaskStatusChangedProcessorStrategy;
import com.xlg.component.utils.ProducerUtils;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-29
 */
@Service
@RocketMQMessageListener(consumerGroup = RocketContants.CONSUMER_GROUP2, topic = RocketContants.MIDDLE_TOPIC)
public class MiddleRewardConsumer implements RocketMQListener<MessageDTO> {

    private static final Logger logger = LoggerFactory.getLogger(MiddleRewardConsumer.class);

    @Resource
    private XlgTaskStatusChangedProcessorStrategy xlgTaskStatusChangedProcessorStrategy;
    @Resource
    private ProducerUtils producerUtils;

    @Override
    public void onMessage(MessageDTO dto) {
        long targetMills = dto.getTargetMills();

        logger.info("[MiddleRewardConsumer] start dto={}", JSON.toJSONString(dto));
        if (System.currentTimeMillis() < targetMills) {
            // 轮训处理
            logger.info("[MiddleRewardConsumer] curMills < targetMills, 继续轮训");
            producerUtils.sendDelayMessage(dto);
            return;
        }
        // >= 此时去变更任务
        logger.info("[MiddleRewardConsumer] 延迟消息结束，准备根据taskType不同去处理消息。dto={},", JSON.toJSONString(dto));
        xlgTaskStatusChangedProcessorStrategy.process(dto);
        logger.info("[MiddleRewardConsumer] consumer end!");
    }
}
