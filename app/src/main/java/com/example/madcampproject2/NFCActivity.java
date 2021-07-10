package com.example.madcampproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NFCActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        Button btnNfcRead = findViewById(R.id.btn_nfc_read);
        Button btnNfcWrite = findViewById(R.id.btn_nfc_write);

        btnNfcRead.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO : click event
                startActivity(new Intent(NFCActivity.this, NFCReadActivity.class));
            }
        });

        btnNfcWrite.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO : click event
                startActivity(new Intent(NFCActivity.this, NFCWriteActivity.class));
            }
        });
    }
}