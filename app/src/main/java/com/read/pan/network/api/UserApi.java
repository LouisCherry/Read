package com.read.pan.network.api;

import com.read.pan.entity.Book;
import com.read.pan.entity.User;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    Call<User> login(@Field("username")String username, @Field("password") String pass);

    @FormUrlEncoded
    @POST("user/regist")
    /**
     * 注册
     * @param  username 用户名
     * @param  pass 密码
     * @param email 邮箱
     */
    Call<ResponseBody> regist(@Field("username")String username,@Field("password") String pass,
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
    Call<User> update(@Part("username") RequestBody username, @Part("password") RequestBody pass,
                @Part("userId")RequestBody userId, @Part("file\";name=\"avatar\";filename=\"pp.png") RequestBody  avatar);

    @FormUrlEncoded
    @POST("user/collect/{bookId}")
    /**
     * 收藏
     * @param userId 用户id
     * @param bookId 书籍id
     */
    Call<ResponseBody> collect(@Field("userId")String userId, @Path("bookId") String bookId);

    @GET("user/{userId}/like/list")
    /**
     * 用户收藏列表
     * @param offset 起始位置
     * @param limit 刷新条数
     */
    Call<List<Book>> collectList(@Path("userId") String userId, @Query("offset") int offset, @Query("limit") int limit);
}
