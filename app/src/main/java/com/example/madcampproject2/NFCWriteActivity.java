package com.example.madcampproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;

import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.socket.emitter.Emitter;

public class NFCWriteActivity extends AppCompatActivity {

    private TextView textView;
    private NfcAdapter mNfcAdapter; //NfcAdapter 를 선언
    Intent intent; // PendingIntent 용 intent
    private PendingIntent mPendingIntent; // PendingIntent 선언
    private IntentFilter[] mIntentFilters; // 이건.. 필터라고 되어있으니, 받은 NdefMessage를 적당히 필터해주는 역할인 듯?
    String userName, userMsg, userMsg2;
    private GpsTracker gpsTracker;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        Log.e("Write Activity::", "onCreate in");

//        LoginResult.getSocket().emit("join", LoginResult.getLoginUser().getEmail());

        setContentView(R.layout.activity_nfc_write);

        // 역시나 NFC 가 사용가능한지 체크
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter != null) {
            Toast.makeText(getApplicationContext(), "Read an NFC tag", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(getApplicationContext(), "This phone is not NFC enabled.", Toast.LENGTH_SHORT)
                    .show();
        }

        // PendingIntent 선언 부분

        // 참고로 PendingIntent는 그 뭐라드라.. 미리 intent를 선언하고 나중에 어떤 이벤트가 발생할 때, intent가 실행되게 하는 것이다. 예약 intent라고 생각하면 편하다. 여기서는 onResume()에 사용되었다
        intent = new Intent(getApplicationContext(), NewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent, 0);

        // set an intent filter for all MIME data
        IntentFilter ndefIntent = new IntentFilter(
                NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefIntent.addDataType("*/*");
            mIntentFilters = new IntentFilter[] { ndefIntent };
        } catch (Exception e) {
            Log.e("TagDispatch", e.toString());
        }
        Log.e("Write Activity::", "onCreate out");
    }

    @Override
    public void onResume() {
        super.onResume();
        //PendingIntent 사용 허용
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, null);
        Log.e("onResume", "onResume");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.e("Write Activity::", "onPause");
        //PendingIntent 사용 ​차단
//        mNfcAdapter.disableForegroundDispatch(this);

//        LoginResult.getSocket().on("nfcClient", new Emitter.Listener() {
//            @Override
//            public void call(final Object... args) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        JSONObject data = (JSONObject) args[0];
//                        try {
//                            LoginResult.setConnectLatitude(data.getDouble("senderLatitude"));
//                            LoginResult.setConnectLongitude(data.getDouble("senderLongitude"));
//
//                            Log.e("WRITE Activity::", " " + LoginResult.getConnectLatitude() + ", " + LoginResult.getConnectLongitude());
//
//                            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
//                            startActivity(intent);
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//            }
//        });

        // Get my GPS
        gpsTracker = new GpsTracker(this);
        double latitude = gpsTracker.getLatitude(); double longitude = gpsTracker.getLongitude();

        // Save our GPS : White dummy botton, Read Activity, request to server at first
        LoginResult.setConnectLatitude(latitude);
        LoginResult.setConnectLongitude(longitude);

        Intent intent = new Intent(getApplicationContext(), NewActivity.class);
        startActivity(intent);

//        finish();
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        Log.e("Write Activity::", "onNewIntent in");
        super.onNewIntent(intent);
        Parcelable[] rawMsgs = intent
                .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        String strSite = new String(msg.getRecords()[0].getPayload());

        if (strSite != null && !strSite.equals("")) {
            userName = strSite.substring(strSite.indexOf("userName") + 9,
                    strSite.indexOf("\n"));
            userMsg = strSite.substring(
                    strSite.indexOf("userMsg") + 8,
                    strSite.indexOf("\n", strSite.indexOf("userMsg") + 8));
            userMsg2 = strSite.substring(strSite.indexOf("userMsg2") + 9,
                    strSite.length());
        }

        textView.setText("userName = " + userName + "\n" +
                "userMsg = " + userMsg + "\n" +
                "userMsg2 = " + userMsg2);
        Log.e("onNewIntent", "onNewIntent(Write)");
        setIntent(intent);

    }
}