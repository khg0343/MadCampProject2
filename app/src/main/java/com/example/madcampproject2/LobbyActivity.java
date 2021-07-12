package com.example.madcampproject2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class LobbyActivity extends AppCompatActivity {

    private FloatingActionButton btnShowMap;
    private TextView txtLoginUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        btnShowMap = findViewById(R.id.btn_show_map);
        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });
        String loginUserInfo;
        if(LoginResult.getLoginUser().getName() != null) {
            loginUserInfo =
                    "Name : " + LoginResult.getLoginUser().getName() + "\n" +
                    "Email : " + LoginResult.getLoginUser().getEmail() + "\n";
        }
        else {
            loginUserInfo = "null";
        }


        txtLoginUserInfo = findViewById(R.id.txt_login_user_info);
        txtLoginUserInfo.setText(loginUserInfo);
    }

}
