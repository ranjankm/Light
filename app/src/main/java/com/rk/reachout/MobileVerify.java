package com.rk.reachout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by Ranjan KM on 04 Jan 2017.
 */

public class MobileVerify extends AppCompatActivity implements View.OnClickListener {
    private EditText mobile_et;
    private EditText otp_et;
    private String mobile = null;
    private String OTPCode;
    private Button verify;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verify);

        mobile_et = (EditText) findViewById(R.id.mobile_et);
        otp_et = (EditText) findViewById(R.id.otp_et);

        verify = (Button) findViewById(R.id.verify_btn);
        verify.setOnClickListener(this);
        Button cont = (Button) findViewById(R.id.continue_btn);
        cont.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mobile_et.setError(null);
        otp_et.setError(null);
        switch (view.getId()) {
            case R.id.verify_btn:
                mobile = mobile_et.getText().toString();
                if (TextUtils.isEmpty(mobile)) {
                    mobile_et.setError("This field is required");
                    mobile_et.requestFocus();
                } else if (mobile.length() != 10) {
                    mobile_et.setError("Invalid mobile number");
                    mobile_et.requestFocus();
                } else {
                    Random rnd = new Random();
                    int otp = 100000 + rnd.nextInt(900000);
                    OTPCode=Integer.valueOf(otp).toString();
                    String message="Your%20one%20time%20password(OTP)%20for%20verification%20is%20"+OTPCode+".";
                    String URL = "https://www.smsgatewayhub.com/api/mt/SendSMS?APIKey=qazwsxedcrfvtgbyhnujmik&senderid=TESTIN&channel=2&DCS=0&flashsms=0&number=91"+mobile+"&text="+message+"&route=1";
                    otp_et.requestFocus();
                    new VerifyMobile(MobileVerify.this).execute(URL.trim());
                }
                break;
            case R.id.continue_btn:
                if (mobile != null) {
                    if (OTPCode.equals(otp_et.getText().toString())) {
                        new VerifyUser(MobileVerify.this).execute(mobile.trim());
                    } else {
                        otp_et.setError("Invalid OTP");
                        otp_et.requestFocus();
                    }
                } else {
                    mobile_et.setError("Verify your mobile first");
                    mobile_et.requestFocus();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private class VerifyMobile extends AsyncTask<String, Void, JSONObject> {

        ProgressDialog dialog;
        Context context;

        VerifyMobile(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Verifying...");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                HashMap<String, String> hashMap = new HashMap<>();
                JSONParser jsonParser = new JSONParser();
                jsonObject = jsonParser.makeHttpRequest(params[0], "POST", hashMap);
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
                    if (jsonObject.getString("ErrorMessage").equals("Success")) {
                        Toast.makeText(MobileVerify.this,"OTP sent successfully",Toast.LENGTH_SHORT).show();
                        verify.setText("RESEND");
                    } else {
                        Toast.makeText(MobileVerify.this,"Try again...",Toast.LENGTH_SHORT).show();
                        mobile_et.requestFocus();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(context,"Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class VerifyUser extends AsyncTask<String, Void, JSONObject> {

        private static final String URL = "http://theoms.16mb.com/New_User.php";
        ProgressDialog dialog;
        Context context;

        VerifyUser(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Verifying...");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("phone", params[0]);
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
                    if (!jsonObject.getString("userid").equals("null")) {
                        new UserSessionManager(context).createUserLoginSession(jsonObject.getString("userid"), jsonObject.getString("name"), jsonObject.getString("city"),
                                jsonObject.getString("shopid"), jsonObject.getString("shopname"));
                        startActivity(new Intent(context, UserHome.class));
                        MobileVerify.this.finish();
                    } else {
                        Intent intent=new Intent(context, UserRegistration.class);
                        intent.putExtra("phone",mobile);
                        startActivity(intent);
                        MobileVerify.this.finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(context,"Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
