package com.example.madcampproject2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Retrofit;

/**
 * Authentication result : success (user details) or error message.
 */
public class LoginResult {
    private static LoginResult instance = null;

    private static Retrofit retrofit;
    private static RetrofitInterface retrofitInterface;
    private static String BASE_URL = "http://192.249.18.141:443";

    private static String name;
    private static String email;
    private static String password;
    private static double latitude;
    private static double longitude;
    private static boolean isActive;

    private LoginResult(){
    }

    public static LoginResult getInstance() {
        if (instance == null ){
            instance = new LoginResult();
        }
        return instance;
    }

    public static Retrofit getRetrofit() { return retrofit; }
    public static RetrofitInterface getRetrofitInterface() { return retrofitInterface; }
    public static String getBaseUrl() { return BASE_URL; }

    public static void setRetrofit(Retrofit input) { retrofit = input; }
    public static void setRetrofitInterface(RetrofitInterface input) { retrofitInterface = input; }

    public static String getName() { return name; }
    public static String getEmail() { return email; }
    public static String getPassword() { return password; }
    public static double getLatitude() { return latitude; }
    public static double getLongitude() { return longitude; }
    public static boolean getIsActive() { return isActive; }

    public static void setName(String input) { name = input; }
    public static void setEmail (String input) { email = input; }
    public static void setPassword (String input) { password = input; }
    public static void setLatitude (double input) { latitude = input; }
    public static void setLongitude (double input) { longitude = input; }
    public static void setIsActive (boolean input) { isActive = input; }

//    public static void setLoginResult (Object object) {
//        instance = (LoginResult) object;
//    }

}
