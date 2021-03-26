package com.xlg.component.dao;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import com.github.phantomthief.tuple.Tuple;
import com.github.phantomthief.tuple.TwoTuple;
import com.xlg.component.model.XlgTask;

/*
 * 任务表 数据库操作
 */
@Lazy
@Repository
public class XlgTaskDAO {
    private final String table = "xlg_task";
    private final String columns = "name,description,status,create_id,ext_params,create_time,start_time,end_time";
    private final String propertiesPlaceholder = ":name,:description,:status,:createId,:extParams,:createTime,:startTime,:endTime";
    private final String all_columns = "id," + columns;
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int batchInsert(List<XlgTask> list) {
        String sql = "insert into " + table + " (" + columns + ") values (" + propertiesPlaceholder + ")";
        MapSqlParameterSource[] parameterSources = list.stream()
                .map(e -> new MapSqlParameterSource()
                        .addValue("name", e.getName())
                        .addValue("description", e.getDescription())
                        .addValue("status", e.getStatus())
                        .addValue("createId", e.getCreateId())
                        .addValue("extParams", e.getExtParams())
                        .addValue("createTime", e.getCreateTime())
                        .addValue("startTime", e.getStartTime())
                        .addValue("endTime", e.getEndTime())
                        .addValue("updateTime", e.getCreateTime())
                )
                .toArray(MapSqlParameterSource[]::new);
        return namedParameterJdbcTemplate.batchUpdate(sql, parameterSources).length;
    }

    public long insert(XlgTask e) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into " + table + " (" + columns + ") values (" + propertiesPlaceholder + ")";
        MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                        .addValue("name", e.getName())
                        .addValue("description", e.getDescription())
                        .addValue("status", e.getStatus())
                        .addValue("createId", e.getCreateId())
                        .addValue("extParams", e.getExtParams())
                        .addValue("createTime", e.getCreateTime())
                        .addValue("startTime", e.getStartTime())
                        .addValue("endTime", e.getEndTime())
                .addValue("updateTime", e.getCreateTime());
        namedParameterJdbcTemplate.update(sql, parameterSource, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void updateStatus(long taskId, long time, int status) {
        String sql = "update " + table + " set status =:status, update_time =:updateTime where id =:taskId";
        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("taskId", taskId)
                .addValue("status", status)
                .addValue("updateTime", time);
        namedParameterJdbcTemplate.update(sql, source);
    }

    public List<XlgTask> getAllTaskByPage(XlgTask model, int offset, int limit) {
        String sql = "select " + all_columns + " from " + table + getCondition(model, offset, limit).getFirst();
        return namedParameterJdbcTemplate.query(sql,
                getCondition(model, 0, 0).getSecond(),
                new BeanPropertyRowMapper<>(XlgTask.class));
    }

    public int update(XlgTask model) {
        model.setUpdateTime(System.currentTimeMillis());
        String sql = "update " + table + " set name=:name, description=:description,update_time=:updateTime where id=:id ";
        return namedParameterJdbcTemplate.update(sql,
                new MapSqlParameterSource("name", model.getName())
                        .addValue("description", model.getDescription())
                        .addValue("updateTime", model.getUpdateTime())
                        .addValue("id", model.getId()));
    }

    private TwoTuple
            <String, MapSqlParameterSource> getCondition(XlgTask model, int offset, int limit) {
        StringBuffer buffer = new StringBuffer(" where 1=1");
        MapSqlParameterSource source = new MapSqlParameterSource();
        if (model.getId() != 0L) {
            buffer.append(" and id = :id");
            source.addValue("id", model.getId());
        }
        if (model.getName() != null && model.getName() != StringUtils.EMPTY) {
            buffer.append(" and name like :name");
            source.addValue("name", "%" + model.getName() + "%");
        }
        if (model.getDescription() != null && model.getDescription() != StringUtils.EMPTY) {
            buffer.append(" and description like :description");
            source.addValue("description", "%" + model.getDescription() + "%");
        }
        if (model.getStatus() != 0) {
            buffer.append(" and status = :status");
            source.addValue("status", model.getStatus());
        }
        if (model.getCreateId() != 0) {
            buffer.append(" and create_id = :createId");
            source.addValue("createId", model.getCreateId());
        }
        if (model.getCreateTime() != 0) {
            buffer.append(" and create_time = :createTime");
            source.addValue("createTime", model.getCreateTime());
        }
        if (model.getStartTime() != 0L) {
            buffer.append(" and start_time >= :startTime");
            source.addValue("startTime", model.getStartTime());
        }
        if (model.getEndTime() != 0L) {
            buffer.append(" and start_time <= :endTime");
            source.addValue("endTime", model.getEndTime());
        }
        if (model.getUpdateTime() != 0L) {
            buffer.append(" and update_time = :updateTime");
            source.addValue("updateTime", model.getEndTime());
        }
        buffer.append(" order by id desc");
        String conditionSql = buffer.length() > 0 ? buffer.toString() : "";
        if (offset >= 0 && limit > 0) {
            conditionSql += " limit " + offset + "," + limit;
        }
        return Tuple.tuple(conditionSql, source);
    }

}