package com.example.madcampproject2;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity {

    private GpsTracker gpsTracker;
    private List<User> activeUsers;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        FloatingActionButton btnActivity = findViewById(R.id.btn_active);

        mapView = new MapView(this);

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

        mapView.setCalloutBalloonAdapter(new CustomBalloonAdapter());

        // 현재 위치 트래킹 모드
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        int colorStroke = android.graphics.Color.argb(255, 255, 232, 18);
        int colorFill = android.graphics.Color.argb(100, 255, 232, 18);

        mapView.setCurrentLocationRadius(500); // Draw a circle around 500 meter
        mapView.setCurrentLocationRadiusStrokeColor(colorStroke);
        mapView.setCurrentLocationRadiusFillColor(colorFill);

        btnActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginResult.getLoginUser().setLatitude(latitude);
                LoginResult.getLoginUser().setLongitude(longitude);
                LoginResult.getLoginUser().setIsActive(true);

                setUserGPSInfo();

                getActiveUsers();

                drawCircleAround(latitude, longitude);

            }
        });


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
                    Toast.makeText(MapActivity.this, "List up successfully", Toast.LENGTH_LONG).show();

                    activeUsers = response.body();
                    markActiveUsers();

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

    private void setUserGPSInfo() {

        HashMap<String, Object> map = new HashMap<>();

        map.put("email", LoginResult.getLoginUser().getEmail());
        map.put("latitude", LoginResult.getLoginUser().getLatitude());
        map.put("longitude", LoginResult.getLoginUser().getLongitude());
        map.put("isactive", LoginResult.getLoginUser().getIsActive());

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
    }

    private void markActiveUsers() {
        mapView.removeAllPOIItems();
        for (User user : activeUsers) {

            Log.e("User : ", user.getName());
            Log.e("Login : ", LoginResult.getLoginUser().getName());

            if (user.getName().equals(LoginResult.getLoginUser().getName())) {
                Log.e("User : ", user.getName());
                Log.e("Login : ", LoginResult.getLoginUser().getName());
            }// do nothing}
            else if (distance(LoginResult.getLoginUser().getLatitude(), LoginResult.getLoginUser().getLongitude(), user.getLatitude(), user.getLongitude(), "meter") < 500) {

                // Pick a certain location with a pin;
                MapPOIItem marker = new MapPOIItem();
                marker.setItemName(user.getName());
                marker.setTag(0);
                marker.setMapPoint(MapPoint.mapPointWithGeoCoord(user.getLatitude(), user.getLongitude()));
                marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

//                marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
//                marker.setCustomImageResourceId(R.drawable.ic_baseline_mode_comment_24);
//                marker.setCustomImageAutoscale(true);
//                marker.setCustomImageAnchor(0.5f, 1.0f);    // 마커 이미지 기준점

                // pin을 mapView에 출력
                mapView.addPOIItem(marker);

            } else {

                // Pick a certain location with a pin;
                MapPOIItem marker = new MapPOIItem();
                marker.setItemName(user.getName());
                marker.setTag(0);
                marker.setMapPoint(MapPoint.mapPointWithGeoCoord(user.getLatitude(), user.getLongitude()));
                marker.setMarkerType(MapPOIItem.MarkerType.YellowPin); // 기본으로 제공하는 BluePin 마커 모양.
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

//                marker.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
//                marker.setCustomImageResourceId(R.drawable.ic_baseline_mode_comment_24);
//                marker.setCustomImageAutoscale(true);
//                marker.setCustomImageAnchor(0.5f, 1.0f);    // 마커 이미지 기준점

                // pin을 mapView에 출력
                mapView.addPOIItem(marker);

            }

        }

    }

    private void drawCircleAround(double latitude, double longitude) {
        MapCircle circle = new MapCircle(
                MapPoint.mapPointWithGeoCoord(latitude, longitude), // center
                500, // radius
                Color.argb(255, 255, 232, 18), // strokeColor
                Color.argb(50, 255, 232, 18) // fillColor
        );
        circle.setTag(1234);
        mapView.addCircle(circle);

        // 지도뷰의 중심좌표와 줌레벨을 Circle이 모두 나오도록 조정.
        MapPointBounds[] mapPointBoundsArray = { circle.getBound() };
        MapPointBounds mapPointBounds = new MapPointBounds(mapPointBoundsArray);
        int padding = 50; // px
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));
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

    public class CustomBalloonAdapter implements CalloutBalloonAdapter {

        private final View mCalloutBalloon;
        private final ImageView image;
        private final TextView name;
        private final TextView email;

        public CustomBalloonAdapter() {
            mCalloutBalloon = (View) getLayoutInflater().inflate(R.layout.marker_layout, (ViewGroup) null);
            image = (ImageView) mCalloutBalloon.findViewById(R.id.imgBalloonImage);
            name = (TextView) mCalloutBalloon.findViewById(R.id.txtBalloonName);
            email = (TextView) mCalloutBalloon.findViewById(R.id.txtBalloonEmail);
        }

        public final View getCalloutBalloon() {
            return this.mCalloutBalloon;
        }

        public final TextView getName() {
            return this.name;
        }

        public final TextView getEmail() {
            return this.email;
        }

        public View getCalloutBalloon(MapPOIItem poiItem) {
            //this.image.setImageResource();
            this.name.setText(poiItem.getItemName());
            this.email.setText("getCalloutBalloon");
            return this.mCalloutBalloon;
        }

        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            this.email.setText("getPressedCalloutBalloon");
            return this.mCalloutBalloon;
        }


    }

    public class MarkerEventListener implements MapView.POIItemEventListener {
        @NotNull
        private final Context context;

        public void onPOIItemSelected(MapView mapView, MapPOIItem poiItem) {

        }

        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem poiItem) {

        }

        public void onCalloutBalloonOfPOIItemTouched(final MapView mapView, final MapPOIItem poiItem, MapPOIItem.CalloutBalloonButtonType buttonType) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            String[] itemList = new String[]{"토스트", "마커 삭제", "취소"};
            builder.setTitle(String.valueOf(poiItem.getItemName()));
            builder.setItems(itemList, (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialog, int which) {
                    switch(which) {
                        case 0: //Toast.makeText(context, "토스트", ).show(); break;
                        case 1: mapView.removePOIItem(poiItem); break;
                        case 2: dialog.dismiss();
                    }
                }
            }));
            builder.show();
        }

        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem poiItem, MapPoint mapPoint) {
        }

        @NotNull
        public final Context getContext() {
            return this.context;
        }

        public MarkerEventListener(Context context) {
            this.context = context;
        }
    }



}

