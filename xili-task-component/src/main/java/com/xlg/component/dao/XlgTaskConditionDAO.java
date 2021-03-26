package com.xlg.component.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.xlg.component.model.XlgTaskCondition;

/*
 * 任务条件表x 数据库操作
 */
@Lazy
@Repository
public class XlgTaskConditionDAO {
    private final String table = "xlg_task_condition";
    private final String columns =
            "task_id,indicator,indicator_name,threshold,status,ext_params,description,create_time,update_time";
    private final String propertiesPlaceholder =
            ":taskId,:indicator,:indicatorName,:threshold,:status,:extParams,:description,:createTime,:updateTime";

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public int batchInsert(List<XlgTaskCondition> list) {
        String sql = "insert into " + table + " (" + columns + ") values (" + propertiesPlaceholder + ")";
        MapSqlParameterSource[] parameterSources = list.stream()
                .map(e -> new MapSqlParameterSource()
                        .addValue("taskId", e.getTaskId())
                        .addValue("indicator", e.getIndicator())
                        .addValue("indicatorName", e.getIndicatorName())
                        .addValue("threshold", e.getThreshold())
                        .addValue("status", e.getStatus())
                        .addValue("extParams", e.getExtParams())
                        .addValue("description", e.getDescription())
                        .addValue("createTime", e.getCreateTime())
                        .addValue("updateTime", e.getUpdateTime())
                )
                .toArray(MapSqlParameterSource[]::new);
        return namedParameterJdbcTemplate.batchUpdate(sql, parameterSources).length;
    }

    public List<XlgTaskCondition> getByTaskId(long taskId) {
        String sql = "select * from " + table + " where task_id =:taskId";
        return namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource("taskId", taskId),
                new BeanPropertyRowMapper<>(XlgTaskCondition.class));
    }
}