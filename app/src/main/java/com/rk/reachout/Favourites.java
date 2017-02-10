package com.rk.reachout;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Ranjan KM on 03 Feb 2017.
 */

public class Favourites extends AsyncTask<String, Void, JSONObject> {
    private static final String URL = "http://theoms.16mb.com/Favourite.php";
    Context context;
    String operation;

    Favourites(Context context, String operation) {
        this.context = context;
        this.operation = operation;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject jsonObject = null;
        try {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("userid", params[0]);
            hashMap.put("shopid", params[1]);
            hashMap.put("operation", operation);
            JSONParser jsonParser = new JSONParser();
            jsonObject = jsonParser.makeHttpRequest(URL, "POST", hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                if (jsonObject.getString("status").equals("success")) {
                    if (operation.equals("add"))
                        Toast.makeText(context, "Shop added to favourites", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Shop removed from favourites", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(context, "Please try again...", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

}