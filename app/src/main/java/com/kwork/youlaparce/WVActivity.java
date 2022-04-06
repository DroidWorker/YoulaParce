package com.kwork.youlaparce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WVActivity extends AppCompatActivity {
    WebView wv;
SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wvactivity);

        sp = getSharedPreferences("appcfg", MODE_PRIVATE);

        wv = findViewById(R.id.wv);
        wv.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.i("gfgg", ""+newProgress);
            }
        });
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        wv.setWebViewClient(new myBrow());
        wv.loadUrl("https://youla.ru/");

    }

    private class myBrow extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
        }
    }

    public void onApplyClick(View view){
        String url = wv.getUrl();
        SharedPreferences.Editor e = sp.edit();
        e.putString("url", url);
        e.apply();
        this.finish();
    }
}