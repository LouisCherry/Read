package com.read.pan.network;

/**
 * Created by pan on 2016/5/1.
 */
public class ResultCode {
    /**
     * 请求成功
     */
    public static final int SUCCESS=200;
    /**
     * 用户名已经存在
     */
    public static final int USERNAMEEXIST=201;
    /**
     * 用户不存在
     */
    public static final int USERNOTEXIST=202;
    /**
     * 用户名或密码错误
     */
    public static final int PASSWRONG=203;
    /**
     * 暂无列表信息
     */
    public static final int EMPTYLIST=204;
}
