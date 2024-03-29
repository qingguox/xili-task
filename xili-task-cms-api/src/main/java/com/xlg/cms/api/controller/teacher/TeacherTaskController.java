package com.xlg.cms.api.controller.teacher;

import static com.xlg.component.enums.UserProgressStatusEnum.DOING;
import static com.xlg.component.enums.UserProgressStatusEnum.FINISHED;
import static com.xlg.component.enums.UserProgressStatusEnum.UNFINISHED;
import static com.xlg.component.enums.UserProgressStatusEnum.UNKNOWN;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xlg.cms.api.dto.TaskSaveDTO.ConditionInfo;
import com.xlg.cms.api.dto.XlgUserDTO;
import com.xlg.cms.api.model.Result;
import com.xlg.cms.api.model.TaskShow;
import com.xlg.cms.api.model.TeacherTask;
import com.xlg.cms.api.model.UploadFile;
import com.xlg.cms.api.utils.AdminUtils;
import com.xlg.cms.api.utils.DateUtils;
import com.xlg.cms.api.utils.ExcelUtils;
import com.xlg.cms.api.utils.PageUtils;
import com.xlg.cms.api.utils.TemplateStoreFileUtil;
import com.xlg.component.common.Page;
import com.xlg.component.dto.XlgUserExtParams;
import com.xlg.component.enums.IndicatorEnum;
import com.xlg.component.enums.RoleEnum;
import com.xlg.component.enums.TaskStatusEnum;
import com.xlg.component.model.XlgTask;
import com.xlg.component.model.XlgTaskCondition;
import com.xlg.component.model.XlgTaskFinishDetail;
import com.xlg.component.model.XlgUser;
import com.xlg.component.service.XlgTaskConditionService;
import com.xlg.component.service.XlgTaskFinishDetailService;
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
    private AdminUtils adminUtils;
    @Autowired
    private XlgTaskUserService xlgTaskUserService;
    @Autowired
    private XlgTaskUserProgressService xlgTaskUserProgressService;
    @Autowired
    private XlgTaskFinishDetailService xlgTaskFinishDetailService;

    /**
     * 页面跳转
     */
    @RequestMapping("/list")
    @ResponseBody
    public Result taskList(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize,
            HttpServletRequest request) {
        boolean checkAdmin = adminUtils.CheckAdmin(request, Lists.newArrayList(RoleEnum.TEACHER, RoleEnum.MANAGER));
        if (!checkAdmin) {
            return Result.error(401, "很抱歉，您没有权限!!");
        }
        long createId = adminUtils.AdminId(request);
        Page page = new Page(pageNo, pageSize);
        XlgTask model = new XlgTask();
        model.setCreateId(createId);
        logger.info("model={}", JSON.toJSONString(model));
        return progress(page, model);
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

        boolean checkAdmin = adminUtils.CheckAdmin(request, Lists.newArrayList(RoleEnum.TEACHER, RoleEnum.MANAGER));
        if (!checkAdmin) {
            return Result.error(401, "很抱歉，您没有权限!!");
        }
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
        return progress(page, model);
    }

    private Result progress(Page page, XlgTask model) {
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

        logger.info("taskIds={}", JSON.toJSONString(taskIds));
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
            show.setTaskUsers(userCount);
            show.setTaskFinished(userFinished);
            show.setTaskUnFinished(userCount - userFinished);
            return show;
        }).sorted(Comparator.comparingLong(TaskShow::getTaskId)).collect(Collectors.toList());
        int total = taskShowList.size();
        List<TeacherTask> taskList = PageUtils.getTaskListByPage(taskShowList, page);
        logger.info("taskList={}", JSON.toJSONString(taskList));
        return Result.ok(total, taskList);
    }

    /**
     * 任务导出
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
                creator.set(data.getCreator());
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
                values.add(creator);
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
                DOING.getValue(), UNKNOWN.getValue()));

        dataList.addAll(finishedUserIdList);
        dataList.addAll(unfinishedUserIdList);
        Workbook workbook = ExcelUtils.createWorkBook();
        Sheet sheet = workbook.createSheet();
        ExcelUtils.writeHeader(sheet,
                ImmutableList.of("已完成学生学号", "已完成学生姓名", "已完成学生作业", "未完成学生学号", "未完成学生姓名"));

        System.out.println(JSON.toJSONString(finishedUserIdList));
        System.out.println(JSON.toJSONString(unfinishedUserIdList));
        if (isNotEmpty(finishedUserIdList) || isNotEmpty(unfinishedUserIdList)) {
            int finishedSize = finishedUserIdList.size();
            int unfinishedSize = unfinishedUserIdList.size();
            for (; finishedSize > 0 || unfinishedSize > 0; finishedSize--,unfinishedSize--) {
                List<Object> values = Lists.newArrayList();
                if (finishedSize <= 0) {
                    values.add("");
                    values.add("");
                    values.add("");
                } else {
                    long createId = finishedUserIdList.get(finishedUserIdList.size() - finishedSize);
                    values.add(createId);
                    values.add(xlgUserService.format(createId));
                    XlgTaskFinishDetail detail = xlgTaskFinishDetailService
                            .getTaskIdAndUserId(taskId, createId, IndicatorEnum.UPLOAD_WORK.getValue());
                    String fileUrl = "";
                    if (detail != null) {
                        UploadFile uploadFile = JSON.parseObject(detail.getExtParams(), UploadFile.class);
                        fileUrl = uploadFile.getUrl();
                    }
                    values.add(fileUrl);
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

    @PostMapping("/user/info")
    @ResponseBody
    public Result userInfo(HttpServletRequest request) {
        Page page = new Page(1, 1);
        long adminId = adminUtils.AdminId(request);
        XlgUser req = new XlgUser();
        req.setUserId(adminId);
        System.out.println(adminId);
        List<XlgUser> allTaskByPage = xlgUserService.getAllTaskByPage(page, req);
        System.out.println(Arrays.toString(allTaskByPage.toArray()));
        XlgUserDTO dto = new XlgUserDTO();
        if (CollectionUtils.isNotEmpty(allTaskByPage)) {
            XlgUser cur = allTaskByPage.get(0);
            dto.setAge(cur.getAge());
            dto.setEmail(cur.getEmail());
            dto.setId(cur.getId());
            dto.setName(cur.getName());
            dto.setPhone(cur.getPhone());
            dto.setSex(cur.getSex());
            dto.setUserId(cur.getUserId());
            dto.setPassword("");
            if (cur.getType() != RoleEnum.TEACHER.getValue()) {
                return Result.error(401, "很抱歉，您没有权限!!");
            }
            dto.setType(RoleEnum.fromValue(cur.getType()).getDesc());
        }
        return Result.ok(dto);
    }


    @PostMapping("/user/edit")
    @ResponseBody
    public Result edit(@RequestBody XlgUserDTO xlgUserDTO) {
        XlgUser request = new XlgUser();
        request.setId(xlgUserDTO.getId());
        request.setUserId(xlgUserDTO.getUserId());
        XlgUser xlgUser = xlgUserService.getAllTaskByPage(new Page(1, 1), request).stream().findFirst().orElse(null);

        XlgUser user = new XlgUser();
        user.setAge(xlgUserDTO.getAge());
        user.setEmail(xlgUserDTO.getEmail());
        user.setId(xlgUserDTO.getId());
        user.setName(xlgUserDTO.getName());
        user.setPhone(xlgUserDTO.getPhone());
        user.setSex(xlgUserDTO.getSex());
        user.setUserId(xlgUserDTO.getUserId());
        String password = xlgUserDTO.getPassword();
        if (StringUtils.isBlank(password)) {
            user.setExtParams(xlgUser.getExtParams());
        } else {
            XlgUserExtParams xlgUserExtParams = new XlgUserExtParams();
            xlgUserExtParams.setPasswordFromMd5(DigestUtils.md5DigestAsHex(password.getBytes()));
            user.setExtParams(JSON.toJSONString(xlgUserExtParams));
        }
        user.setUpdateTime(System.currentTimeMillis());
        user.setType(RoleEnum.valueOfDesc(xlgUserDTO.getType()).value);
        System.out.println(user.toString());
        int count = xlgUserService.update(user);
        if (count > 0) {
            return Result.ok("修改成功");
        } else {
            return Result.error("修改失败");
        }
    }
}
