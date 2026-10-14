package com.sysupd;

import android.content.*;

public class BootReceiver extends BroadcastReceiver {
    public void onReceive(Context ctx, Intent i) {
        ForegroundService.start(ctx);
    }
}
