
package com.qapp.quickboot.poweroff;

import android.content.*;


class this._cls0 extends BroadcastReceiver
{

    public void onReceive(Context context, Intent intent)
    {
        Object obj = PowerOffTask.access$000();
        synchronized(obj)
        {
            PowerOffTask.access$100("done");
            PowerOffTask.access$000().notifyAll();
        }
        return;
        exception;
        obj1;
        JVM INSTR monitorexit ;
        throw exception;
    }

    final PowerOffTask this$0;

    ()
    {
        this$0 = PowerOffTask.this;
        super();
    }
}
