package com.kwork.youlaparce;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class ParceService extends Service {
    String url1, url2, url3, url4, url5;
    String resUrl, itemName;
    SharedPreferences sp;
    WebView wv;
    int interval;

    public ParceService(){

    }
    @Override
    public void onCreate(){
        Log.i("gfggfgfgf","create");
        sp = getSharedPreferences("appcfg", MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            String processName = getProcessName(this);
            String packageName = this.getPackageName();
            if (!packageName.equals(processName)) {
                WebView.setDataDirectorySuffix(processName);
            }
        }
        super.onCreate();
    }
    @Override
    public void onDestroy(){
        Log.i("gfggfgfgf","clooooose");
        wv.destroy();
        super.onDestroy();
    }
    
    public int onStartCommand(Intent intent, int flags, int startId) {
        interval = sp.getInt("interval", 900000);//default 15 min = 900000 msec
        loadData();
        return super.onStartCommand(intent, flags, startId);
    }
    private String getProcessName(Context context) {
        if (context == null) return null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == android.os.Process.myPid()) {
                return processInfo.processName;
            }
        }
        return null;
    }
    class CDT extends CountDownTimer {

        public CDT(long startTime, long interval) {
            super(startTime, interval);
            Log.i("gfggfgfgfgf", "timerStarted");
        }

        @Override
        public void onFinish() {
            Log.i("gfggf", "loaddata");
            loadData();
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }
    }

    void sendNotif(int step, String itemName) {
        String url="https://google.com";
        switch (step){
            case 0:
                url = sp.getString("url1", "");
                break;
            case 1:
                url = sp.getString("url2", "");
                break;
            case 2:
                url = sp.getString("url3", "");
                break;
            case 3:
                url = sp.getString("url4", "");
                break;
            case 4:
                url = sp.getString("url5", "");
                break;
        }
        String msgCount = "";
        Boolean flag = false;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == 1) {
                int count=sp.getInt("msgCount", 0);
                msgCount = "(новых уведомлений "+(count+1)+")";
                SharedPreferences.Editor e =sp.edit();
                e.putInt("msgCount", count+1);
                e.apply();
                flag=true;
            }
        }
        if (!flag){
            SharedPreferences.Editor e =sp.edit();
            e.putInt("msgCount", 0);
            e.apply();
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Uri path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("юла")
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .setSound(path)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msgCount+"\nновый товар: "+itemName));

        Notification notification = builder.build();

// Show Notification
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
        Log.i("gfggf", "notification");
    }
    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    int count = 5;
    int step = 0;
    void loadData(){
        step=0;
        count=5;
        url1 = sp.getString("url1", "");
        if (url1.equals("")) count--;
        url2 = sp.getString("url2", "");
        if (url2.equals("")) count--;
        url3 = sp.getString("url3", "");
        if (url3.equals("")) count--;
        url4 = sp.getString("url4", "");
        if (url4.equals("")) count--;
        url5 = sp.getString("url5", "");
        if (url5.equals("")) count--;
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        final WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG ,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.BOTTOM | Gravity.LEFT;
        params.x = -40;
        params.y = 0;
        params.width = 50;
        params.height = 300;

        wv = new WebView(this);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.i("gfgg", ""+newProgress);
                if (newProgress>=80){
                    view.evaluateJavascript("var btn = document.getElementsByClassName('sc-XhUPp sc-beLLfS kcbcgP ePKYgn')[0].click()", null);
                    //view.evaluateJavascript("javascript: (function(){var arr = ''; arr += document.getElementsByClassName('sc-kBrnbA sc-dNUOEE euyEAw iCErtZ')[0].firstChild.href; arr+='|||'; arr += document.getElementsByClassName('sc-cOajty sc-fOuWYj jMbTCK UVxoo')[0].innerHTML; arr+='|||'; arr+= document.getElementsByTagName('image')[0].getAttributeNS('http://www.w3.org/1999/xlink', 'href'); return arr;})()", new ValueCallback<String>() {
                    view.evaluateJavascript("javascript: (function(){var arr = ''; arr += document.getElementsByClassName('sc-kBrnbA sc-dNUOEE euyEAw eDTCjk')[0].firstChild.href;arr+=':::'; arr += document.getElementsByClassName('sc-cOajty sc-fOuWYj jMbTCK UVxoo')[0].innerHTML; return arr;})()", new ValueCallback<String>() {                        @Override
                        public void onReceiveValue(String s) {
                            Log.i("gfggfggfggf", s+" view= "+view.getUrl());
                            if (!s.equals("null")) {
                                step++;
                                String[] sarr = s.split(":::");
                                String resurl = sarr[0];
                                String iname = sarr[1];
                                Log.i("gfggfg", s+"=|="+resurl+"|"+iname);
                                wv.setVisibility(View.GONE);
                                if (!sp.getString("resurl"+step, "asd").equals(resurl)) {
                                    SharedPreferences.Editor e = sp.edit();
                                    e.putString("resurl"+step, resurl);
                                    e.apply();
                                    Log.i("gfggf","update");
                                    itemName = iname;
                                    sendNotif(step, iname);
                                }
                                if (step!=count){
                                    if (step==1&&!url2.equals("")) {
                                        view.setVisibility(View.VISIBLE);
                                        view.loadUrl(url2);
                                    }
                                    else if (step==2&&!url3.equals("")) {
                                        view.setVisibility(View.VISIBLE);
                                        view.loadUrl(url3);
                                    }
                                    else if (step==3&&!url4.equals("")) {
                                        view.loadUrl(url4);
                                        view.setVisibility(View.VISIBLE);
                                    }
                                    else if (step==4&&!url5.equals("")) {
                                        view.loadUrl(url5);
                                        view.setVisibility(View.VISIBLE);
                                    }
                                }
                                else {
                                    CDT cdt = new CDT(interval, 1000);
                                    Log.i("gfggf", ""+interval);
                                    cdt.start();
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
                //view.setVisibility(View.GONE);
                Log.i("gfggf", "pf"+interval);
            }
        });
        wv.loadUrl(url1);
        windowManager.addView(wv, params);
    }
    public IBinder onBind(Intent arg0) {
        return null;
    }
}