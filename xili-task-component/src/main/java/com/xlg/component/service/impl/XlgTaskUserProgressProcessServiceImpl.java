package com.xlg.component.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xlg.component.dto.XlgTaskUserProgressDTO;
import com.xlg.component.enums.AllStatusEnum;
import com.xlg.component.enums.IndicatorEnum;
import com.xlg.component.enums.UserProgressStatusEnum;
import com.xlg.component.model.XlgRegister;
import com.xlg.component.model.XlgTaskCondition;
import com.xlg.component.model.XlgTaskFinishDetail;
import com.xlg.component.model.XlgTaskUserProgress;
import com.xlg.component.model.XlgTaskUserProgressItem;
import com.xlg.component.service.XlgRegisterService;
import com.xlg.component.service.XlgTaskConditionService;
import com.xlg.component.service.XlgTaskFinishDetailService;
import com.xlg.component.service.XlgTaskUserProgressItemService;
import com.xlg.component.service.XlgTaskUserProgressProcessService;
import com.xlg.component.service.XlgTaskUserProgressService;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-04-10
 * 进度处理
 */
@Lazy
@Service
public class XlgTaskUserProgressProcessServiceImpl implements XlgTaskUserProgressProcessService {

    private static final Logger logger = LoggerFactory.getLogger(XlgTaskUserProgressProcessServiceImpl.class);
    @Autowired
    private XlgRegisterService xlgRegisterService;
    @Autowired
    private XlgTaskConditionService xlgTaskConditionService;
    @Autowired
    private XlgTaskUserProgressService xlgTaskUserProgressService;
    @Autowired
    private XlgTaskFinishDetailService xlgTaskFinishDetailService;
    @Autowired
    private XlgTaskUserProgressItemService xlgTaskUserProgressItemService;

    @Override
    public void processProgress(XlgTaskUserProgressDTO dto) {
        logger.info("[XlgTaskUserProgressProcessServiceImpl] progress receive dto={}", JSON.toJSONString(dto));

        long taskId = dto.getTaskId();
        long userId = dto.getUserId();
        long indicator = dto.getIndicator();
        int actionDate = dto.getActionDate();
        long actionValue = dto.getActionValue();
        long actionTime = dto.getActionTime();

        //1. 查看监控是否有，也就是任务是否过期
        XlgRegister xlgRegister =
                xlgRegisterService.getByTaskIdAndTimeAndStatus(taskId, actionTime, AllStatusEnum.TACH.getValue());
        //1-1. 没有查询到监控数据，则return 无法完成，过时了
        if (xlgRegister == null) {
            logger.info("[XlgTaskUserProgressProcessServiceImpl] processProgress taskId={}, xlgRegister is detach 失效!",
                    taskId);
            return;
        }
        //1-2. 有监控数据, 拿出task_condition
        List<XlgTaskCondition> xlgTaskConditionList = xlgTaskConditionService.getByTaskId(taskId);
        // 过滤出我们需要的condition,
        List<XlgTaskCondition> conditionList =
                xlgTaskConditionList.stream().filter(x -> x.getIndicator() == indicator).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(xlgTaskConditionList) || CollectionUtils.isEmpty(conditionList)) {
            logger.info(
                    "[XlgTaskUserProgressProcessServiceImpl] processProgress taskId={}, this task not we need "
                            + "condition, curIndicator={}",
                    taskId, indicator);
            return;
        }
        // condition转为 map 为进度item使用
        Map<Long, XlgTaskCondition> conditionIdMap =
                conditionList.stream().collect(Collectors.toMap(XlgTaskCondition::getId, Function.identity()));

        //2. 查询progress
        XlgTaskUserProgress userProgress = xlgTaskUserProgressService.getByTaskIdAndUserId(taskId, userId);
        //2-1. 没有progress，则初始化
        if (userProgress == null) {
            userProgress = initUserProgress(taskId, userId);
        }
        //2-2. 有progress

