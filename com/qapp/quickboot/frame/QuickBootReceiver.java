
package com.qapp.quickboot.frame;

import android.content.*;
import android.util.Log;
import com.qapp.quickboot.poweron.AlarmBootTask;

public class QuickBootReceiver extends BroadcastReceiver
{

    public QuickBootReceiver()
    {
    }

    private static void logd(Object obj)
    {
        String s = Thread.currentThread().getStackTrace()[3].getMethodName();
        String s1 = (new StringBuilder()).append("[").append(s).append("] ").append(obj).toString();
        Log.d("QuickBoot", (new StringBuilder()).append(s1).append("").toString());
    }

    public void onReceive(Context context, Intent intent)
    {
        String s = intent.getAction();
        logd(s);
        if("com.android.deskclock.ALARM_ALERT".equals(s))
            (new AlarmBootTask(context)).execute(new Object[0]);
    }
}
