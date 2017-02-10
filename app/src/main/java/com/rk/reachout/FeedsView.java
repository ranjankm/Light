package com.rk.reachout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FeedsView extends AppCompatActivity {

    private ArrayList<Feed> feeds;
    private RecyclerView feedsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds_view);

        feedsView = (RecyclerView) findViewById(R.id.feeds_list);
        final UserSessionManager sessionManager = new UserSessionManager(FeedsView.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Feeds");
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FloatingActionButton addShop = (FloatingActionButton) findViewById(R.id.add_feed);
        addShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(FeedsView.this);
                AlertDialog.Builder newFeedDialogBuilder = new AlertDialog.Builder(FeedsView.this);
                final View dialog = li.inflate(R.layout.dialog_new_feed_view, null);
                newFeedDialogBuilder.setView(dialog);
                final EditText feed = (EditText) dialog.findViewById(R.id.new_feed_edittext);
                final Button submit_button = (Button)dialog.findViewById(R.id.feed_submit_button);
                final Button cancel_button = (Button)dialog.findViewById(R.id.feed_cancel_button);
                final AlertDialog alertDialog = newFeedDialogBuilder.create();
                alertDialog.show();
                submit_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new NewFeed(FeedsView.this, feed.getText().toString()).execute(sessionManager.getShopid());
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

        new Feeds(FeedsView.this).execute(sessionManager.getShopid());
    }

    protected void loadFeeds() {
        RecyclerView.LayoutManager shopLayoutManager = new LinearLayoutManager(FeedsView.this, LinearLayoutManager.VERTICAL, false);
        feedsView.setLayoutManager(shopLayoutManager);
        feedsView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.Adapter adapter = new FeedsViewAdapter(feeds);
        feedsView.setAdapter(adapter);
    }

    public class Feeds extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/Feeds_List.php";
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
                    feeds = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    int count = 0;
                    while (count < jsonArray.length()) {
                        JSONObject JO = jsonArray.getJSONObject(count);
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

    public class NewFeed extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/New_Feed.php";
        ProgressDialog dialog;
        Context context;
        String feed;

        NewFeed(Context context, String feed) {
            this.context = context;
            this.feed = feed;
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
                hashMap.put("feed", feed);
                System.out.println("Shopid"+params[0]);
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
                    if(jsonObject.getString("status").equals("success")) {
                        Toast.makeText(context, "Feed submitted successfully", Toast.LENGTH_SHORT).show();
                        feeds.add(0,new Feed(feed, "-1"));
                    }
                    else
                        Toast.makeText(context, "Please try again...", Toast.LENGTH_SHORT).show();
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
