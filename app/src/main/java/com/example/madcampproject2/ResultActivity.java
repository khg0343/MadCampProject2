package com.example.madcampproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private TextView txtLoginUserResult;
    private TextView txtConnectUserResult;

    String loginUserResult;
    String connectUserResult;

    double distLoginUser;
    double distConnectUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        if(LoginResult.getLoginUser().getEmail() != null) {
            distLoginUser = distance(
                    LoginResult.getConnectLatitude(),
                    LoginResult.getConnectLongitude(),
                    LoginResult.getLoginUser().getLatitude(),
                    LoginResult.getLoginUser().getLongitude(),
                    "meter");
        }
        if(LoginResult.getConnectUser().getEmail() != null) {
            distConnectUser = distance(
                    LoginResult.getConnectLatitude(),
                    LoginResult.getConnectLongitude(),
                    LoginResult.getConnectUser().getLatitude(),
                    LoginResult.getConnectUser().getLongitude(),
                    "meter");
        }



        if(LoginResult.getConnectUser().getEmail() != null && LoginResult.getLoginUser().getEmail() != null) {
            if(distLoginUser > distConnectUser) {

                loginUserResult =
                        "Name : " + LoginResult.getLoginUser().getName() + "\n" +
                        "Distance : " + (int) Math.round(distLoginUser) + "m \n" +
                        "Score : " + (int) Math.round(distLoginUser) + "+ 50 (Win Bonus) =" + (int) (Math.round(distLoginUser)+50) + "\n";

                connectUserResult =
                        "Name : " + LoginResult.getConnectUser().getName() + "\n" +
                        "Distance : " + (int) Math.round(distConnectUser) + "m \n" +
                        "Score : " + (int) Math.round(distConnectUser) + "\n";

            } else if (distLoginUser < distConnectUser) {

                loginUserResult =
                        "Name : " + LoginResult.getLoginUser().getName() + "\n" +
                        "Distance : " + (int) Math.round(distLoginUser) + "m \n" +
                        "Score : " + (int) Math.round(distLoginUser) + "\n";

                connectUserResult =
                        "Name : " + LoginResult.getConnectUser().getName() + "\n" +
                        "Distance : " + (int) Math.round(distConnectUser) + "m \n" +
                        "Score : " + (int) Math.round(distConnectUser) + "+ 50 (Win Bonus) =" + (int) (Math.round(distConnectUser)+50) + "\n";

            } else {

                loginUserResult =
                        "Name : " + LoginResult.getLoginUser().getName() + "\n" +
                        "Distance : " + (int) Math.round(distLoginUser) + "m \n" +
                        "Score : " + (int) Math.round(distLoginUser) + "+ 100 (Same Distance Bonus) =" + (int) (Math.round(distLoginUser)+50) + "\n";

                connectUserResult =
                        "Name : " + LoginResult.getConnectUser().getName() + "\n" +
                        "Distance : " + (int) Math.round(distConnectUser) + "m \n" +
                        "Score : " + (int) Math.round(distConnectUser) + "+ 100 (Same Distance Bonus) =" + (int) (Math.round(distConnectUser)+50) + "\n";

            }

        }
        else {
            loginUserResult = "null";
            connectUserResult = "null";
        }


        txtLoginUserResult = findViewById(R.id.txt_login_user_result);
        txtLoginUserResult.setText(loginUserResult);
        txtConnectUserResult = findViewById(R.id.txt_connect_user_result);
        txtConnectUserResult.setText(connectUserResult);

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