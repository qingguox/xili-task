package com.xlg.cms.api.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-22
 */
public class XlgUserDTO {
    /**
     * 主键ID
     */
    public long id;

    /**
     * 用户ID==工号
     */
    public long userId;

    /**
     * 用户姓名 小写
     */
    public String name;

    /**
     * 年龄
     */
    public int age;

    /**
     * 性别
     */
    public String sex;

    /**
     * 工种
     */
    public String type;

    /**
     * 电话
     */
    public long phone;

    /**
     * qq邮箱
     */
    public String email;

    /**
     * 密码
     */
    public String password;

    /**
     * 扩展字段
     */
    public String extParams;

    /**
     * 创建时间
     */
    public long createTime;

    /**
     * 修改时间
     */
    public long updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExtParams() {
        return extParams;
    }

    public void setExtParams(String extParams) {
        this.extParams = extParams;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
