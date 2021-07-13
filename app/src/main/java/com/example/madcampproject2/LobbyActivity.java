package com.example.madcampproject2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;


public class LobbyActivity extends AppCompatActivity {

    private FloatingActionButton btnShowMap;
    private Button btnLogout;

    private TextView txtLoginUserName;
    private TextView txtLoginUserEmail;
    private TextView txtLoginUserLevel;
    private TextView txtLevelInfo;
    private ProgressBar barLevel;

    @SuppressLint("NewApi")
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
//                finish();
            }
        });

        btnLogout = findViewById(R.id.btn_logout_on_lobby);
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
                                finish();
                            }
                        });
            }
        });

        txtLoginUserName = findViewById(R.id.txt_login_user_name);
        txtLoginUserEmail = findViewById(R.id.txt_login_user_email);
        txtLoginUserLevel = findViewById(R.id.txt_login_user_level);
        txtLevelInfo = findViewById(R.id.txt_level_info);
        barLevel = findViewById(R.id.bar_level);

        txtLoginUserName.setText(LoginResult.getLoginUser().getName());
        txtLoginUserEmail.setText(LoginResult.getLoginUser().getEmail());


        int Level = (int)(LoginResult.getLoginUser().getScore()/1000) + 1;
        int Score = (int)LoginResult.getLoginUser().getScore()%1000;
        txtLoginUserLevel.setText(String.valueOf(Level));
        txtLevelInfo.setText(Level + "/1000");
        barLevel.setProgress(Score, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
