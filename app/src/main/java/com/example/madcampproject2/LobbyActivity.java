package com.example.madcampproject2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;


public class LobbyActivity extends AppCompatActivity {

    private FloatingActionButton btnShowMap;
    private Button btnLogout;
    private TextView txtLoginUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        btnShowMap = findViewById(R.id.btn_show_map);
        btnLogout = findViewById(R.id.btn_logout_on_lobby);
        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserManagement.getInstance()
                        .requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(LobbyActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
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
