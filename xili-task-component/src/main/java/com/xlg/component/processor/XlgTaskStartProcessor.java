package com.xlg.component.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xlg.component.dto.MessageDTO;
import com.xlg.component.enums.TaskStatusEnum;
import com.xlg.component.enums.TaskType;
import com.xlg.component.model.XlgTask;
import com.xlg.component.model.XlgTaskUser;
import com.xlg.component.model.XlgUser;
import com.xlg.component.service.XlgTaskService;
import com.xlg.component.service.XlgTaskUserService;
import com.xlg.component.service.XlgUserService;
import com.xlg.component.utils.MailSendUtils;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-30
 * 任务开始处理
 */
@Service
public class XlgTaskStartProcessor implements XlgTaskStatusChangedProcessor {

    private static final Logger logger = LoggerFactory.getLogger(XlgTaskStartProcessor.class);

    @Resource
    private MailSendUtils mailSendUtils;
    @Resource
    private TemplateEngine templateEngine;
    @Resource
    private XlgUserService xlgUserService;
    @Autowired
    private XlgTaskService xlgTaskService;
    @Autowired
    private XlgTaskUserService xlgTaskUserService;

    @Value("${constant.project.url}")
    private String PROJECT_URL;

    @Override
    public boolean support(TaskType taskType) {
        return TaskType.TASK_START == taskType;
    }

    @Override
    public void process(MessageDTO dto) {
        long taskId = dto.getTaskId();
        long now = System.currentTimeMillis();
        //1. 任务状态变更
        List<XlgTask> taskByIds = xlgTaskService.getTaskByIds(Lists.newArrayList(taskId));
        if (CollectionUtils.isEmpty(taskByIds)) {
            logger.error("[XlgTaskStartProcessor] taskByIds is null, dto={}", JSON.toJSONString(dto));
            return;
        }
        XlgTask xlgTask = taskByIds.get(0);
        if (xlgTask.getId() != taskId || xlgTask.getStatus() != TaskStatusEnum.PENDING.getValue()) {
            logger.error("[XlgTaskStartProcessor] taskId={} is null, / status is not need ! task={}, dto={}", taskId,
                    JSON.toJSONString(xlgTask), JSON.toJSONString(dto));
            return;
        }
        int count = xlgTaskService.updateStatus(taskId, now, TaskStatusEnum.ONLINE.getValue());
        if (count <= 0) {
            logger.info("[XlgTaskStartProcessor] 修改失败, taskId={}, dto={}", taskId, JSON.toJSONString(dto));
            return;
        }
        logger.info("[XlgTaskStartProcessor] 修改成功, taskId={}", taskId);
        logger.info("[XlgTaskStartProcessor] 任务taskId={}, 已经开始, 准备通知这个任务下的学生。", taskId);
        List<XlgTaskUser> userList = xlgTaskUserService.getUserByTaskId(taskId);
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }
        userList.forEach(user -> {
            XlgUser xlgUser = xlgUserService.getByUserId(user.getUserId());
            String content = "<html>\n" +
                    "<body>\n" +
                    "<h1 style=\"color: red\">${username}同学你好, 您的老师给您下发了一个任务${taskId}"
                    + ", 任务已经开始了，请您及时完成!! 链接地址：<a style='text-decoration: none;' href='${url}'>${url}</a></h1>" +
                    "</body>\n" +
                    "</html>";
            Map<String, Object> map = new HashMap<>();
            map.put("username", xlgUser.getName());
            map.put("taskId", taskId);
            map.put("url", PROJECT_URL);
            content = StrSubstitutor.replace(content, map);
            try {
                mailSendUtils.sendHtmlMail(xlgUser.getEmail(), "Html邮件发送", content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        logger.info("[XlgTaskStartProcessor] 任务taskId={}, 已经开始, 邮件发送，共通知userCount={}", taskId, userList.size());
    }

    public static void main(String[] args) {
        String content = "<html>\n" +
                "<body>\n" +
                "<h1 style=\"color: red\">${username}同学你好, 您的老师给您下发了一个任务${taskId}"
                + ", 任务已经开始了，请您及时完成!! 链接地址：<a style='text-decoration: none;' href='${url}'>${url}</a></h1>" +
                "</body>\n" +
                "</html>";
        Map<String, Object> map = new HashMap<>();
        map.put("username", "asdfas");
        map.put("taskId", 0);
        map.put("url", "http://localhost:8082/");
        content = StrSubstitutor.replace(content, map);
        System.out.println(content);
    }
}
