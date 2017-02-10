package com.rk.reachout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Ranjan KM on 21 Jan 2017.
 */

public class ShopLogin extends AppCompatActivity implements View.OnClickListener {
    private EditText password_et;
    private UserSessionManager sessionManager;
    private TextView shopname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_login);
        RelativeLayout loginlayout = (RelativeLayout) findViewById(R.id.shop_login_layout);
        LinearLayout registerlayout = (LinearLayout) findViewById(R.id.shop_register_layout);
        sessionManager = new UserSessionManager(ShopLogin.this);
        shopname = (TextView) findViewById(R.id.shop_name);
        if (getIntent().getStringExtra("layout").equals("login")) {
            registerlayout.setVisibility(View.GONE);
            loginlayout.setVisibility(View.VISIBLE);
            loadShopName();
            password_et = (EditText) findViewById(R.id.shop_password);
            Button login = (Button) findViewById(R.id.shop_login_button);
            login.setOnClickListener(this);

            TextView changeShop = (TextView) findViewById(R.id.change_shop);
            changeShop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater li = LayoutInflater.from(ShopLogin.this);
                    AlertDialog.Builder newFeedDialogBuilder = new AlertDialog.Builder(ShopLogin.this);
                    final View dialog = li.inflate(R.layout.dialog_changeshop_view, null);
                    newFeedDialogBuilder.setView(dialog);
                    final EditText tin = (EditText) dialog.findViewById(R.id.change_shop_tin);
                    final Button submit_button = (Button) dialog.findViewById(R.id.changeshop_yes_button);
                    final Button cancel_button = (Button) dialog.findViewById(R.id.changeshop_no_button);
                    final AlertDialog alertDialog = newFeedDialogBuilder.create();
                    alertDialog.show();
                    submit_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new ChangeShop(ShopLogin.this, tin.getText().toString()).execute(sessionManager.getUserid());
                            alertDialog.cancel();
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.cancel();
                        }
                    });
                }
            });
        } else {
            loginlayout.setVisibility(View.GONE);
            registerlayout.setVisibility(View.VISIBLE);
            Button register = (Button) findViewById(R.id.shop_register_button);
            register.setOnClickListener(this);
        }
    }

    void loadShopName() {
        shopname.setText(sessionManager.getShopName().toUpperCase());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shop_login_button:
                password_et.setError(null);
                String password = password_et.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    password_et.setError("This field is required");
                    password_et.requestFocus();
                } else {
                    new ShopAuthentication(ShopLogin.this).execute(sessionManager.getShopid(), password);
                }
                break;
            case R.id.shop_register_button:
                startActivity(new Intent(ShopLogin.this, ShopRegistration.class));
                ShopLogin.this.finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class ChangeShop extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/Change_Shop.php";
        ProgressDialog dialog;
        Context context;
        String tin;

        ChangeShop(Context context, String tin) {
            this.context = context;
            this.tin = tin;
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
                hashMap.put("tin", tin);
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
                    if (jsonObject.getString("status").equals("success")) {
                        Toast.makeText(context, "Shop changed successfully.", Toast.LENGTH_SHORT).show();
                        sessionManager.setShopName(jsonObject.getString("shopname"));
                        sessionManager.setShopid(jsonObject.getString("shopid"));
                    } else
                        Toast.makeText(context, "TIN doesn't exist", Toast.LENGTH_SHORT).show();
                    loadShopName();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class ShopAuthentication extends AsyncTask<String, Void, JSONObject> {

        private static final String URL = "http://theoms.16mb.com/Shop_Login.php";
        ProgressDialog dialog;
        Context context;

        ShopAuthentication(Context context) {
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
                hashMap.put("shopid", params[0]);
                hashMap.put("password", params[1]);
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
                    if (jsonObject.getString("status").equals("verified")) {
                        UserSessionManager sessionManager = new UserSessionManager(context);
                        sessionManager.setShopLogin(true);
                        startActivity(new Intent(context, ShopkeeperHome.class));
                        ShopLogin.this.finish();
                    } else if (jsonObject.getString("status").equals("pending"))
                        Toast.makeText(context, "Please wait 24 hours for verification", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
