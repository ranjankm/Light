package com.rk.reachout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryView extends AppCompatActivity {

    private ArrayList<Shop> shops = new ArrayList<>();
    private RecyclerView shopsView;
    private String category;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_view);
        category = getIntent().getStringExtra("fragment_name");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(category);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryView.this.finish();
            }
        });

        shopsView = (RecyclerView) findViewById(R.id.shops_list);

        FloatingActionButton addShop = (FloatingActionButton) findViewById(R.id.add_shop);
        addShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryView.this, Search.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });

        sessionManager = new UserSessionManager(CategoryView.this);
    }

    @Override
    protected void onResume() {
        new FavouritesShops(CategoryView.this).execute(sessionManager.getUserid(), category);
        super.onResume();
    }

    protected void loadShops() {
        RecyclerView.LayoutManager shopLayoutManager = new LinearLayoutManager(CategoryView.this, LinearLayoutManager.VERTICAL, false);
        shopsView.setLayoutManager(shopLayoutManager);
        shopsView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.Adapter adapter = new CategoryViewAdapter(shops);
        shopsView.setAdapter(adapter);
    }

    public class FavouritesShops extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/Favourite_Shops_List.php";
        ProgressDialog dialog;
        Context context;

        public FavouritesShops(Context context) {
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
                hashMap.put("category", params[1]);
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
                    shops.clear();
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    int count = 0;
                    while (count < jsonArray.length()) {
                        JSONObject JO = jsonArray.getJSONObject(count);
                        shops.add(new Shop(JO.getString("shopid"), JO.getString("shopname"), JO.getString("address"), JO.getString("area"), JO.getString("phone"), JO.getString("status"), true));
                        count++;
                    }
                    loadShops();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.MyViewHolder> {

        final ArrayList<Shop> shops;

        CategoryViewAdapter(ArrayList<Shop> shops) {
            this.shops = shops;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shops_item_view, parent,
                    false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {
            holder.shopName.setText(shops.get(listPosition).getShopname());
            holder.shopAddress.setText(shops.get(listPosition).getShopaddress());
            holder.shopPhone.setText(shops.get(listPosition).getShopphone());
            if (shops.get(listPosition).getShopstatus().equals("open"))
                holder.shopStatus.setBackgroundResource(R.drawable.shop_status_on);
            else
                holder.shopStatus.setBackgroundResource(R.drawable.shop_status_off);
            if (shops.get(listPosition).isShopfavourite())
                holder.shopfavourite.setBackgroundResource(R.drawable.favourite);
            else
                holder.shopfavourite.setBackgroundResource(R.drawable.unfavourite);
        }

        @Override
        public int getItemCount() {
            if (shops == null) {
                return 0;
            }
            return shops.size();
        }

        public void deleteItem(int position) {
            shops.remove(position);
            notifyDataSetChanged();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView shopName, shopAddress, shopPhone;
            View shopStatus;
            CardView shopCard;
            ImageView shopfavourite;

            MyViewHolder(View itemView) {
                super(itemView);
                shopCard = (CardView) itemView.findViewById(R.id.shop_card);
                shopName = (TextView) itemView.findViewById(R.id.shop_name);
                shopAddress = (TextView) itemView.findViewById(R.id.shop_address);
                shopPhone = (TextView) itemView.findViewById(R.id.shop_phone);
                shopStatus = (View) itemView.findViewById(R.id.shop_status);
                shopfavourite = (ImageView) itemView.findViewById(R.id.shop_favourite);

                shopCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CategoryView.this, ShopkeeperView.class);
                        intent.putExtra("category", category);
                        intent.putExtra("shopid", shops.get(getAdapterPosition()).getShopid());
                        startActivity(intent);
                    }
                });

                shopfavourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (shops.get(getAdapterPosition()).isShopfavourite()) {
                            shops.get(getAdapterPosition()).setShopfavourite(false);
                            new Favourites(CategoryView.this, "delete").execute(sessionManager.getUserid(), shops.get(getAdapterPosition()).getShopid());
                            deleteItem(getAdapterPosition());
                        } else
                            shops.get(getAdapterPosition()).setShopfavourite(true);
                        notifyDataSetChanged();
                    }
                });
            }
        }
    }
}
