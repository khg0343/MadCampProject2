package com.example.madcampproject2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kakao.auth.AuthType;

import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity {

    private GpsTracker gpsTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        FloatingActionButton btnActivity = findViewById(R.id.btn_active);

        MapView mapView = new MapView(this);

        // 중심점 변경
        gpsTracker = new GpsTracker(this);

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
        // 줌 레벨 변경
        mapView.setZoomLevel(7, true);
        // 중심점 변경 + 줌 레벨 변경 (줌 레벨 숫자가 작을수록 확대되어 보임)
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 3, true);
        // 줌 인
        mapView.zoomIn(true);
        // 줌 아웃
        mapView.zoomOut(true);

        // 현재 위치 트래킹 모드
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);

        btnActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginResult.setLatitude(latitude);
                LoginResult.setLongitude(longitude);
                LoginResult.setIsActive(true);

                Log.e("Click Activate:: ", "name  " + LoginResult.getName());
                Log.e("Click Activate:: ", "email  " + LoginResult.getEmail());
                Log.e("Click Activate:: ", "email  " + LoginResult.getPassword());
                Log.e("Click Activate:: ", "latitude  " + LoginResult.getLatitude());
                Log.e("Click Activate:: ", "longitude  " + LoginResult.getLongitude());
                Log.e("Click Activate:: ", "isActive  " + LoginResult.getIsActive());

                getActiveUsers();
            }
        });


        // Pick a certain location with a pin;
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("Default Marker");
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        // pin을 mapView에 출력
        mapView.addPOIItem(marker);

        // mapView 보여주기
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.mapView);
        mapViewContainer.addView(mapView);


    }

    private void getActiveUsers() {

        HashMap<String, Boolean> map = new HashMap<>();
        map.put("isactive", true);

        Call<List<User>> call = LoginResult.getRetrofitInterface().findActiveUsers(map);

        call.enqueue(new Callback<List<User>>() {


            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Log.e("Active User", Integer.toString(response.code()));
                if (response.code() == 200) {
                    Toast.makeText(MapActivity.this,
                            "List up successfully", Toast.LENGTH_LONG).show();


                    List<User> activeUsers = response.body();
                    for(User user : activeUsers){
                        Log.e("Active User", "name : " + user.getName() + " latitude : " + user.getLatitude() + " longitude : " + user.getLongitude());
                    }


                } else if (response.code() == 406) {
                    Toast.makeText(MapActivity.this,
                            "Error", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(MapActivity.this, t.getMessage() + " Failed",
                        Toast.LENGTH_LONG).show();
            }
        });

    }


}
