package com.nalive.tnfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    TextView tv;
    private NfcAdapter nfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);

        nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc == null) {
            tv.setText("No NFC feature");
        } else if (nfc.isEnabled() == false) {
            tv.setText("NFC is disabled");
        } else {
            tv.setText("NFC is ok");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {

            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            int parcelablesLength = parcelables != null ? parcelables.length : 0;

            Toast.makeText(this, "NFC intent received NDEF count: " + parcelablesLength, Toast.LENGTH_LONG).show();

            if (parcelablesLength < 1) {
                Toast.makeText(this, "No NDEF messages found", Toast.LENGTH_LONG).show();
            } else {
                readTextFromTag(parcelables);
            }

        }
    }

    private void readTextFromTag(Parcelable[] parcelables) {
        NdefMessage message = (NdefMessage)parcelables[0];

        NdefRecord records[] = message.getRecords();

        if (records == null || records.length < 0) {
            Toast.makeText(this, "No message", Toast.LENGTH_SHORT).show();
            return;
        }

        byte payload[] =  records[0].getPayload();
        String textEncoding = new String(payload);

        tv.setText(textEncoding);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter filters[] = new IntentFilter[]{};

        nfc.enableForegroundDispatch(this, pendingIntent, filters, null);

    }

    @Override
    protected void onPause() {
        nfc.disableForegroundDispatch(this);

        super.onPause();
    }

    private void formatTag(@NonNull Tag tag, NdefMessage message) {
        try {
            NdefFormatable formatable = NdefFormatable.get(tag);

            if (formatable == null) {
                Toast.makeText(this, "Tag is not ndef formatable", Toast.LENGTH_SHORT).show();
            }

            formatable.connect();
            formatable.format(message);
            formatable.close();
        } catch (Exception e) {
            Log.e("Format TAG", e.getMessage());
        }
    }

    private void writeMessage(@NonNull Tag tag, NdefMessage message) {

        Ndef ndef = Ndef.get(tag);

        try {

            if (ndef == null) {
                formatTag(tag, message);
            } else {
                ndef.connect();

                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag is not wratable", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("Write Tag", e.getMessage());
        } finally {
            if (ndef != null) {
                try {
                    ndef.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private NdefRecord createTextRecord(String content) {
        try {
            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
                    new byte[]{}, content.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    private NdefMessage createMessage(String text) {
        NdefMessage message = new NdefMessage(new NdefRecord[]{
                createTextRecord(text)
        });

        return message;
    }
}

