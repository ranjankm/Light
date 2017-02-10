package com.rk.reachout;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ranjan KM on 17 Jan 2017.
 */

public class UserSessionManager {

    private static final String KEY_USERID = "userid";
    private static final String KEY_NAME = "name";
    private static final String KEY_SHOPID = "shopid";
    private static final String KEY_SHOP_NAME = "shopname";
    private static final String PREFER_NAME = "USER_LOGIN_DETAILS";
    private static final String KEY_CITY = "city";
    private static final String IS_USER_LOGIN = "userlogin";
    private static final String IS_SHOP_LOGIN = "shoplogin";
    private SharedPreferences pref;
    private SharedPreferences.Editor sessionEditor;

    UserSessionManager(Context context) {
        pref = context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        sessionEditor = pref.edit();
    }

    void createUserLoginSession(String userid, String name, String city, String shopid, String shopname) {
        sessionEditor.putBoolean(IS_USER_LOGIN, true);
        sessionEditor.putString(KEY_USERID, userid);
        sessionEditor.putString(KEY_NAME, name);
        sessionEditor.putString(KEY_CITY, city);
        sessionEditor.putString(KEY_SHOPID, shopid);
        sessionEditor.putString(KEY_SHOP_NAME, shopname);
        sessionEditor.putBoolean(IS_SHOP_LOGIN,false);
        sessionEditor.apply();
    }

    boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    boolean isShopLoggedIn() {
        return pref.getBoolean(IS_SHOP_LOGIN, false);
    }

    void setShopLogin(Boolean shopLogin){
        sessionEditor.putBoolean(IS_SHOP_LOGIN,shopLogin);
        sessionEditor.apply();
    }

    void setShopid(String shopid){
        sessionEditor.putString(KEY_SHOPID, shopid);
        sessionEditor.apply();
    }

    void setShopName(String name){
        sessionEditor.putString(KEY_SHOP_NAME,name);
        sessionEditor.apply();
    }

    String getUserid() {
        return pref.getString(KEY_USERID, null);
    }

    String getName() {
        return pref.getString(KEY_NAME, null);
    }

    String getCity() {
        return pref.getString(KEY_CITY, null);
    }

    String getShopid() {
        return pref.getString(KEY_SHOPID, null);
    }

    String getShopName() {
        return pref.getString(KEY_SHOP_NAME, null);
    }

    void logoutUser() {
        sessionEditor.clear();
        sessionEditor.apply();
    }

    void logoutShop() {
        sessionEditor.putBoolean(IS_SHOP_LOGIN,false);
        sessionEditor.apply();
    }
}