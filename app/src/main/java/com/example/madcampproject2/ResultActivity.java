package com.example.madcampproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private TextView txtLoginUserResult;
    private TextView txtConnectUserResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        String loginUserInfo;
        if(LoginResult.getLoginUser().getEmail() != null) {
            loginUserInfo =
                    "Name : " + LoginResult.getLoginUser().getName() + "\n" +
                            "Email : " + LoginResult.getLoginUser().getEmail() + "\n"+
                            "Lat : " + LoginResult.getLoginUser().getLatitude() + "\n" +
                            "Long : " + LoginResult.getLoginUser().getLongitude() + "\n";
        }
        else {
            loginUserInfo = "null";
        }


        txtLoginUserResult = findViewById(R.id.txt_login_user_result);
        txtLoginUserResult.setText(loginUserInfo);

        String loginUserResult;
        if(LoginResult.getConnectUser().getEmail() != null) {
            loginUserResult =
                    "Name : " + LoginResult.getConnectUser().getName() + "\n" +
                            "Email : " + LoginResult.getConnectUser().getEmail() + "\n" +
                            "Lat : " + LoginResult.getConnectUser().getLatitude() + "\n" +
                            "Long : " + LoginResult.getConnectUser().getLongitude() + "\n";
        }
        else {
            loginUserResult = "null";
        }

        txtConnectUserResult = findViewById(R.id.txt_connect_user_result);
        txtConnectUserResult.setText(loginUserResult);

//        firstDistance = distance(double lat1, double lon1, double lat2, double lon2, String unit)


    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if (unit == "meter") {
            dist = dist * 1609.344;
        }

        return (dist);
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}