package com.xlg.cms.api.controller.student;

import static com.xlg.component.enums.UserProgressStatusEnum.DOING;
import static com.xlg.component.enums.UserProgressStatusEnum.FINISHED;
import static com.xlg.component.enums.UserProgressStatusEnum.UNFINISHED;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
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
import com.xlg.cms.api.dto.TaskSaveDTO.ConditionInfo;
import com.xlg.cms.api.dto.XlgUserDTO;
import com.xlg.cms.api.model.Result;
import com.xlg.cms.api.model.StudentTask;
import com.xlg.cms.api.model.TaskToFinished;
import com.xlg.cms.api.model.UploadFile;
import com.xlg.cms.api.utils.AdminUtils;
import com.xlg.cms.api.utils.DateUtils;
import com.xlg.cms.api.utils.ExcelUtils;
import com.xlg.cms.api.utils.PageUtils;
import com.xlg.cms.api.utils.TemplateStoreFileUtil;
import com.xlg.component.common.Page;
import com.xlg.component.dto.XlgTaskUserProgressDTO;
import com.xlg.component.dto.XlgUserExtParams;
import com.xlg.component.enums.IndicatorEnum;
import com.xlg.component.enums.RoleEnum;
import com.xlg.component.enums.TaskStatusEnum;
import com.xlg.component.model.XlgTask;
import com.xlg.component.model.XlgTaskCondition;
import com.xlg.component.model.XlgTaskFinishDetail;
import com.xlg.component.model.XlgTaskUserProgress;
import com.xlg.component.model.XlgUser;
import com.xlg.component.service.XlgTaskConditionService;
import com.xlg.component.service.XlgTaskFinishDetailService;
import com.xlg.component.service.XlgTaskService;
import com.xlg.component.service.XlgTaskUserProgressProcessService;
import com.xlg.component.service.XlgTaskUserProgressService;
import com.xlg.component.service.XlgUserService;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-02-22
 */
@Controller
@RequestMapping("/student/task")
public class StudentTaskController {

    private static final Logger logger = LoggerFactory.getLogger(StudentTaskController.class);
    @Autowired
    private XlgTaskService xlgTaskService;
    @Autowired
    private AdminUtils adminUtils;
    @Autowired
    private XlgTaskUserProgressService xlgTaskUserProgressService;
    @Autowired
    private XlgUserService xlgUserService;
    @Autowired
    private XlgTaskConditionService xlgTaskConditionService;
    @Autowired
    private XlgTaskFinishDetailService xlgTaskFinishDetailService;
    @Autowired
    private XlgTaskUserProgressProcessService xlgTaskUserProgressProcessService;

    /**
     * 页面跳转
     */
    @RequestMapping("/list")
    @ResponseBody
    public Result list(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize,
            HttpServletRequest request) {
        long adminId = adminUtils.AdminId(request);
        boolean checkAdmin = adminUtils.CheckAdmin(request, Lists.newArrayList(RoleEnum.STUDENT));
        if (!checkAdmin) {
            return Result.ok(401, "没有权限！！");
        }
        Page page = new Page(pageNo, pageSize);
        XlgTask model = new XlgTask();
        int finished = 0;
        Result result = progress(page, model, adminId, finished);
        logger.info("[StudentTaskController] result={}", JSON.toJSONString(result));
        return result;
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
            @RequestParam("finished") int finished,
            @RequestParam("creator") String creator,
            @RequestParam("pageNo") int pageNo,
            @RequestParam("pageSize") int pageSize, HttpServletRequest request) {
        long adminId = adminUtils.AdminId(request);
        logger.info(
                "taskId={}, taskName={}, status={}, creator={}, finished={}, taskDesc={}, startTime={}, endTime={}, pageNo={}, "
                        + "pageSize={}",
                taskId,
                taskName, status, creator, finished, taskDesc, startTime, endTime, pageNo, pageSize);

        boolean checkAdmin = adminUtils.CheckAdmin(request, Lists.newArrayList(RoleEnum.STUDENT));
        if (!checkAdmin) {
            return Result.ok(401, "没有权限！！");
        }
        XlgUser user = new XlgUser();
        user.setType(RoleEnum.TEACHER.getValue());
        user.setName(creator);
        List<XlgUser> creatorList = xlgUserService.getAllTaskByPage(new Page(), user);

        System.out.println(Arrays.toString(creatorList.toArray()));
        Page page = new Page(pageNo, pageSize);
        XlgTask model = new XlgTask();
        model.setId(taskId);
        model.setName(taskName);
        model.setStatus(status);
        model.setDescription(taskDesc);
        model.setStartTime(startTime);
        model.setEndTime(endTime);

        List<StudentTask> result = creatorList.stream().map(cur -> {
            model.setCreateId(cur.getUserId());
            return progressInner(model, adminId, finished, page);
        }).flatMap(Collection::stream).collect(Collectors.toList());
        int total = result.size();
        List<StudentTask> studentTasks = PageUtils.getTaskListByPage(result, page);
        return Result.ok(total, studentTasks);
    }