        //3. 用progressId, taskId, userId 找progress_item
        long progressId = userProgress.getId();
        List<XlgTaskUserProgressItem> xlgTaskUserProgressItemList = xlgTaskUserProgressItemService.getByProgressIdAndUserId(progressId, userId);
        //3-1. 没有，初始化
        if (CollectionUtils.isEmpty(xlgTaskUserProgressItemList)) {
            xlgTaskUserProgressItemList = initUserProgressItem(progressId, userId, taskId, xlgTaskConditionList);
        }
        // 进度item的个数
        long progressItemSize = xlgTaskUserProgressItemList.size();
//        List<XlgTaskUserProgressItem> progressItemList =
//                xlgTaskUserProgressItemList.stream().filter(curItem -> curItem.getIndicator() == indicator)
//                        .collect(Collectors.toList());
        //3-2. 有判断是否符合条件threshold，符合updateStatus。不符合，不做操作。
        xlgTaskUserProgressItemList.forEach(curItem -> {
            if (curItem.getIndicator() == indicator){
                long oldActionValue = curItem.getActionValue();
                long newActionValue = oldActionValue + actionValue;
                curItem.setActionValue(newActionValue);
                long curConditionId = curItem.getConditionId();
                // 拿到匹配的condition
                XlgTaskCondition condition = conditionIdMap.get(curConditionId);
                if (condition != null) {
                    // 如果此时可以完成，则更新状态
                    if (newActionValue >= condition.getThreshold()) {
                        curItem.setStatus(UserProgressStatusEnum.FINISHED.getValue());
                    }
                }
            }
        });
        logger.info("[XlgTaskUserProgressProcessServiceImpl] processProgress update start progressItemList={}", JSON.toJSONString(xlgTaskUserProgressItemList));
        // 更新progressItem
        int itemCount = xlgTaskUserProgressItemService.batchUpdate(xlgTaskUserProgressItemList);
        if (itemCount > 0) {
            logger.info("[XlgTaskUserProgressProcessServiceImpl] processProgress update success end taskId={}", taskId);
        } else {
            logger.info("[XlgTaskUserProgressProcessServiceImpl] processProgress update error end taskId={}", taskId);
        }

        //4. 过滤出此时progressId, taskId, userId 几个完成的item 和 总的进度子项个数对比，
        long finishedItemSize = xlgTaskUserProgressItemList.stream()
                .filter(curItem -> curItem.getStatus() == UserProgressStatusEnum.FINISHED.getValue()).count();
        //4-1. 如果>= 更新progressStatus, < 不做操作
        if (finishedItemSize >= progressItemSize) {
            int count = xlgTaskUserProgressService.updateStatus(progressId, UserProgressStatusEnum.FINISHED.getValue());
            if (count > 0) {
                logger.info("[XlgTaskUserProgressProcessServiceImpl] processProgress updateProgressStatus success taskId={}", taskId);
            } else {
                logger.error("[XlgTaskUserProgressProcessServiceImpl] processProgress updateProgressStatus error taskId={}", taskId);
            }
        }
        //4-2. 更新progressFinished 完成几个condition
        int count = xlgTaskUserProgressService.updateFinishedCount(progressId, finishedItemSize);
        if (count > 0) {
            logger.info("[XlgTaskUserProgressProcessServiceImpl] processProgress updateProgressFinishedCount success taskId={}", taskId);
        } else {
            logger.error("[XlgTaskUserProgressProcessServiceImpl] processProgress updateProgressFinishedCount error taskId={}", taskId);
        }

