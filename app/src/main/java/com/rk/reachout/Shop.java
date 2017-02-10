package com.rk.reachout;

/**
 * Created by Ranjan KM on 20 Jan 2017.
 */

class Shop {
    private String shopid;
    private String shopname;
    private String shopaddress;
    private String shopphone;
    private String shopstatus;
    private String shoparea;
    private boolean shopfavourite;

    Shop(String shopid, String shopname, String shopaddress, String shoparea, String shopphone, String shopstatus, boolean shopfavourite) {
        this.shopid = shopid;
        this.shopname = shopname;
        this.shopaddress = shopaddress;
        this.shoparea = shoparea;
        this.shopphone = shopphone;
        this.shopstatus = shopstatus;
        this.shopfavourite = shopfavourite;
    }

    String getShopid() {
        return shopid;
    }

    String getShopname() {
        return shopname;
    }

    String getShopaddress() {
        return shopaddress;
    }

    String getShopphone() {
        return shopphone;
    }

    String getShopstatus() {
        return shopstatus;
    }

    String getShopArea() {
        return shoparea;
    }

    public boolean isShopfavourite() {
        return shopfavourite;
    }

    public void setShopfavourite(boolean shopfavourite) {
        this.shopfavourite = shopfavourite;
    }
}
