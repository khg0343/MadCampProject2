package com.example.madcampproject2;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface API {

    String BASE_URL = "https://172.10.18.141:443";

    @GET("marvel")
    Call<List<MessageNet>> getHeroes();
}