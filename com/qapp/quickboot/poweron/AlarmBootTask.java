
package com.qapp.quickboot.poweron;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import com.qapp.quickboot.utils.Utils;

public class AlarmBootTask extends AsyncTask
{

    public AlarmBootTask(Context context)
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
        if(SystemService.isRunning("qbcharger"))
            Utils.stopQbCharger();
        Utils.restoreStatus(mContext);
        SystemProperties.set("sys.shutdown.requested", "");
        Intent intent = new Intent("android.intent.action.BOOT_COMPLETED", null);
        intent.putExtra("android.intent.extra.user_handle", 0);
        intent.putExtra("from_quickboot", true);
        try
        {
            mActivityManager.broadcastIntent(null, intent, null, null, 0, null, null, "android.permission.RECEIVE_BOOT_COMPLETED", -1, false, false, -1);
        }
        catch(RemoteException remoteexception)
        {
            remoteexception.printStackTrace();
        }
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
        logd("AlarmBootTask exit");
        Utils.exit(mContext);
        super.onPostExecute(obj);
    }

    protected void onPreExecute()
    {
        if(!Utils.isUnderQuickBoot(mContext))
            Utils.exit(mContext);
        logd("AlarmBootTask start");
        acquireWakeLock();
        SystemProperties.set("sys.quickboot.poweron", "1");
        super.onPreExecute();
    }

    private IActivityManager mActivityManager;
    private Context mContext;
    private android.os.PowerManager.WakeLock mWakeLock;
}
