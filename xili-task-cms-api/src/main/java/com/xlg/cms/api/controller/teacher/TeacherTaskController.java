package com.xlg.cms.api.controller.teacher;

import static com.xlg.component.enums.UserProgressStatusEnum.DOING;
import static com.xlg.component.enums.UserProgressStatusEnum.FINISHED;
import static com.xlg.component.enums.UserProgressStatusEnum.UNFINISHED;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xlg.cms.api.dto.TaskSaveDTO.ConditionInfo;
import com.xlg.cms.api.model.Result;
import com.xlg.cms.api.model.TaskShow;
import com.xlg.cms.api.model.TeacherTask;
import com.xlg.cms.api.model.UploadFile;
import com.xlg.cms.api.utils.DateUtils;
import com.xlg.cms.api.utils.ExcelUtils;
import com.xlg.cms.api.utils.TemplateStoreFileUtil;
import com.xlg.component.common.Page;
import com.xlg.component.dao.XlgTaskUserDAO;
import com.xlg.component.enums.IndicatorEnum;
import com.xlg.component.enums.TaskStatusEnum;
import com.xlg.component.model.XlgTask;
import com.xlg.component.model.XlgTaskCondition;
import com.xlg.component.service.XlgTaskConditionService;
import com.xlg.component.service.XlgTaskService;
import com.xlg.component.service.XlgTaskUserProgressService;
import com.xlg.component.service.XlgTaskUserService;
import com.xlg.component.service.XlgUserService;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-02-22
 */
@Controller
@RequestMapping("/teacher/task")
public class TeacherTaskController {

    private static final Logger logger = LoggerFactory.getLogger(TeacherTaskController.class);

    @Autowired
    private XlgTaskService xlgTaskService;
    @Autowired
    private XlgTaskConditionService xlgTaskConditionService;
    @Autowired
    private XlgUserService xlgUserService;
    @Autowired
    private XlgTaskUserService xlgTaskUserService;
    @Autowired
    private XlgTaskUserProgressService xlgTaskUserProgressService;
    @Autowired
    private XlgTaskUserDAO xlgTaskUserDAO;

    /**
     * 页面跳转
     */
    @RequestMapping("/list")
    @ResponseBody
    public Result taskList(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize,
            HttpServletRequest request) {
        Object user = request.getSession().getAttribute("user");
        long creator = 0;
        if (user != null) {
            creator = Long.parseLong((String) user);
        }
        Page page = new Page(pageNo, pageSize);
        XlgTask model = new XlgTask();
        model.setCreateId(creator);
        List<XlgTask> tasks = xlgTaskService.getAllTaskByPage(page, model);
        Set<Long> taskIds = tasks.stream().map(XlgTask::getId).collect(Collectors.toSet());

        // 根据id 去task——user表找总人数
        Map<Long, Long> taskIdTOUserCountMap = Maps.newHashMap();
        // 根据id 去user_progress表找 status = 2完成人数  未完成人数 = userCount - 完成人数
        Map<Long, Long> taskIdTOUserFinishedMap = Maps.newHashMap();
        taskIds.forEach(taskId -> {
            long userCount = xlgTaskUserService.getUserCountByTaskId(taskId);
            long userFinished = xlgTaskUserProgressService
                    .getUserFinishedByTaskId(taskId, FINISHED.getValue());
            taskIdTOUserCountMap.put(taskId, userCount);
            taskIdTOUserFinishedMap.put(taskId, userFinished);
        });

        List<TeacherTask> taskShowList = tasks.stream().map(task -> {
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
            long userCount = taskIdTOUserCountMap.get(task.getId());
            long userFinished = taskIdTOUserFinishedMap.get(task.getId());
            TeacherTask show = new TeacherTask();
            show.setCreator(xlgUserService.format(task.getCreateId()));
            show.setCreateTime(DateUtils.format(task.getCreateTime()));
            show.setEndTime(DateUtils.format(task.getEndTime()));
            show.setStartTime(DateUtils.format(task.getStartTime()));
            show.setStatus(TaskStatusEnum.fromValue(task.getStatus()).getDesc());
            show.setTaskDesc(task.getDescription());
            show.setTaskName(task.getName());
            show.setTaskId(task.getId());
            show.setFile(JSON.parseObject(task.getExtParams(), UploadFile.class));
            show.setIndFile(indFile.get());
            show.setConditionList(conditionInfoList);
            show.setTaskUsers(userCount);
            show.setTaskFinished(userFinished);
            show.setTaskUnFinished(userCount - userFinished);
            return show;
        }).sorted(Comparator.comparingLong(TaskShow::getTaskId)).collect(Collectors.toList());
        return Result.ok(taskShowList);
    }

