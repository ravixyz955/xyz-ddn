package com.xyzinnotech.ddn.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.xyzinnotech.ddn.model.Dwelling;

/**
 * Created by KT on 28/12/15.
 */
public class DataUtils {

    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_MOBILE = "key_mobile";
    private static final String KEY_COUNTRY_CODE = "key_country_code";
    private static final String KEY_NAME = "key_name";
    private static final String KEY_PASSWORD = "key_password";
    private static final String KEY_TOKEN = "key_token";
    private static final String KEY_STATUS = "key_status";
    private static final String KEY_AUTO_STATUS = "key_auto_status";
    private static final String KEY_ACTIVE = "key_active";
    private static final String KEY_DWELLING = "key_active";
    private static final String USER_PREF = "user_pref";
    private static final String DWELLING_PREF = "dwelling_pref";

    public static void saveName(Context mContext, String name) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, name);
        editor.apply();
    }

    public static void saveEmail(Context mContext, String email) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public static void saveMobile(Context mContext, String mobile) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_MOBILE, mobile);
        editor.apply();
    }

    public static void savePassword(Context mContext, String password) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public static void saveToken(Context mContext, String token) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public static void saveCountryCode(Context mContext, String countryCode) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_COUNTRY_CODE, countryCode);
        editor.apply();
    }

    public static String getEmail(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public static String getName(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NAME, null);
    }

    public static String getMobileNumber(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_MOBILE, null);
    }

    public static String getPassword(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PASSWORD, null);
    }

    public static String getToken(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public static String getCountryCode(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_COUNTRY_CODE, null);
    }

    public static void saveStatus(Context mContext, String status) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTO_STATUS, null);
        editor.putString(KEY_STATUS, status);
        editor.apply();
    }

    public static void saveAutoStatus(Context mContext, String status) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUTO_STATUS, status);
        editor.apply();
    }

    public static String getStatus(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_STATUS, null);
    }

    public static String getAutoStatus(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_AUTO_STATUS, null);
    }

    public static void setActive(Context mContext, boolean active) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_ACTIVE, active);
        editor.apply();
    }

    public static boolean isActivated(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_ACTIVE, false);
    }

    public static void saveDwelling(Context mContext, String dwellingJson) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(DWELLING_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DWELLING, dwellingJson);
        editor.apply();
    }

    public static Dwelling getDwelling(Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(DWELLING_PREF, Context.MODE_PRIVATE);
        String dwellingJson = sharedPreferences.getString(KEY_DWELLING, null);
        Dwelling dwelling = null;
        if(dwellingJson != null) {
            dwelling = new Gson().fromJson(dwellingJson, Dwelling.class);
        }
        return dwelling;
    }
}
