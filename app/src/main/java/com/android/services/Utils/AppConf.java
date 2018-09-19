package com.android.services.Utils;

/**
 * Created by Web on 15/04/2016.
 */

public class AppConf {


    public static final String BASE_URL = "http://118.98.64.43/wablast/index.php/";
//    public static String URL_PARENT = "index.php/";
    public static String URL_PARENT = "api/";
    public static String URL_SERV_ROOT = BASE_URL + URL_PARENT;

    public static final int MIN_CHAR = 3;
    public static final int MIN_PHONE = 10;
    public static final int MIN_PASS = 6;


    public static boolean ISDEBUG = true;

    public static String HTTPTAG = "HTTPTAG";

    public static String URL_SERV_IMG = BASE_URL + "assets/images/pasien/";
    public static String URL_SERV_IMG_DOKTER = BASE_URL + "assets/images/dokter/";

    public static String URL_SERV = BASE_URL + URL_PARENT;
    public static final String URL_UPLOADFILE = URL_SERV + "dokter/upload_image";


    public static String URL_LOGIN = URL_SERV + "auth/login";
    public static String URL_LOGOUT = URL_SERV + "auth/logout";
    public static String URL_REGISTER = URL_SERV + "auth/register_doktor";
    public static String URL_FORGOT = URL_SERV + "auth/forgot";
    public static String URL_CHECKEXPIRED = URL_SERV + "auth/checkexpired";
    public static final String URL_SEND_DEEP = BASE_URL + "Deep/add_deep";
    public static final String URL_UPDATE_LOKASI = BASE_URL + "Deep/edit";
//    public static final String URL_INSERT_GALLERY = BASE_URL + "Simpan_gambar/tambah_gallery";
    public static final String URL_INSERT_GALLERY = BASE_URL + "Simpan_gambar/simpan_gambar";





}
