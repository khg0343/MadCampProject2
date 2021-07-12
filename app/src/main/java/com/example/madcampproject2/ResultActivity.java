package com.example.madcampproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ResultActivity extends AppCompatActivity {

    private double userLatitude = LoginResult.getLoginUser().getLatitude();
    private double userLongitude = LoginResult.getLoginUser().getLongitude();

    private double connectUserLatitude = LoginResult.getConnectUser().getLatitude();
    private double connectUserLongitude = LoginResult.getConnectUser().getLongitude();

    private double ourLatitude = ;
    private double ourLongitude = ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


    }

    public double getOurDistance() { // 우리의 시작 시점 거리 구하기
        double squareLatitude = Math.pow((userLatitude - connectUserLatitude), 2);
        double squareLongitude = Math.pow((userLongitude - connectUserLongitude), 2);
        return Math.pow((squareLatitude + squareLongitude), 0.5);
    }

    public double getUserTravel() { // 내가 이동해 온 거리 구하기
        double squareLatitude = Math.pow((userLatitude - ourLatitude), 2);
        double squareLongitude = Math.pow((userLongitude - ourLongitude), 2);
        return Math.pow((squareLatitude + squareLongitude), 0.5);
    }

    public double getConnectUserTravel() { // 상대방이 이동해 온 거리 구하기
        double squareLatitude = Math.pow((connectUserLatitude - ourLatitude), 2);
        double squareLongitude = Math.pow((connectUserLongitude - ourLongitude), 2);
        return Math.pow((squareLatitude + squareLongitude), 0.5);
    }

}