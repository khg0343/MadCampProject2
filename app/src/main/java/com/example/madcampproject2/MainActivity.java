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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private Button btnNfcRead;
    private Button btnNfcWrite;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        btnNfcRead = findViewById(R.id.btn_nfc_read);
        btnNfcWrite = findViewById(R.id.btn_nfc_write);

        btnNfcRead.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO : click event
                startActivity(new Intent(MainActivity.this, NFCReadActivity.class));
            }
        });

        btnNfcWrite.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO : click event
                startActivity(new Intent(MainActivity.this, NFCWriteActivity.class));
            }
        });

    }

}
