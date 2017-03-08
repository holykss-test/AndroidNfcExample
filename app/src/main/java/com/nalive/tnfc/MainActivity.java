package com.nalive.tnfc;

import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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
            tv.setText("No NFC feature");
        } else if (nfc.isEnabled() == false) {
            tv.setText("NFC is disabled");
        } else {
            tv.setText("NFC is ok");
        }
    }
}
