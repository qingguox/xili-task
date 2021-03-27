package com.xlg.component.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.xlg.component.model.XlgTaskUser;

/*
 * 任务用户表 数据库操作
 */
@Lazy
@Repository
public class XlgTaskUserDAO {
    private final String table = "xlg_task_user";
    private final String columns = "task_id,user_id,status,create_id,create_time,update_time";
    private final String propertiesPlaceholder = ":taskId,:userId,:status,:createId,:createTime,:updateTime";

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int batchInsert(List<XlgTaskUser> list) {
        String sql = "insert into " + table + " (" + columns + ") values (" + propertiesPlaceholder + ")";
        MapSqlParameterSource[] parameterSources = list.stream()
                .map(e -> new MapSqlParameterSource()
                        .addValue("taskId", e.getTaskId())
                        .addValue("userId", e.getUserId())
                        .addValue("status", e.getStatus())
                        .addValue("createId", e.getCreateId())
                        .addValue("createTime", e.getCreateTime())
                        .addValue("updateTime", e.getUpdateTime())
                )
                .toArray(MapSqlParameterSource[]::new);
        return namedParameterJdbcTemplate.batchUpdate(sql, parameterSources).length;
    }

    public long getUserCountByTaskId(long taskId) {
        String sql = "select count(1) from " + table + " where task_id =:taskId";
        long count =
                namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource("taskId", taskId), Long.class);
        return count;
    }
}