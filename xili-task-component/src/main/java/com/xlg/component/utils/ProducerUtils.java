package com.xlg.component.utils;

import static com.xlg.component.common.RocketContants.MIDDLE_TOPIC;
import static com.xlg.component.common.TaskConstants.ONE_THOUSAND;
import static com.xlg.component.common.TaskConstants.THREE_THOUSAND;

import java.util.List;

import javax.annotation.Resource;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xlg.component.dto.MessageDTO;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-29
 */
@Component
public class ProducerUtils {

    private static final List<Integer> defaultLevel;

    static {
        // 秒级
        defaultLevel = Lists.newArrayList();
        defaultLevel.add(1);
        defaultLevel.add(5);
        defaultLevel.add(10);
        defaultLevel.add(30);
        defaultLevel.add(60);
        defaultLevel.add(120);
        defaultLevel.add(180);
        defaultLevel.add(240);
        defaultLevel.add(300);
        defaultLevel.add(360);
        defaultLevel.add(420);
        defaultLevel.add(480);
        defaultLevel.add(540);
        defaultLevel.add(600);
        defaultLevel.add(1200);
        defaultLevel.add(1800);
        defaultLevel.add(3600);
        defaultLevel.add(7200);
    }

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ProducerUtils.class);

    // controller发送消息
    public void sendMessage(MessageDTO dto) {
        org.springframework.messaging.Message<MessageDTO> message = MessageBuilder.withPayload(dto).build();
        //1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h rocketMQ自动支持18个级别 等级全部转化为秒
        //            message.setDelayTimeLevel(2);                           // 5s
        SendResult sendResult = rocketMQTemplate.syncSend(MIDDLE_TOPIC, message, THREE_THOUSAND); // 3秒超时
        logger.info("sendResult={}", JSON.toJSONString(sendResult));
    }

    // consumer发送消息，轮训处理
    public void sendDelayMessage(MessageDTO dto) {
        long targetMills = dto.getTargetMills();
        long nowMills = System.currentTimeMillis();
        logger.info("[ProducerUtils] sendDelayMessage start targetMills={}, nowMills={}",
                JSON.toJSONString(targetMills), JSON.toJSONString(nowMills));
        int second = (int) ((targetMills - nowMills) / ONE_THOUSAND);
        logger.info("second={}", second);
        int index = calculateNum(second);// 毫秒化为秒
        logger.info("[ProducerUtils] sendDelayMessage index={}", index);
        // 比1s小 直接发送1s
        if (index == -1) {
            index = 0;
        }
        int secondLevel = index + 1;
        logger.info("[ProducerUtils] sendDelayMessage end dto={}, secondLevel={}", JSON.toJSONString(dto), secondLevel);
        sendMessageWithDelayLevel(dto, secondLevel);
    }

    // 发送带延迟等级的消息
    private void sendMessageWithDelayLevel(MessageDTO dto, int secondLevel) {
        org.springframework.messaging.Message<MessageDTO> message = MessageBuilder.withPayload(dto).build();
        //1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h rocketMQ自动支持18个级别 等级全部转化为
        // 5s
        SendResult sendResult = rocketMQTemplate.syncSend(MIDDLE_TOPIC, message, THREE_THOUSAND, secondLevel); // 3秒超时
        logger.info("sendResult={}", JSON.toJSONString(sendResult));
    }

    public static int calculateNum(long second) {
        for (int i = defaultLevel.size() - 1; i >= 0; i--) {
            int cul = (int) second / defaultLevel.get(i);
            if (cul != 0) {
                logger.info("calculateNum, second={}, kko={}, i={}, curl={}", second, defaultLevel.get(i), i, cul);
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        long second = 0;
        int i = calculateNum((1617283161000L - System.currentTimeMillis()) / ONE_THOUSAND);
        System.out.println(i);
    }
}
