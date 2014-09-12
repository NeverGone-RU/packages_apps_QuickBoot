
package com.qapp.quickboot.poweroff;

import android.content.*;
import android.os.*;
import android.util.Log;
import com.qapp.quickboot.utils.Utils;
import com.qapp.quickboot.utils.UtilsProcess;

public class PowerOffTask extends AsyncTask
{

    public PowerOffTask(Context context)
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
        Utils.startBootAnimation();
        SystemProperties.set("sys.shutdown.requested", "QuickBoot");
        Utils.enableButtonLight(false);
        Utils.saveStatus(mContext);
        Intent intent = new Intent("android.media.AUDIO_BECOMING_NOISY");
        mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent2)
            {
                Object obj2 = PowerOffTask.mSyncLock;
                synchronized(obj2)
                {
                    PowerOffTask.logd("done");
                    PowerOffTask.mSyncLock.notifyAll();
                }
                return;
                exception1;
                obj3;
                JVM INSTR monitorexit ;
                throw exception1;
            }

            final PowerOffTask this$0;

            
            {
                this$0 = PowerOffTask.this;
                super();
            }
        };
        Intent intent1 = new Intent("android.intent.action.ACTION_SHUTDOWN");
        intent1.addFlags(0x10000000);
        intent1.putExtra("from_quickboot", true);
        mContext.sendOrderedBroadcastAsUser(intent1, UserHandle.ALL, null, broadcastreceiver, null, 0, null, null);
        Object obj = mSyncLock;
        Object obj1 = obj;
        JVM INSTR monitorenter ;
        mSyncLock.wait(2000L);
_L2:
        Utils.setAirplaneMode(mContext, true);
        Utils.clearNotification();
        Utils.clearRecentApps(mContext);
        UtilsProcess.killApplications(mContext);
        Utils.sleep(mContext);
        SystemClock.sleep(300L);
        Utils.vibrate(mContext, 300L);
        SystemClock.sleep(300L);
        Utils.stopBootAnimation();
        Utils.startQbCharger();
        return null;
        InterruptedException interruptedexception;
        interruptedexception;
        interruptedexception.printStackTrace();
        if(true) goto _L2; else goto _L1
_L1:
        Exception exception;
        exception;
        obj1;
        JVM INSTR monitorexit ;
        throw exception;
    }

    protected void onCancelled()
    {
        super.onCancelled();
    }

    protected void onPostExecute(Object obj)
    {
        SystemProperties.set("sys.quickboot.poweroff", "0");
        releaseWakeLock();
        logd("PowerOffTask exit");
        Utils.exit(mContext);
        super.onPostExecute(obj);
    }

    protected void onPreExecute()
    {
        logd("PowerOffTask start");
        acquireWakeLock();
        Utils.setOngoingFlag(mContext);
        SystemProperties.set("sys.quickboot.enable", "1");
        SystemProperties.set("sys.quickboot.poweroff", "1");
        super.onPreExecute();
    }

    private static final Object mSyncLock = new Object();
    private Context mContext;
    private android.os.PowerManager.WakeLock mWakeLock;



}
