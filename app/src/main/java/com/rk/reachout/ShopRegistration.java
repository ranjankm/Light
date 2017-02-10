package com.rk.reachout;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ShopRegistration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText shopname, shopaddress, shoptin, shopemail, shopphone, shopotime, shopctime, shopholidays, shopproducts, shopbrands;
    private Spinner shopcategory, shopcity, shoparea;
    private UserSessionManager sessionManager;
    private ArrayList<String> cities = new ArrayList<>();
    private ArrayList<String> areas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_registration);

        sessionManager = new UserSessionManager(ShopRegistration.this);

        shopname = (EditText) findViewById(R.id.sreg_shop_name);
        shopaddress = (EditText) findViewById(R.id.sreg_address);
        shoptin = (EditText) findViewById(R.id.sreg_shop_tin);
        shopemail = (EditText) findViewById(R.id.sreg_email);
        shopphone = (EditText) findViewById(R.id.sreg_phone);
        shopotime = (EditText) findViewById(R.id.sreg_opening_time);
        shopctime = (EditText) findViewById(R.id.sreg_closing_time);
        shopholidays = (EditText) findViewById(R.id.sreg_holidays);
        shopproducts = (EditText) findViewById(R.id.sreg_products);
        shopbrands = (EditText) findViewById(R.id.sreg_brands);
        Button sreg_button = (Button) findViewById(R.id.sreg_register);

        shopcity = (Spinner) findViewById(R.id.sreg_city_spinner);
        shopcity.setOnItemSelectedListener(this);
        shoparea = (Spinner) findViewById(R.id.sreg_area_spinner);
        shoparea.setOnItemSelectedListener(this);
        shopcategory = (Spinner) findViewById(R.id.sreg_category_spinner);

        new CitiesList(ShopRegistration.this).execute();

        final ImageButton otimeButton = (ImageButton) findViewById(R.id.sreg_opening_time_picker);
        otimeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimePicker(shopotime);
            }
        });

        ImageButton ctimeButton = (ImageButton) findViewById(R.id.sreg_closing_time_picker);
        ctimeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimePicker(shopctime);
            }
        });

        sreg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        switch (spinner.getId()) {
            case R.id.sreg_city_spinner:
                new AreasList(ShopRegistration.this).execute(parent.getItemAtPosition(position).toString());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadCities() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ShopRegistration.this, android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shopcity.setAdapter(adapter);
        shopcity.setSelection(adapter.getPosition(sessionManager.getCity()));
    }

    private void loadAreas() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ShopRegistration.this, android.R.layout.simple_spinner_item, areas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shoparea.setAdapter(adapter);
    }

    void showTimePicker(final TextView time) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker = new TimePickerDialog(ShopRegistration.this, R.style.TimePickerStyle, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                int hour = selectedHour % 12;
                time.setText(String.format(Locale.ENGLISH, "%02d:%02d %s", hour == 0 ? 12 : hour, selectedMinute, selectedHour < 12 ? "AM" : "PM"));
            }
        }, hour, minute, false);
        mTimePicker.show();
    }

    void validate() {
        boolean submit = true;
        shopname.setError(null);
        shopaddress.setError(null);
        shoptin.setError(null);
        shopemail.setError(null);
        shopphone.setError(null);
        shopotime.setError(null);
        shopctime.setError(null);
        shopproducts.setError(null);
        shopbrands.setError(null);
        final String category = shopcategory.getSelectedItem().toString();
        final String name = shopname.getText().toString();
        final String address = shopaddress.getText().toString();
        final String city = shopcity.getSelectedItem().toString();
        final String area = shoparea.getSelectedItem().toString();
        final String tin = shoptin.getText().toString();
        final String email = shopemail.getText().toString();
        final String phone = shopphone.getText().toString();
        final String otime = shopotime.getText().toString();
        final String ctime = shopctime.getText().toString();
        final String holidays = shopholidays.getText().toString();
        final String products = shopproducts.getText().toString();
        final String brands = shopbrands.getText().toString();
        if (TextUtils.isEmpty(name)) {
            shopname.setError("This field is required");
            submit = false;
        }
        if (TextUtils.isEmpty(address)) {
            shopaddress.setError("This field is required");
            submit = false;
        }
        if (TextUtils.isEmpty(tin)) {
            shoptin.setError("This field is required");
            submit = false;
        }
        if (TextUtils.isEmpty(email)) {
            shopemail.setError("This field is required");
            submit = false;
        }
        if (TextUtils.isEmpty(phone)) {
            shopphone.setError("This field is required");
            submit = false;
        }
        if (TextUtils.isEmpty(otime)) {
            shopotime.setError("This field is required");
            submit = false;
        }
        if (TextUtils.isEmpty(ctime)) {
            shopctime.setError("This field is required");
            submit = false;
        }
        if (TextUtils.isEmpty(products)) {
            shopproducts.setError("This field is required");
            submit = false;
        }
        if (TextUtils.isEmpty(brands)) {
            shopbrands.setError("This field is required");
            submit = false;
        }
        if (submit) {
            LayoutInflater li = LayoutInflater.from(ShopRegistration.this);
            AlertDialog.Builder newFeedDialogBuilder = new AlertDialog.Builder(ShopRegistration.this);
            final View dialog = li.inflate(R.layout.dialog_new_feed_view, null);
            newFeedDialogBuilder.setView(dialog);
            final TextView dialogHead = (TextView) dialog.findViewById(R.id.dialog_head);
            dialogHead.setText("PASSWORD");
            final EditText password = (EditText) dialog.findViewById(R.id.new_feed_edittext);
            password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password.setHint("Minimum 6 characters");
            final Button submit_button = (Button) dialog.findViewById(R.id.feed_submit_button);
            final Button cancel_button = (Button) dialog.findViewById(R.id.feed_cancel_button);
            final AlertDialog alertDialog = newFeedDialogBuilder.create();
            alertDialog.show();

            submit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    password.setError(null);
                    String pass=password.getText().toString();
                    if(pass.length()<6){
                        password.setError("Password length in less than 6 characters");
                        password.requestFocus();

                    }else
                    new RegisterShop(ShopRegistration.this).execute(category, name, address, city, area, tin, email, phone, otime, ctime, holidays, products, brands,
                            sessionManager.getUserid(),pass);
                }
            });

            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                }
            });
        }
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

    private class RegisterShop extends AsyncTask<String, Void, JSONObject> {

        private static final String URL = "http://theoms.16mb.com/Shop_Registration.php";
        ProgressDialog dialog;
        Context context;

        RegisterShop(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Registering...");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonObject = null;
            try {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("category", params[0]);
                hashMap.put("name", params[1]);
                hashMap.put("address", params[2]);
                hashMap.put("city", params[3]);
                hashMap.put("area", params[4]);
                hashMap.put("tin", params[5]);
                hashMap.put("email", params[6]);
                hashMap.put("phone", params[7]);
                hashMap.put("otime", params[8]);
                hashMap.put("ctime", params[9]);
                hashMap.put("holidays", params[10]);
                hashMap.put("products", params[11]);
                hashMap.put("brands", params[12]);
                hashMap.put("userid", params[13]);
                hashMap.put("password", params[14]);

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
                        Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        sessionManager.setShopid(jsonObject.getString("shopid"));
                        sessionManager.setShopName(jsonObject.getString("shopname"));
                        ShopRegistration.this.finish();
                    } else {
                        Toast.makeText(context, "Try again...", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
