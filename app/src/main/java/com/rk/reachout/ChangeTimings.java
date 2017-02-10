package com.rk.reachout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Ranjan KM on 25 Jan 2017.
 */

public class ChangeTimings extends AppCompatActivity {

    private EditText otime, ctime, holidays;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_timings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Change Timings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeTimings.this.finish();
            }
        });

        otime = (EditText) findViewById(R.id.opening_time);
        ctime = (EditText) findViewById(R.id.closing_time);
        holidays = (EditText) findViewById(R.id.holidays);

        new PreviousTimings(ChangeTimings.this).execute(new UserSessionManager(ChangeTimings.this).getShopid());
        ImageButton otimeButton = (ImageButton)findViewById(R.id.opening_time_picker);
        otimeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimePicker(otime);
            }
        });

        ImageButton ctimeButton = (ImageButton)findViewById(R.id.closing_time_picker);
        ctimeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimePicker(ctime);
            }
        });

        Button submit = (Button) findViewById(R.id.change_timings_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateTimings();
            }
        });
    }

    void showTimePicker(final TextView time){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(ChangeTimings.this,R.style.TimePickerStyle, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                int hour = selectedHour % 12;
                time.setText(String.format(Locale.ENGLISH,"%02d:%02d %s", hour == 0 ? 12 : hour, selectedMinute, selectedHour < 12 ? "AM" : "PM"));
            }
        }, hour, minute, false);
        mTimePicker.show();
    }

    void validateTimings() {
        String otimimgs = otime.getText().toString();
        String ntimings = ctime.getText().toString();
        String holiday = holidays.getText().toString();

        otime.setError(null);
        ctime.setError(null);
        holidays.setError(null);

        if (TextUtils.isEmpty(otimimgs)) {
            otime.setError("This field is required");
        } else if (TextUtils.isEmpty(ntimings)) {
            ctime.setError("This field is required");
        } else
            new TimingsChange(ChangeTimings.this).execute(new UserSessionManager(ChangeTimings.this).getShopid(), otimimgs.trim(), ntimings.trim(), holiday.trim());
    }

    class PreviousTimings extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/Previous_Timings.php";
        ProgressDialog dialog;
        Context context;

        PreviousTimings(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Updating timings...");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("shopid", params[0]);
                JSONParser jsonParser = new JSONParser();
                jsonObject = jsonParser.makeHttpRequest(URL, "POST", hashMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            dialog.cancel();
            if (jsonObject != null) {
                try {
                    otime.setText(jsonObject.getString("otime"));
                    ctime.setText(jsonObject.getString("ctime"));
                    holidays.setText(jsonObject.getString("holidays"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public class TimingsChange extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/Change_Timings.php";
        ProgressDialog dialog;
        Context context;

        TimingsChange(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Updating timings...");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("shopid", params[0]);
                hashMap.put("otime", params[1]);
                hashMap.put("ctime", params[2]);
                hashMap.put("holidays", params[3]);
                JSONParser jsonParser = new JSONParser();
                jsonObject = jsonParser.makeHttpRequest(URL, "POST", hashMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            dialog.cancel();
            if (jsonObject != null) {
                try {
                    if (jsonObject.getString("status").equals("success")) {
                        Toast.makeText(context, "Timings changed successfully.", Toast.LENGTH_SHORT).show();
                        ChangeTimings.this.finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
