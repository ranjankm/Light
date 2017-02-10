package com.rk.reachout;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.LinkAddress;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Ranjan KM on 25 Jan 2017.
 */

public class UserProfile extends AppCompatActivity {

    private HashMap<String, String> userProfile;
    private TextView username, city, phone, email, gender, ageGroup, shopName;
    private LinearLayout shopLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("User Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfile.this.finish();
            }
        });

        username = (TextView) findViewById(R.id.user_name);
        city = (TextView) findViewById(R.id.user_city);
        phone = (TextView) findViewById(R.id.user_phone);
        email = (TextView) findViewById(R.id.user_email);
        gender = (TextView) findViewById(R.id.user_gender);
        ageGroup = (TextView) findViewById(R.id.user_age_group);
        shopName = (TextView) findViewById(R.id.user_shopname);
        shopLayout = (LinearLayout)findViewById(R.id.shop_layout);

        new UserProfileFetch(UserProfile.this).execute(new UserSessionManager(UserProfile.this).getUserid());
    }

    void loadProfile() {
        username.setText(userProfile.get("username"));
        city.setText(userProfile.get("city"));
        phone.setText(userProfile.get("phone"));
        email.setText(userProfile.get("email"));
        ageGroup.setText(userProfile.get("age"));
        if (userProfile.get("shopname") != null) {
            shopName.setText(userProfile.get("shopname"));
            shopLayout.setVisibility(View.VISIBLE);
        }else
            shopLayout.setVisibility(View.GONE);
        gender.setText(userProfile.get("gender"));

    }

    public class UserProfileFetch extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/User_Profile.php";
        ProgressDialog dialog;
        Context context;

        UserProfileFetch(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading...");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("userid", params[0]);
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
                    userProfile = new HashMap<>();
                    JSONObject JO = jsonObject.getJSONObject("profile");
                    userProfile.put("username", JO.getString("name"));
                    userProfile.put("email", JO.getString("email"));
                    userProfile.put("phone", JO.getString("phone"));
                    userProfile.put("gender", JO.getString("gender"));
                    userProfile.put("city", JO.getString("city"));
                    userProfile.put("shopname", JO.getString("shopname"));
                    userProfile.put("age", JO.getString("age"));
                    loadProfile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
