package com.xlg.component.dao;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.xlg.component.model.XlgTaskUserProgress;

/*
 * 任务用户进度表 数据库操作
 */
@Lazy
@Repository
public class XlgTaskUserProgressDAO {
    private final String table = "xlg_task_user_progress";
    private final String columns = "task_id,user_id,finished,status,create_time,update_time";
    private final String propertiesPlaceholder = ":taskId,:userId,:finished,:status,:createTime,:updateTime";
    private final String all_columns = "id," + columns;
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public int batchInsert(List<XlgTaskUserProgress> list) {
        String sql = "insert into " + table + " (" + columns + ") values (" + propertiesPlaceholder + ")";
        MapSqlParameterSource[] parameterSources = list.stream()
                .map(e -> new MapSqlParameterSource()
                        .addValue("taskId", e.getTaskId())
                        .addValue("userId", e.getUserId())
                        .addValue("finished", e.getFinished())
                        .addValue("status", e.getStatus())
                        .addValue("createTime", e.getCreateTime())
                        .addValue("updateTime", e.getUpdateTime())
                )
                .toArray(MapSqlParameterSource[]::new);
        return namedParameterJdbcTemplate.batchUpdate(sql, parameterSources).length;
    }

    public List<XlgTaskUserProgress> getByTaskId(long taskId) {
        String sql = "select " + all_columns + " from " + table + " where task_id=:taskId";
        return namedParameterJdbcTemplate.query(sql,
                new MapSqlParameterSource("taskId", taskId),
                new BeanPropertyRowMapper<>(XlgTaskUserProgress.class));
    }

    public int updateStatus(List<Long> unfinishedIds, long taskId, int status) {
        if (CollectionUtils.isEmpty(unfinishedIds)) {
            return 0;
        }
        String sql = "update " + table + " set status =:status, update_time =:updateTime where task_id =:taskId and id =:id";
        MapSqlParameterSource[] source = unfinishedIds.stream().map(id -> new MapSqlParameterSource()
                .addValue("taskId", taskId)
                .addValue("status", status)
                .addValue("updateTime", System.currentTimeMillis())
                .addValue("id", id)
        ).toArray(MapSqlParameterSource[]::new);
        return namedParameterJdbcTemplate.batchUpdate(sql, source).length;
    }
}