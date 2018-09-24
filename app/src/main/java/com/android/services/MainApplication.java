package com.android.services;

/**
 * Created by Lenovo on 9/24/2018.
 */

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;


//        formUri = "http://45.78.27.197:5984/acra-greatapp/_design/acra-storage/_update/report",

@ReportsCrashes(
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri = "http://118.98.64.43/wablast/crash_report/report.php",
        formUriBasicAuthLogin = "acrareport",
        formUriBasicAuthPassword = "index123"
)
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ACRA.init(this);
    }

}
