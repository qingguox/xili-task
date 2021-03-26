package com.xlg.component.dao;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.github.phantomthief.tuple.Tuple;
import com.github.phantomthief.tuple.TwoTuple;
import com.xlg.component.model.XlgUser;

/*
 * 用户表 数据库操作
 */
@Lazy
@Repository
public class XlgUserDAO {
    private final String table = "xlg_user";
    private final String columns = "user_id,name,age,sex,type,phone,email,ext_params,create_time,update_time";
    private final String propertiesPlaceholder =
            ":userId,:name,:age,:sex,:type,:phone,:email,:extParams,:createTime,:updateTime";
    private final String all_columns = "id," + columns;

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int batchInsert(List<XlgUser> list) {
        String sql = "insert into " + table + " (" + columns + ") values (" + propertiesPlaceholder + ")";
        MapSqlParameterSource[] parameterSources = list.stream()
                .map(e -> new MapSqlParameterSource()
                        .addValue("userId", e.getUserId())
                        .addValue("name", e.getName())
                        .addValue("age", e.getAge())
                        .addValue("sex", e.getSex())
                        .addValue("phone", e.getPhone())
                        .addValue("email", e.getEmail())
                        .addValue("extParams", e.getExtParams())
                        .addValue("createTime", e.getCreateTime())
                        .addValue("updateTime", e.getUpdateTime())
                )
                .toArray(MapSqlParameterSource[]::new);
        return namedParameterJdbcTemplate.batchUpdate(sql, parameterSources).length;
    }

    public XlgUser getByUserId(long userId) {
        String sql = "select * from " + table + " where user_id=:userId";
        List<XlgUser> userList = namedParameterJdbcTemplate
                .query(sql, new MapSqlParameterSource("userId", userId), new BeanPropertyRowMapper<>(XlgUser.class));
        return CollectionUtils.isEmpty(userList) ? new XlgUser() : userList.get(0);
    }

    public List<XlgUser> getAllTaskByPage(XlgUser request, int offset, int count) {
        String sql = "select " + all_columns + " from " + table + getCondition(request, offset, count).getFirst();
        return namedParameterJdbcTemplate.query(sql, getCondition(request, offset, count).getSecond(),
                new BeanPropertyRowMapper<>(XlgUser.class));
    }

    public int delete(long id) {
        String sql = "delete from " + table + " where id=:id";
        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource()
                        .addValue("id", id));
    }

    public int update(XlgUser user) {
        String sql = "update " + table + " set user_id=:userId,name=:name,age=:age,sex=:sex,type=:type,phone=:phone,email=:email where id=:id";
        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("userId", user.getUserId())
                .addValue("name", user.getName())
                .addValue("age", user.getAge())
                .addValue("sex", user.getSex())
                .addValue("type", user.getType())
                .addValue("phone", user.getPhone())
                .addValue("email", user.getEmail())
        );
    }

    private TwoTuple
            <String, MapSqlParameterSource> getCondition(XlgUser model, int offset, int limit) {
        StringBuffer buffer = new StringBuffer(" where 1=1");
        MapSqlParameterSource source = new MapSqlParameterSource();
        if (model.getId() != 0L) {
            buffer.append(" and id = :id");
            source.addValue("id", model.getId());
        }
        if (model.getUserId() != 0L) {
            buffer.append(" and user_id = :userId");
            source.addValue("userId", "%" + model.getUserId() + "%");
        }
        if (model.getName() != null && model.getName() != StringUtils.EMPTY) {
            buffer.append(" and name like :name");
            source.addValue("name", "%" + model.getName() + "%");
        }
        if (model.getAge() != 0) {
            buffer.append(" and age = :age");
            source.addValue("age", model.getAge());
        }
        if (model.getSex() != null && model.getSex() != StringUtils.EMPTY) {
            buffer.append(" and sex = :sex");
            source.addValue("sex", model.getSex());
        }
        if (model.getType() != 0L) {
            buffer.append(" and type = :type");
            source.addValue("type", model.getType());
        }
        if (model.getCreateTime() != 0) {
            buffer.append(" and create_time = :createTime");
            source.addValue("createTime", model.getCreateTime());
        }
        if (model.getPhone() != 0L) {
            buffer.append(" and phone <= :phone");
            source.addValue("phone", model.getPhone());
        }
        if (model.getEmail() != null && model.getEmail() != StringUtils.EMPTY) {
            buffer.append(" and email = :email");
            source.addValue("email", model.getEmail());
        }
        if (model.getUpdateTime() != 0L) {
            buffer.append(" and update_time = :updateTime");
            source.addValue("updateTime", model.getUpdateTime());
        }
        buffer.append(" order by id desc");
        String conditionSql = buffer.length() > 0 ? buffer.toString() : "";
        if (offset >= 0 && limit > 0) {
            conditionSql += " limit " + offset + "," + limit;
        }
        return Tuple.tuple(conditionSql, source);
    }

    public XlgUser getByName(String creator) {
        String sql = "select * from " + table + " where name=:creator";
        List<XlgUser> userList = namedParameterJdbcTemplate
                .query(sql, new MapSqlParameterSource("creator", creator), new BeanPropertyRowMapper<>(XlgUser.class));
        return CollectionUtils.isEmpty(userList) ? new XlgUser() : userList.get(0);
    }
}