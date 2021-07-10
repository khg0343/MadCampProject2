package com.example.madcampproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;

import com.example.madcampproject2.databinding.ActivityNfcReadBinding;
import com.example.madcampproject2.ui.login.LoginActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class NFCWriteActivity extends AppCompatActivity {

    private String TAG = "NFCReadActivity";
    private PendingIntent nfcPendingIntent;
    private NfcAdapter nfcAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_write);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String writeValue = "http://www.naver.com";
        NdefMessage message = createTagMessage(writeValue);

        writeTag(message, detectedTag);

    }

    private NdefMessage createTagMessage(String msg) {
        return new NdefMessage(NdefRecord.createUri(msg));
    }

    public final void writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(this.getApplicationContext(), (CharSequence)"can not write NFC tag", Toast.LENGTH_SHORT).show();
                }

                if (ndef.getMaxSize() < size) {
                    Toast.makeText(this.getApplicationContext(), (CharSequence)"NFC tag size too large", Toast.LENGTH_SHORT).show();
                }

                ndef.writeNdefMessage(message);
                Toast.makeText(this.getApplicationContext(), (CharSequence)"NFC tag is writted", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.i(TAG,"error");
        }

    }
}