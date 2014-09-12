
package com.qapp.quickboot.frame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.qapp.quickboot.poweroff.PowerOffTask;
import com.qapp.quickboot.poweron.PowerOnTask;
import com.qapp.quickboot.poweron.RestoreTask;

public class QuickBoot extends Activity
{

    public QuickBoot()
    {
        mContext = null;
    }

    void init()
    {
        mContext = getApplicationContext();
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        init();
        Intent intent = getIntent();
        int i = 0;
        if(intent != null)
        {
            boolean flag = intent.hasExtra("mode");
            i = 0;
            if(flag)
                i = intent.getIntExtra("mode", 0);
        }
        switch(i)
        {
        case 2: // '\002'
            (new RestoreTask(mContext)).execute(new Object[0]);
            return;

        case 0: // '\0'
            (new PowerOffTask(mContext)).execute(new Object[0]);
            return;

        case 1: // '\001'
            (new PowerOnTask(mContext)).execute(new Object[0]);
            return;
        }
        (new PowerOffTask(mContext)).execute(new Object[0]);
    }

    private Context mContext;
}
