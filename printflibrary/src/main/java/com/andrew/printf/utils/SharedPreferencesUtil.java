package com.andrew.printf.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

    public static String getContentByKey(String key,Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(key,null);
    }

    public static void setContentByKey(String key,String content,Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        sharedPreferences.edit().putString(key,content).apply();
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("BLUETOOTH_LIB", Context.MODE_PRIVATE);
    }
}
