package com.example.madcampproject2;

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

import java.net.Socket;
import java.util.HashMap;

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
                Log.e("Click Activate:: ", "password  " + LoginResult.getPassword());
                Log.e("Click Activate:: ", "latitude  " + LoginResult.getLatitude());
                Log.e("Click Activate:: ", "longitude  " + LoginResult.getLongitude());
                Log.e("Click Activate:: ", "isActive  " + LoginResult.getIsActive());

                HashMap<String, String> map = new HashMap<>();

                map.put("name", LoginResult.getName());
                map.put("email", LoginResult.getEmail());
                map.put("password", LoginResult.getPassword());
                map.put("latitude", Double.toString(LoginResult.getLatitude()));
                map.put("longitude", Double.toString(LoginResult.getLongitude()));
                map.put("isactive", Boolean.toString(LoginResult.getIsActive()));

                Call<Void> call = LoginResult.getRetrofitInterface().executeActive(map);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.e("onResponse::", "waiting...");

                        if (response.code() == 200) {
                            Log.e("onResponse::", "got response 200");
                            Toast.makeText(MapActivity.this,
                                    "Send to Server successfully", Toast.LENGTH_LONG).show();
//                            Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
//                            startActivity(intent);

                        } else if (response.code() == 400) {
                            Log.e("onResponse::", "got response 400");
                            Toast.makeText(MapActivity.this,
                                    "Failed to send ", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MapActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

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
        map.put("name", false);

        //Call<Void> call = LoginResult.getRetrofitInterface().findActiveUsers(map);

    }


}
