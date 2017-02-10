package com.rk.reachout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class AdvView extends AppCompatActivity {

    private RecyclerView advView;
    private ArrayList<Object> advs;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_view);

        advView = (RecyclerView) findViewById(R.id.adv_list);

        sessionManager = new UserSessionManager(AdvView.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Advertisements");
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FloatingActionButton addAdv = (FloatingActionButton) findViewById(R.id.add_adv);
        addAdv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdvView.this, AdvForms.class));
            }
        });
    }

    @Override
    protected void onResume() {
        new Advs(AdvView.this).execute(sessionManager.getShopid());
        super.onPostResume();
    }

    protected void loadAdvs() {
        RecyclerView.LayoutManager shopLayoutManager = new LinearLayoutManager(AdvView.this, LinearLayoutManager.VERTICAL, false);
        advView.setLayoutManager(shopLayoutManager);
        advView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.Adapter adapter = new AdvsViewAdapter(advs);
        advView.setAdapter(adapter);
    }

    public class Advs extends AsyncTask<String, Void, JSONObject> {
        private static final String URL = "http://theoms.16mb.com/Adv_List.php";
        ProgressDialog dialog;
        Context context;

        Advs(Context context) {
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
                    advs = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONArray("advlist");
                    int count = 0;
                    while (count < jsonArray.length()) {
                        JSONObject JO = jsonArray.getJSONObject(count);
                        if (JO.getString("advcategory").equals("deals"))
                            advs.add(new DealsAdv(JO.getString("pnames"), JO.getString("bnames"), JO.getString("price"), JO.getString("discount"), JO.getString("validity"), JO.getString("img1"), JO.getString("img2"), JO.getString("img3"), false));
                        else if (JO.getString("advcategory").equals("sales"))
                            advs.add(new SalesAdv(JO.getString("pnames"), JO.getString("bnames"), JO.getString("sname"), JO.getString("discount"), JO.getString("validity"), JO.getString("img1"), JO.getString("img2"), JO.getString("img3"), false));
                        else
                            advs.add(new NewAdv(JO.getString("pnames"), JO.getString("bnames"), JO.getString("price"), JO.getString("discount"), JO.getString("img1"), JO.getString("img2"), JO.getString("img3"), false));
                        count++;
                    }
                    loadAdvs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public class AdvsViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        final ArrayList<Object> advs;
        final int DEALS = 1, SALES = 2, NEW = 3;
        int PREVIOUS_INDEX = -1;

        AdvsViewAdapter(ArrayList<Object> advs) {
            this.advs = advs;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            RecyclerView.ViewHolder viewHolder = null;
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            switch (viewType) {
                case DEALS:
                    View v1 = inflater.inflate(R.layout.adv_deals_item_view, viewGroup, false);
                    viewHolder = new DealsViewHolder(v1);
                    break;
                case SALES:
                    View v2 = inflater.inflate(R.layout.adv_sales_item_view, viewGroup, false);
                    viewHolder = new SalesViewHolder(v2);
                    break;
                case NEW:
                    View v3 = inflater.inflate(R.layout.adv_new_item_view, viewGroup, false);
                    viewHolder = new NewViewHolder(v3);
                    break;
            }
            return viewHolder;
        }

        @Override
        public int getItemViewType(int position) {
            if (advs.get(position) instanceof DealsAdv) {
                return DEALS;
            } else if (advs.get(position) instanceof SalesAdv) {
                return SALES;
            } else if (advs.get(position) instanceof NewAdv) {
                return NEW;
            }
            return -1;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            switch (viewHolder.getItemViewType()) {
                case DEALS:
                    DealsViewHolder dealsViewHolder = (DealsViewHolder) viewHolder;
                    configureDeslsViewHolder(dealsViewHolder, position);
                    break;
                case SALES:
                    SalesViewHolder salesViewHolder = (SalesViewHolder) viewHolder;
                    configureSalesViewHolder(salesViewHolder, position);
                    break;
                case NEW:
                    NewViewHolder newViewHolder = (NewViewHolder) viewHolder;
                    configureNewViewHolder(newViewHolder, position);
                    break;
            }
        }

        void configureDeslsViewHolder(final DealsViewHolder holder, int position) {
            DealsAdv dealsAdv = (DealsAdv) advs.get(position);
            if (dealsAdv != null) {
                holder.title.setText(dealsAdv.getAdvTitle().toUpperCase());
                holder.bname.setText(dealsAdv.getAdvBnames());
                if (dealsAdv.isCardStatus())
                    holder.imagesLayout.setVisibility(View.VISIBLE);
                else
                    holder.imagesLayout.setVisibility(View.GONE);
                if (!dealsAdv.getAdvPrice().equals("0"))
                    holder.price.setText(dealsAdv.getAdvPrice());
                else
                    holder.priceLayout.setVisibility(View.GONE);
                if (!dealsAdv.getAdvDiscount().equals("0"))
                    holder.discount.setText(dealsAdv.getAdvDiscount());
                else
                    holder.discountLayout.setVisibility(View.GONE);
                new CountDownTimer(Long.parseLong(dealsAdv.getAdvValidity()), 1000) {
                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) (millisUntilFinished / 1000);
                        int days = seconds / (24 * 60 * 60);
                        int hours = seconds / (60 * 60);
                        int minutes = seconds / 60;
                        hours = hours % 24;
                        minutes = minutes % 60;
                        seconds = seconds % 60;
                        if (days != 0)
                            holder.validity.setText(String.format(Locale.ENGLISH, "Offer ends in %02dd %02dh %02dm %02ds", days, hours, minutes, seconds));
                        else
                            holder.validity.setText(String.format(Locale.ENGLISH, "Offer ends in %02dh %02dm %02ds", hours, minutes, seconds));
                    }

                    public void onFinish() {
                        holder.validity.setText("Offer expired");
                    }
                }.start();
            }
        }

        void configureSalesViewHolder(final SalesViewHolder holder, int position) {
            SalesAdv salesAdv = (SalesAdv) advs.get(position);
            if (salesAdv != null) {
                holder.bname.setText(salesAdv.getAdvBnames());
                holder.discount.setText(salesAdv.getAdvDiscount());
                holder.sname.setText(salesAdv.getAdvSname().toUpperCase());
                if (salesAdv.isCardStatus())
                    holder.imagesLayout.setVisibility(View.VISIBLE);
                else
                    holder.imagesLayout.setVisibility(View.GONE);
                new CountDownTimer(Long.parseLong(salesAdv.getAdvValidity()), 1000) {
                    public void onTick(long millisUntilFinished) {
                        int seconds = (int) (millisUntilFinished / 1000);
                        int days = seconds / (24 * 60 * 60);
                        int hours = seconds / (60 * 60);
                        int minutes = seconds / 60;
                        hours = hours % 24;
                        minutes = minutes % 60;
                        seconds = seconds % 60;
                        if (days != 0)
                            holder.validity.setText(String.format(Locale.ENGLISH, "Offer ends in %02dd %02dh %02dm %02ds", days, hours, minutes, seconds));
                        else
                            holder.validity.setText(String.format(Locale.ENGLISH, "Offer ends in %02dh %02dm %02ds", hours, minutes, seconds));
                    }

                    public void onFinish() {
                        holder.validity.setText("Offer expired");
                    }
                }.start();
            }
        }

        void configureNewViewHolder(final NewViewHolder holder, int position) {
            NewAdv newAdv = (NewAdv) advs.get(position);
            if (newAdv != null) {
                holder.title.setText(newAdv.getAdvTitle().toUpperCase());
                holder.bname.setText(newAdv.getAdvBnames());
                if (newAdv.isCardStatus())
                    holder.imagesLayout.setVisibility(View.VISIBLE);
                else
                    holder.imagesLayout.setVisibility(View.GONE);

                if (!newAdv.getAdvPrice().equals("0"))
                    holder.price.setText(newAdv.getAdvPrice());
                else
                    holder.priceLayout.setVisibility(View.GONE);
                if (!newAdv.getAdvDiscount().equals("0"))
                    holder.discount.setText(newAdv.getAdvDiscount());
                else
                    holder.discountLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            if (advs == null) {
                return 0;
            }
            return advs.size();
        }

        class DealsViewHolder extends RecyclerView.ViewHolder {

            TextView title, bname, price, discount, validity, shopname;
            LinearLayout priceLayout, discountLayout, imagesLayout;
            ImageView img1, img2, img3;
            CardView advCard;

            DealsViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.product_name);
                bname = (TextView) itemView.findViewById(R.id.brand_name);
                price = (TextView) itemView.findViewById(R.id.price);
                discount = (TextView) itemView.findViewById(R.id.discount);
                validity = (TextView) itemView.findViewById(R.id.validity);
                shopname = (TextView) itemView.findViewById(R.id.shop_name);
                priceLayout = (LinearLayout) itemView.findViewById(R.id.adv_price_view);
                discountLayout = (LinearLayout) itemView.findViewById(R.id.adv_discount_view);
                img1 = (ImageView) itemView.findViewById(R.id.adv_img1);
                img2 = (ImageView) itemView.findViewById(R.id.adv_img2);
                img3 = (ImageView) itemView.findViewById(R.id.adv_img3);
                advCard = (CardView) itemView.findViewById(R.id.adv_deals_item_card);
                imagesLayout = (LinearLayout) itemView.findViewById(R.id.adv_images);

                advCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DealsAdv dealsAdv = (DealsAdv) advs.get(getAdapterPosition());
                        if (dealsAdv.isCardStatus()) {
                            dealsAdv.setCardStatus(false);
                            PREVIOUS_INDEX = -1;
                        } else {
                            dealsAdv.setCardStatus(true);
                            if (PREVIOUS_INDEX != -1 && PREVIOUS_INDEX != getAdapterPosition()) {
                                if (advs.get(PREVIOUS_INDEX) instanceof DealsAdv) {
                                    ((DealsAdv) advs.get(PREVIOUS_INDEX)).setCardStatus(false);
                                } else if (advs.get(PREVIOUS_INDEX) instanceof SalesAdv) {
                                    ((SalesAdv) advs.get(PREVIOUS_INDEX)).setCardStatus(false);
                                } else if (advs.get(PREVIOUS_INDEX) instanceof NewAdv) {
                                    ((NewAdv) advs.get(PREVIOUS_INDEX)).setCardStatus(false);
                                }
                            }
                        }
                        notifyItemChanged(PREVIOUS_INDEX);
                        PREVIOUS_INDEX = getAdapterPosition();
                        notifyItemChanged(getAdapterPosition());
                    }
                });
            }
        }

        class SalesViewHolder extends RecyclerView.ViewHolder {

            TextView sname, bname, discount, validity, shopname;
            LinearLayout imagesLayout;
            ImageView img1, img2, img3;
            CardView advCard;

            SalesViewHolder(View itemView) {
                super(itemView);
                bname = (TextView) itemView.findViewById(R.id.brand_name);
                sname = (TextView) itemView.findViewById(R.id.sale_name);
                discount = (TextView) itemView.findViewById(R.id.discount);
                validity = (TextView) itemView.findViewById(R.id.validity);
                shopname = (TextView) itemView.findViewById(R.id.shop_name);
                img1 = (ImageView) itemView.findViewById(R.id.adv_img1);
                img2 = (ImageView) itemView.findViewById(R.id.adv_img2);
                img3 = (ImageView) itemView.findViewById(R.id.adv_img3);
                advCard = (CardView) itemView.findViewById(R.id.adv_sales_item_card);
                imagesLayout = (LinearLayout) itemView.findViewById(R.id.adv_images);

                advCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SalesAdv salesAdv = (SalesAdv) advs.get(getAdapterPosition());
                        if (salesAdv.isCardStatus()) {
                            salesAdv.setCardStatus(false);
                            PREVIOUS_INDEX = -1;
                        } else {
                            salesAdv.setCardStatus(true);
                            if (PREVIOUS_INDEX != -1 && PREVIOUS_INDEX != getAdapterPosition()) {
                                if (advs.get(PREVIOUS_INDEX) instanceof DealsAdv) {
                                    ((DealsAdv) advs.get(PREVIOUS_INDEX)).setCardStatus(false);
                                } else if (advs.get(PREVIOUS_INDEX) instanceof SalesAdv) {
                                    ((SalesAdv) advs.get(PREVIOUS_INDEX)).setCardStatus(false);
                                } else if (advs.get(PREVIOUS_INDEX) instanceof NewAdv) {
                                    ((NewAdv) advs.get(PREVIOUS_INDEX)).setCardStatus(false);
                                }
                            }
                        }
                        notifyItemChanged(PREVIOUS_INDEX);
                        PREVIOUS_INDEX = getAdapterPosition();
                        notifyItemChanged(getAdapterPosition());
                    }
                });
            }
        }

        class NewViewHolder extends RecyclerView.ViewHolder {

            TextView title, bname, price, discount, shopname;
            LinearLayout priceLayout, discountLayout, imagesLayout;
            ImageView img1, img2, img3;
            CardView advCard;

            NewViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.product_name);
                bname = (TextView) itemView.findViewById(R.id.brand_name);
                price = (TextView) itemView.findViewById(R.id.price);
                discount = (TextView) itemView.findViewById(R.id.discount);
                shopname = (TextView) itemView.findViewById(R.id.shop_name);
                priceLayout = (LinearLayout) itemView.findViewById(R.id.adv_price_view);
                discountLayout = (LinearLayout) itemView.findViewById(R.id.adv_discount_view);
                img1 = (ImageView) itemView.findViewById(R.id.adv_img1);
                img2 = (ImageView) itemView.findViewById(R.id.adv_img2);
                img3 = (ImageView) itemView.findViewById(R.id.adv_img3);
                advCard = (CardView) itemView.findViewById(R.id.adv_new_item_card);
                imagesLayout = (LinearLayout) itemView.findViewById(R.id.adv_images);

                advCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NewAdv newAdv = (NewAdv) advs.get(getAdapterPosition());
                        if (newAdv.isCardStatus()) {
                            newAdv.setCardStatus(false);
                            PREVIOUS_INDEX = -1;
                        } else {
                            newAdv.setCardStatus(true);
                            if (PREVIOUS_INDEX != -1 && PREVIOUS_INDEX != getAdapterPosition()) {
                                if (advs.get(PREVIOUS_INDEX) instanceof DealsAdv) {
                                    ((DealsAdv) advs.get(PREVIOUS_INDEX)).setCardStatus(false);
                                } else if (advs.get(PREVIOUS_INDEX) instanceof SalesAdv) {
                                    ((SalesAdv) advs.get(PREVIOUS_INDEX)).setCardStatus(false);
                                } else if (advs.get(PREVIOUS_INDEX) instanceof NewAdv) {
                                    ((NewAdv) advs.get(PREVIOUS_INDEX)).setCardStatus(false);
                                }
                            }
                        }
                        notifyItemChanged(PREVIOUS_INDEX);
                        PREVIOUS_INDEX = getAdapterPosition();
                        notifyItemChanged(getAdapterPosition());
                    }
                });
            }
        }
    }
}
