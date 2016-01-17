package com.vaslabs.sdc.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.vaslabs.sdc_dashboard.API.API;

import java.io.IOException;

import eu.livotov.zxscan.ScannerView;


public class QRApiScannerActivity extends Activity {

    private Context mContext;
    private ScannerView scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrapi_scanner);
        mContext = this;
        scanner = (ScannerView) findViewById(R.id.scanner);
        scanner.setScannerViewEventListener(new ScannerView.ScannerViewEventListener() {
            @Override
            public void onScannerReady() {

            }

            @Override
            public void onScannerFailure(int i) {

            }

            public boolean onCodeScanned(final String data) {
                scanner.stopScanner();
                try {
                    API.saveApiToken(mContext, data);
                    Toast.makeText(mContext, "API has been set up with token: " + data, Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    Toast.makeText(mContext, "Something went wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        scanner.startScanner();
    }
}
