package com.xlg.component.common;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-21
 */
public class UserToken {

    public String userName;
    public String password;
    public Boolean rememberMe;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
