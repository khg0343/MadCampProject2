package com.example.madcampproject2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Authentication result : success (user details) or error message.
 */
public class LoginResult extends AppCompatActivity {

    public static final LoginResult instance = new LoginResult();

    public static LoginResult getInstance() {
        return instance;
    }

    public static String name;

    public static String email;

    public static double latitude;

    public static double longitude;

    public static boolean isActive;


}
