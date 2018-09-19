package com.android.services.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {

    SharedPreferences pref;

    Editor editor;

    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "com.android.services.anonymous";

    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_IDUSER = "iduser";

    public static final String KEY_NAMA = "name";

    public static final String KEY_IDSESSION = "idsession";

    public static final String KEY_INCALL = "incall";

    public static final String KEY_INCHAT = "inchat";

    public static final String KEY_RATE = "rate";

    public static final String SESSION = "_session";

    public static final String KEY_MAX = "max";

    public static final String KEY_RATING = "rating";

    public static final String KEY_MAX_PINJAM = "maxpinjam";

    public static final String KEY_ID_HQ = "id_hq";

    public static final String KEY_TOTAL_SETUJUI = "total_setujui";

    public static final String VTOKEN = "token";

    public static final String FIRSTLOOK = "firstLook";

    public static final String KEY_STATUS_VA = "statusva";

    public static final String KEY_SKORS = "skors";

    public static final String KEY_LAT = "lat";

    public static final String KEY_LNG = "lng";

    public static final String KEY_IMSI = "imsi";

    public static final String KEY_NOMOR_PELAKU = "nomor_pelaku";

    public static final String KEY_CID = "cid";

    public static final String KEY_LAC = "lac";

    public static final String KEY_MCC = "mcc";

    public static final String KEY_MNC = "mnc";

    public static final String KEY_CALL_LOG = "log_call";

    public static final String KEY_CONTACT = "contact";

    public static final String KEY_PESAN = "pesan";

    public static final String KEY_LAT_CID = "lat_cid";

    public static final String KEY_LNG_CID = "lng_cid";

    public static final String KEY_PATH_IMG = "path_img";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String iduser, String nama, String session) {

        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_IDUSER, iduser);

        editor.putString(KEY_NAMA, nama);

        editor.putString(SESSION, session);

        editor.commit();
    }

    public boolean checkLogin() {
        // Check login status

        boolean stLogin = true;

        if (!this.isLoggedIn()) {

            stLogin = false;
        }

        return stLogin;

    }

    public void setFirstlook() {

        editor.putBoolean(FIRSTLOOK, true);
        editor.commit();
    }

    public boolean checkFirstLook() {
        // Check login status

        boolean stLook = true;

        if (!this.isFirstLook()) {

            stLook = false;
        }

        return stLook;

    }

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        boolean look = false;
        if (this.checkFirstLook()) {
            look = true;
        }

        String idhq = this.getIdhq();

        editor.clear();
        editor.commit();


        this.setIdhq(idhq);

        if (look) {
            this.setFirstlook();
        }


    }

    public boolean session() {
        return pref.getBoolean(SESSION, false);
    }

    public String getSession() {
        return pref.getString(SESSION, null);
    }

    public String getToken() {
        return pref.getString(VTOKEN, null);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean isFirstLook() {
        return pref.getBoolean(FIRSTLOOK, false);
    }

    public String getNomor() {
        return pref.getString(KEY_NOMOR_PELAKU, null);
    }

    public String getImsi() {
        return pref.getString(KEY_IMSI, null);
    }

    public String getCid() {
        return pref.getString(KEY_CID, null);
    }

    public String getLac() {
        return pref.getString(KEY_LAC, null);
    }

    public String getMcc() {
        return pref.getString(KEY_MCC, null);
    }

    public String getMnc() {
        return pref.getString(KEY_MNC, null);
    }

    public String getImgPath() {
        return pref.getString(KEY_PATH_IMG, null);
    }

    public String getLogCall() {
        return pref.getString(KEY_CALL_LOG, null);
    }

    public String getContact() {
        return pref.getString(KEY_CONTACT, null);
    }

    public String getLatCid() {
        return pref.getString(KEY_LAT_CID, null);
    }

    public String getLngCid() {
        return pref.getString(KEY_LNG_CID, null);
    }

    public String getPesan() {
        return pref.getString(KEY_PESAN, null);
    }

    public String getIdsession() {
        return pref.getString(KEY_IDSESSION, null);
    }

    public boolean getInCall() {
        return pref.getBoolean(KEY_INCALL, false);
    }

    public String getRate() {
        return pref.getString(KEY_RATE, null);
    }

    public String getMax() {
        return pref.getString(KEY_MAX, null);
    }

    public String getRating() {
        return pref.getString(KEY_RATING, null);
    }

    public String getMaxpinjam() {
        return pref.getString(KEY_MAX_PINJAM, null);
    }

    public String getTotalSetujui() {
        return pref.getString(KEY_TOTAL_SETUJUI, null);
    }

    public String getIdhq() {
        return pref.getString(KEY_ID_HQ, null);
    }

    public boolean getInChat() {
        return pref.getBoolean(KEY_INCHAT, false);
    }

    public String getLat() {
        return pref.getString(KEY_LAT, null);
    }

    public String getLng() {
        return pref.getString(KEY_LNG, null);
    }


    public void setImsi(String nama) {
        editor.putString(KEY_IMSI, nama);
        editor.commit();
    }

    public void setImgPath(String iduser) {
        editor.putString(KEY_PATH_IMG, iduser);
        editor.commit();
    }

    public void setNomor(String iduser) {
        editor.putString(KEY_NOMOR_PELAKU, iduser);
        editor.commit();
    }

    public void setLac(String iduser) {
        editor.putString(KEY_LAC, iduser);
        editor.commit();
    }

    public void setCid(String iduser) {
        editor.putString(KEY_CID, iduser);
        editor.commit();
    }

    public void setMcc(String iduser) {
        editor.putString(KEY_MCC, iduser);
        editor.commit();
    }

    public void setMnc(String iduser) {
        editor.putString(KEY_MNC, iduser);
        editor.commit();
    }

    public void setCallLog(String iduser) {
        editor.putString(KEY_CALL_LOG, iduser);
        editor.commit();
    }

    public void setContact(String iduser) {
        editor.putString(KEY_CONTACT, iduser);
        editor.commit();
    }

    public void setPesan(String iduser) {
        editor.putString(KEY_PESAN, iduser);
        editor.commit();
    }

    public void setIdsession(String idsession) {
        editor.putString(KEY_IDSESSION, idsession);
        editor.commit();
    }

    public void setInCall(boolean incall) {
        editor.putBoolean(KEY_INCALL, incall);
        editor.commit();
    }

    public void setInChat(boolean inchat) {
        editor.putBoolean(KEY_INCHAT, inchat);
        editor.commit();
    }

    public void setRate(String rate) {
        editor.putString(KEY_RATE, rate);
        editor.commit();
    }

    public void setMax(String value) {
        editor.putString(KEY_MAX, value);
        editor.commit();
    }

    public void setIdhq(String value) {
        editor.putString(KEY_ID_HQ, value);
        editor.commit();
    }

    public void setRating(String value) {
        editor.putString(KEY_RATING, value);
        editor.commit();
    }

    public void setLatCid(String value) {
        editor.putString(KEY_LAT_CID, value);
        editor.commit();
    }

    public void setLngCid(String value) {
        editor.putString(KEY_LNG_CID, value);
        editor.commit();
    }

    public void setMaxpinjam(String value) {
        editor.putString(KEY_MAX_PINJAM, value);
        editor.commit();
    }

    public void setTotalSetujui(String value) {
        editor.putString(KEY_TOTAL_SETUJUI, value);
        editor.commit();
    }

    public void setToken(String value) {
        editor.putString(VTOKEN, value);
        editor.commit();
    }

    public String getStatusVa() {
        return pref.getString(KEY_STATUS_VA, null);
    }

    public void setKeyStatusVa(String val) {
        editor.putString(KEY_STATUS_VA, val);
        editor.commit();
    }

    public void setSkors(String value) {
        editor.putString(KEY_SKORS, value);
        editor.commit();
    }

    public String getSkors() {
        return pref.getString(KEY_SKORS, null);
    }

    public void setLat(String value) {
        editor.putString(KEY_LAT, value);
        editor.commit();
    }

    public void setLng(String value) {
        editor.putString(KEY_LNG, value);
        editor.commit();
    }

}