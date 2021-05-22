package com.xlg.cms.api.controller.task;

import static com.xlg.component.common.TaskConstants.ONE;
import static com.xlg.component.common.TaskConstants.THREE;
import static com.xlg.component.enums.XlgTaskCache.TASK_REGISTER;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.xlg.cms.api.dto.TaskIndicatorDTO;
import com.xlg.cms.api.dto.TaskSaveDTO;
import com.xlg.cms.api.dto.TaskSaveDTO.ConditionInfo;
import com.xlg.cms.api.model.Result;
import com.xlg.cms.api.model.TaskShow;
import com.xlg.cms.api.model.UploadFile;
import com.xlg.cms.api.utils.AdminUtils;
import com.xlg.cms.api.utils.BlobFileUtils;
import com.xlg.cms.api.utils.DateUtils;
import com.xlg.cms.api.utils.ExcelUtils;
import com.xlg.cms.api.utils.PageUtils;
import com.xlg.cms.api.utils.TemplateStoreFileUtil;
import com.xlg.component.common.Page;
import com.xlg.component.common.TaskConstants;
import com.xlg.component.dto.MessageDTO;
import com.xlg.component.enums.AllStatusEnum;
import com.xlg.component.enums.IndicatorEnum;
import com.xlg.component.enums.RoleEnum;
import com.xlg.component.enums.TaskStatusEnum;
import com.xlg.component.enums.TaskType;
import com.xlg.component.enums.UserProgressStatusEnum;
import com.xlg.component.model.XlgIndicator;
import com.xlg.component.model.XlgRegister;
import com.xlg.component.model.XlgTask;
import com.xlg.component.model.XlgTaskCondition;
import com.xlg.component.model.XlgTaskUser;
import com.xlg.component.model.XlgTaskUserProgress;
import com.xlg.component.service.XlgIndicatorService;
import com.xlg.component.service.XlgRegisterService;
import com.xlg.component.service.XlgTaskConditionService;
import com.xlg.component.service.XlgTaskService;
import com.xlg.component.service.XlgTaskUserProgressService;
import com.xlg.component.service.XlgTaskUserService;
import com.xlg.component.service.XlgUserService;
import com.xlg.component.utils.ProducerUtils;


/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-02-20
 */
@Controller
@RequestMapping("/task")
public class TaskController {

    private final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private AdminUtils adminUtils;
    @Resource
    private XlgIndicatorService xlgIndicatorService;
    @Resource
    private XlgTaskService xlgTaskService;
    @Resource
    private XlgTaskConditionService xlgTaskConditionService;
    @Resource
    private XlgTaskUserService xlgTaskUserService;
    @Resource
    private XlgRegisterService xlgRegisterService;
    @Resource
    private XlgTaskUserProgressService xlgTaskUserProgressService;
    @Resource
    private XlgUserService xlgUserService;
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Resource
    private ProducerUtils producerUtils;
    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;


    /**
     * 任务列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public Result taskList(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize) {
        Page page = new Page(pageNo, pageSize);
        XlgTask request = new XlgTask();
        return progress(page, request);
    }

    /**
     * 任务查找
     */
    @RequestMapping("/search")
    @ResponseBody
    public Result search(@RequestParam("taskId") long taskId,
            @RequestParam("taskName") String taskName,
            @RequestParam("status") int status,
            @RequestParam("creator") String creator,
            @RequestParam("taskDesc") String taskDesc,
            @RequestParam("startTime") long startTime,
            @RequestParam("endTime") long endTime,
            @RequestParam("pageNo") int pageNo,
            @RequestParam("pageSize") int pageSize) {

        logger.info(
                "taskId={}, taskName={}, status={}, creator={}, taskDesc={}, startTime={}, endTime={}, pageNo={}, "
                        + "pageSize={}",
                taskId,
                taskName, status, creator, taskDesc, startTime, endTime, pageNo, pageSize);
        Page page = new Page(pageNo, pageSize);
        XlgTask request = new XlgTask();
        request.setId(taskId);
        request.setName(taskName);
        request.setStatus(status);
        request.setDescription(taskDesc);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setCreateId(xlgUserService.formatNameToCreateId(creator));
        return progress(page, request);
    }

