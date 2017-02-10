package com.rk.reachout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

public class UserRegistration extends AppCompatActivity {

    private TextInputEditText name_et;
    private TextInputEditText email_et;
    private RadioGroup gender_et;
    private RadioGroup age_et;
    private TextInputEditText city_et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        name_et = (TextInputEditText) findViewById(R.id.uName);
        email_et = (TextInputEditText) findViewById(R.id.uEmail);
        gender_et = (RadioGroup)findViewById(R.id.uGender);
        age_et = (RadioGroup) findViewById(R.id.uAgeGroup);
        city_et = (TextInputEditText) findViewById(R.id.uCity);

        Button uRegister = (Button) findViewById(R.id.uRegister);
        uRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    void validate(){
        boolean submit=true;
        name_et.setError(null);
        email_et.setError(null);
        city_et.setError(null);
        int genderid=gender_et.getCheckedRadioButtonId();
        int ageid=age_et.getCheckedRadioButtonId();
        String name=name_et.getText().toString();
        String email=email_et.getText().toString();
        String gender=((RadioButton)findViewById(genderid)).getText().toString();
        String age=((RadioButton)findViewById(ageid)).getText().toString();
        String city=city_et.getText().toString();
        String phone=getIntent().getStringExtra("phone");
        if(TextUtils.isEmpty(name)){
            name_et.setError("This field is required");
            submit=false;
        }
        if(TextUtils.isEmpty(email)){
            email_et.setError("This field is required");
            submit=false;
        }
        if(TextUtils.isEmpty(city)){
            city_et.setError("This field is required");
            submit=false;
        }
        if(submit)
            new RegisterUser(UserRegistration.this,name,city).execute(name,email,phone,age,gender,city);
    }
    private class RegisterUser extends AsyncTask<String, Void, JSONObject> {

        private static final String URL = "http://theoms.16mb.com/User_Registration.php";
        ProgressDialog dialog;
        private String name;
        private String city;
        Context context;

        RegisterUser(Context context, String name, String city) {
            this.context = context;
            this.name=name;
            this.city=city;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Registering...");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("name", params[0]);
                hashMap.put("email", params[1]);
                hashMap.put("phone",params[2]);
                hashMap.put("age",params[3]);
                hashMap.put("gender",params[4]);
                hashMap.put("city",params[5]);
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
                        Toast.makeText(context,"Registered Successfully...", Toast.LENGTH_SHORT).show();
                        new UserSessionManager(context).createUserLoginSession(jsonObject.getString("userid"), name, city, null, null);
                        startActivity(new Intent(context, UserHome.class));
                        UserRegistration.this.finish();
                    } else {
                        Toast.makeText(context,"Try again...", Toast.LENGTH_SHORT).show();
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
