package com.rk.reachout;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ranjan KM on 25 Jan 2017.
 */

public class ShopProfile extends AppCompatActivity {

    private HashMap<String, String> shopProfile;
    private TextView tin, shopname, address, city, area, phone, email, category, timings, holidays, products, brands;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Shop Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopProfile.this.finish();
            }
        });

        tin =(TextView) findViewById(R.id.shop_tin);
        shopname = (TextView)findViewById(R.id.shop_name);
        address = (TextView)findViewById(R.id.shop_address);
        city = (TextView)findViewById(R.id.shop_city);
        area = (TextView)findViewById(R.id.shop_area);
        phone = (TextView)findViewById(R.id.shop_phone);
        email = (TextView)findViewById(R.id.shop_email);
        category = (TextView) findViewById(R.id.shop_category);
        timings = (TextView)findViewById(R.id.shop_timings);
        holidays = (TextView)findViewById(R.id.shop_holidays);
        products = (TextView)findViewById(R.id.shop_products);
        brands = (TextView)findViewById(R.id.shop_brands);

        new ShopProfileFetch(ShopProfile.this).execute(new UserSessionManager(ShopProfile.this).getShopid());
    }

    void loadProfile() {
        tin.setText(shopProfile.get("tin"));
        shopname.setText(shopProfile.get("shopname"));
        address.setText(shopProfile.get("address"));
        city.setText(shopProfile.get("city"));
        area.setText(shopProfile.get("area"));
        phone.setText(shopProfile.get("phone"));
        email.setText(shopProfile.get("email"));
        category.setText(shopProfile.get("category"));
        timings.setText(shopProfile.get("timings"));
        holidays.setText(shopProfile.get("holidays"));
        products.setText(shopProfile.get("products"));
        brands.setText(shopProfile.get("brands"));
    }

    public class ShopProfileFetch extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/Shop_Profile.php";
        ProgressDialog dialog;
        Context context;

        ShopProfileFetch(Context context) {
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
                    shopProfile = new HashMap<>();
                    JSONObject JO = jsonObject.getJSONObject("profile");
                    shopProfile.put("tin", JO.getString("tin"));
                    shopProfile.put("shopname", JO.getString("shopname"));
                    shopProfile.put("address", JO.getString("address").replace(", ","\n"));
                    shopProfile.put("city",JO.getString("city"));
                    shopProfile.put("area", JO.getString("area"));
                    shopProfile.put("email", JO.getString("email"));
                    shopProfile.put("phone", JO.getString("phone"));
                    shopProfile.put("category",JO.getString("category"));
                    shopProfile.put("timings", JO.getString("otime") + " - " + JO.getString("ctime"));
                    shopProfile.put("holidays", JO.getString("holidays"));
                    shopProfile.put("products", JO.getString("products"));
                    shopProfile.put("brands", JO.getString("brands"));
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
