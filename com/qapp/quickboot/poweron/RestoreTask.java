
package com.qapp.quickboot.poweron;

import android.content.Context;
import android.os.*;
import android.util.Log;
import com.qapp.quickboot.utils.Utils;

public class RestoreTask extends AsyncTask
{

    public RestoreTask(Context context)
    {
        mContext = null;
        mWakeLock = null;
        mContext = context;
        mWakeLock = ((PowerManager)mContext.getSystemService("power")).newWakeLock(1, "QuickBoot");
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
        Utils.restoreStatus(mContext);
        return null;
    }

    protected void onCancelled()
    {
        super.onCancelled();
    }

    protected void onPostExecute(Object obj)
    {
        SystemProperties.set("sys.quickboot.enable", "0");
        Utils.clearOngoingFlag(mContext);
        releaseWakeLock();
        logd("RestoreTask exit");
        Utils.exit(mContext);
        super.onPostExecute(obj);
    }

    protected void onPreExecute()
    {
        logd("RestoreTask start");
        if(!Utils.isOngoing(mContext))
            Utils.exit(mContext);
        acquireWakeLock();
        super.onPreExecute();
    }

    private Context mContext;
    private android.os.PowerManager.WakeLock mWakeLock;
}
