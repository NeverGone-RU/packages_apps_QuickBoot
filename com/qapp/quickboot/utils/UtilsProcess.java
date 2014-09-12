
package com.qapp.quickboot.utils;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.os.RemoteException;
import android.util.Log;
import java.util.*;

public class UtilsProcess
{

    public UtilsProcess()
    {
    }

    private static void clearAppAlarms(Context context, ArrayList arraylist)
    {
        if(arraylist != null && arraylist.size() > 0)
        {
            String as[] = new String[arraylist.size()];
            for(int i = 0; i < as.length; i++)
                as[i] = (String)arraylist.get(i);

            Intent intent = new Intent("org.codeaurora.quickboot.appkilled");
            intent.putExtra("android.intent.extra.PACKAGES", as);
            context.sendBroadcast(intent, "android.permission.DEVICE_POWER");
            return;
        } else
        {
            return;
        }
    }

    static ArrayList getLauncherList(Context context)
    {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        List list = context.getPackageManager().queryIntentActivities(intent, 0);
        ArrayList arraylist;
        if(list == null)
        {
            logd("No Launcher");
            arraylist = null;
        } else
        {
            arraylist = new ArrayList();
            ResolveInfo resolveinfo = context.getPackageManager().resolveActivity(intent, 0);
            if(resolveinfo != null && !"android".equals(resolveinfo.activityInfo.packageName))
            {
                arraylist.add(resolveinfo.activityInfo.packageName);
                return arraylist;
            }
            for(Iterator iterator = list.iterator(); iterator.hasNext(); arraylist.add(((ResolveInfo)iterator.next()).activityInfo.packageName));
            if(arraylist.size() == 0)
                return null;
        }
        return arraylist;
    }

    private static String getLiveWallPaper(Context context)
    {
        WallpaperInfo wallpaperinfo = WallpaperManager.getInstance(context).getWallpaperInfo();
        if(wallpaperinfo != null)
            return wallpaperinfo.getComponent().getPackageName();
        else
            return null;
    }

    private static ArrayList getProcessWhiteList(Context context)
    {
        ArrayList arraylist = new ArrayList();
        if(isPowerOffAlarmSupported(context))
            arraylist.add("com.android.deskclock");
        return arraylist;
    }

    private static boolean isKillablePackage(Context context, PackageInfo packageinfo)
    {
        return (1 & packageinfo.applicationInfo.flags) == 0 || (8 & packageinfo.applicationInfo.flags) == 0;
    }

    private static boolean isPowerOffAlarmSupported(Context context)
    {
        return true;
    }

    public static void killApplication(String s, int i)
    {
        IActivityManager iactivitymanager = ActivityManagerNative.getDefault();
        try
        {
            iactivitymanager.killApplicationWithAppId(s, i, "quickboot");
            return;
        }
        catch(RemoteException remoteexception)
        {
            logd(remoteexception);
        }
    }

    public static void killApplications(Context context)
    {
        PackageManager packagemanager = context.getPackageManager();
        IActivityManager iactivitymanager = ActivityManagerNative.getDefault();
        List list = packagemanager.getInstalledPackages(0);
        String s = getLiveWallPaper(context);
        ArrayList arraylist = getLauncherList(context);
        ArrayList arraylist1 = getProcessWhiteList(context);
        ArrayList arraylist2 = new ArrayList();
        Iterator iterator = list.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            PackageInfo packageinfo = (PackageInfo)iterator.next();
            String s1 = packageinfo.packageName;
            if(!arraylist.contains(s1) && !s1.equals(s) && !s1.equals(context.getPackageName()) && !arraylist1.contains(packageinfo.applicationInfo.processName) && isKillablePackage(context, packageinfo))
            {
                arraylist2.add(s1);
                try
                {
                    iactivitymanager.killApplicationWithAppId(s1, packageinfo.applicationInfo.uid, "quickboot");
                }
                catch(RemoteException remoteexception)
                {
                    logd(remoteexception);
                }
            }
        } while(true);
        clearAppAlarms(context, arraylist2);
    }

    private static void logd(Object obj)
    {
        String s = Thread.currentThread().getStackTrace()[3].getMethodName();
        String s1 = (new StringBuilder()).append("[").append(s).append("] ").append(obj).toString();
        Log.d("QuickBoot", (new StringBuilder()).append(s1).append("").toString());
    }
}
