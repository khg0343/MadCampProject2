package com.example.madcampproject2.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madcampproject2.R;
import com.example.madcampproject2.SessionCallback;
import com.example.madcampproject2.ui.login.LoginViewModel;
import com.example.madcampproject2.ui.login.LoginViewModelFactory;
import com.example.madcampproject2.databinding.ActivityLoginBinding;

import com.kakao.auth.AuthType;
import com.kakao.auth.Session;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

public class LoginActivity extends AppCompatActivity {

//    private LoginViewModel loginViewModel;
//    private ActivityLoginBinding binding;

    private Button btn_custom_login;
    private Button btn_custom_login_out;
    private SessionCallback sessionCallback = new SessionCallback();
    Session session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.e("OnCreate:", "Activity Login");

        btn_custom_login = (Button) findViewById(R.id.btn_custom_login);
        btn_custom_login_out = (Button) findViewById(R.id.btn_custom_login_out);

        session = Session.getCurrentSession();
        session.addCallback(sessionCallback);

        btn_custom_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
            }
        });

        btn_custom_login_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagement.getInstance()
                        .requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                                Toast.makeText(LoginActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

}