    /**
     * 任务查找
     */
    @RequestMapping("/search")
    @ResponseBody
    public Result search(@RequestParam("taskId") long taskId,
            @RequestParam("taskName") String taskName,
            @RequestParam("status") int status,
            @RequestParam("taskDesc") String taskDesc,
            @RequestParam("startTime") long startTime,
            @RequestParam("endTime") long endTime,
            @RequestParam("pageNo") int pageNo,
            @RequestParam("pageSize") int pageSize, HttpServletRequest request) {

        Object user = request.getSession().getAttribute("user");
        long creator = 0;
        if (user != null) {
            creator = Long.parseLong((String) user);
        }
        logger.info(
                "taskId={}, taskName={}, status={}, creator={}, taskDesc={}, startTime={}, endTime={}, pageNo={}, "
                        + "pageSize={}",
                taskId,
                taskName, status, creator, taskDesc, startTime, endTime, pageNo, pageSize);
        Page page = new Page(pageNo, pageSize);
        XlgTask model = new XlgTask();
        model.setId(taskId);
        model.setName(taskName);
        model.setStatus(status);
        model.setDescription(taskDesc);
        model.setStartTime(startTime);
        model.setEndTime(endTime);
        model.setCreateId(creator);
        List<XlgTask> tasks = xlgTaskService.getAllTaskByPage(page, model);
        Set<Long> taskIds = tasks.stream().map(XlgTask::getId).collect(Collectors.toSet());

        // 根据id 去task——user表找总人数
        Map<Long, Long> taskIdTOUserCountMap = Maps.newHashMap();
        // 根据id 去user_progress表找 status = 2完成人数  未完成人数 = userCount - 完成人数
        Map<Long, Long> taskIdTOUserFinishedMap = Maps.newHashMap();
        taskIds.forEach(curId -> {
            long userCount = xlgTaskUserService.getUserCountByTaskId(curId);
            long userFinished = xlgTaskUserProgressService
                    .getUserFinishedByTaskId(curId, FINISHED.getValue());
            taskIdTOUserCountMap.put(curId, userCount);
            taskIdTOUserFinishedMap.put(curId, userFinished);
        });

        List<TeacherTask> taskShowList = tasks.stream().map(task -> {
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
            long userCount = taskIdTOUserCountMap.get(task.getId());
            long userFinished = taskIdTOUserFinishedMap.get(task.getId());
            TeacherTask show = new TeacherTask();
            show.setCreator(xlgUserService.format(task.getCreateId()));
            show.setCreateTime(DateUtils.format(task.getCreateTime()));
            show.setEndTime(DateUtils.format(task.getEndTime()));
            show.setStartTime(DateUtils.format(task.getStartTime()));
            show.setStatus(TaskStatusEnum.fromValue(task.getStatus()).getDesc());
            show.setTaskDesc(task.getDescription());
            show.setTaskName(task.getName());
            show.setTaskId(task.getId());
            show.setFile(JSON.parseObject(task.getExtParams(), UploadFile.class));
            show.setIndFile(indFile.get());
            show.setConditionList(conditionInfoList);
            show.setTaskUsers(userCount);
            show.setTaskFinished(userFinished);
            show.setTaskUnFinished(userCount - userFinished);
            return show;
        }).sorted(Comparator.comparingLong(TaskShow::getTaskId)).collect(Collectors.toList());
        return Result.ok(taskShowList);
    }

