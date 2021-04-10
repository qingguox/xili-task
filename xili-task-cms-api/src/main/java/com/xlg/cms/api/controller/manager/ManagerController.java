package com.xlg.cms.api.controller.manager;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xlg.cms.api.dto.TaskSaveDTO;
import com.xlg.cms.api.dto.XlgUserDTO;
import com.xlg.cms.api.model.Result;
import com.xlg.cms.api.model.UploadFile;
import com.xlg.cms.api.utils.ExcelUtils;
import com.xlg.cms.api.utils.TemplateStoreFileUtil;
import com.xlg.component.common.Page;
import com.xlg.component.dto.XlgUserExtParams;
import com.xlg.component.enums.RoleEnum;
import com.xlg.component.model.XlgUser;
import com.xlg.component.service.XlgUserService;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-21
 */
@Controller
@RequestMapping("/manager")
public class ManagerController {


    private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);

    @Autowired
    private XlgUserService xlgUserService;

    /**
     * 页面跳转
     * @param
     * @return
     */
    @RequestMapping("/")
    public String page(Model model) {
        model.addAttribute("isCaptcha", true);
        return "manager";
    }

    /**
     * 页面跳转
     * @param
     * @return
     */
    @RequestMapping("/mq")
    public void mq(Model model, HttpServletResponse response) throws IOException {
        model.addAttribute("isCaptcha", true);
        response.sendRedirect("http://www.qingguox.xyz:8999/");
    }

    /**
     * 页面list
     */
    @RequestMapping("/list")
    @ResponseBody
    public Result page(Model model, @RequestParam("pageNo") int pageNo, @RequestParam("pageSize") int pageSize) {
        Page page = new Page(pageNo, pageSize);
        List<XlgUser> users = xlgUserService.getAllTaskByPage(page, new XlgUser());
        List<XlgUserDTO> xlgUserDTOS = users.stream().map(cur -> {
            XlgUserDTO dto = new XlgUserDTO();
            dto.setAge(cur.getAge());
            dto.setEmail(cur.getEmail());
            dto.setId(cur.getId());
            dto.setName(cur.getName());
            dto.setPhone(cur.getPhone());
            dto.setSex(cur.getSex());
            dto.setUserId(cur.getUserId());
            dto.setType(RoleEnum.fromValue(cur.getType()).getDesc());
            return dto;
        }).sorted(Comparator.comparingLong(XlgUserDTO::getId)).collect(Collectors.toList());
        return Result.ok(xlgUserDTOS);
    }

    @RequestMapping("/remove")
    @ResponseBody
    public Result remove(@RequestParam("id") long id, @RequestParam("time") long time) {
        // 1. task变更
        int count = xlgUserService.delete(id);
        if (count > 0) {
            return Result.ok("删除成功!");
        } else {
            return Result.error("删除失败!");
        }
    }

    /**
     * 管理员修改数据
     * @param xlgUserDTO
     * @return
     */
    @RequestMapping("/edit")
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


    /**
     * 任务状态枚举
     */
    @RequestMapping("/status")
    @ResponseBody
    public Result status() {
        RoleEnum[] values = RoleEnum.values();
        List<Map<String, Object>> collect =
                Arrays.stream(values).map(RoleEnum::toValueDescMap).collect(Collectors.toList());
        return Result.ok(collect);
    }

    /**
     * 用户列表查找
     */
    @RequestMapping("/search")
    @ResponseBody
    public Result search(@RequestBody HashMap<String, String> map) {
        String name = map.get("name");
        String status = map.get("status");
        String pageNo = map.get("pageNo");
        String pageSize = map.get("pageSize");
        int stats = 0;
        Page page = new Page(Integer.parseInt(pageNo), Integer.parseInt(pageSize));
        if (StringUtils.isNotEmpty(status)) {
            stats = Integer.parseInt(status);
        }
        logger.info("name={}, stats={}, pageNo={}, pageSize={}", name, stats, pageNo, pageSize);
        XlgUser request = new XlgUser();
        if (StringUtils.isNotEmpty(name)) {
            request.setName(name);
        }
        request.setType(stats);
        List<XlgUser> users = xlgUserService.getAllTaskByPage(page, request);
        List<XlgUserDTO> xlgUserDTOS = users.stream().map(cur -> {
            XlgUserDTO dto = new XlgUserDTO();
            dto.setAge(cur.getAge());
            dto.setEmail(cur.getEmail());
            dto.setId(cur.getId());
            dto.setName(cur.getName());
            dto.setPhone(cur.getPhone());
            dto.setSex(cur.getSex());
            dto.setUserId(cur.getUserId());
            dto.setType(RoleEnum.fromValue(cur.getType()).getDesc());
            return dto;
        }).sorted(Comparator.comparingLong(XlgUserDTO::getId)).collect(Collectors.toList());
        return Result.ok(xlgUserDTOS);
    }

    /**
     * 导出
     */
    @GetMapping(value = "/export")
    public void templateExport(@RequestParam("name") String name, @RequestParam("status") String status,
            @RequestParam("pageNo") String pageNo, @RequestParam("pageSize") String pageSize,
            HttpServletResponse response, HttpServletRequest request) {
        Map<String, String> map = Maps.newHashMap();
        map.put("name", name);
        map.put("status", status);
        map.put("pageNo", pageNo);
        map.put("pageSize", pageSize);
        Result search = search((HashMap<String, String>) map);
        Object msg = search.get("msg");
        List<XlgUserDTO> dataList = Lists.newArrayList();
        if (!Objects.isNull(msg)) {
            dataList.addAll((Collection<? extends XlgUserDTO>) msg);
        }

        Workbook workbook = ExcelUtils.createWorkBook();
        Sheet sheet = workbook.createSheet();
        ExcelUtils.writeHeader(sheet, ImmutableList.of("id", "工号", "姓名", "年龄", "性别", "工种", "电话", "邮箱"));

        if (isNotEmpty(dataList)) {
            dataList.forEach(data -> {
                List<Object> values = Lists.newArrayList();
                values.add(data.getId());
                values.add(data.getUserId());
                values.add(data.getName());
                values.add(data.getAge());
                values.add(data.getSex());
                values.add(data.getType());
                values.add(data.getPhone());
                values.add(data.getEmail());
                ExcelUtils.writeRow(sheet, values);
            });
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
//            TemplateStoreFileUtil.download2(request, response, baos.toByteArray(), "用户名单-", "xls");
            TemplateStoreFileUtil.download(response, baos.toByteArray(), "用户列表", ".xls");
        } catch (IOException e) {
            logger.error("生成xls/xlsx文件失败", e);
        }
    }

    /**
     * 学生和老师信息
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @RequestMapping("/add")
    @ResponseBody
    public Result addTask(@RequestBody TaskSaveDTO taskSaveDTO) throws IOException, InvalidFormatException {

        logger.info("taskSaveDTO={}", JSON.toJSONString(taskSaveDTO));
        UploadFile file = taskSaveDTO.getFile();
        UploadFile indFile = taskSaveDTO.getIndFile();
        List<XlgUser> userList = Lists.newArrayList();
        if (file != null) {
            String urlPath = file.url;
            List<XlgUser> xlgUsers = progress(urlPath, file.name);
            userList.addAll(xlgUsers);
        }
        if (indFile != null) {
            String urlPath = indFile.url;
            List<XlgUser> xlgUsers = progress(urlPath, indFile.name);
            userList.addAll(xlgUsers);
        }
        logger.info("insert into xlg_user userList={}", JSON.toJSONString(userList));
        xlgUserService.batchInsert(userList);
        return Result.ok();
    }

    private List<XlgUser> progress(String urlPath, String fileName) throws IOException, InvalidFormatException {
        Sheet sheet = getSheet(urlPath, fileName);
        List<XlgUser> userList = Lists.newArrayList();
        int rows = sheet.getLastRowNum();
        for (int i = 1; i <= rows; i++) {
            Row row = sheet.getRow(i);
            long userId = (long) row.getCell(0).getNumericCellValue();
            String name = row.getCell(1).getStringCellValue();
            int age = (int) row.getCell(2).getNumericCellValue();
            String sex = row.getCell(3).getStringCellValue();
            String type = row.getCell(4).getStringCellValue();
            long phone = (long) row.getCell(5).getNumericCellValue();
            String email = row.getCell(6).getStringCellValue();

            long now = System.currentTimeMillis();
            XlgUserExtParams xlgUserExtParams = new XlgUserExtParams();
            xlgUserExtParams.setPasswordFromMd5(DigestUtils.md5DigestAsHex("666666".getBytes()));
            XlgUser user = new XlgUser();
            user.setUserId(userId);
            user.setName(name);
            user.setAge(age);
            user.setSex(sex);
            user.setType(RoleEnum.valueOfDesc(type).getValue());
            user.setPhone(phone);
            user.setEmail(email);
            user.setCreateTime(now);
            user.setUpdateTime(now);
            user.setExtParams(JSON.toJSONString(xlgUserExtParams));
            userList.add(user);
        }
        return userList;
    }

    private Sheet getSheet(String urlPath, String fileName) throws IOException, InvalidFormatException {
        System.out.println("url:" + urlPath);
        URL url = new URL(urlPath);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setConnectTimeout(3000);
        // 设置 User-Agent 避免被拦截
        http.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
        String contentType = http.getContentType();
        System.out.println("contentType: " + contentType);
        // 获取文件大小
        long length = http.getContentLengthLong();
        System.out.println("文件大小：" + (length / 1024) + "KB");
        // 获取文件名
        InputStream inputStream = http.getInputStream();

        Workbook workbook = null;
        workbook = WorkbookFactory.create(inputStream);
        // 建立链接从请求中获取数据
        Sheet sheet = workbook.getSheetAt(0);
        return sheet;
    }



}
