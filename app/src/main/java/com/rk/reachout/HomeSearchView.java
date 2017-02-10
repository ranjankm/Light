package com.rk.reachout;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by Ranjan KM on 02 Feb 2017.
 */

public class HomeSearchView extends AppCompatActivity {

    private ArrayList<Object> searchResults;
    private RecyclerView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search);

        searchView = (RecyclerView) findViewById(R.id.home_search_view);
        final UserSessionManager sessionManager = new UserSessionManager(HomeSearchView.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Search");
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        EditText searchbox=(EditText)findViewById(R.id.home_search_box);
        searchbox.setGravity(Gravity.LEFT);
    }
}