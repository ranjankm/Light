package com.rk.reachout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Search extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ArrayList<String> cities = new ArrayList<>();
    private ArrayList<String> areas = new ArrayList<>();
    private ArrayList<Shop> shops = new ArrayList<>();
    private UserSessionManager sessionManager;
    private RecyclerView shopsView;
    private Spinner city;
    private Spinner area;
    private SearchViewAdapter shopAdapter;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        category = getIntent().getStringExtra("category");

        sessionManager = new UserSessionManager(Search.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(category);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search.this.finish();
            }
        });

        shopsView = (RecyclerView) findViewById(R.id.shops_list);

        city = (Spinner) findViewById(R.id.city_spinner);
        city.setOnItemSelectedListener(this);
        area = (Spinner) findViewById(R.id.area_spinner);
        area.setOnItemSelectedListener(this);

        final SearchView searchBox = (SearchView) findViewById(R.id.shops_searchbox);
        new CitiesList(Search.this).execute();

        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBox.setQueryHint("Shop Name");
                searchBox.setIconified(false);
            }
        });
        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                shopAdapter.filter(query, "name");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                shopAdapter.filter(newText, "name");
                return true;
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        switch (spinner.getId()) {
            case R.id.city_spinner:
                new ShopsList(Search.this).execute(parent.getSelectedItem().toString(), category);
                new AreasList(Search.this).execute(parent.getItemAtPosition(position).toString());
                break;
            case R.id.area_spinner:
                shopAdapter.filter(parent.getSelectedItem().toString(), "area");
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadCities() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Search.this, android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter);
        city.setSelection(adapter.getPosition(sessionManager.getCity()));
    }

    private void loadAreas() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Search.this, android.R.layout.simple_spinner_item, areas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        area.setAdapter(adapter);
    }

    protected void loadShops() {
        RecyclerView.LayoutManager shopLayoutManager = new LinearLayoutManager(Search.this, LinearLayoutManager.VERTICAL, false);
        shopsView.setLayoutManager(shopLayoutManager);
        shopsView.setItemAnimator(new DefaultItemAnimator());
        shopAdapter = new SearchViewAdapter(shops);
        shopsView.setAdapter(shopAdapter);
    }

    public class CitiesList extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/Cities.php";
        ProgressDialog dialog;
        Context context;

        CitiesList(Context context) {
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
                    cities.clear();
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    int count = 0;
                    while (count < jsonArray.length()) {
                        cities.add(jsonArray.getString(count));
                        count++;
                    }
                    loadCities();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public class AreasList extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/Areas.php";
        ProgressDialog dialog;
        Context context;

        AreasList(Context context) {
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
                hashMap.put("city", params[0]);
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
                    areas.clear();
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    int count = 0;
                    areas.add("Select place");
                    while (count < jsonArray.length()) {
                        areas.add(jsonArray.getString(count));
                        count++;
                    }
                    loadAreas();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public class ShopsList extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/Shops_List.php";
        ProgressDialog dialog;
        Context context;

        ShopsList(Context context) {
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
                hashMap.put("city", params[0]);
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
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    int count = 0;
                    while (count < jsonArray.length()) {
                        JSONObject JO = jsonArray.getJSONObject(count);
                        shops.add(new Shop(JO.getString("shopid"), JO.getString("shopname"), JO.getString("address"), JO.getString("area"), JO.getString("phone"), JO.getString("status"), false));
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

    public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewAdapter.MyViewHolder> {

        final ArrayList<Shop> shops;
        final ArrayList<Shop> areashops = new ArrayList<>();
        final ArrayList<Shop> shopscopy = new ArrayList<>();

        SearchViewAdapter(ArrayList<Shop> shops) {
            this.shops = shops;
            shopscopy.addAll(shops);
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

        void filter(String text, String attribute) {
            shops.clear();
            text = text.toLowerCase();
            if (attribute.equals("area")) {
                areashops.clear();
                if (text.equals("select place")) {
                    shops.addAll(shopscopy);
                    areashops.addAll(shopscopy);
                } else {
                    for (Shop item : shopscopy) {
                        if (item.getShopArea().toLowerCase().contains(text)) {
                            shops.add(item);
                            areashops.add(item);
                        }
                    }
                }
            } else {
                if (text.isEmpty()) {
                    shops.addAll(areashops);
                } else {
                    for (Shop item : areashops) {
                        if (item.getShopname().toLowerCase().contains(text)) {
                            shops.add(item);
                        }
                    }
                }
            }
            notifyDataSetChanged();
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
                        Intent intent = new Intent(Search.this, ShopkeeperView.class);
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
                            new Favourites(Search.this, "delete").execute(sessionManager.getUserid(), shops.get(getAdapterPosition()).getShopid());
                        } else {
                            shops.get(getAdapterPosition()).setShopfavourite(true);
                            new Favourites(Search.this, "add").execute(sessionManager.getUserid(), shops.get(getAdapterPosition()).getShopid());
                        }
                        notifyDataSetChanged();
                    }
                });
            }
        }
    }
}
