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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

//-----------------------------------------------------------------------------
        int countstarts  = sp.getInt("cs", 0);
        if (countstarts>99){
            Toast.makeText(this, "Ошибка: приложение недоступно!!!", Toast.LENGTH_SHORT).show();
            Button b = findViewById(R.id.button2);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "Ошибка: приложение недоступно!!!", Toast.LENGTH_SHORT).show();
                }
            });
            Button b1 = findViewById(R.id.button);
            b1.setClickable(false);
        }
        else{
            SharedPreferences.Editor e = sp.edit();
            e.putInt("cs", countstarts++);
            e.apply();
        }
//---------------------------------------------------------------------------------------------------

        String url = sp.getString("url", "");
        int interval = sp.getInt("interval", 15);
        int intervalPos = 0;
        switch (interval){
            case 0:
                intervalPos = 0;
                break;
            case 1:
                intervalPos = 1;
                break;
            case 3:
                intervalPos = 2;
                break;
            case 5:
                intervalPos = 3;
                break;
            case 10:
                intervalPos = 4;
                break;
            case 15:
                intervalPos = 5;
                break;
            case 30:
                intervalPos = 6;
                break;
            case 45:
                intervalPos = 7;
                break;
            case 60:
                intervalPos = 8;
                break;
        }
;

        Spinner spinner = findViewById(R.id.spinner4);
        ArrayList<String> times = new ArrayList<>();
        times.add("30 секунд");
        times.add("1 минута");
        times.add("3 минуты");
        times.add("5 минут");
        times.add("10 минут");
        times.add("15 минут");
        times.add("30 минут");
        times.add("45 минут");
        times.add("60 минут");
        ArrayAdapter<?> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, times);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(intervalPos);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 switch (i){
                     case 0:
                         intervall = 30000;
                         break;
                     case 1:
                         intervall = 60000;
                         break;
                     case 2:
                         intervall = 180000;
                         break;
                     case 3:
                         intervall = 300000;
                         break;
                     case 4:
                         intervall = 600000;
                         break;
                     case 5:
                         intervall = 900000;
                         break;
                     case 6:
                         intervall = 1800000;
                         break;
                     case 7:
                         intervall = 2700000;
                         break;
                     case 8:
                         intervall = 3600000;
                         break;
                 }
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
        checkUrl();
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
    void checkUrl(){
        ArrayList<String> urls = new ArrayList<>();
        if (!sp.getString("url1", "").equals(""))
            urls.add(sp.getString("url1", ""));
        if (!sp.getString("url2", "").equals(""))
            urls.add(sp.getString("url2", ""));
        if (!sp.getString("url3", "").equals(""))
            urls.add(sp.getString("url3", ""));
        if (!sp.getString("url4", "").equals(""))
            urls.add(sp.getString("url4", ""));
        if (!sp.getString("url5", "").equals(""))
            urls.add(sp.getString("url5", ""));
        ArrayList<String> names = new ArrayList<>();
        if (!sp.getString("name1", "").equals(""))
            names.add(sp.getString("name1", ""));
        if (!sp.getString("name2", "").equals(""))
            names.add(sp.getString("name2", ""));
        if (!sp.getString("name3", "").equals(""))
            names.add(sp.getString("name3", ""));
        if (!sp.getString("name4", "").equals(""))
            names.add(sp.getString("name4", ""));
        if (!sp.getString("name5", "").equals(""))
            names.add(sp.getString("name5", ""));
        TextView[] tvs = new TextView[5];
        tvs[0] = findViewById(R.id.name1);
        tvs[0].setText("слот не активен");
        tvs[1] = findViewById(R.id.name2);
        tvs[1].setText("слот не активен");
        tvs[2] = findViewById(R.id.name3);
        tvs[2].setText("слот не активен");
        tvs[3] = findViewById(R.id.name4);
        tvs[3].setText("слот не активен");
        tvs[4] = findViewById(R.id.name5);
        tvs[4].setText("слот не активен");
        Log.i("gfggf", names.size()+"|||"+urls.size());
        for (int i=0; i<names.size(); i++){
            tvs[i].setText(names.get(i));
            tvs[i].setHint(urls.get(i));
        }
    }

    public void onOpenWVclick(View view){
        startActivity(new Intent(MainActivity.this, WVActivity.class));
    }

    public void onStartServiceClick(View view){
        SharedPreferences.Editor e = sp.edit();
        EditText etname = findViewById(R.id.etname);
        if (sp.getString("name1", "").equals("")&&!sp.getString("url1", "").equals("")){
            e.putString("name1", etname.getText().toString());
        }
        else if (sp.getString("name2", "").equals("")&&!sp.getString("url2", "").equals("")){
            e.putString("name2", etname.getText().toString());
        }
        else if (sp.getString("name3", "").equals("")&&!sp.getString("url3", "").equals("")){
            e.putString("name3", etname.getText().toString());
        }
        else if (sp.getString("name4", "").equals("")&&!sp.getString("url4", "").equals("")){
            e.putString("name4", etname.getText().toString());
        }
        else if (sp.getString("name5", "").equals("")&&!sp.getString("url5", "").equals("")){
            e.putString("name5", etname.getText().toString());
        }
        e.commit();
        Intent intent = new Intent(MainActivity.this, ParceService.class);
        startService(intent);
        checkService();
        checkUrl();
    }
    public void onStopServiceClick(View view){
        stopService(new Intent(MainActivity.this, ParceService.class));
        checkService();
    }
    public void onOpenSetClick(View view){
        TextView tv = (TextView)view;
        String url = tv.getHint().toString();
        if (!url.equals("")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }
    public void onCancelClick(View view){
        String id = getResources().getResourceEntryName(view.getId());
        TextView[] tvs = new TextView[5];
        tvs[0] = findViewById(R.id.name1);
        tvs[1] = findViewById(R.id.name2);
        tvs[2] = findViewById(R.id.name3);
        tvs[3] = findViewById(R.id.name4);
        tvs[4] = findViewById(R.id.name5);
        String name = id;
        switch (id){
            case "cancel1":
                name = tvs[0].getText().toString();
                break;
            case "cancel2":
                name = tvs[1].getText().toString();
                break;
            case "cancel3":
                name = tvs[2].getText().toString();
                break;
            case "cancel4":
                name = tvs[3].getText().toString();
                break;
            case "cancel5":
                name = tvs[4].getText().toString();
                break;
        }
        deleteItem(name);
        checkUrl();
    }
    void deleteItem(String name){
        SharedPreferences.Editor e = sp.edit();
        if (sp.getString("name1", "").equals(name)){
            e.putString("name1", "");
            e.putString("url1", "");
        }
        else if (sp.getString("name2", "").equals(name)){
            e.putString("name2", "");
            e.putString("url2", "");
        }
        else if (sp.getString("name3", "").equals(name)){
            e.putString("name3", "");
            e.putString("url3", "");
        }
        else if (sp.getString("name4", "").equals(name)){
            e.putString("name4", "");
            e.putString("url4", "");
        }
        else if (sp.getString("name5", "").equals(name)){
            e.putString("name5", "");
            e.putString("url5", "");
        }
        e.apply();
    }
}