package com.xlg.component.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.xlg.component.model.XlgIndicator;

/*
 * 指标表 数据库操作
 */
@Lazy
@Repository
public class XlgIndicatorDAO {
    private final String table = "xlg_indicator";
    private final String columns = "indicator,name,type,content,ext_params,create_time,update_time";
    private final String propertiesPlaceholder = ":indicator,:name,:type,:content,:extParams,:createTime,:updateTime";

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public int batchInsert(List<XlgIndicator> list) {
        String sql = "insert into " + table + " (" + columns + ") values (" + propertiesPlaceholder + ")";
        MapSqlParameterSource[] parameterSources = list.stream()
                .map(e -> new MapSqlParameterSource()
                        .addValue("indicator", e.getIndicator())
                        .addValue("name", e.getName())
                        .addValue("type", e.getType())
                        .addValue("content", e.getContent())
                        .addValue("extParams", e.getExtParams())
                        .addValue("createTime", e.getCreateTime())
                        .addValue("updateTime", e.getUpdateTime())
                )
                .toArray(MapSqlParameterSource[]::new);
        return namedParameterJdbcTemplate.batchUpdate(sql, parameterSources).length;
    }

    public List<XlgIndicator> getAllIndicators() {
        String sql = "select * from " + table;
        return namedParameterJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(XlgIndicator.class));
    }
}