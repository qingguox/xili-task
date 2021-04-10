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
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
import com.xlg.cms.api.utils.DateUtils;
import com.xlg.cms.api.utils.ExcelUtils;
import com.xlg.cms.api.utils.TemplateStoreFileUtil;
import com.xlg.component.common.Page;
import com.xlg.component.enums.IndicatorEnum;
import com.xlg.component.enums.RoleEnum;
import com.xlg.component.enums.TaskStatusEnum;
import com.xlg.component.model.XlgTask;
import com.xlg.component.model.XlgTaskCondition;
import com.xlg.component.model.XlgTaskUserProgress;
import com.xlg.component.model.XlgUser;
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
@RequestMapping("/student/task")
public class StudentTaskController {

    private static final Logger logger = LoggerFactory.getLogger(StudentTaskController.class);
    @Autowired
    private XlgTaskService xlgTaskService;
    @Autowired
    private XlgTaskUserProgressService xlgTaskUserProgressService;
    @Autowired
    private XlgUserService xlgUserService;
    @Autowired
    private XlgTaskConditionService xlgTaskConditionService;
    @Autowired
    private XlgTaskUserService xlgTaskUserService;

    /**
     * 页面跳转
     */
    @RequestMapping("/list")
    @ResponseBody
    public Result list(@RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize,
            HttpServletRequest request) {

        // TODO 改成一个adminId 一样
        Object user = request.getSession().getAttribute("user");
        long curUserId = 0;
        if (user != null) {
            curUserId = Long.parseLong((String) user);
        }

        Page page = new Page(pageNo, pageSize);
        XlgTask model = new XlgTask();
        int finished = 0;
        List<StudentTask> list = progress(page, model, curUserId, finished);
        logger.info("[StudentTaskController] taskList={}", JSON.toJSONString(list));
        return Result.ok(list);
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
            @RequestParam("pageNo") int pageNo,
            @RequestParam("pageSize") int pageSize, HttpServletRequest request) {

        // TODO 改成一个adminId 一样
        Object user = request.getSession().getAttribute("user");
        long curUserId = 0;
        if (user != null) {
            curUserId = Long.parseLong((String) user);
        }
        logger.info(
                "taskId={}, taskName={}, status={}, creator={}, finished={}, taskDesc={}, startTime={}, endTime={}, pageNo={}, "
                        + "pageSize={}",
                taskId,
                taskName, status, curUserId, finished, taskDesc, startTime, endTime, pageNo, pageSize);

        Page page = new Page(pageNo, pageSize);
        XlgTask model = new XlgTask();
        model.setId(taskId);
        model.setName(taskName);
        model.setStatus(status);
        model.setDescription(taskDesc);
        model.setStartTime(startTime);
        model.setEndTime(endTime);
        model.setCreateId(curUserId);
        List<StudentTask> list = progress(page, model, curUserId, finished);
        return Result.ok(list);
    }


    private List<StudentTask> progress(Page page, XlgTask model, long curUserId, int finished) {
        List<XlgTask> tasks = xlgTaskService.getAllTaskByPage(page, model);
        Set<Long> taskIds = tasks.stream().map(XlgTask::getId).collect(Collectors.toSet());

        //1. 获取当前用户的所有进度，然后反查任务
        List<XlgTaskUserProgress> progressList = xlgTaskUserProgressService.getProgressListByUserId(taskIds, curUserId);
        //
        List<Long> taskIdList =
                progressList.stream().map(XlgTaskUserProgress::getTaskId).distinct().collect(Collectors.toList());

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
            StudentTask task = new StudentTask();
            task.setTaskId(curTask.getId());
            task.setTaskName(curTask.getName());
            task.setStartTime(DateUtils.format(curTask.getStartTime()));
            task.setEndTime(DateUtils.format(curTask.getEndTime()));
            task.setStatus(TaskStatusEnum.fromValue(curTask.getStatus()).getDesc());
            task.setCreateTime(DateUtils.format(curTask.getCreateTime()));
            task.setTaskDesc(curTask.getDescription());
            task.setCreator(xlgUserService.format(curTask.getCreateId()));
            task.setFile(JSON.parseObject(curTask.getExtParams(), UploadFile.class));
            task.setIndFile(indFile.get());
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
     * 任务查找
     */
    @RequestMapping("/export")
    public void export(@RequestParam("taskId") long taskId,
            @RequestParam("taskName") String taskName,
            @RequestParam("status") int status,
            @RequestParam("taskDesc") String taskDesc,
            @RequestParam("startTime") long startTime,
            @RequestParam("endTime") long endTime,
            @RequestParam("finished") int finished,
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

        Result search = search(taskId, taskName, status, taskDesc, startTime, endTime, finished, pageNo, pageSize, request);
        Object msg = search.get("msg");
        List<StudentTask> dataList = Lists.newArrayList();
        if (!Objects.isNull(msg)) {
            dataList.addAll((Collection<? extends StudentTask>) msg);
        }

        String userName = xlgUserService.format(createId);
        AtomicReference<String> creator = new AtomicReference<>();
        Workbook workbook = ExcelUtils.createWorkBook();
        Sheet sheet = workbook.createSheet();
        ExcelUtils.writeHeader(sheet,
                ImmutableList.of("任务id", "任务名称", "任务开始时间", "任务结束时间",
                        "任务状态", "任务创建时间", "任务备注", "创建人", "学生完成"));

        creator.set("");
        if (isNotEmpty(dataList)) {
            dataList.forEach(data -> {
                List<Object> values = Lists.newArrayList();
                creator.set(data.getCreator());
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
        // TODO 改成一个adminId 一样
        Object user = request.getSession().getAttribute("user");
        long curUserId = 0;
        if (user != null) {
            curUserId = Long.parseLong((String) user);
        }

        XlgUser req = new XlgUser();
        req.setUserId(curUserId);
        System.out.println(curUserId);
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
            dto.setType(RoleEnum.fromValue(cur.getType()).getDesc());
        }
        return Result.ok(dto);
    }


    @PostMapping("/user/edit")
    @ResponseBody
    public Result edit(@RequestBody XlgUserDTO xlgUserDTO) {
        XlgUser user = new XlgUser();
        user.setAge(xlgUserDTO.getAge());
        user.setEmail(xlgUserDTO.getEmail());
        user.setId(xlgUserDTO.getId());
        user.setName(xlgUserDTO.getName());
        user.setPhone(xlgUserDTO.getPhone());
        user.setSex(xlgUserDTO.getSex());
        user.setUserId(xlgUserDTO.getUserId());
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

    @PostMapping("/todo")
    @ResponseBody
    public Result toDo(@RequestBody TaskToFinished taskToFinished) {
        logger.info("[StudentTaskController] receive taskToFinished={}", JSON.toJSONString(taskToFinished));
        return Result.ok("提交成功!!");
    }

}
