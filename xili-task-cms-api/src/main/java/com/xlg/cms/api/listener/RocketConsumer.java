package com.xlg.cms.api.listener;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import com.xlg.component.common.RocketContants;

@Service
@RocketMQMessageListener(consumerGroup = RocketContants.CONSUMER_GROUP1, topic = RocketContants.TEST_TOPIC)
public class RocketConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        System.err.println("接收到消息：" + message);
    }
}