    private Result progress(Page page, XlgTask model, long curUserId, int finished) {
        List<StudentTask> list = progressInner(model, curUserId, finished, page);
        int total = list.size();
        List<StudentTask> studentTasks = PageUtils.getTaskListByPage(list, page);
        logger.info("list={}", JSON.toJSONString(list));
        return Result.ok(total, studentTasks);
    }

    private List<StudentTask> progressInner(XlgTask model, long curUserId, int finished, Page page) {
        List<XlgTask> tasks = xlgTaskService.getAllTaskByPage(page, model);
        Set<Long> taskIds = tasks.stream().map(XlgTask::getId).collect(Collectors.toSet());

        logger.info("tasks={}", JSON.toJSONString(taskIds));
        //1. 获取当前用户的所有进度，然后反查任务
        List<XlgTaskUserProgress> progressList = xlgTaskUserProgressService.getProgressListByUserId(taskIds, curUserId);
        if (CollectionUtils.isEmpty(progressList)) {
            return Lists.newArrayList();
        }
        //
        List<Long> taskIdList =
                progressList.stream().map(XlgTaskUserProgress::getTaskId).distinct().collect(Collectors.toList());

        logger.info("taskIdList={}", JSON.toJSONString(taskIdList));
        // 2. 多次Id -> 一次
        List<XlgTask> taskList = tasks.stream().filter(cur -> taskIdList.contains(cur.getId())).collect(Collectors.toList());
        Map<Long, XlgTask> idTaskMap = taskList.stream().collect(Collectors.toMap(XlgTask::getId, Function.identity()));

        List<StudentTask> list = progressList.stream().map(curProgress -> {
            XlgTask curTask = Optional.ofNullable(idTaskMap.get(curProgress.getTaskId())).orElse(new XlgTask());
            List<XlgTaskCondition> conditionList = xlgTaskConditionService.getByTaskId(curTask.getId());
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
            XlgTaskFinishDetail detail = xlgTaskFinishDetailService
                    .getTaskIdAndUserId(curTask.getId(), curUserId, IndicatorEnum.UPLOAD_WORK.getValue());
            String extParams = "";
            if (detail != null) {
                extParams =  detail.getExtParams();
            }
            StudentTask task = new StudentTask();
            task.setTaskId(curTask.getId());
            task.setTaskName(curTask.getName());
            task.setStartTime(DateUtils.YYYY_MM_DD_HHMMSS.print(curTask.getStartTime()));
            task.setEndTime(DateUtils.YYYY_MM_DD_HHMMSS.print(curTask.getEndTime()));
            task.setStatus(TaskStatusEnum.fromValue(curTask.getStatus()).getDesc());
            task.setCreateTime(DateUtils.YYYY_MM_DD_HHMMSS.print(curTask.getCreateTime()));
            task.setTaskDesc(curTask.getDescription());
            task.setCreator(xlgUserService.format(curTask.getCreateId()));
            task.setFile(JSON.parseObject(curTask.getExtParams(), UploadFile.class));
            task.setIndFile(indFile.get());
            task.setUploadFile(JSON.parseObject(extParams, UploadFile.class));
            task.setConditionList(conditionInfoList);
            task.setUserFinished(
                    curProgress.getStatus() == FINISHED.getValue() ? FINISHED.getValue() : UNFINISHED.getValue());
            return task;
        }).filter(cur -> {
            if (finished == 0) {
                return true;
            } else if (finished == FINISHED.getValue()) {
                return finished == cur.getUserFinished();
            } else if (finished == UNFINISHED.getValue()) {
                return finished == cur.getUserFinished() || DOING.getValue() == cur.getUserFinished();
            }
            return false;
        }).collect(Collectors.toList());
        return list;
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
            @RequestParam("finished") int finished,
            @RequestParam("creator") String creator,
            @RequestParam("pageNo") int pageNo,
            @RequestParam("pageSize") int pageSize, HttpServletRequest request, HttpServletResponse response) {

        Object user = request.getSession().getAttribute("user");
        long createId = 0;
        if (user != null) {
            createId = Long.parseLong((String) user);
        }
        logger.info(
                "taskId={}, taskName={}, status={}, createId={}, taskDesc={}, startTime={}, endTime={}, finished={}, pageNo={}, "
                        + "pageSize={}",
                taskId,
                taskName, status, createId, taskDesc, startTime, endTime, pageNo, pageSize);

        Result search = search(taskId, taskName, status, taskDesc, startTime, endTime, finished, creator, pageNo, pageSize, request);
        Object msg = search.get("msg");
        List<StudentTask> dataList = Lists.newArrayList();
        if (!Objects.isNull(msg)) {
            dataList.addAll((Collection<? extends StudentTask>) msg);
        }

        String userName = xlgUserService.format(createId);
        AtomicReference<String> creatorN = new AtomicReference<>();
        Workbook workbook = ExcelUtils.createWorkBook();
        Sheet sheet = workbook.createSheet();
        ExcelUtils.writeHeader(sheet,
                ImmutableList.of("任务id", "任务名称", "任务开始时间", "任务结束时间",
                        "任务状态", "任务创建时间", "任务备注", "创建人", "学生完成"));

        creatorN.set("");
        if (isNotEmpty(dataList)) {
            dataList.forEach(data -> {
                List<Object> values = Lists.newArrayList();
                creatorN.set(data.getCreator());
                values.add(data.getTaskId());
                values.add(data.getTaskName());
                values.add(data.getStartTime());
                values.add(data.getEndTime());
                values.add(data.getStatus());
                values.add(data.getCreateTime());
                values.add(data.getTaskDesc());
                values.add(creator);
                values.add(data.getUserFinished() == FINISHED.getValue() ? FINISHED.desc : UNFINISHED.desc);
                ExcelUtils.writeRow(sheet, values);
            });
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            TemplateStoreFileUtil.download(response, baos.toByteArray(), "学生-" + userName + "-任务列表", ".xls");
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
            dto.setType(RoleEnum.fromValue(cur.getType()).getDesc());
            if (cur.getType() != RoleEnum.STUDENT.getValue()) {
                return Result.error(401, "很抱歉，您没有权限!!");
            }
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
            return Result.ok(200, "修改成功");
        } else {
            return Result.error(500, "修改失败");
        }
    }

