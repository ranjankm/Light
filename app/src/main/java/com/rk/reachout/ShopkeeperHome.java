package com.rk.reachout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ranjan KM on 11 Jan 2017.
 */

public class ShopkeeperHome extends AppCompatActivity {
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private UserSessionManager sessionManager;
    private Switch shop_status;
    private DrawerLayout navigationDrawer;
    private TextView shopRatings, shopRatingUsers, shopFavourites;
    private ImageView qrcode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopkepper_homeview);
        sessionManager = new UserSessionManager(ShopkeeperHome.this);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(sessionManager.getShopName());
        setSupportActionBar(toolbar);

        shop_status = (Switch)findViewById(R.id.shop_status_switch);
        shopRatings =(TextView)findViewById(R.id.shop_view_ratings);
        shopRatingUsers =(TextView) findViewById(R.id.shop_view_ratings_users);
        shopFavourites =(TextView) findViewById(R.id.shop_favourites);
        Button feeds=(Button)findViewById(R.id.shop_feeds_button);
        Button adv=(Button)findViewById(R.id.shop_adv_button);
        qrcode=(ImageView)findViewById(R.id.qrcode);

        feeds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopkeeperHome.this,FeedsView.class));
            }
        });

        adv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopkeeperHome.this,AdvView.class));
            }
        });

        shop_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    new ShopStatusToggle(ShopkeeperHome.this).execute(sessionManager.getShopid(),"open");
                }else{
                    new ShopStatusToggle(ShopkeeperHome.this).execute(sessionManager.getShopid(),"close");
                }
                toggleSwitchColor(shop_status,isChecked);
            }
        });

        setupDrawer();
        generateQRCode();
        new ShopStatus(ShopkeeperHome.this).execute(sessionManager.getShopid());
    }

    void toggleSwitchColor(Switch _switch, Boolean status){
        if(status){
            _switch.getThumbDrawable().setColorFilter( ContextCompat.getColor(ShopkeeperHome.this, R.color.switchOnThumb),PorterDuff.Mode.MULTIPLY);
            _switch.getTrackDrawable().setColorFilter(ContextCompat.getColor(ShopkeeperHome.this, R.color.switchOnTrack), PorterDuff.Mode.MULTIPLY);
        }else{
            _switch.getThumbDrawable().setColorFilter( ContextCompat.getColor(ShopkeeperHome.this, R.color.switchOffThumb),PorterDuff.Mode.MULTIPLY);
            _switch.getTrackDrawable().setColorFilter(ContextCompat.getColor(ShopkeeperHome.this, R.color.switchOffTrack), PorterDuff.Mode.MULTIPLY);
        }
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void generateQRCode(){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(sessionManager.getShopid(), BarcodeFormat.QR_CODE,400,400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void setupDrawer() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        navigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        mDrawerToggle = new ActionBarDrawerToggle(this, navigationDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        navigationDrawer.addDrawerListener(mDrawerToggle);
        View headerLayout = navigationView.inflateHeaderView(R.layout.drawer_shop_header_view);
        TextView shopname = (TextView) headerLayout.findViewById(R.id.shopname);
        shopname.setText(sessionManager.getName());
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        navigationDrawer.closeDrawers();
        switch (menuItem.getItemId()) {
            case R.id.nav_shop_profile:
                startActivity(new Intent(ShopkeeperHome.this,ShopProfile.class));
                break;

            case R.id.nav_change_timings:
                startActivity(new Intent(ShopkeeperHome.this, ChangeTimings.class));
                break;
            case R.id.nav_change_password:
                startActivity(new Intent(ShopkeeperHome.this, ChangePassword.class));
                break;

            case R.id.nav_sign_out:
                LayoutInflater li = LayoutInflater.from(ShopkeeperHome.this);
                AlertDialog.Builder newFeedDialogBuilder = new AlertDialog.Builder(ShopkeeperHome.this);
                final View dialog = li.inflate(R.layout.dialog_logout_view, null);
                newFeedDialogBuilder.setView(dialog);
                final TextView logout_msg = (TextView) dialog.findViewById(R.id.logout_msg);
                logout_msg.setText("You will be logged out from the shopkeeper view. Are you sure?");
                final Button yes_button = (Button)dialog.findViewById(R.id.logout_yes_button);
                final Button no_button = (Button)dialog.findViewById(R.id.logout_no_button);
                final AlertDialog alertDialog = newFeedDialogBuilder.create();
                alertDialog.show();
                yes_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sessionManager.logoutShop();
                        startActivity(new Intent(ShopkeeperHome.this, UserHome.class));
                        ShopkeeperHome.this.finish();
                        alertDialog.cancel();
                    }
                });
                no_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
            default:
        }
    }

    protected void loadData(String ratings, String users, String favourites) {

        if(ratings.equals("null"))
            ratings="0.0";
        shopRatings.setText(String.format("%s/5", ratings));
        shopRatingUsers.setText(String.format("(%s)", users));
        shopFavourites.setText(favourites);
    }

    public class ShopStatusToggle extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/Shop_Status_Toggle.php";
        ProgressDialog dialog;
        Context context;

        ShopStatusToggle(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Updating status...");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("shopid", params[0]);
                hashMap.put("status",params[1]);
                System.out.println("shopid : "+params[0]);
                System.out.println("status : "+params[1]);
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
                    if(jsonObject.getString("status").equals("fail")){
                        shop_status.setChecked(false);
                        Toast.makeText(context, "Please try again...", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public class ShopStatus extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/Shop_Status.php";
        ProgressDialog dialog;
        Context context;

        ShopStatus(Context context) {
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
                    if(jsonObject.getString("status").equals("open")){
                        shop_status.setChecked(true);
                        toggleSwitchColor(shop_status,true);
                    }else{
                        shop_status.setChecked(false);
                        toggleSwitchColor(shop_status,false);
                    }
                    loadData(jsonObject.getString("ratings"),jsonObject.getString("users"),jsonObject.getString("favourites"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
