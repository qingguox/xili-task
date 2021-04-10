package com.xlg.component.dao;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.xlg.component.model.XlgTaskFinishDetail;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-04-10
 */
@Lazy
@Repository
public class XlgTaskFinishDetailDao {

    private final String table = "xlg_task_finish_detail";
    private final String columns = "task_id,indicator,user_id,ext_params,create_time,update_time";
    private final String propertiesPlaceholder =
            ":taskId,:indicator,:userId,:extParams,:createTime,:updateTime";

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int batchInsert(List<XlgTaskFinishDetail> list) {
        String sql = "insert into " + table + " (" + columns + ") values (" + propertiesPlaceholder + ")";
        MapSqlParameterSource[] parameterSources = list.stream()
                .map(e -> new MapSqlParameterSource()
                        .addValue("taskId", e.getTaskId())
                        .addValue("indicator", e.getIndicator())
                        .addValue("extParams", e.getExtParams())
                        .addValue("userId", e.getUserId())
                        .addValue("createTime", e.getCreateTime())
                        .addValue("updateTime", e.getUpdateTime())
                )
                .toArray(MapSqlParameterSource[]::new);
        return namedParameterJdbcTemplate.batchUpdate(sql, parameterSources).length;
    }


    public XlgTaskFinishDetail getTaskIdAndUserId(long taskId, long userId, long indicator) {
        String sql = "select * from " + table + " where task_id=:taskId and user_id=:userId and indicator=:indicator";
        List<XlgTaskFinishDetail> finishDetailList = namedParameterJdbcTemplate.query(sql, new MapSqlParameterSource()
                .addValue("taskId", taskId)
                .addValue("userId", userId)
                .addValue("indicator", indicator), new BeanPropertyRowMapper<>(XlgTaskFinishDetail.class));
        return CollectionUtils.isNotEmpty(finishDetailList) ? finishDetailList.get(0) : null;
    }

    public int updateExtParams(XlgTaskFinishDetail detail) {
        String sql = "update " + table + " set ext_params=:extParams where id=:id";
        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("id", detail.getId())
                .addValue("extParams", detail.getExtParams()));
    }
}
