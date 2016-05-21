package com.read.pan.network.api;

import com.read.pan.entity.Book;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by pan on 2016/5/21.
 */
public interface BookApi {
    @FormUrlEncoded
    @POST("user/top")
    /**
     * 登录
     * @param offset 起始位置
     * @param limit 刷新条数
     */
    Call<Book> login(@Field("offset")int offset,@Field("limit") int limit);
}
