
package com.qapp.quickboot.utils;

import android.app.ActivityManager;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.view.IWindowManager;
import com.android.internal.statusbar.IStatusBarService;
import java.util.*;


public class Utils
{

    public Utils()
    {
    }

    public static void clearNotification()
    {
        logd("");
        IStatusBarService istatusbarservice = com.android.internal.statusbar.IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        try
        {
            istatusbarservice.onClearAllNotifications();
            return;
        }
        catch(RemoteException remoteexception)
        {
            remoteexception.printStackTrace();
        }
    }

    public static void clearOngoingFlag(Context context)
    {
        SystemProperties.set("persist.sys.quickboot_ongoing", null);
    }

    public static void clearRecentApps(Context context)
    {
        logd("");
        ActivityManager activitymanager = (ActivityManager)context.getSystemService("activity");
        ArrayList arraylist = UtilsProcess.getLauncherList(context);
        arraylist.add(context.getPackageName());
        Iterator iterator = activitymanager.getRecentTasks(50, 2).iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            android.app.ActivityManager.RecentTaskInfo recenttaskinfo = (android.app.ActivityManager.RecentTaskInfo)iterator.next();
            Intent intent = recenttaskinfo.baseIntent;
            if(intent != null)
            {
                String s = intent.getComponent().getPackageName();
                if(!arraylist.contains(s))
                {
                    logd((new StringBuilder()).append("Remove ").append(s).toString());
                    activitymanager.removeTask(recenttaskinfo.persistentId, 1);
                }
            }
        } while(true);
    }

    public static void enableButtonLight(boolean flag)
    {
    }

    public static void enableWindowInput(boolean flag)
    {
        logd("");
        IWindowManager iwindowmanager = android.view.IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        try
        {
            iwindowmanager.setEventDispatching(flag);
            return;
        }
        catch(RemoteException remoteexception)
        {
            remoteexception.printStackTrace();
        }
    }

    public static void exit(Context context)
    {
        logd("");
        UtilsProcess.killApplication(context.getPackageName(), Process.myUid());
    }

    public static boolean isAirplaneModeOn(Context context)
    {
        return android.provider.Settings.Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) == 1;
    }

    public static boolean isOngoing(Context context)
    {
        return SystemProperties.getBoolean("persist.sys.quickboot_ongoing", false);
    }

    public static boolean isUnderQuickBoot(Context context)
    {
        return "1".equals(SystemProperties.get("sys.quickboot.enable", "0"));
    }

    private static void logd(Object obj)
    {
        String s = Thread.currentThread().getStackTrace()[3].getMethodName();
        String s1 = (new StringBuilder()).append("[").append(s).append("] ").append(obj).toString();
        Log.d("QuickBoot", (new StringBuilder()).append(s1).append("").toString());
    }

    public static void restoreStatus(Context context)
    {
        logd("");
        setAirplaneMode(context, UtilsSharedPreferences.getBooleanValueSaved(context, "airplane_mode", isAirplaneModeOn(context)));
    }

    public static void saveStatus(Context context)
    {
        logd("");
        UtilsSharedPreferences.saveBooleanValue(context, "airplane_mode", isAirplaneModeOn(context));
    }

    public static void setAirplaneMode(Context context, boolean flag)
    {
        if(isAirplaneModeOn(context) == flag)
            return;
        android.content.ContentResolver contentresolver = context.getContentResolver();
        int i;
        if(flag)
            i = 1;
        else
            i = 0;
        android.provider.Settings.Global.putInt(contentresolver, "airplane_mode_on", i);
        Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
        intent.putExtra("state", flag);
        context.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    public static void setOngoingFlag(Context context)
    {
        SystemProperties.set("persist.sys.quickboot_ongoing", "true");
    }

    public static void sleep(Context context)
    {
        logd("");
        ((PowerManager)context.getSystemService("power")).goToSleep(SystemClock.uptimeMillis());
    }

    public static void startBootAnimation()
    {
        logd("");
        SystemProperties.set("service.bootanim.exit", "0");
        SystemProperties.set("ctl.start", "bootanim");
    }

    public static void startQbCharger()
    {
        logd("");
        SystemProperties.set("sys.qbcharger.enable", "true");
    }

    public static void stopBootAnimation()
    {
        logd("");
        SystemProperties.set("ctl.stop", "bootanim");
    }

    public static void stopQbCharger()
    {
        logd("");
        SystemProperties.set("sys.qbcharger.enable", "false");
    }

    public static void vibrate(Context context, long l)
    {
        logd("");
        ((Vibrator)context.getSystemService("vibrator")).vibrate(l);
    }

    public static void wakeup(Context context)
    {
        logd("");
        ((PowerManager)context.getSystemService("power")).wakeUp(SystemClock.uptimeMillis());
    }
}
