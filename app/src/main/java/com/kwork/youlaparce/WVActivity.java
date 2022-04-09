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
        if (sp.getString("name1", "").equals(""))
            e.putString("url1", url);
        else if (sp.getString("name2", "").equals(""))
            e.putString("url2", url);
        else if (sp.getString("name3", "").equals(""))
            e.putString("url3", url);
        else if (sp.getString("name4", "").equals(""))
            e.putString("url4", url);
        else if (sp.getString("name5", "").equals(""))
            e.putString("url5", url);
        e.apply();
        this.finish();
    }
}