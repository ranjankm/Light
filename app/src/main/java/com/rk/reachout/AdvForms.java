package com.rk.reachout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Ranjan KM on 27 Jan 2017.
 */

public class AdvForms extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {
    private static final int PICK_IMAGE = 1;
    private static final int PICK_CAMERA = 2;
    private EditText advProductTags, advProductName, advSaleName, advBrandName, advPrice, advDiscounts, advValidityTo, advValidityFrom;
    private RadioGroup advValidityGroup;
    private int type = 1;
    private String validityFrom = "", validityTo = "";
    private SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
    private int validityToken = 0;
    private Button adv_img_button1, adv_img_button2, adv_img_button3;
    private ImageView adv_img1, adv_img2, adv_img3;
    private TextView adv_img1_name, adv_img2_name, adv_img3_name;
    private Uri imageUri;
    private int IMAGE_NUM = 0;
    private String image1 = "", image2 = "", image3 = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_form);
        Spinner advType = (Spinner) findViewById(R.id.adv_form_spinner);
        final LinearLayout bestDeals = (LinearLayout) findViewById(R.id.best_deals_layout);
        final LinearLayout discounts = (LinearLayout) findViewById(R.id.discounts_layout);
        final LinearLayout newItems = (LinearLayout) findViewById(R.id.new_items_layout);
        final UserSessionManager sessionManager = new UserSessionManager(AdvForms.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("New Advertisement");
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        advType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Adv Selected");
                switch (position) {
                    case 0:
                        type = 1;
                        bestDeals.setVisibility(View.VISIBLE);
                        discounts.setVisibility(View.GONE);
                        newItems.setVisibility(View.GONE);
                        advProductTags = (EditText) findViewById(R.id.adv1_product_tag);
                        advProductName = (EditText) findViewById(R.id.adv1_product_name);
                        advBrandName = (EditText) findViewById(R.id.adv1_brand_name);
                        advPrice = (EditText) findViewById(R.id.adv1_price);
                        advDiscounts = (EditText) findViewById(R.id.adv1_discount);
                        advValidityGroup = (RadioGroup) findViewById(R.id.adv1_validity);
                        advValidityFrom = (EditText) findViewById(R.id.adv1_validity_date_from);
                        advValidityTo = (EditText) findViewById(R.id.adv1_validity_date_to);
                        advValidityTo.setEnabled(false);
                        advValidityFrom.setEnabled(false);
                        adv_img1 = (ImageView) findViewById(R.id.adv1_img1);
                        adv_img2 = (ImageView) findViewById(R.id.adv1_img2);
                        adv_img3 = (ImageView) findViewById(R.id.adv1_img3);
                        adv_img_button1 = (Button) findViewById(R.id.adv1_img1_button);
                        adv_img_button2 = (Button) findViewById(R.id.adv1_img2_button);
                        adv_img_button3 = (Button) findViewById(R.id.adv1_img3_button);
                        adv_img1_name = (TextView) findViewById(R.id.adv1_img1_name);
                        adv_img2_name = (TextView) findViewById(R.id.adv1_img2_name);
                        adv_img3_name = (TextView) findViewById(R.id.adv1_img3_name);
                        break;
                    case 1:
                        type = 2;
                        bestDeals.setVisibility(View.GONE);
                        discounts.setVisibility(View.VISIBLE);
                        newItems.setVisibility(View.GONE);
                        advProductTags = (EditText) findViewById(R.id.adv2_product_tag);
                        advSaleName = (EditText) findViewById(R.id.adv2_sale_naem);
                        advProductName = (EditText) findViewById(R.id.adv2_product_name);
                        advBrandName = (EditText) findViewById(R.id.adv2_brand_name);
                        advDiscounts = (EditText) findViewById(R.id.adv2_discount);
                        advValidityGroup = (RadioGroup) findViewById(R.id.adv2_validity);
                        advValidityFrom = (EditText) findViewById(R.id.adv2_validity_date_from);
                        advValidityTo = (EditText) findViewById(R.id.adv2_validity_date_to);
                        advValidityTo.setEnabled(false);
                        advValidityFrom.setEnabled(false);
                        adv_img1 = (ImageView) findViewById(R.id.adv2_img1);
                        adv_img2 = (ImageView) findViewById(R.id.adv2_img2);
                        adv_img3 = (ImageView) findViewById(R.id.adv2_img3);
                        adv_img_button1 = (Button) findViewById(R.id.adv2_img1_button);
                        adv_img_button2 = (Button) findViewById(R.id.adv2_img2_button);
                        adv_img_button3 = (Button) findViewById(R.id.adv2_img3_button);
                        adv_img1_name = (TextView) findViewById(R.id.adv2_img1_name);
                        adv_img2_name = (TextView) findViewById(R.id.adv2_img2_name);
                        adv_img3_name = (TextView) findViewById(R.id.adv2_img3_name);
                        break;
                    case 2:
                        type = 3;
                        bestDeals.setVisibility(View.GONE);
                        discounts.setVisibility(View.GONE);
                        newItems.setVisibility(View.VISIBLE);
                        advProductTags = (EditText) findViewById(R.id.adv3_product_tag);
                        advProductName = (EditText) findViewById(R.id.adv3_product_name);
                        advBrandName = (EditText) findViewById(R.id.adv3_brand_name);
                        advPrice = (EditText) findViewById(R.id.adv3_price);
                        advDiscounts = (EditText) findViewById(R.id.adv3_discount);
                        adv_img1 = (ImageView) findViewById(R.id.adv3_img1);
                        adv_img2 = (ImageView) findViewById(R.id.adv3_img2);
                        adv_img3 = (ImageView) findViewById(R.id.adv3_img3);
                        adv_img_button1 = (Button) findViewById(R.id.adv3_img1_button);
                        adv_img_button2 = (Button) findViewById(R.id.adv3_img2_button);
                        adv_img_button3 = (Button) findViewById(R.id.adv3_img3_button);
                        adv_img1_name = (TextView) findViewById(R.id.adv3_img1_name);
                        adv_img2_name = (TextView) findViewById(R.id.adv3_img2_name);
                        adv_img3_name = (TextView) findViewById(R.id.adv3_img3_name);
                        break;
                }
                advValidityGroup.setOnCheckedChangeListener(AdvForms.this);
                adv_img_button1.setOnClickListener(AdvForms.this);
                adv_img_button2.setOnClickListener(AdvForms.this);
                adv_img_button3.setOnClickListener(AdvForms.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button submitButton = (Button) findViewById(R.id.adv_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(AdvForms.this);
                AlertDialog.Builder newFeedDialogBuilder = new AlertDialog.Builder(AdvForms.this);
                final View dialog = li.inflate(R.layout.dialog_new_feed_view, null);
                newFeedDialogBuilder.setView(dialog);
                final TextView dialogHead = (TextView) dialog.findViewById(R.id.dialog_head);
                dialogHead.setText("EXPIRY DATE");
                final EditText expiryDate = (EditText) dialog.findViewById(R.id.new_feed_edittext);
                expiryDate.setHint("dd/mm/yyyy");
                final Button submit_button = (Button) dialog.findViewById(R.id.feed_submit_button);
                final Button cancel_button = (Button) dialog.findViewById(R.id.feed_cancel_button);
                final AlertDialog alertDialog = newFeedDialogBuilder.create();
                alertDialog.show();

                submit_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String expiry = "";
                        try {
                            expiry = dateFormat2.format(dateFormat1.parse(expiryDate.getText().toString()));
                            if (validityToken == 1) {
                                validityFrom = dateFormat2.format(dateFormat1.parse(advValidityFrom.getText().toString()));
                                validityTo = dateFormat2.format(dateFormat1.parse(advValidityTo.getText().toString()));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        switch (type) {
                            case 1:
                                new NewAdv(AdvForms.this, type).execute(sessionManager.getShopid(), expiry, Integer.valueOf(type).toString(), advProductTags.getText().toString(),
                                        advProductName.getText().toString(), advBrandName.getText().toString(), advPrice.getText().toString(), advDiscounts.getText().toString(), validityFrom, validityTo,
                                        image1,image2,image3);
                                break;
                            case 2:
                                new NewAdv(AdvForms.this, type).execute(sessionManager.getShopid(), expiry, Integer.valueOf(type).toString(), advProductTags.getText().toString(),
                                        advSaleName.getText().toString(), advProductName.getText().toString(), advBrandName.getText().toString(), advDiscounts.getText().toString(), validityFrom, validityTo,
                                        image1,image2,image3);
                                break;
                            case 3:
                                new NewAdv(AdvForms.this, type).execute(sessionManager.getShopid(), expiry, Integer.valueOf(type).toString(), advProductTags.getText().toString(), advProductName.getText().toString(),
                                        advBrandName.getText().toString(), advPrice.getText().toString(), advDiscounts.getText().toString(),image1,image2,image3);
                                break;
                        }
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
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri = null;
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImageUri = data.getData();
                }
                break;
            case PICK_CAMERA:
                if (resultCode == RESULT_OK) {
                    selectedImageUri = imageUri;
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        if (selectedImageUri != null) {
            try {
                Bitmap image;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] byteArray;
                switch (IMAGE_NUM) {
                    case 1:
                        image = decodeFile(selectedImageUri);
                        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();
                        image1 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        adv_img1_name.setVisibility(View.GONE);
                        adv_img1.setVisibility(View.VISIBLE);
                        adv_img1.setImageBitmap(image);
                        break;
                    case 2:
                        image = decodeFile(selectedImageUri);
                        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();
                        image2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        adv_img2_name.setVisibility(View.GONE);
                        adv_img2.setVisibility(View.VISIBLE);
                        adv_img2.setImageBitmap(image);
                        break;
                    case 3:
                        image = decodeFile(selectedImageUri);
                        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();
                        image3 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        adv_img3_name.setVisibility(View.GONE);
                        adv_img3.setVisibility(View.VISIBLE);
                        adv_img3.setImageBitmap(image);
                        break;
                    default:
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Internal error", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        LayoutInflater li = LayoutInflater.from(AdvForms.this);
        AlertDialog.Builder newFeedDialogBuilder = new AlertDialog.Builder(AdvForms.this);
        final View dialog = li.inflate(R.layout.dialog_upload_view, null);
        newFeedDialogBuilder.setView(dialog);
        final LinearLayout camera = (LinearLayout) dialog.findViewById(R.id.img_upload_camera);
        final LinearLayout gallery = (LinearLayout) dialog.findViewById(R.id.img_upload_gallery);
        final AlertDialog alertDialog = newFeedDialogBuilder.create();
        alertDialog.show();
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = "new-photo-name.jpg";
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, fileName);
                values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(intent, PICK_CAMERA);
                alertDialog.cancel();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                alertDialog.cancel();
            }
        });
        switch (v.getId()) {
            case R.id.adv1_img1_button:
                IMAGE_NUM = 1;
                break;
            case R.id.adv1_img2_button:
                IMAGE_NUM = 2;
                break;
            case R.id.adv1_img3_button:
                IMAGE_NUM = 3;
                break;
            case R.id.adv2_img1_button:
                IMAGE_NUM = 1;
                break;
            case R.id.adv2_img2_button:
                IMAGE_NUM = 2;
                break;
            case R.id.adv2_img3_button:
                IMAGE_NUM = 3;
                break;
            case R.id.adv3_img1_button:
                IMAGE_NUM = 1;
                break;
            case R.id.adv3_img2_button:
                IMAGE_NUM = 2;
                break;
            case R.id.adv3_img3_button:
                IMAGE_NUM = 3;
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        Calendar cal = Calendar.getInstance();
        if (group.getId() == R.id.adv1_validity) {
            System.out.println("id " + checkedId);
            switch (checkedId) {
                case R.id.adv1_validity_today:
                    validityToken = 0;
                    validityFrom = dateFormat2.format(cal.getTime());
                    cal.add(Calendar.DATE, 1);
                    validityTo = dateFormat2.format(cal.getTime());
                    advValidityTo.setEnabled(false);
                    advValidityFrom.setEnabled(false);
                    break;
                case R.id.adv1_validity_week:
                    validityToken = 0;
                    validityFrom = dateFormat2.format(cal.getTime());
                    cal.add(Calendar.DATE, 7);
                    validityTo = dateFormat2.format(cal.getTime());
                    advValidityTo.setEnabled(false);
                    advValidityFrom.setEnabled(false);
                    break;
                case R.id.adv1_validity_date:
                    advValidityTo.setEnabled(true);
                    advValidityFrom.setEnabled(true);
                    validityToken = 1;
                    break;
            }
        } else if (group.getId() == R.id.adv2_validity) {
            System.out.println("id " + checkedId);
            switch (checkedId) {
                case R.id.adv2_validity_today:
                    validityToken = 0;
                    validityFrom = dateFormat2.format(cal.getTime());
                    cal.add(Calendar.DATE, 1);
                    validityTo = dateFormat2.format(cal.getTime());
                    advValidityTo.setEnabled(false);
                    advValidityFrom.setEnabled(false);
                    break;
                case R.id.adv2_validity_week:
                    validityToken = 0;
                    validityFrom = dateFormat2.format(cal.getTime());
                    cal.add(Calendar.DATE, 7);
                    validityTo = dateFormat2.format(cal.getTime());
                    advValidityTo.setEnabled(false);
                    advValidityFrom.setEnabled(false);
                    break;
                case R.id.adv2_validity_date:
                    validityToken = 1;
                    advValidityTo.setEnabled(true);
                    advValidityFrom.setEnabled(true);
                    break;
            }
        }
    }

    /*void validateForm(){
        String advProductTags, advProductName, advSaleName, advBrandName, advPrice, advDiscounts, advValidityTo, advValidityFrom;
    }*/

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private Bitmap decodeFile(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);
        final int REQUIRED_SIZE = 300;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

    }

    public class NewAdv extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/New_Adv.php";
        ProgressDialog dialog;
        Context context;
        int type;

        NewAdv(Context context, int type) {
            this.context = context;
            this.type = type;
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
                hashMap.put("expiry", params[1]);
                hashMap.put("type", params[2]);
                switch (type) {
                    case 1:
                        hashMap.put("ptags", params[3]);
                        hashMap.put("pnames", params[4]);
                        hashMap.put("bnames", params[5]);
                        hashMap.put("price", params[6]);
                        hashMap.put("discount", params[7]);
                        hashMap.put("validityfrom", params[8]);
                        hashMap.put("validityto", params[9]);
                        hashMap.put("image1", params[10]);
                        hashMap.put("image2", params[11]);
                        hashMap.put("image3", params[12]);
                        break;
                    case 2:
                        hashMap.put("ptags", params[3]);
                        hashMap.put("sname", params[4]);
                        hashMap.put("pnames", params[5]);
                        hashMap.put("bnames", params[6]);
                        hashMap.put("discount", params[7]);
                        hashMap.put("validityfrom", params[8]);
                        hashMap.put("validityto", params[9]);
                        hashMap.put("image1", params[10]);
                        hashMap.put("image2", params[11]);
                        hashMap.put("image3", params[12]);
                        break;
                    case 3:
                        hashMap.put("ptags", params[3]);
                        hashMap.put("pnames", params[4]);
                        hashMap.put("bnames", params[5]);
                        hashMap.put("price", params[6]);
                        hashMap.put("discount", params[7]);
                        hashMap.put("image1", params[8]);
                        hashMap.put("image2", params[9]);
                        hashMap.put("image3", params[10]);
                }
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
                        Toast.makeText(context, "Advertisement submitted successfully", Toast.LENGTH_SHORT).show();
                        AdvForms.this.finish();
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


}
