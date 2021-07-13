package com.example.madcampproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.util.Log;
import android.widget.TextView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class ResultActivity extends AppCompatActivity {

    private MapView mapView;
    private GpsTracker gpsTracker;

    double distLoginUser;
    double distConnectUser;

    private TextView txtLoginUserName;
    private TextView txtLoginUserDistance;
    private TextView txtLoginUserScore;

    private TextView txtConnectUserName;
    private TextView txtConnectUserDistance;
    private TextView txtConnectUserScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

//        mapView = new MapView(this);
//        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.resultMapView);
//        mapViewContainer.addView(mapView);

        // 중심점 변경
//        gpsTracker = new GpsTracker(this);
//
//        double latitude = gpsTracker.getLatitude(); double longitude = gpsTracker.getLongitude();
//        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
//        mapView.setZoomLevel(7, true); // 줌 레벨 변경
//        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 3, true); // 중심점 변경 + 줌 레벨 변경 (줌 레벨 숫자가 작을수록 확대되어 보임)


//        txtLoginUserName = findViewById(R.id.txt_login_user_name_result);
//        txtLoginUserDistance = findViewById(R.id.txt_login_user_distance_result);
//        txtLoginUserScore = findViewById(R.id.txt_login_user_score_result);
//
//        txtConnectUserName = findViewById(R.id.txt_connect_user_name_result);
//        txtConnectUserDistance = findViewById(R.id.txt_connect_user_distance_result);
//        txtConnectUserScore = findViewById(R.id.txt_connect_user_score_result);
//
//
//        if(LoginResult.getLoginUser().getEmail() != null) {
//            distLoginUser = distance(
//                    LoginResult.getConnectLatitude(),
//                    LoginResult.getConnectLongitude(),
//                    LoginResult.getLoginUser().getLatitude(),
//                    LoginResult.getLoginUser().getLongitude(),
//                    "meter");
//
////            MapPOIItem marker = new MapPOIItem();
////            marker.setItemName(LoginResult.getLoginUser().getName());
////            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(LoginResult.getLoginUser().getLatitude(), LoginResult.getLoginUser().getLongitude()));
////            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
//
//        }
//        if(LoginResult.getConnectUser().getEmail() != null) {
//            distConnectUser = distance(
//                    LoginResult.getConnectLatitude(),
//                    LoginResult.getConnectLongitude(),
//                    LoginResult.getConnectUser().getLatitude(),
//                    LoginResult.getConnectUser().getLongitude(),
//                    "meter");
//
////            MapPOIItem marker = new MapPOIItem();
////            marker.setItemName(LoginResult.getConnectUser().getName());
////            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(LoginResult.getConnectUser().getLatitude(), LoginResult.getConnectUser().getLongitude()));
////            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
//        }
//
////        MapPOIItem marker = new MapPOIItem();
////        marker.setItemName("Connect");
////        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(LoginResult.getConnectLatitude(), LoginResult.getConnectLongitude()));
////        marker.setMarkerType(MapPOIItem.MarkerType.RedPin); // 기본으로 제공하는 RedPin 마커 모양.
//
//        if(LoginResult.getConnectUser().getEmail() != null && LoginResult.getLoginUser().getEmail() != null) {
//            if(distLoginUser > distConnectUser) {
//
//                txtLoginUserName.setText(LoginResult.getLoginUser().getName());
//                txtLoginUserDistance.setText((int) Math.round(distLoginUser) + "m");
//                txtLoginUserScore.setText((int)(Math.round(distLoginUser)+50));
//
//                txtConnectUserName.setText(LoginResult.getConnectUser().getName());
//                txtConnectUserDistance.setText((int) Math.round(distConnectUser) + "m");
//                txtConnectUserScore.setText((int)(Math.round(distConnectUser)));
//
//            } else if (distLoginUser < distConnectUser) {
//
//                txtLoginUserName.setText(LoginResult.getLoginUser().getName());
//                txtLoginUserDistance.setText((int) Math.round(distLoginUser) + "m");
//                txtLoginUserScore.setText((int)(Math.round(distLoginUser)));
//
//                txtConnectUserName.setText(LoginResult.getConnectUser().getName());
//                txtConnectUserDistance.setText((int) Math.round(distConnectUser) + "m");
//                txtConnectUserScore.setText((int)(Math.round(distConnectUser)+50));
//
//            } else {
//
//                txtLoginUserName.setText(LoginResult.getLoginUser().getName());
//                txtLoginUserDistance.setText((int) Math.round(distLoginUser) + "m");
//                txtLoginUserScore.setText((int)(Math.round(distLoginUser)+100));
//
//                txtConnectUserName.setText(LoginResult.getConnectUser().getName());
//                txtConnectUserDistance.setText((int) Math.round(distConnectUser) + "m");
//                txtConnectUserScore.setText((int)(Math.round(distConnectUser)+100));
//
//            }
//
//        }
//        Log.e("Result Activity::", "OnResume out");

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Result Activity::", "OnResume in");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
        startActivity(intent);
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
        Log.e("Result Activity", "Destroy");
        LoginResult.getSocket().emit("leave", LoginResult.getLoginUser().getEmail());
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