        // 5. 记录完成情况
        XlgTaskFinishDetail xlgTaskFinishDetail = xlgTaskFinishDetailService.getTaskIdAndUserId(taskId, userId, indicator);
        if (xlgTaskFinishDetail == null) {
            xlgTaskFinishDetail = initTaskFinishDetail(taskId, userId, indicator);
        }
        if (indicator == IndicatorEnum.UPLOAD_WORK.getValue()) {
            xlgTaskFinishDetail.setExtParams(dto.getExtParams());
            int detailCount = xlgTaskFinishDetailService.updateExtParams(xlgTaskFinishDetail);
            if (detailCount > 0) {
                logger.info("[XlgTaskUserProgressProcessServiceImpl] processProgress updateTaskFinishDetail success taskId={}", taskId);
            } else {
                logger.error("[XlgTaskUserProgressProcessServiceImpl] processProgress updateTaskFinishDetail error taskId={}", taskId);
            }
        }
        logger.info("[XlgTaskUserProgressProcessServiceImpl] processProgress updateTaskFinishDetail update end !!!");
    }

    /**
     * 初始化 任务完成详情
     * @param taskId
     * @param userId
     * @param indicator
     * @return
     */
    private XlgTaskFinishDetail initTaskFinishDetail(long taskId, long userId, long indicator) {
        long nowMills = System.currentTimeMillis();
        XlgTaskFinishDetail detail = new XlgTaskFinishDetail();
        detail.setIndicator(indicator);
        detail.setTaskId(taskId);
        detail.setUserId(userId);
        detail.setCreateTime(nowMills);
        detail.setUpdateTime(nowMills);
        detail.setExtParams(JSON.toJSONString(""));
        int count = xlgTaskFinishDetailService.batchInsert(Lists.newArrayList(detail));
        if (count > 0) {
            logger.info("[XlgTaskUserProgressProcessServiceImpl] initTaskFinishDetail success detail={}", JSON.toJSONString(detail));
        } else {
            logger.error("[XlgTaskUserProgressProcessServiceImpl] initTaskFinishDetail error detail={}", JSON.toJSONString(detail));
        }
        detail = xlgTaskFinishDetailService.getTaskIdAndUserId(taskId, userId, indicator);
        return detail;
    }

    /**
     * 对用户进度子项表初始化
     * @param progressId
     * @param userId
     * @param taskId
     * @param conditionList
     * @return
     */
    private List<XlgTaskUserProgressItem> initUserProgressItem(long progressId, long userId, long taskId, List<XlgTaskCondition> conditionList) {
        List<XlgTaskUserProgressItem> progressItemList = conditionList.stream().map(curCondition -> {
            long nowMills = System.currentTimeMillis();
            XlgTaskUserProgressItem item = new XlgTaskUserProgressItem();
            item.setTaskId(taskId);
            item.setProgressId(progressId);
            item.setIndicator(curCondition.getIndicator());
            item.setConditionId(curCondition.getId());
            item.setUserId(userId);
            item.setActionValue(0L);
            item.setStatus(UserProgressStatusEnum.DOING.getValue());
            item.setCreateTime(nowMills);
            item.setUpdateTime(nowMills);
            return item;
        }).collect(Collectors.toList());
        int count = xlgTaskUserProgressItemService.batchInsert(progressItemList);
        if (count > 0) {
            logger.info("[XlgTaskUserProgressProcessServiceImpl] initUserProgressItem success progressItemList={}", JSON.toJSONString(progressItemList));
        } else {
            logger.error("[XlgTaskUserProgressProcessServiceImpl] initUserProgressItem error progressItemList={}", JSON.toJSONString(progressItemList));
        }
        progressItemList = xlgTaskUserProgressItemService.getByProgressIdAndUserId(progressId, userId);
        return progressItemList;
    }

    /**
     * 对用户进度初始化
     * @param taskId
     * @param userId
     * @return
     */
    private XlgTaskUserProgress initUserProgress(long taskId, long userId) {
        long nowMills = System.currentTimeMillis();
        XlgTaskUserProgress userProgress = new XlgTaskUserProgress();
        userProgress.setUserId(userId);
        userProgress.setTaskId(taskId);
        userProgress.setStatus(UserProgressStatusEnum.DOING.getValue());
        userProgress.setFinished(0);
        userProgress.setCreateTime(nowMills);
        userProgress.setUserId(nowMills);
        int count = xlgTaskUserProgressService.batchInsert(Lists.newArrayList(userProgress));
        if (count > 0) {
            logger.info("[XlgTaskUserProgressProcessServiceImpl] initUserProgress success userProgress={}", JSON.toJSONString(userProgress));
        } else {
            logger.error("[XlgTaskUserProgressProcessServiceImpl] initUserProgress error userProgress={}", JSON.toJSONString(userProgress));
        }
        userProgress = xlgTaskUserProgressService.getByTaskIdAndUserId(taskId, userId);
        return userProgress;
    }
}
