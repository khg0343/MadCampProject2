package com.example.madcampproject2;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
public class LoginResult {

    private String name;

    private String email;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}