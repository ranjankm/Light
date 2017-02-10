package com.rk.reachout;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ranjan KM on 11 Jan 2017.
 */

public class ShopkeeperView extends AppCompatActivity {
    private ArrayList<Feed> feeds;
    private RecyclerView feedsView;
    private HashMap<String, String> shopProfile;
    private TextView shopname, address, phone, email, timings, holidays, products, brands, shopRatings, shopRatingUsers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopkepper_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(getIntent().getStringExtra("category"));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopkeeperView.this.finish();
            }
        });

        feedsView = (RecyclerView) findViewById(R.id.user_feeds_view);

        shopname = (TextView)findViewById(R.id.shop_view_name);
        address = (TextView)findViewById(R.id.shop_view_address);
        phone = (TextView)findViewById(R.id.shop_view_phone);
        email = (TextView)findViewById(R.id.shop_view_email);
        timings = (TextView)findViewById(R.id.shop_view_timings);
        holidays = (TextView)findViewById(R.id.shop_view_holidays);
        products = (TextView)findViewById(R.id.shop_view_products);
        brands = (TextView)findViewById(R.id.shop_view_brands);
        shopRatings =(TextView)findViewById(R.id.shop_view_ratings);
        shopRatingUsers =(TextView) findViewById(R.id.shop_view_ratings_users);

        LinearLayout viewMore= (LinearLayout)findViewById(R.id.shop_view_more);
        final LinearLayout emailLayout = (LinearLayout)findViewById(R.id.shop_email_layout);
        final LinearLayout holidaysLayout = (LinearLayout)findViewById(R.id.shop_holidays_layout);
        final LinearLayout productsLayout  = (LinearLayout)findViewById(R.id.shop_products_layout);
        final LinearLayout brandsLayout = (LinearLayout)findViewById(R.id.shop_brands_layout);
        final TextView viewMoreText = (TextView)findViewById(R.id.shop_view_more_text);
        final ImageView viewMoreImage = (ImageView)findViewById(R.id.shop_view_more_image);
        viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("clicked");
                if(emailLayout.getVisibility()==View.GONE){
                    System.out.println("View");
                    emailLayout.setVisibility(View.VISIBLE);
                    holidaysLayout.setVisibility(View.VISIBLE);
                    productsLayout.setVisibility(View.VISIBLE);
                    brandsLayout.setVisibility(View.VISIBLE);
                    viewMoreText.setText("LESS");
                    viewMoreImage.setImageDrawable(ContextCompat.getDrawable(ShopkeeperView.this,R.drawable.less_arrow));
                }else{
                    System.out.println("hide");
                    emailLayout.setVisibility(View.GONE);
                    holidaysLayout.setVisibility(View.GONE);
                    productsLayout.setVisibility(View.GONE);
                    brandsLayout.setVisibility(View.GONE);
                    viewMoreText.setText("MORE");
                    viewMoreImage.setImageDrawable(ContextCompat.getDrawable(ShopkeeperView.this,R.drawable.more_arrow));
                }
            }
        });

        new Feeds(ShopkeeperView.this).execute(getIntent().getStringExtra("shopid"));
    }

    void loadProfile() {
        shopname.setText(shopProfile.get("shopname").toUpperCase());
        address.setText(shopProfile.get("address"));
        phone.setText(shopProfile.get("phone"));
        email.setText(shopProfile.get("email"));
        timings.setText(shopProfile.get("timings"));
        holidays.setText(shopProfile.get("holidays"));
        products.setText(shopProfile.get("products"));
        brands.setText(shopProfile.get("brands"));
        shopRatings.setText(String.format("%s/5", shopProfile.get("ratings")));
        shopRatingUsers.setText(String.format("(%s)", shopProfile.get("users")));
    }

    protected void loadFeeds() {
        RecyclerView.LayoutManager shopLayoutManager = new LinearLayoutManager(ShopkeeperView.this, LinearLayoutManager.VERTICAL, false);
        feedsView.setLayoutManager(shopLayoutManager);
        feedsView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.Adapter adapter = new FeedsViewAdapter(feeds);
        feedsView.setAdapter(adapter);
    }

    public class Feeds extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/Shop_View.php";
        ProgressDialog dialog;
        Context context;

        Feeds(Context context) {
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
                    shopProfile.put("shopname", JO.getString("shopname"));
                    shopProfile.put("address", JO.getString("address").replace(", ","\n"));
                    shopProfile.put("email", JO.getString("email"));
                    shopProfile.put("phone", JO.getString("phone"));
                    shopProfile.put("timings", JO.getString("otime") + " - " + JO.getString("ctime"));
                    shopProfile.put("holidays", JO.getString("holidays"));
                    shopProfile.put("products", JO.getString("products"));
                    shopProfile.put("brands", JO.getString("brands"));
                    JO = jsonObject.getJSONObject("ratings");
                    if(!JO.getString("ratings").equals("null"))
                        shopProfile.put("ratings", JO.getString("ratings"));
                    else
                        shopProfile.put("ratings","0.0");
                    shopProfile.put("users", JO.getString("users"));
                    loadProfile();
                    feeds = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONArray("feeds");
                    int count = 0;
                    while (count < jsonArray.length()) {
                        JO = jsonArray.getJSONObject(count);
                        feeds.add(new Feed(JO.getString("feed"), JO.getString("date")));
                        count++;
                    }
                    loadFeeds();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public class FeedsViewAdapter extends RecyclerView.Adapter<FeedsViewAdapter.MyViewHolder> {

        final ArrayList<Feed> feeds;

        FeedsViewAdapter(ArrayList<Feed> feeds) {
            this.feeds = feeds;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feeds_item_view, parent,
                    false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
            holder.feedName.setText(feeds.get(listPosition).getFeedName());
            if (feeds.get(listPosition).getFeedDate().equals("-1"))
                holder.feedDate.setText("Now");
            else if (feeds.get(listPosition).getFeedDate().equals("0"))
                holder.feedDate.setText("Today");
            else if (feeds.get(listPosition).getFeedDate().equals("1"))
                holder.feedDate.setText("Yesterday");
            else
                holder.feedDate.setText(String.format("%s days ago", feeds.get(listPosition).getFeedDate()));
        }

        @Override
        public int getItemCount() {
            if (feeds == null) {
                return 0;
            }
            return feeds.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView feedName, feedDate;

            MyViewHolder(View itemView) {
                super(itemView);
                feedName = (TextView) itemView.findViewById(R.id.feed_name);
                feedDate = (TextView) itemView.findViewById(R.id.feed_date);
            }
        }
    }
}
