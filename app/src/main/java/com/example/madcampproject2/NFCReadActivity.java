package com.example.madcampproject2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;

import com.example.madcampproject2.databinding.ActivityNfcReadBinding;
import com.example.madcampproject2.ui.login.LoginActivity;

import java.util.Arrays;

public class NFCReadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_read);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (messages == null) {
                return;
            }

            for (Parcelable message : messages) {
                showMsg((NdefMessage) message);
            }
        }

    }

    public void showMsg(NdefMessage mMessage) {
        NdefRecord[] recs = mMessage.getRecords();

        for (NdefRecord record : recs) {
            if (Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {

                Uri u = record.toUri();

                Intent j = new Intent(Intent.ACTION_VIEW);
                j.setData(u);
                startActivity(j);
                finish();
            }
        }
    }
}