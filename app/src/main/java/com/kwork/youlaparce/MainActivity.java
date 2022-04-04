package com.kwork.youlaparce;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
SharedPreferences sp;
int intervall = 15;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Проверить, было ли предоставлено разрешение
        if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
        }

        sp = getSharedPreferences("appcfg", this.MODE_PRIVATE);

        String url = sp.getString("url", "");
        int interval = sp.getInt("interval", 15);

        TextView tvurl = findViewById(R.id.tvurl);
        TextView tvinterval = findViewById(R.id.tvinterval);

        tvurl.setText(tvurl.getText().toString()+url);
        tvinterval.setText(tvinterval.getText().toString()+interval/60000);

        Spinner spinner = findViewById(R.id.spinner4);
        ArrayList<String> times = new ArrayList<>();
        times.add("15 минут");
        times.add("30 минут");
        times.add("45 минут");
        times.add("60 минут");
        ArrayAdapter<?> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, times);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                intervall = 60000*15*(i+1);
                SharedPreferences.Editor e = sp.edit();
                e.putInt("interval", intervall);
                e.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        checkService();
    }
    @Override
    public void onResume(){
        checkService();
        super.onResume();
    }

    void checkService(){
        ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(50);

        TextView tvstatus = findViewById(R.id.status);
        for (int i=0; i<rs.size(); i++) {
            ActivityManager.RunningServiceInfo
                    rsi = rs.get(i);
            Log.i("Service", "Process " + rsi.process + " with component " + rsi.service.getClassName());
            if (rsi.service.getClassName().equals(ParceService.class.getName())){
                tvstatus.setText("сервис запущен");
                return;
            }

        }
        tvstatus.setText("сервис не запущен");
    }

    public void onOpenWVclick(View view){
        startActivity(new Intent(MainActivity.this, WVActivity.class));
    }

    public void onStartServiceClick(View view){
        String url = sp.getString("url", "");
        Log.i("aaaaaaaaa", url);
        Intent intent = new Intent(MainActivity.this, ParceService.class);
        intent.putExtra("url", url);
        startService(intent);
        checkService();
    }
    public void onStopServiceClick(View view){
        stopService(new Intent(MainActivity.this, ParceService.class));
        checkService();
    }
}