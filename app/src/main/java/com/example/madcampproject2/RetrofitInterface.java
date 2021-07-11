package com.example.madcampproject2;

import java.util.HashMap;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/login")
    Call<LoginResult> executeLogin(@Body HashMap<String, Object> map);

    @POST("/signup")
    Call<Void> executeSignup (@Body HashMap<String, Object> map);


    @POST("./active_users")
    Call<Void> findActiveUsers (@Body HashMap<String, Boolean> map);

    @POST("/active")
    Call<Void> executeActive (@Body HashMap<String, Object> map);
}
