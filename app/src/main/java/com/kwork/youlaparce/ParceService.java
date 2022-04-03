package com.kwork.youlaparce;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ParceService extends Service {
    String url;
    String resUrl;
    SharedPreferences sp;
    WebView wv;

    public ParceService(){

    }
    @Override
    public void onCreate(){
        Log.i("gfggfgfgf","create");
        sp = getSharedPreferences("appcfg", MODE_PRIVATE);
        super.onCreate();
    }
    @Override
    public void onDestroy(){
        Log.i("gfggfgfgf","clooooose");
        wv.destroy();
        super.onDestroy();
    }
    
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null)
            url = intent.getStringExtra("url");
        else{
            url = sp.getString("url", "");
        }
        loadData();
        return super.onStartCommand(intent, flags, startId);
    }

    void sendNotif() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("url", resUrl);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("юла")
                        .setContentText(resUrl)
                        .setContentIntent(pIntent);

        Notification notification = builder.build();

// Show Notification
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    void loadData(){
        final WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.BOTTOM | Gravity.LEFT;
        params.x = 0;
        params.y = 0;
        params.width = 500;
        params.height = 250;

        wv = new WebView(this);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.i("gfgg", ""+newProgress);
                if (newProgress>=80){
                    view.evaluateJavascript("javascript: (function(){return document.getElementsByClassName('sc-kBrnbA sc-dNUOEE euyEAw eDTCjk')[0].firstChild.href})()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.i("gfggfgg", url);
                            Log.i("gfggfggfggf", s);
                            if (!s.equals("null")) {
                                wv.setVisibility(View.GONE);
                                if (!sp.getString("resurl", "asd").equals(s)) {
                                    Log.i("gfggf","update");
                                    SharedPreferences.Editor e = sp.edit();
                                    e.putString("resurl", s);
                                    e.apply();
                                    resUrl = s;
                                    sendNotif();
                                }
                            }
                        }
                    });
                }
            }
        });

        wv.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.d("Error","loading web view: request: "+request+" error: "+error);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //CDT cdt = new CDT(30000, 1000);
                //cdt.start();
                Log.i("gfggf", "pf");
            }
            class CDT extends CountDownTimer {

                public CDT(long startTime, long interval) {
                    super(startTime, interval);
                }

                @Override
                public void onFinish() {
                    Log.i("gfggf", url);
                    wv.evaluateJavascript("javascript: (function(){return document.getElementsByClassName('sc-kBrnbA sc-dNUOEE euyEAw eDTCjk')[0].innerHTML})()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.i("gfggfggfggf", s);
                        }
                    });
                }

                @Override
                public void onTick(long millisUntilFinished) {

                }
            }

            /*@Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {


                /*if (request.getUrl().toString().contains("/endProcess")) {

                    windowManager.removeView(wv);

                    wv.post(new Runnable() {
                        @Override
                        public void run() {
                            wv.destroy();
                        }
                    });
                    stopSelf();
                    return new WebResourceResponse("bgsType", "someEncoding", null);
                }
                else {
                    return null;
                //}
            }*/
        });
        wv.loadUrl(url);
        windowManager.addView(wv, params);

        //------------------------------
        /*WebView wv = new WebView(this);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // Ignore SSL certificate errors
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("gfggf", "pf");
            }
        });
        wv.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress>60){
                    view.evaluateJavascript("javascript: (function(){return document.getElementsByClassName('sc-kBrnbA sc-dNUOEE euyEAw eDTCjk')[0].innerHTML})()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            Log.i("gfggfggfggf", s);
                        }
                    });
                }
                Log.i("gfgg", ""+newProgress);
            }
        });
        Log.i("gfggf", url);
        wv.loadUrl(url);*/
    }
    public IBinder onBind(Intent arg0) {
        return null;
    }
}