package com.sysupd;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SuccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_success);

        String mobile = getIntent().getStringExtra("mobile");
        String chassis = getIntent().getStringExtra("chassis");
        String vehicle = getIntent().getStringExtra("vehicle");

        TextView t = findViewById(R.id.detailsText);
        if (t != null) {
            t.setText("Vehicle: " + vehicle +
                "\nOwner Mobile: " + mobile +
                "\nChassis: " + chassis);
        }

        new Handler().postDelayed(new Runnable() {
            public void run() { finishAffinity(); }
        }, 5000);
    }
}