    /**
     * 任务查找
     */
    @RequestMapping("/export")
    public void export(@RequestParam("taskId") long taskId,
            @RequestParam("taskName") String taskName,
            @RequestParam("status") int status,
            @RequestParam("taskDesc") String taskDesc,
            @RequestParam("startTime") long startTime,
            @RequestParam("endTime") long endTime,
            @RequestParam("pageNo") int pageNo,
            @RequestParam("pageSize") int pageSize, HttpServletRequest request, HttpServletResponse response) {


        Object user = request.getSession().getAttribute("user");
        long createId = 0;
        if (user != null) {
            createId = Long.parseLong((String) user);
        }
        logger.info(
                "taskId={}, taskName={}, status={}, createId={}, taskDesc={}, startTime={}, endTime={}, pageNo={}, "
                        + "pageSize={}",
                taskId,
                taskName, status, createId, taskDesc, startTime, endTime, pageNo, pageSize);

        Result search = search(taskId, taskName, status, taskDesc, startTime, endTime, pageNo, pageSize, request);
        Object msg = search.get("msg");
        List<TeacherTask> dataList = Lists.newArrayList();
        if (!Objects.isNull(msg)) {
            dataList.addAll((Collection<? extends TeacherTask>) msg);
        }

        AtomicReference<String> creator = new AtomicReference<>(xlgUserService.format(createId));
        Workbook workbook = ExcelUtils.createWorkBook();
        Sheet sheet = workbook.createSheet();
        ExcelUtils.writeHeader(sheet,
                ImmutableList.of("任务id", "任务名称", "任务开始时间", "任务结束时间",
                        "任务状态", "任务创建时间", "任务备注", "任务总人数", "任务完成人数", "任务未完成人数", "创建人"));

        creator.set("");
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
                values.add(data.getTaskUsers());
                values.add(data.getTaskFinished());
                values.add(data.getTaskUnFinished());
                values.add(data.getCreator());
                creator.set(data.getCreator());
                ExcelUtils.writeRow(sheet, values);
            });
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            TemplateStoreFileUtil.download(response, baos.toByteArray(), "教师-" + creator + "-创建的任务列表", ".xls");
        } catch (IOException e) {
            logger.error("生成xls/xlsx文件失败", e);
        }
    }

    /**
     * 导出指定任务的学生完成情况
     */
    @RequestMapping("/finished-export")
    public void finishedExport(@RequestParam("taskId") long taskId, HttpServletRequest request, HttpServletResponse response) {


        List<Long> dataList = Lists.newArrayList();
        List<Long> finishedUserIdList = xlgTaskUserProgressService.getStatusByTaskId(taskId, Lists.newArrayList(FINISHED.getValue()));
        List<Long> unfinishedUserIdList = xlgTaskUserProgressService.getStatusByTaskId(taskId, Lists.newArrayList(UNFINISHED.getValue(),
                DOING.getValue()));

        dataList.addAll(finishedUserIdList);
        dataList.addAll(unfinishedUserIdList);
        Workbook workbook = ExcelUtils.createWorkBook();
        Sheet sheet = workbook.createSheet();
        ExcelUtils.writeHeader(sheet,
                ImmutableList.of("已完成学生学号", "已完成学生姓名", "未完成学生学号", "未完成学生姓名"));


        if (isNotEmpty(finishedUserIdList) || isNotEmpty(unfinishedUserIdList)) {
            int finishedSize = finishedUserIdList.size();
            int unfinishedSize = unfinishedUserIdList.size();
            for (; finishedSize > 0 || unfinishedSize > 0; finishedSize--,unfinishedSize--) {
                List<Object> values = Lists.newArrayList();
                if (finishedSize <= 0) {
                    values.add("");
                    values.add("");
                } else {
                    long createId = finishedUserIdList.get(finishedUserIdList.size() - finishedSize);
                    values.add(createId);
                    values.add(xlgUserService.format(createId));
                }
                if (unfinishedSize <= 0) {
                    values.add("");
                    values.add("");
                } else {
                    long createId = unfinishedUserIdList.get(unfinishedUserIdList.size() - unfinishedSize);
                    values.add(createId);
                    values.add(xlgUserService.format(createId));
                }
                ExcelUtils.writeRow(sheet, values);
            }
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            TemplateStoreFileUtil.download(response, baos.toByteArray(), "任务-" + taskId + "-学生完成情况", ".xls");
        } catch (IOException e) {
            logger.error("生成xls/xlsx文件失败", e);
        }
    }
}
