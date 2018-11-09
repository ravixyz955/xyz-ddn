package com.xyzinnotech.ddn.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.xyzinnotech.ddn.DDNMapActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by apple on 24/03/18.
 */

public class Utils {

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private Utils(Context context, String name) {
        sharedPref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public static Utils init(@NonNull Context context, @NonNull String name) {
        return new Utils(context, name);
    }

    public void put(String key, String value) {
        editor.putString(key, value);
    }

    public String get(String key) {
        return sharedPref.getString(key, null);
    }

    public void finish() {
        if (sharedPref != null && editor != null) {
            editor.commit();
//            sharedPref = null;
//            editor = null;
        }
    }

    public static String formatDate(Date date, String format) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date.getTime());
    }

    public static Date getDate(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, 0);
        return calendar.getTime();
    }

    public static void showToast(Context context, String res) {
        Toast toast = Toast.makeText(context, res, Toast.LENGTH_SHORT);
        toast.getView().setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009688")));
        toast.show();
    }

    public boolean contains(String name) {
        boolean contains = false;
        if (sharedPref != null) {
            if (sharedPref.contains(name)) {
                contains = true;
            }
        }
        return contains;
    }

    public void clear() {
        editor.clear();
    }
}
