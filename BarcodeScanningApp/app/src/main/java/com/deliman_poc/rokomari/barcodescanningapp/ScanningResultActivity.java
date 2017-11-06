package com.deliman_poc.rokomari.barcodescanningapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ScanningResultActivity extends AppCompatActivity {



    private TextView tvScanningText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning_result);

        tvScanningText=findViewById(R.id.textview_scanning_result);

        Intent intent=getIntent();
        tvScanningText.setText(intent.getStringExtra("scanned_text"));
    }
}