    private Result progress(Page page, XlgTask model) {
        List<XlgTask> tasks = xlgTaskService.getAllTaskByPage(new Page(), model);
        List<TaskShow> taskShowList = tasks.stream().map(task -> {
            List<XlgTaskCondition> conditionList = xlgTaskConditionService.getByTaskId(task.getId());
            List<ConditionInfo> conditionInfoList = Lists.newArrayList();
            AtomicReference<UploadFile> indFile = new AtomicReference<>(new UploadFile());
            conditionList.forEach(condition -> {
                ConditionInfo info = new ConditionInfo();
                info.setStatus(String.valueOf(condition.getIndicator()));
                conditionInfoList.add(info);
                if (condition.getIndicator() == IndicatorEnum.STUDENT_KNOWN.getValue()) {
                    indFile.set(JSON.parseObject(condition.getExtParams(), UploadFile.class));
                }
            });
            TaskShow show = new TaskShow();
            show.setCreator(xlgUserService.format(task.getCreateId()));
            show.setCreateTime(DateUtils.YYYY_MM_DD_HHMMSS.print(task.getCreateTime()));
            show.setEndTime(DateUtils.YYYY_MM_DD_HHMMSS.print(task.getEndTime()));
            show.setStartTime(DateUtils.YYYY_MM_DD_HHMMSS.print(task.getStartTime()));
            show.setStatus(TaskStatusEnum.fromValue(task.getStatus()).getDesc());
            show.setTaskDesc(task.getDescription());
            show.setTaskName(task.getName());
            show.setTaskId(task.getId());
            show.setFile(JSON.parseObject(task.getExtParams(), UploadFile.class));
            show.setIndFile(indFile.get());
            show.setConditionList(conditionInfoList);
            return show;
        }).sorted(Comparator.comparingLong(TaskShow::getTaskId)).collect(Collectors.toList());
        int total = taskShowList.size();
        List<TaskShow> taskShows = PageUtils.getTaskListByPage(taskShowList, page);
        return Result.ok(total, taskShows);
    }

    /**
     * 任务列表导出
     */
    @RequestMapping("/export")
    public void export(@RequestParam("taskId") long taskId,
            @RequestParam("taskName") String taskName,
            @RequestParam("status") int status,
            @RequestParam("creator") String creator,
            @RequestParam("taskDesc") String taskDesc,
            @RequestParam("startTime") long startTime,
            @RequestParam("endTime") long endTime,
            @RequestParam("pageNo") int pageNo,
            @RequestParam("pageSize") int pageSize, HttpServletResponse response, HttpServletRequest request) {

        logger.info(
                "taskId={}, taskName={}, status={}, creator={}, taskDesc={}, startTime={}, endTime={}, pageNo={}, "
                        + "pageSize={}",
                taskId,
                taskName, status, creator, taskDesc, startTime, endTime, pageNo, pageSize);

        logger.info(
                "taskId={}, taskName={}, status={}, creator={}, taskDesc={}, startTime={}, endTime={}, pageNo={}, "
                        + "pageSize={}",
                taskId,
                taskName, status, creator, taskDesc, startTime, endTime, pageNo, pageSize);

        Result search = search(taskId, taskName, status, creator, taskDesc, startTime, endTime, pageNo, pageSize);
        Object msg = search.get("msg");
        List<TaskShow> dataList = Lists.newArrayList();
        if (!Objects.isNull(msg)) {
            dataList.addAll((Collection<? extends TaskShow>) msg);
        }

        Workbook workbook = ExcelUtils.createWorkBook();
        Sheet sheet = workbook.createSheet();
        ExcelUtils.writeHeader(sheet,
                ImmutableList.of("任务id", "任务名称", "任务开始时间", "任务结束时间",
                        "任务状态", "任务创建时间", "任务备注", "创建人"));

        if (isNotEmpty(dataList)) {
            dataList.forEach(data -> {
                List<Object> values = Lists.newArrayList();
                values.add(data.getTaskId());
                values.add(data.getTaskName());
                values.add(data.getStartTime());
                values.add(data.getEndTime());
                values.add(data.getStatus());
                values.add(data.getCreateTime());
                values.add(data.getTaskDesc());
                values.add(data.getCreator());
                ExcelUtils.writeRow(sheet, values);
            });
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            TemplateStoreFileUtil.download(response, baos.toByteArray(), "任务列表", ".xls");
        } catch (IOException e) {
            logger.error("生成xls/xlsx文件失败", e);
        }
    }


