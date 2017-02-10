package com.rk.reachout;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Ranjan KM on 25 Jan 2017.
 */

public class ChangePassword extends AppCompatActivity {

    private EditText oldPass, newPass, newPassRepeat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Change Password");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePassword.this.finish();
            }
        });

        oldPass = (EditText) findViewById(R.id.old_password);
        newPass = (EditText) findViewById(R.id.new_password);
        newPassRepeat = (EditText) findViewById(R.id.repeat_new_password);

        Button submit = (Button) findViewById(R.id.change_pass_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePassword();
            }
        });
    }

    void validatePassword() {
        String opass = oldPass.getText().toString();
        String npass = newPass.getText().toString();
        String nrpass = newPassRepeat.getText().toString();

        oldPass.setError(null);
        newPass.setError(null);
        newPassRepeat.setError(null);

        if (TextUtils.isEmpty(opass)) {
            oldPass.setError("This field is required");
            oldPass.requestFocus();
        } else if (TextUtils.isEmpty(npass)) {
            newPass.setError("This field is required");
            newPass.requestFocus();
        } else if (TextUtils.isEmpty(nrpass)) {
            newPassRepeat.setError("This field is required");
            newPassRepeat.requestFocus();
        } else if (!(nrpass.equals(npass))) {
            newPassRepeat.setError("The new password doesn't match");
            newPassRepeat.requestFocus();
        } else
            new PasswordChange(ChangePassword.this).execute(new UserSessionManager(ChangePassword.this).getShopid(), opass, npass);
    }

    public class PasswordChange extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/Change_Password.php";
        ProgressDialog dialog;
        Context context;

        PasswordChange(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Changing password...");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("shopid", params[0]);
                hashMap.put("opass", params[1]);
                hashMap.put("npass", params[2]);
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
                        Toast.makeText(context, "Password changed successfully.", Toast.LENGTH_SHORT).show();
                        ChangePassword.this.finish();
                    } else
                        Toast.makeText(context, "Invalid password", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
