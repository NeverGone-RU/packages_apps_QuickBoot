
package com.qapp.quickboot.poweron;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import com.qapp.quickboot.utils.Utils;

public class PowerOnTask extends AsyncTask
{

    public PowerOnTask(Context context)
    {
        mContext = null;
        mWakeLock = null;
        mActivityManager = null;
        mContext = context;
        mWakeLock = ((PowerManager)mContext.getSystemService("power")).newWakeLock(1, "QuickBoot");
        mActivityManager = ActivityManagerNative.getDefault();
    }

    private void acquireWakeLock()
    {
        if(!mWakeLock.isHeld())
            mWakeLock.acquire();
    }

    private static void logd(Object obj)
    {
        String s = Thread.currentThread().getStackTrace()[3].getMethodName();
        String s1 = (new StringBuilder()).append("[").append(s).append("] ").append(obj).toString();
        Log.d("QuickBoot", (new StringBuilder()).append(s1).append("").toString());
    }

    private void releaseWakeLock()
    {
        if(mWakeLock.isHeld())
            mWakeLock.release();
    }

    protected transient Object doInBackground(Object aobj[])
    {
        Intent intent = new Intent("org.codeaurora.quickboot.poweron_start");
        mContext.sendBroadcast(intent, "android.permission.DEVICE_POWER");
        if(SystemService.isRunning("qbcharger"))
            Utils.stopQbCharger();
        while(SystemService.isRunning("qbcharger")) 
        {
            int i;
            Intent intent1;
            RemoteException remoteexception;
            try
            {
                Thread.sleep(100L);
            }
            catch(InterruptedException interruptedexception1) { }
            logd("waiting qbcharger stopped...");
        }
        Utils.startBootAnimation();
        i = 10;
        do
            try
            {
                Thread.sleep(50L);
            }
            catch(InterruptedException interruptedexception) { }
        while(--i > 0 && !SystemService.isRunning("bootanim"));
        Utils.vibrate(mContext, 300L);
        SystemClock.sleep(300L);
        Utils.wakeup(mContext);
        Utils.restoreStatus(mContext);
        SystemProperties.set("sys.shutdown.requested", "");
        intent1 = new Intent("android.intent.action.BOOT_COMPLETED", null);
        intent1.putExtra("android.intent.extra.user_handle", 0);
        intent1.putExtra("from_quickboot", true);
        try
        {
            mActivityManager.broadcastIntent(null, intent1, null, null, 0, null, null, "android.permission.RECEIVE_BOOT_COMPLETED", -1, false, false, -1);
        }
        // Misplaced declaration of an exception variable
        catch(RemoteException remoteexception)
        {
            remoteexception.printStackTrace();
        }
        Utils.enableWindowInput(false);
        SystemClock.sleep(2000L);
        Utils.stopBootAnimation();
        Utils.enableWindowInput(true);
        return null;
    }

    protected void onCancelled()
    {
        super.onCancelled();
    }

    protected void onPostExecute(Object obj)
    {
        SystemProperties.set("sys.quickboot.enable", "0");
        SystemProperties.set("sys.quickboot.poweron", "0");
        Utils.clearOngoingFlag(mContext);
        releaseWakeLock();
        logd("PowerOnTask exit");
        Utils.exit(mContext);
        super.onPostExecute(obj);
    }

    protected void onPreExecute()
    {
        logd("PowerOnTask start");
        acquireWakeLock();
        SystemProperties.set("sys.quickboot.poweron", "1");
        super.onPreExecute();
    }

    private IActivityManager mActivityManager;
    private Context mContext;
    private android.os.PowerManager.WakeLock mWakeLock;
}
