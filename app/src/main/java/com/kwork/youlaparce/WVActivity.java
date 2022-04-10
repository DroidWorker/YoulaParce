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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;

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
        wv.setWebViewClient(new myBrow());
        settings.setJavaScriptEnabled(true);
        wv.loadUrl("https://youla.ru/");

        Spinner spinner = findViewById(R.id.spinner1);
        HashMap<Integer, String> urls = new HashMap<>();
        ArrayList<String> url = new ArrayList<>();
        urls.put(0, "https://youla.ru/");
        String url1, url2, url3, url4, url5;
        url1 = sp.getString("url1", "");
        url2 = sp.getString("url2", "");
        url3 = sp.getString("url3", "");
        url4 = sp.getString("url4", "");
        url5 = sp.getString("url5", "");
        url.add("предыдущие запросы");
        if (!url1.equals("")) {urls.put(1, url1);
        url.add(sp.getString("name1", ""));
        }
        if (!url2.equals("")) {urls.put(2, url2);
            url.add(sp.getString("name2", ""));
        }
        if (!url3.equals("")) {urls.put(3, url3);
            url.add(sp.getString("name3", ""));
        }
        if (!url4.equals("")) {urls.put(4, url4);
            url.add(sp.getString("name4", ""));
        }
        if (!url5.equals("")) {urls.put(5, url5);
            url.add(sp.getString("name5", ""));
        }

        ArrayAdapter<?> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, url);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                wv.loadUrl(urls.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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