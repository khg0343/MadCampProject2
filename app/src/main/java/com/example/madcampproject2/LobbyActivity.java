package com.example.madcampproject2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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
                finish();
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
                                LoginResult.getLoginUser().setIsActive(false);
                                Toast.makeText(LobbyActivity.this, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
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

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();

        int Level = (int)(LoginResult.getLoginUser().getScore()/1000) + 1;
        int Score = (int)LoginResult.getLoginUser().getScore()%1000;
        txtLoginUserLevel.setText(String.valueOf(Level));

        txtLevelInfo.setText(Score + "/1000");
        barLevel.setProgress(Score, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        LoginResult.getLoginUser().setIsActive(false);
        Toast.makeText(LobbyActivity.this, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
