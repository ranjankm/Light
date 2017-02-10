package com.rk.reachout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
/**
 * Created by Ranjan KM on 04 Jan 2017.
 */

public class Splash extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final UserSessionManager sessionManager=new UserSessionManager(Splash.this);
        System.out.println("Login status : "+sessionManager.isUserLoggedIn());

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(sessionManager.isUserLoggedIn()){
                    Intent intent = new Intent(Splash.this,UserHome.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(Splash.this,MobileVerify.class);
                    startActivity(intent);
                }
                Splash.this.finish();
            }
        }, 2000);
    }
}
