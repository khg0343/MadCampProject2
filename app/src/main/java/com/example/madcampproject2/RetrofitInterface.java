package com.example.madcampproject2;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/login")
    Call<User> executeLogin(@Body HashMap<String,Object> map);

    @POST("/signup")
    Call<Void> executeSignup (@Body HashMap<String, Object> map);

    @POST("/active")
    Call<Void> executeActive (@Body HashMap<String, Object> map);

    @POST("/active")
    Call<Void> executeInActive (@Body HashMap<String, Object> map);

    @POST("/score")
    Call<Void> executeScore (@Body HashMap<String, Object> map);

    @POST("./active_users")
    Call<List<User>> findActiveUsers (@Body HashMap<String, Boolean> map);

}
