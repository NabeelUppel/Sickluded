package com.example.sickluded;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import java.util.Set;

public class SharedPreferenceClass {

    Context c;

    public static SharedPreferences getSharedPreferences(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c);
    }

    public static void addData(Context c, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(c).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void addStringSet(Context c, String key, Set<String> value) {
        getSharedPreferences(c).edit().putStringSet(key, value).apply();
    }

    public static String getData(Context c, String key) {

        return getSharedPreferences(c).getString(key, "");
    }

    public static void deleteAllData(Context c) {
        SharedPreferences.Editor editor = getSharedPreferences(c).edit();
        editor.clear();
        editor.apply();
    }

}
