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
import android.graphics.PixelFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
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

public class ParceService extends Service {
    String url;
    String resUrl;
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
        if (intent!=null)
            url = intent.getStringExtra("url");
        else{
            url = sp.getString("url", "");
        }
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

    void sendNotif() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Uri path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("юла")
                        .setContentText("новый товар в вашей категории: "+resUrl)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .setSound(path);

        Notification notification = builder.build();

// Show Notification
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
        Log.i("gfggf", "notification");
    }

    void loadData(){
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
        params.x = 0;
        params.y = 0;
        params.width = 50;
        params.height = 1000;

        wv = new WebView(this);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.i("gfgg", ""+newProgress);
                if (newProgress>=80){
                    view.evaluateJavascript("var btn = document.getElementsByClassName('sc-XhUPp sc-beLLfS kcbcgP ePKYgn')[0].click()", null);
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
                                CDT cdt = new CDT(interval, 1000);
                                cdt.start();
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
                view.setVisibility(View.GONE);
                Log.i("gfggf", "pf"+interval);
            }
        });
        wv.loadUrl(url);
        windowManager.addView(wv, params);
    }
    public IBinder onBind(Intent arg0) {
        return null;
    }
}