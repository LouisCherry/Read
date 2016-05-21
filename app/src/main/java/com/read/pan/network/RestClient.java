package com.read.pan.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.read.pan.network.api.BookApi;
import com.read.pan.network.api.UserApi;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pan on 2016/5/14.
 * Restful风格访问
 */
public class RestClient {
    private static final String BASE_URL= "http://192.168.191.1:8080/ReadWeb/";
    private static RestClient instance = new RestClient();
    private static UserApi userApi;
    private static BookApi bookApi;
    private RestClient(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        userApi = retrofit.create(UserApi.class);
        bookApi=retrofit.create(BookApi.class);
    }

    public static UserApi userApi(){
        return userApi;
    }
    public static BookApi bookApi(){ return bookApi;}
}
