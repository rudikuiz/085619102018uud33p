package com.android.services.Utils;

import android.util.Log;

/**
 * Created by Tambora on 02/09/2016.
 */
public class AndLog {


    private static boolean show = AppConf.ISDEBUG;

    public static void ShowLog(String tag, String message) {
        if (show) {

            int maxLogSize = 2000;
            for (int i = 0; i <= message.length() / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i + 1) * maxLogSize;
                end = end > message.length() ? message.length() : end;
//                android.util.Log.d(tag, message.substring(start, end));

                Log.i(tag, message.substring(start, end));
            }


        }
    }
}