    /**
     * 指标集合
     */
    @RequestMapping("/indicators")
    @ResponseBody
    public Result indicators() {
        List<XlgIndicator> indicators = xlgIndicatorService.getAllIndicators();
        List<TaskIndicatorDTO> list = indicators.stream().map(cur -> {
            TaskIndicatorDTO dto = new TaskIndicatorDTO();
            dto.setValue((int) cur.getIndicator());
            dto.setDesc(cur.getName());
            return dto;
        }).collect(Collectors.toList());
        return Result.ok(list);
    }

    /**
     * 手动下线任务
     */
    @RequestMapping("/remove")
    @ResponseBody
    public Result manualOffline(@RequestParam("taskId") long taskId, @RequestParam("time") long time,
            HttpServletRequest request) {
        boolean hasAdmin = adminUtils.CheckAdmin(request, Lists.newArrayList(RoleEnum.MANAGER, RoleEnum.TEACHER));
        if (!hasAdmin) {
            return Result.ok(401, "没有权限！！！");
        }
        // 1. task变更
        xlgTaskService.updateStatus(taskId, time, TaskStatusEnum.MANUAL_OFFLINE.getValue());
        // 2. register变更
        xlgRegisterService.updateStatus(taskId, time, AllStatusEnum.DETACH.getValue());
        // 3. userProgress 进行中的用户改为 未完成
        xlgTaskUserProgressService.updateStatus(taskId);
        // 使监控缓存失效
        String key = TASK_REGISTER.getDesc().concat(String.valueOf(taskId));
        redisTemplate.delete(key);
        return Result.ok();
    }

    /**
     * 只允许修改任务名称和描述
     */
    @RequestMapping("/edit")
    @ResponseBody
    public Result edit(@RequestBody TaskSaveDTO taskSaveDTO, HttpServletRequest request) {
        boolean hasAdmin = adminUtils.CheckAdmin(request, Lists.newArrayList(RoleEnum.MANAGER, RoleEnum.TEACHER));
        if (!hasAdmin) {
            return Result.ok(401, "没有权限！！！");
        }
        XlgTask task = new XlgTask();
        task.setId(taskSaveDTO.getTaskId());
        task.setName(taskSaveDTO.getTaskName());
        task.setDescription(taskSaveDTO.getTaskDesc());
        int count = xlgTaskService.update(task);
        if (count > 0) {
            return Result.ok("修改成功");
        } else {
            return Result.error("修改失败");
        }
    }

    /**
     * 任务状态枚举
     */
    @RequestMapping("/status")
    @ResponseBody
    public Result status() {
        TaskStatusEnum[] values = TaskStatusEnum.values();
        List<Map<String, Object>> collect =
                Arrays.stream(values).map(TaskStatusEnum::toValueDescMap).collect(Collectors.toList());
        return Result.ok(collect);
    }

