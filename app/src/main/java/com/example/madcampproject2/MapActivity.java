package com.example.madcampproject2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity {

    private GpsTracker gpsTracker;
    private List<User> activeUsers;
    private MapView mapView;
    private MarkerEventListener eventListener = new MarkerEventListener(this);
    private static Socket socket;

    FloatingActionButton btnActivity;
    Switch switchActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Log.e("Map Activity::onCreate ", "set socket : " + LoginResult.getSocket());
        LoginResult.getSocket().emit("join", LoginResult.getLoginUser().getEmail());
        Log.e("Map Activity", "Create");

        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.mapView);
        mapViewContainer.addView(mapView);

        // 중심점 변경
        gpsTracker = new GpsTracker(this);

        double latitude = gpsTracker.getLatitude(); double longitude = gpsTracker.getLongitude();
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
        mapView.setZoomLevel(7, true); // 줌 레벨 변경
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 3, true); // 중심점 변경 + 줌 레벨 변경 (줌 레벨 숫자가 작을수록 확대되어 보임)

        mapView.zoomIn(true); // 줌 인
        mapView.zoomOut(true); // 줌 아웃

        mapView.setCalloutBalloonAdapter(new CustomBalloonAdapter());
        mapView.setPOIItemEventListener(eventListener);  // 마커 클릭 이벤트 리스너 등록

        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading); // 현재 위치 트래킹 모드

        btnActivity = findViewById(R.id.btn_active);
        btnActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mapView.setCurrentLocationRadius(500); // Draw a circle around 500 meter
                mapView.setCurrentLocationRadiusStrokeColor(Color.argb(200, 255, 232, 18));
                mapView.setCurrentLocationRadiusFillColor(Color.argb(30, 255, 232, 18));

                LoginResult.getLoginUser().setLatitude(latitude);
                LoginResult.getLoginUser().setLongitude(longitude);
                LoginResult.getLoginUser().setIsActive(true);

                setActive();

                getActiveUsers();

            }
        });

        switchActivity = findViewById(R.id.sb_active);
        switchActivity.bringToFront();
        switchActivity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    btnActivity.show();
                    mapView.setCurrentLocationRadius(500); // Draw a circle around 500 meter
                    setActive();
                } else {
                    // The toggle is disabled
                    btnActivity.hide();
                    mapView.setCurrentLocationRadius(0); // Draw a circle around 500 meter
                    mapView.removeAllPOIItems();
                    setInActive();
                }
            }
        });

        LoginResult.getSocket().on("requestClient", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        View view = getLayoutInflater().inflate(R.layout.dialog_accept, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                        builder.setView(view);
                        builder.setCancelable(false);
                        AlertDialog dialog = builder.create(); // Helper for dismiss();
                        dialog.show();

                        final TextView txtTitle = view.findViewById(R.id.txt_ask);
                        Button btnYes = view.findViewById(R.id.btn_yes);
                        Button btnNo = view.findViewById(R.id.btn_no);

                        try {
                            txtTitle.setText(data.getString("senderName") + " 님이 \n우리 지금 만나자고 하였습니다. \n수락하시겠습니까?");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        btnYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                try {
                                    LoginResult.getSocket().emit("acceptServer",
                                            LoginResult.getLoginUser().getName(),
                                            LoginResult.getLoginUser().getEmail(),
                                            LoginResult.getLoginUser().getLatitude(),
                                            LoginResult.getLoginUser().getLongitude(),
                                            data.getString("senderEmail"));

                                    LoginResult.getConnectUser().setName(data.getString("senderName"));
                                    LoginResult.getConnectUser().setEmail(data.getString("senderEmail"));
                                    LoginResult.getConnectUser().setLatitude(data.getDouble("senderLatitude"));
                                    LoginResult.getConnectUser().setLongitude(data.getDouble("senderLongitude"));

                                    Intent intent = new Intent(getApplicationContext(), NFCWriteActivity.class);
                                    startActivity(intent);
//                                    finish();
                                }
                                catch (JSONException e) { e.printStackTrace(); }
                            }
                        });

                        btnNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try { LoginResult.getSocket().emit("rejectServer",
                                        LoginResult.getLoginUser().getName(),
                                        LoginResult.getLoginUser().getEmail(),
                                        data.getString("senderEmail")); }
                                catch (JSONException e) { e.printStackTrace(); }
                            }
                        });

                    }
                });
            }
        });

        LoginResult.getSocket().on("acceptClient", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            Toast.makeText(MapActivity.this, data.getString("senderName") + "님이 요청을 수락하였습니다.", Toast.LENGTH_SHORT).show();

                            LoginResult.getConnectUser().setName(data.getString("senderName"));
                            LoginResult.getConnectUser().setEmail(data.getString("senderEmail"));
                            LoginResult.getConnectUser().setLatitude(data.getDouble("senderLatitude"));
                            LoginResult.getConnectUser().setLongitude(data.getDouble("senderLongitude"));

                            Intent intent = new Intent(getApplicationContext(), NFCReadActivity.class);
                            startActivity(intent);
//                            finish();
                            //connect
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });

        LoginResult.getSocket().on("rejectClient", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            Toast.makeText(MapActivity.this, data.getString("senderName") + "님이 요청을 거절하였습니다.", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });
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
        Log.e("Map Activity", "Destroy");

        super.onDestroy();
        mapView.setCurrentLocationRadius(0);
        LoginResult.getLoginUser().setIsActive(false);
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

    private void setInActive() {
        LoginResult.getLoginUser().setIsActive(false);

        // Send two user's information to server
        HashMap<String, Object> map = new HashMap<>();

        LoginResult.getLoginUser().setIsActive(false);

        LoginResult.getLoginUser().setIsActive(false);
        map.put("email", LoginResult.getLoginUser().getEmail());
        map.put("isactive", LoginResult.getLoginUser().getIsActive());

        Call<Void> call = LoginResult.getRetrofitInterface().executeInActive(map);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 200) {
                    Toast.makeText(MapActivity.this,
                            "Send to Server successfully", Toast.LENGTH_LONG).show();

                } else if (response.code() == 400) {
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

    private void setActive() {

        LoginResult.getLoginUser().setIsActive(true);
        LoginResult.getLoginUser().setLatitude(gpsTracker.latitude);
        LoginResult.getLoginUser().setLongitude(gpsTracker.longitude);

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
                marker.setItemName(user.getEmail());
                marker.setUserObject(user.getName());
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

            }
        }

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

    public static class MarkerEventListener implements MapView.POIItemEventListener {

        private final Context context;

        public void onPOIItemSelected(MapView mapView, MapPOIItem poiItem) {

        }

        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem poiItem) {

        }

        public void onCalloutBalloonOfPOIItemTouched(final MapView mapView, final MapPOIItem poiItem, MapPOIItem.CalloutBalloonButtonType buttonType) {

//            View view = View.inflate(context,R.layout.dialog_request, null);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_request, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(view);
            AlertDialog dialog = builder.create(); // Helper for dismiss();
            dialog.show();

            final TextView txtTitle = view.findViewById(R.id.txt_ask);
            Button btnRequest = view.findViewById(R.id.btn_request);

            txtTitle.setText(poiItem.getUserObject().toString() + " 님께 \n우리 지금 만나자를 요청하시겠습니까?");

            btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("email", poiItem.getItemName());
                    LoginResult.getSocket().emit("requestServer",
                            LoginResult.getLoginUser().getName(),
                            LoginResult.getLoginUser().getEmail(),
                            LoginResult.getLoginUser().getLatitude(),
                            LoginResult.getLoginUser().getLongitude(),
                            poiItem.getItemName());
                }
            });

        }

        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem poiItem, MapPoint mapPoint) {
        }

        public MarkerEventListener(Context context) {
            this.context = context;
        }
    }

}
