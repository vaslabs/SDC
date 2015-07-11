package com.vaslabs.sdc.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qrapi_scanner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
