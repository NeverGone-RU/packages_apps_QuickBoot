
package com.qapp.quickboot.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UtilsSharedPreferences
{

    public UtilsSharedPreferences()
    {
    }

    public static boolean getBooleanValueSaved(Context context, String s, boolean flag)
    {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(s, flag);
    }

    public static void saveBooleanValue(Context context, String s, boolean flag)
    {
        android.content.SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(s, flag);
        editor.commit();
    }
}
