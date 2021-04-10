package com.xlg.component.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.xlg.component.model.XlgRegister;

/*
 * 指标表注册 数据库操作
 */
@Lazy
@Repository
public class XlgRegisterDAO {
    private final String table = "xlg_register";
    private final String columns = "task_id,status,ext_params,start_time,end_time,create_time,update_time";
    private final String propertiesPlaceholder =
            ":taskId,:status,:extParams,:startTime,:endTime,:createTime,:updateTime";

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int batchInsert(List<XlgRegister> list) {
        String sql = "insert into " + table + " (" + columns + ") values (" + propertiesPlaceholder + ")";
        MapSqlParameterSource[] parameterSources = list.stream()
                .map(e -> new MapSqlParameterSource()
                        .addValue("taskId", e.getTaskId())
                        .addValue("status", e.getStatus())
                        .addValue("extParams", e.getExtParams())
                        .addValue("startTime", e.getStartTime())
                        .addValue("endTime", e.getEndTime())
                        .addValue("createTime", e.getCreateTime())
                        .addValue("updateTime", e.getUpdateTime())
                )
                .toArray(MapSqlParameterSource[]::new);
        return namedParameterJdbcTemplate.batchUpdate(sql, parameterSources).length;
    }

    public int updateStatus(long taskId, long time, int status) {
        String sql = "update " + table + " set status =:status, update_time =:updateTime where task_id =:taskId";
        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("taskId", taskId)
                .addValue("status", status)
                .addValue("updateTime", time);
        return namedParameterJdbcTemplate.update(sql, source);
    }
}