package com.example.huseyincengiz.redditapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by HuseyinCengiz on 2.09.2017.
 */

public class WebViewActivity extends AppCompatActivity {

    private static final String TAG = "WebViewActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Intent webActivityIntent = getIntent();
        WebView webview = (WebView) findViewById(R.id.webview);
        final TextView textProgress = (TextView) findViewById(R.id.webviewprogressText);
        final ProgressBar progress = (ProgressBar) findViewById(R.id.webviewProgressBar);
        progress.setVisibility(View.VISIBLE);
        Log.d(TAG, "onCreate: Started.");
        webview.getSettings().setJavaScriptEnabled(true);//web tarayıının javascript çalıştırıcağını söylüyoruz
        String Url = webActivityIntent.getStringExtra("url");
        webview.loadUrl(Url);//açacağı urli yüklüyoruz
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.GONE);
                textProgress.setText("");
            }
        });//Burada webview'e Webview client ekliyoruz bunda callback fonksiyonlar var.Sayfa bittiğinde ,hata aldığında olacakları belirtiyoruz.
    }
}
