package com.xlg.cms.api.controller.student;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xlg.component.enums.TaskStatusEnum;
import com.xlg.cms.api.model.Result;
import com.xlg.cms.api.model.StudentTask;
import com.xlg.cms.api.model.TeacherTask;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-02-22
 */
@Controller
@RequestMapping("/student/task")
public class StudentTaskController {

    /**
     * 页面跳转
     */
    @RequestMapping("/list")
    @ResponseBody
    public Result page(Model model) {
        StudentTask task = new StudentTask();
        task.setCreator("wangqingwei");
        task.setCreateTime("2021-02-20 20:10:11");
        task.setEndTime("2021-02-20 20:10:11");
        task.setStartTime("2021-02-20 20:10:11");
        task.setStatus(TaskStatusEnum.fromValue(2).getDesc());
        task.setTaskDesc("fasdfaf");
        task.setTaskName("nia");
        task.setTaskId(1);
        task.setTaskUsers(20);
        task.setTaskFinished(12);
        task.setTaskUnFinished(8);
        task.setUserFinished(1);
        List<TeacherTask> list = new ArrayList<>();
        list.add(task);

        task = new StudentTask();
        task.setCreator("wangqingwei");
        task.setCreateTime("2021-02-20 20:10:11");
        task.setEndTime("2021-02-20 20:10:11");
        task.setStartTime("2021-02-20 20:10:11");
        task.setStatus(TaskStatusEnum.fromValue(3).getDesc());
        task.setTaskDesc("fasdfaf");
        task.setTaskName("nia");
        task.setTaskId(2);
        task.setTaskUsers(20);
        task.setTaskFinished(12);
        task.setTaskUnFinished(8);
        task.setUserFinished(0);
        list.add(task);
        return Result.ok(list);
    }
}
