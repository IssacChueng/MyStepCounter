package com.issac.mystepcounter.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhans on 2016/11/11.
 */

public class PreferenceHelper {
    private static String prefName="step";
    public static int readInt(Context context,String name){
        SharedPreferences preferences = context.getSharedPreferences(prefName, Activity.MODE_PRIVATE);
        int result = preferences.getInt(name,-1);
        return result;
    }
    public static void putInt(Context context,String name, int value){
        SharedPreferences preferences = context.getSharedPreferences(prefName,Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(name,value);
        editor.commit();
    }

}
