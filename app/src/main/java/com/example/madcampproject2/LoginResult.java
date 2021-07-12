package com.example.madcampproject2;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import java.net.Socket;

import io.socket.client.Socket;
import retrofit2.Retrofit;

import java.net.URISyntaxException;

/**
 * Authentication result : success (user details) or error message.
 */
public class LoginResult {
    private static LoginResult instance = null;

    private static Retrofit retrofit;
    private static RetrofitInterface retrofitInterface;
    private static String BASE_URL = "http://192.249.18.141:443";
    private static Socket socket;

    private static User loginUser = new User();
    private static boolean isLogin;

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

    public static User getLoginUser() { return loginUser; }
    public static void setLoginUser(User input) { loginUser = input; }

    public static boolean getIsLogin() { return isLogin; }
    public static void setIsLogin(boolean input) { isLogin = input; }


    public static Socket getSocket() { return socket; }
    public static void setSocket(Socket input) { socket = input; }

}
