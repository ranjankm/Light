package com.rk.reachout;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Ranjan KM on 03 Feb 2017.
 */

public class QRScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        new Favourites(QRScanner.this,"add").execute(new UserSessionManager(QRScanner.this).getUserid(), result.getText());
        Intent intent = new Intent(QRScanner.this, ShopkeeperView.class);
        intent.putExtra("category", "Add Favourite");
        intent.putExtra("shopid", result.getText());
        startActivity(intent);
        QRScanner.this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }
}