    /**
     * 去完成任务
     */
    @PostMapping("/todo")
    @ResponseBody
    public Result toDo(@RequestBody TaskToFinished taskToFinished, HttpServletRequest request) {
        logger.info("[StudentTaskController] receive taskToFinished={}", JSON.toJSONString(taskToFinished));
        long adminId = adminUtils.AdminId(request);
        // 指标值
        long indicator = taskToFinished.getStatus();
        long taskId = taskToFinished.getTaskId();
        UploadFile indFile = taskToFinished.getIndFile();

        long currentMills = System.currentTimeMillis();
        int actionDate = Integer.parseInt(DateUtils.YYYY_MM_DD.print(currentMills));
        // 1. 构造进度DTO
        XlgTaskUserProgressDTO userProgressDTO = new XlgTaskUserProgressDTO();
        userProgressDTO.setUserId(adminId);
        userProgressDTO.setIndicator(indicator);
        userProgressDTO.setTaskId(taskId);
        userProgressDTO.setActionValue(1);
        userProgressDTO.setActionDate(actionDate);
        userProgressDTO.setActionTime(currentMills);
        userProgressDTO.setExtParams(JSON.toJSONString(Optional.ofNullable(indFile).orElse(new UploadFile())));
        // 2. 调用进度的处理
        logger.info("[StudentTaskController] send to progress start userProgressDTO={}", JSON.toJSONString(userProgressDTO));
        xlgTaskUserProgressProcessService.processProgress(userProgressDTO);
        logger.info("[StudentTaskController] send to progress end");
        return Result.ok("提交成功!!");
    }
}