    /**
     * 添加任务
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @RequestMapping("/add")
    @ResponseBody
    public Result addTask(@RequestBody TaskSaveDTO taskSaveDTO, HttpServletRequest request) {
        System.out.println("***********************************************************");
        System.out.println(JSON.toJSONString(taskSaveDTO));
        System.out.println("***********************************************************");
        boolean hasAdmin = adminUtils.CheckAdmin(request, Lists.newArrayList(RoleEnum.MANAGER, RoleEnum.TEACHER));
        if (!hasAdmin) {
            return Result.ok(401, "没有权限！！！");
        }
        long createId = 3170211060L;
        int taskStatus = 0;
        int userProgressStatus = 0;
        long now = System.currentTimeMillis();
        if (taskSaveDTO.getStartTime() <= now) {
            taskStatus = TaskStatusEnum.ONLINE.getValue();
            userProgressStatus = UserProgressStatusEnum.DOING.getValue();
        } else {
            taskStatus = TaskStatusEnum.PENDING.getValue();
            userProgressStatus = UserProgressStatusEnum.UNKNOWN.getValue();
        }
        // 1. 插入任务表task
        long taskId = initTask(taskSaveDTO, createId, taskStatus);
        // 2. 插入task_user
        Set<Long> userSet = initTaskUser(taskSaveDTO, taskId, createId);
        // 3. 初始化userProgress
        initUserProgress(userSet, userProgressStatus, taskId);
        // 4. 插入条件表
        initTaskCondition(taskSaveDTO, taskId);

        // 5. 注册register 实际上是任务注册，状态1 监控中，此时指标模块看两点，1.监控中,2.监控时间
        initRegister(taskSaveDTO, taskId);

        // 6. 准备发送mq到，指定topic，处理任务，进度开始，结束status变化，和register结束status
        // 待进行
        MessageDTO dto = new MessageDTO();
        dto.setTaskId(taskId);
        if (taskStatus == TaskStatusEnum.PENDING.getValue()) {
            dto.setTargetMills(taskSaveDTO.getStartTime());
            dto.setTaskType(TaskType.TASK_START);
            producerUtils.sendMessage(dto);
            logger.info("任务id={},开始时间还没到，所以是待进行中，发送任务状态变为开始的消息", taskId);
        }
        logger.info("任务id={},发送任务结束的处理消息，延迟消息哦，主要是任务，进度状态变更，还有注册表变无效哦", taskId);
        dto.setTargetMills(taskSaveDTO.getEndTime());
        dto.setTaskType(TaskType.TASK_END);
        producerUtils.sendMessage(dto);
        return Result.ok("任务添加成功!!");
    }


    @RequestMapping("/redis")
    public void getRedis() {
        redisTemplate.opsForValue().set("key", 234);
        System.out.println(redisTemplate.opsForValue().get("key"));
    }

    private void initRegister(TaskSaveDTO taskSaveDTO, long taskId) {
        long now = System.currentTimeMillis();
        XlgRegister register = new XlgRegister();
        register.setCreateTime(now);
        register.setUpdateTime(now);
        register.setStartTime(taskSaveDTO.getStartTime());
        register.setEndTime(taskSaveDTO.getEndTime());
        register.setExtParams(JSON.toJSONString(""));
        register.setStatus(AllStatusEnum.TACH.getValue());
        register.setTaskId(taskId);
        xlgRegisterService.batchInsert(Lists.newArrayList(register));
        // 从数据库中重新拿出来
        XlgRegister xlgRegister = xlgRegisterService.getByTaskId(taskId);
        // TODO 写入缓存中默认保留一天
        String key = TASK_REGISTER.getDesc().concat(String.valueOf(taskId));
        //设置的是3s失效，3s之内查询有结果，3s之后返回null
        redisTemplate.opsForValue().set(key, JSON.toJSONString(xlgRegister), THREE, TimeUnit.SECONDS);
        redisTemplate.expire(key, ONE, TimeUnit.DAYS);
        logger.info("[TaskController] initRegister redis set key={}, value={}, expire time one day", key,
                JSON.toJSONString(xlgRegister));
        logger.info("insert into register={}", JSON.toJSONString(register));
    }

    private void initTaskCondition(TaskSaveDTO taskSaveDTO, long taskId) {
        long now = System.currentTimeMillis();
        List<ConditionInfo> conditionInfos = taskSaveDTO.getConditionList();
        // 学生通知
        UploadFile indFile = taskSaveDTO.getIndFile();
        List<XlgTaskCondition> conditionList = conditionInfos.stream().map(curInfo -> {
            int indicator = Integer.parseInt(curInfo.getStatus());
            XlgTaskCondition condition = new XlgTaskCondition();
            condition.setTaskId(taskId);
            condition.setCreateTime(now);
            condition.setUpdateTime(now);
            condition.setStatus(AllStatusEnum.TACH.getValue());
            condition.setIndicator(indicator);
            condition.setThreshold(1);
            condition.setDescription("");
            condition.setIndicatorName(IndicatorEnum.fromValue(indicator).getDesc());
            if (indicator == IndicatorEnum.UPLOAD_WORK.getValue()) {
                condition.setExtParams(JSON.toJSONString(""));
            } else if (indicator == IndicatorEnum.STUDENT_KNOWN.getValue()) {
                condition.setExtParams(JSON.toJSONString(indFile));
            }
            return condition;
        }).collect(Collectors.toList());
        xlgTaskConditionService.batchInsert(conditionList);
        logger.info("insert into taskConditionList={}", JSON.toJSONString(conditionList));
    }

    private void initUserProgress(Set<Long> userSet, int userProgressStatus, long taskId) {
        long now = System.currentTimeMillis();
        List<XlgTaskUserProgress> userProgressList = userSet.stream().map(userId -> {
            XlgTaskUserProgress userProgress = new XlgTaskUserProgress();
            userProgress.setCreateTime(now);
            userProgress.setUpdateTime(now);
            userProgress.setFinished(TaskConstants.ZERO);
            userProgress.setTaskId(taskId);
            userProgress.setUserId(userId);
            userProgress.setStatus(userProgressStatus);
            return userProgress;
        }).collect(Collectors.toList());
        xlgTaskUserProgressService.batchInsert(userProgressList);
        logger.info("insert into userProgress={}", JSON.toJSONString(userProgressList));
    }

    private Set<Long> initTaskUser(TaskSaveDTO taskSaveDTO, long taskId, long createId) {
        long now = System.currentTimeMillis();
        // 学生名单
        UploadFile file = taskSaveDTO.getFile();
        Set<Long> userSet = BlobFileUtils.getSetFromFile(Long.class, file.getUrl());
        List<XlgTaskUser> userList = userSet.stream().map(userId -> {
            XlgTaskUser user = new XlgTaskUser();
            user.setCreateId(createId);
            user.setCreateTime(now);
            user.setStatus(AllStatusEnum.TACH.getValue());
            user.setTaskId(taskId);
            user.setUpdateTime(now);
            user.setUserId(userId);
            return user;
        }).collect(Collectors.toList());
        xlgTaskUserService.batchInsert(userList);
        logger.info("insert into userList={}", JSON.toJSONString(userList));
        return userSet;
    }

    private long initTask(TaskSaveDTO taskSaveDTO, long createId, int taskStatus) {
        long now = System.currentTimeMillis();
        XlgTask task = new XlgTask();
        task.setName(taskSaveDTO.getTaskName());
        task.setDescription(taskSaveDTO.getTaskDesc());
        task.setStartTime(taskSaveDTO.getStartTime());
        task.setEndTime(taskSaveDTO.getEndTime());
        task.setExtParams(JSON.toJSONString(taskSaveDTO.getFile()));
        task.setCreateId(createId);
        task.setCreateTime(now);
        task.setStatus(taskStatus);
        task.setUpdateTime(now);
        long taskId = xlgTaskService.insert(task);
        //        long taskId = 9;
        logger.info("insert task={}, taskId={}", JSON.toJSONString(task), taskId);
        return taskId;
    }
}
