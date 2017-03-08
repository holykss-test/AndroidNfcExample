package com.nalive.tnfc;

import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    private NfcAdapter nfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);

        nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc == null) {
            Toast.makeText(this, "No NFC feature", Toast.LENGTH_SHORT).show();
        } else if (nfc.isEnabled() == false) {
            Toast.makeText(this, "NFC is disabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "NFC is ok", Toast.LENGTH_SHORT).show();
        }
    }
}
