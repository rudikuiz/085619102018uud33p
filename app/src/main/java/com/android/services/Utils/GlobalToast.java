package com.android.services.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Tambora on 02/09/2016.
 */
public class GlobalToast {


    public static void ShowToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }
}
