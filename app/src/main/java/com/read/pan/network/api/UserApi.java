package com.read.pan.network.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by pan on 2016/5/14.
 */
public interface UserApi {
    @FormUrlEncoded
    @POST("user/login")
    /**
     * 登录
     * @param username 用户名
     * @param pass 密码
     */
    Call<ResponseBody> login(@Field("username")String username, @Field("password") String pass);

    @FormUrlEncoded
    @POST("user/regist")
    /**
     * 注册
     * @param  username 用户名
     * @param  pass 密码
     * @param email 邮箱
     */
    void regist(@Field("username")String username,@Field("password") String pass,
                @Field("email")String email);

    @Multipart
    @POST("user/updateInfo")
    /**
     * 更改信息
     * @param  username 用户名
     * @param  pass 密码
     * @param userId 用户id
     * @param avater 头像
     */
    void update(@Part("username")String username, @Part("password") String pass,
                @Part("userId")String userId, @Part("avatar")RequestBody avatar);
}
