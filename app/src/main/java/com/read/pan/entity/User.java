package com.read.pan.entity;

import java.util.Date;
import java.util.UUID;

/**
 * Created by pan on 2016/5/18.
 */
public class User {
    private String userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 性别
     * 0.女;1.男
     */
    private int gender;
    /**
     * 出生年月
     */
    private Date birthday;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 个性签名
     */
    private String saying;
    /**
     * 密码
     */
    private String pass;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 注册时间
     */
    private Date registTime;
    /**
     * 是否验证邮箱
     */
    private boolean ifValid;

    /**
     * 是否管理员
     * false.不是管理员;1.是管理员
     */
    private boolean flag;

    /**
     * 邮箱
     */
    private String email;

    public User() {
    }

    public User(String userName, String pass, String email) {
        this.userId = UUID.randomUUID().toString();
        this.userName = userName;
        this.pass = pass;
        this.email = email;
        this.gender = 0;
        this.registTime = new Date(System.currentTimeMillis());
        ifValid = false;
        flag = false;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSaying() {
        return saying;
    }

    public void setSaying(String saying) {
        this.saying = saying;
    }

    public Date getRegistTime() {
        return registTime;
    }

    public void setRegistTime(Date registTime) {
        this.registTime = registTime;
    }

    public boolean isIfValid() {
        return ifValid;
    }

    public void setIfValid(boolean ifValid) {
        this.ifValid = ifValid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

