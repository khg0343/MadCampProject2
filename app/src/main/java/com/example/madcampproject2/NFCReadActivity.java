package com.example.madcampproject2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;

import io.socket.emitter.Emitter;

public class NFCReadActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {

    private GpsTracker gpsTracker;
    private TextView mTextView;
    private static final int MESSAGE_SENT = 1; //추후 Handler 메시지에 사용
    private NfcAdapter mNfcAdapter; //NfcAdapter 를 선언

    Context context;
    String userName, userMsg, userMsg2;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        LoginResult.getSocket().emit("join", LoginResult.getLoginUser().getEmail());

        Log.e("Read Activity::", "onCreate in");

        // Nfc 로 전송할 메시지 선언​
        userName = "TestUser";
        userMsg = "Hello~!";
        userMsg2 = "Thank you.";

        setContentView(R.layout.activity_nfc_read);

        // nfc 가 사용가능한지 체크
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter != null) {
            Toast.makeText(getApplicationContext(), "Tap to beam to another NFC device!!!", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(getApplicationContext(), "This phone is not NFC enabled!!!", Toast.LENGTH_SHORT)
                    .show();
        }

        // 아래 2줄은 안드로이드빔을 성공적으로 전송했을 경우 이벤트 호출을 위해서 작성
//        mNfcAdapter.setNdefPushMessageCallback(this, this);
//        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        Log.e("Read Activity::", "onCreate out");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.e("Read Activity::", "onPause");
        //PendingIntent 사용 ​차단
//        mNfcAdapter.disableForegroundDispatch(this);

        // Get my GPS
        gpsTracker = new GpsTracker(this);
        double latitude = gpsTracker.getLatitude(); double longitude = gpsTracker.getLongitude();

        // Save our GPS : White dummy botton, Read Activity, request to server at first
        LoginResult.setConnectLatitude(latitude);
        LoginResult.setConnectLongitude(longitude);

        // Send to Server

//        LoginResult.getSocket().emit("nfcServer",
//                LoginResult.getLoginUser().getName(),
//                LoginResult.getLoginUser().getEmail(),
//                LoginResult.getConnectLatitude(),
//                LoginResult.getConnectLongitude(),
//                LoginResult.getConnectUser().getEmail());
//        Log.e("Read Activity::", " " + LoginResult.getConnectLatitude() + ", " + LoginResult.getConnectLongitude());

        Intent intent = new Intent(getApplicationContext(), NewActivity.class);
        startActivity(intent);
    }

    // NFC 전송 타입을 설정한다... 라고 해야 하나. 아무튼 어떤 타입의 데이터를 전송할 꺼다 라고 선언하는 부분
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        Log.e("Read Activity::", "CreateMimeRecord in");
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                mimeBytes, new byte[0], payload);
        Log.e("createMimeRecord", "createMimeRecord");

        return mimeRecord;
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        Log.e("Read Activity::", "createNdefMessage in");
        String text = ("userName:" + userName +"\n"+"userMsg:"+ userMsg + "\n"+"userMsg2:"+userMsg);
        NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord(
                "application/com.example.test", text.getBytes())
                /**
                 * The Android Application Record (AAR) is commented out. When a device
                 * receives a push with an AAR in it, the application specified in the
                 * AAR is guaranteed to run. The AAR overrides the tag dispatch system.
                 * You can add it back in to guarantee that this activity starts when
                 * receiving a beamed message. For now, this code uses the tag dispatch
                 * system.
                */
                // ,NdefRecord.createApplicationRecord("com.example.android.beam")
        });
        Log.e("createNdefMessage", "createNdefMessage");
        return msg;
    }

    @Override
    public void onNdefPushComplete(NfcEvent arg0) {
        Log.e("onNdefPushComplete", "onNdefPushComplete");
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e("Read Activity::", "handleMessage in");
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getApplicationContext(), "send message!!!", Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };
}