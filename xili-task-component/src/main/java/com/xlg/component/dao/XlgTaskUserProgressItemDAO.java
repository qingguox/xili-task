package com.xlg.component.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.xlg.component.model.XlgTaskUserProgressItem;

/*
 * 任务用户进度子项表 数据库操作
 */
@Lazy
@Repository
public class XlgTaskUserProgressItemDAO {
    private final String table = "xlg_task_user_progress_item";
    private final String columns =
            "progress_id,indicator,condition_id,task_id,user_id,action_value,status,create_time,update_time";
    private final String propertiesPlaceholder =
            ":progressId,:indicator,:conditionId,:taskId,:userId,:actionValue,:status,:createTime,:updateTime";

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int batchInsert(List<XlgTaskUserProgressItem> list) {
        String sql = "insert into " + table + " (" + columns + ") values (" + propertiesPlaceholder + ")";
        MapSqlParameterSource[] parameterSources = list.stream()
                .map(e -> new MapSqlParameterSource()
                        .addValue("progressId", e.getProgressId())
                        .addValue("indicator", e.getIndicator())
                        .addValue("conditionId", e.getConditionId())
                        .addValue("taskId", e.getTaskId())
                        .addValue("userId", e.getUserId())
                        .addValue("actionValue", e.getActionValue())
                        .addValue("status", e.getStatus())
                        .addValue("createTime", e.getCreateTime())
                        .addValue("updateTime", e.getUpdateTime())
                )
                .toArray(MapSqlParameterSource[]::new);
        return namedParameterJdbcTemplate.batchUpdate(sql, parameterSources).length;
    }

    public List<XlgTaskUserProgressItem> getByProgressIdAndUserId(long progressId, long userId) {
        String sql = "select * from " + table + " where progress_id=:progressId and user_id=:userId";
        return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource()
                .addValue("progressId", progressId)
                .addValue("userId", userId), new BeanPropertyRowMapper<>(XlgTaskUserProgressItem.class));
    }

    public int batchUpdate(List<XlgTaskUserProgressItem> list) {
        long nowTime = System.currentTimeMillis();
        String sql = "update " + table + " set action_value=:actionValue, status=:status, update_time=:updateTime where id =:id";
        MapSqlParameterSource[] parameterSources = list.stream()
                .map(e -> new MapSqlParameterSource()
                        .addValue("actionValue", e.getActionValue())
                        .addValue("status", e.getStatus())
                        .addValue("updateTime", nowTime)
                        .addValue("id", e.getId())
                )
                .toArray(MapSqlParameterSource[]::new);
        return namedParameterJdbcTemplate.batchUpdate(sql, parameterSources).length;
    }
}