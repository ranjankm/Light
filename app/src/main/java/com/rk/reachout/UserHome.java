package com.rk.reachout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Ranjan KM on 05 Jan 2017.
 */

public class UserHome extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView homeGridView;
    private ArrayList<String> homeGridIcons;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private UserSessionManager sessionManager;
    private ArrayList<String> cities;
    private Spinner city;
    private int closeflag = 0;
    private DrawerLayout navigationDrawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        sessionManager = new UserSessionManager(UserHome.this);

        homeGridView = (RecyclerView) findViewById(R.id.home_grid_view);
        homeGridIcons = new ArrayList<>();
        homeGridView.requestFocus();

        ImageView dealsButton = (ImageView) findViewById(R.id.deals_button);
        dealsButton.setOnClickListener(this);
        ImageView salesButton = (ImageView) findViewById(R.id.sales_button);
        salesButton.setOnClickListener(this);
        ImageView newButton = (ImageView) findViewById(R.id.new_button);
        newButton.setOnClickListener(this);

        EditText searchbox = (EditText) findViewById(R.id.home_search_box);
        searchbox.clearFocus();
        searchbox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    startActivity(new Intent(UserHome.this, HomeSearchView.class));
            }
        });

        setupDrawer();
        loadCategories();
        loadIcons();
        loadCities();
    }

    @Override
    public void onClick(View v) {
        final Intent intent = new Intent(UserHome.this, HomeAdvView.class);
        switch (v.getId()) {
            case R.id.deals_button:
                intent.putExtra("category", "Best Deals");
                break;
            case R.id.sales_button:
                intent.putExtra("category", "Sales and Discounts");
                break;
            case R.id.new_button:
                intent.putExtra("category", "New in the Market");
                break;
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        homeGridView.requestFocus();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (closeflag == 0) {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            closeflag = 1;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    closeflag = 0;
                }
            }, 3000);
        } else if (closeflag == 1) {
            finish();
        }
    }

    private void loadCities() {
        cities = new ArrayList<>();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    HashMap<String, String> hashMap = new HashMap<>();
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = jsonParser.makeHttpRequest("http://theoms.16mb.com/Cities.php", "POST", hashMap);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    int count = 0;
                    while (count < jsonArray.length()) {
                        cities.add(jsonArray.getString(count));
                        count++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        showCities();
    }

    private void showCities() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(UserHome.this, android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter);
        city.setSelection(adapter.getPosition(sessionManager.getCity()));
    }

    private void loadCategories() {
        homeGridIcons.add("Garments");
        homeGridIcons.add("General Stores");
        homeGridIcons.add("Electronics");
        homeGridIcons.add("Medicines");
        homeGridIcons.add("Hardware");
        homeGridIcons.add("Furniture");
        homeGridIcons.add("Kitchen/Tools");
        homeGridIcons.add("Automobiles");
        homeGridIcons.add("Personal Care");
        homeGridIcons.add("Grocery");
        homeGridIcons.add("Stationary");
        homeGridIcons.add("Footwear");
        homeGridIcons.add("Restaurants");
    }

    private void loadIcons() {
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        homeGridView.setLayoutManager(mGridLayoutManager);
        UserHomeViewAdapter mAdapter = new UserHomeViewAdapter(UserHome.this, homeGridIcons);
        homeGridView.setAdapter(mAdapter);
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
        View headerLayout = navigationView.inflateHeaderView(R.layout.drawer_header_view);
        TextView username = (TextView) headerLayout.findViewById(R.id.username);
        city = (Spinner) headerLayout.findViewById(R.id.drawer_cities_spinner);
        username.setText(sessionManager.getName());
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
            case R.id.nav_my_profile:
                startActivity(new Intent(UserHome.this, UserProfile.class));
                break;
            case R.id.nav_settings:
                Toast.makeText(UserHome.this, "2", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_sign_out:
                LayoutInflater li = LayoutInflater.from(UserHome.this);
                AlertDialog.Builder newFeedDialogBuilder = new AlertDialog.Builder(UserHome.this);
                @SuppressLint("InflateParams") final View dialog = li.inflate(R.layout.dialog_logout_view, null);
                newFeedDialogBuilder.setView(dialog);
                final TextView logout_msg = (TextView) dialog.findViewById(R.id.logout_msg);
                String msg;
                if (sessionManager.isShopLoggedIn())
                    msg = "You will be logged out from both the user view and the shopkeeper view. Are you sure?";
                else
                    msg = "Are you sure?";
                logout_msg.setText(msg);
                final Button yes_button = (Button) dialog.findViewById(R.id.logout_yes_button);
                final Button no_button = (Button) dialog.findViewById(R.id.logout_no_button);
                final AlertDialog alertDialog = newFeedDialogBuilder.create();
                alertDialog.show();
                yes_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sessionManager.logoutUser();
                        startActivity(new Intent(UserHome.this, MobileVerify.class));
                        UserHome.this.finish();
                        alertDialog.cancel();
                    }
                });
                no_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
                break;

            case R.id.nav_scan_qrcode:
                startActivity(new Intent(UserHome.this, QRScanner.class));
                break;
            default:
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.shop_login:
                if (sessionManager.isShopLoggedIn())
                    startActivity(new Intent(this, ShopkeeperHome.class));
                else {
                    Intent intent = new Intent(this, ShopLogin.class);
                    if (!sessionManager.getShopid().equals("null")) {
                        intent.putExtra("layout", "login");
                        startActivity(intent);
                    } else {
                        intent.putExtra("layout", "register");
                        startActivity(intent);
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class UserHomeViewAdapter extends RecyclerView.Adapter<UserHomeViewAdapter.ViewHolder> {

        private final Activity context;
        private ArrayList<String> mDataset;

        UserHomeViewAdapter(Activity context, ArrayList<String> myDataset) {
            this.context = context;
            mDataset = myDataset;
        }

        @Override
        public UserHomeViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            DisplayMetrics metrics = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
            int viewwidth;
            int margin = (int) (5 * metrics.density);

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_item_view, parent, false);
            viewwidth = ((width - margin) / 4);
            v.setLayoutParams(new RecyclerView.LayoutParams(viewwidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final String name = mDataset.get(position);
            switch (name) {
                case "Garments":
                    holder.category_item_image.setBackgroundResource(R.drawable.garments);
                    break;
                case "General Stores":
                    holder.category_item_image.setBackgroundResource(R.drawable.generalstore);
                    break;
                case "Electronics":
                    holder.category_item_image.setBackgroundResource(R.drawable.electronics);
                    break;
                case "Medicines":
                    holder.category_item_image.setBackgroundResource(R.drawable.health);
                    break;
                case "Hardware":
                    holder.category_item_image.setBackgroundResource(R.drawable.hardware);
                    break;
                case "Furniture":
                    holder.category_item_image.setBackgroundResource(R.drawable.household);
                    break;
                case "Kitchen/Tools":
                    holder.category_item_image.setBackgroundResource(R.drawable.kitchen);
                    break;
                case "Automobiles":
                    holder.category_item_image.setBackgroundResource(R.drawable.automobile);
                    break;
                case "Personal Care":
                    holder.category_item_image.setBackgroundResource(R.drawable.personalcare);
                    break;
                case "Grocery":
                    holder.category_item_image.setBackgroundResource(R.drawable.grocery);
                    break;
                case "Stationary":
                    holder.category_item_image.setBackgroundResource(R.drawable.stationary);
                    break;
                case "Footwear":
                    holder.category_item_image.setBackgroundResource(R.drawable.footwears);
                    break;
                case "Restaurants":
                    holder.category_item_image.setBackgroundResource(R.drawable.restaurants);
                    break;
            }
            holder.category_item_text.setText(name);
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout category_item;
            TextView category_item_text;
            ImageView category_item_image;

            ViewHolder(View v) {
                super(v);
                category_item = (LinearLayout) v.findViewById(R.id.category_item);
                category_item_image = (ImageView) v.findViewById(R.id.category_item_image);
                category_item_text = (TextView) v.findViewById(R.id.category_item_text);
                System.out.println("Position " + getAdapterPosition());

                final Intent intent = new Intent(UserHome.this, CategoryView.class);

                category_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (mDataset.get(getAdapterPosition())) {
                            case "Garments":
                                intent.putExtra("fragment_name", "Garments");
                                startActivity(intent);
                                break;
                            case "General Stores":
                                intent.putExtra("fragment_name", "General Stores");
                                startActivity(intent);
                                break;
                            case "Electronics":
                                intent.putExtra("fragment_name", "Electronics");
                                startActivity(intent);
                                break;
                            case "Medicines":
                                intent.putExtra("fragment_name", "Medicines");
                                startActivity(intent);
                                break;
                            case "Hardware":
                                intent.putExtra("fragment_name", "Hardware");
                                startActivity(intent);
                                break;
                            case "Furniture":
                                intent.putExtra("fragment_name", "Furniture");
                                startActivity(intent);
                                break;
                            case "Kitchen/Tools":
                                intent.putExtra("fragment_name", "Kitchen/Tools");
                                startActivity(intent);
                                break;
                            case "Automobiles":
                                intent.putExtra("fragment_name", "Automobiles");
                                startActivity(intent);
                                break;
                            case "Personal Care":
                                intent.putExtra("fragment_name", "Personal Care");
                                startActivity(intent);
                                break;
                            case "Grocery":
                                intent.putExtra("fragment_name", "Grocery");
                                startActivity(intent);
                                break;
                            case "Stationary":
                                intent.putExtra("fragment_name", "Stationary");
                                startActivity(intent);
                                break;
                            case "Footwear":
                                intent.putExtra("fragment_name", "Footwear");
                                startActivity(intent);
                                break;
                            case "Restaurants":
                                intent.putExtra("fragment_name", "Restaurants");
                                startActivity(intent);
                                break;
                        }
                    }
                });
            }
        }
    }
}
