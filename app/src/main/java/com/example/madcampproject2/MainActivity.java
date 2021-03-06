package com.example.madcampproject2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private boolean isLogin = false;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private SessionCallback sessionCallback = new SessionCallback();
    Session session;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        LoginResult.reset();

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }

        try {
            Log.e("socket on map::", "socket is opened 1");

            LoginResult.setSocket(IO.socket("http://192.249.18.141:443"));
            LoginResult.getSocket().connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        session = Session.getCurrentSession();
        session.addCallback(sessionCallback);

        LoginResult.setRetrofit(new Retrofit.Builder()
                .baseUrl(LoginResult.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        );

        LoginResult.setRetrofitInterface(LoginResult.getRetrofit().create(RetrofitInterface.class));

        Button btnLogin = findViewById(R.id.btn_login);
        Button btnSignUp = findViewById(R.id.btn_signup);
        FloatingActionButton btnLogout = findViewById(R.id.btn_logout);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLoginDialog();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSignupDialog();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagement.getInstance()
                        .requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                                Toast.makeText(MainActivity.this, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
                                setInActive();
                            }
                        });
            }
        });

        getAppKeyHash();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }



    private void handleLoginDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_login, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);

        AlertDialog dialog = builder.create(); // Helper for dismiss();
        dialog.show();

        Button loginBtn = view.findViewById(R.id.login);
        final EditText emailEdit = view.findViewById(R.id.emailEdit);
        final EditText passwordEdit = view.findViewById(R.id.passwordEdit);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss(); // Need to handle dialog with dismiss function;

                HashMap<String, Object> map = new HashMap<>();

                map.put("email", emailEdit.getText().toString());
                map.put("password", passwordEdit.getText().toString());

                Call<User> call = LoginResult.getRetrofitInterface().executeLogin(map);

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.code() == 200) {

                            LoginResult.setLoginUser(response.body());
                            LoginResult.setIsLogin(true);

                            Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                            startActivity(intent);
                            finish();

                        } else if (response.code() == 404) {
                            Toast.makeText(MainActivity.this, "Wrong Credentials",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(MainActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    private void handleSignupDialog() {

        View view = getLayoutInflater().inflate(R.layout.dialog_signup, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);

        AlertDialog dialog = builder.create(); // Helper for dismiss();
        dialog.show();

        Button signupBtn = view.findViewById(R.id.signup);
        final EditText nameEdit = view.findViewById(R.id.nameEdit);
        final EditText emailEdit = view.findViewById(R.id.emailEdit);
        final EditText passwordEdit = view.findViewById(R.id.passwordEdit);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss(); // Need to handle dialog with dismiss function;

                HashMap<String, Object> map = new HashMap<>();
                map.put("name", nameEdit.getText().toString());
                map.put("email", emailEdit.getText().toString());
                map.put("password", passwordEdit.getText().toString());

                Call<Void> call = LoginResult.getRetrofitInterface().executeSignup(map);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                        if (response.code() == 200) {
                            Toast.makeText(MainActivity.this,
                                    "Signed up successfully", Toast.LENGTH_LONG).show();

                            LoginResult.getLoginUser().setName(nameEdit.getText().toString());
                            LoginResult.getLoginUser().setEmail(emailEdit.getText().toString());
                            LoginResult.getLoginUser().setPassword(passwordEdit.getText().toString());
                            LoginResult.getLoginUser().setScore(0);
                            LoginResult.setIsLogin(true);

                            Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                            startActivity(intent);
                            finish();

                        } else if (response.code() == 400) {
                            Toast.makeText(MainActivity.this, "Already registered", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MainActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });



            }
        });
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("name not found", e.toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        // ?????? ?????? ??????
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // ????????????|????????? ??????????????? ?????? ????????? ????????? SDK??? ??????
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_ENABLE_REQUEST_CODE) {//???????????? GPS ?????? ???????????? ??????
            if (checkLocationServicesStatus()) {
                if (checkLocationServicesStatus()) {
                    Log.d("@@@", "onActivityResult : GPS ????????? ?????????");
                    checkRunTimePermission();
                }
            }
        }
    }

    private class SessionCallback implements ISessionCallback {

        // ???????????? ????????? ??????
        @Override
        public void onSessionOpened() {
            Log.e("onSessionOpened", "open");
            requestMe();

        }

        // ???????????? ????????? ??????
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
        }

        // ????????? ?????? ??????
        public void requestMe() {
            Log.e("Debug", "Hello");
            UserManagement.getInstance()
                    .me(new MeV2ResponseCallback() {
                        @Override
                        public void onSessionClosed(ErrorResult errorResult) {
                            Log.e("KAKAO_API", "????????? ?????? ??????: " + errorResult);
                        }

                        @Override
                        public void onFailure(ErrorResult errorResult) {
                            Log.e("KAKAO_API", "????????? ?????? ?????? ??????: " + errorResult);
                        }


                        @Override
                        public void onSuccess(MeV2Response result) {
                            UserAccount kakaoAccount = result.getKakaoAccount();
                            if (kakaoAccount != null) {

                                // ?????????
                                String email = kakaoAccount.getEmail();
                                if (email != null) Log.i("KAKAO_API", "email: " + email);
                                else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) { }
                                else { }

                                // ?????????
                                Profile profile = kakaoAccount.getProfile();
                                if (profile != null) {
                                    Log.i("KAKAO_API", "nickname: " + profile.getNickname());
                                    Log.i("KAKAO_API", "profile image: " + profile.getProfileImageUrl());
                                    Log.i("KAKAO_API", "thumbnail image: " + profile.getThumbnailImageUrl());
                                } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) { }
                                else { }

                                HashMap<String, Object> map = new HashMap<>();
                                map.put("name", profile.getNickname());
                                map.put("email", kakaoAccount.getEmail());
                                map.put("isactive", false);

                                Call<Void> call = LoginResult.getRetrofitInterface().executeSignup(map);

                                call.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {

                                        if (response.code() == 200) {
                                            Toast.makeText(MainActivity.this, "Signed up console.log(\"<5>\")successfully", Toast.LENGTH_LONG).show();
                                        } else if (response.code() == 400) {
                                            Toast.makeText(MainActivity.this, "Already registered", Toast.LENGTH_LONG).show();
                                        }

                                        LoginResult.getLoginUser().setName(profile.getNickname());
                                        LoginResult.getLoginUser().setEmail(kakaoAccount.getEmail());
                                        LoginResult.getLoginUser().setPassword("NoPassword");
                                        LoginResult.getLoginUser().setScore(0);

                                        LoginResult.setIsLogin(true);

                                        Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(MainActivity.this, t.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
        }

    }

    /*
     * ActivityCompat.requestPermissions??? ????????? ????????? ????????? ????????? ???????????? ??????????????????.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // ?????? ????????? PERMISSIONS_REQUEST_CODE ??????, ????????? ????????? ???????????? ??????????????????

            boolean check_result = true;


            // ?????? ???????????? ??????????????? ???????????????.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //?????? ?????? ????????? ??? ??????
                ;
            }
            else {
                // ????????? ???????????? ????????? ?????? ????????? ??? ?????? ????????? ??????????????? ?????? ???????????????.2 ?????? ????????? ????????????.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(MainActivity.this, "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //????????? ????????? ??????
        // 1. ?????? ???????????? ????????? ????????? ???????????????.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. ?????? ???????????? ????????? ?????????
            // ( ??????????????? 6.0 ?????? ????????? ????????? ???????????? ???????????? ????????? ?????? ????????? ?????? ???????????????.)


            // 3.  ?????? ?????? ????????? ??? ??????



        } else {  //2. ????????? ????????? ????????? ?????? ????????? ????????? ????????? ???????????????. 2?????? ??????(3-1, 4-1)??? ????????????.

            // 3-1. ???????????? ????????? ????????? ??? ?????? ?????? ????????????
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. ????????? ???????????? ?????? ?????????????????? ???????????? ????????? ????????? ???????????? ????????? ????????????.
                Toast.makeText(MainActivity.this, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.", Toast.LENGTH_LONG).show();
                // 3-3. ??????????????? ????????? ????????? ?????????. ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. ???????????? ????????? ????????? ??? ?????? ?????? ???????????? ????????? ????????? ?????? ?????????.
                // ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }

    //??????????????? GPS ???????????? ?????? ????????????
    private void showDialogForLocationServiceSetting() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n"
                + "?????? ????????? ???????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setInActive() {
        LoginResult.getLoginUser().setIsActive(false);

        // Send two user's information to server
        HashMap<String, Object> map = new HashMap<>();

        LoginResult.getLoginUser().setIsActive(false);
        map.put("email", LoginResult.getLoginUser().getEmail());
        map.put("isactive", LoginResult.getLoginUser().getIsActive());

        Call<Void> call = LoginResult.getRetrofitInterface().executeInActive(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 200) {
                    Toast.makeText(MainActivity.this,
                            "Send to Server successfully", Toast.LENGTH_LONG).show();

                } else if (response.code() == 405) {
                    Toast.makeText(MainActivity.this,
                            "Failed to send ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}