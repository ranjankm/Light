package com.rk.reachout;

/**
 * Created by Ranjan KM on 31 Jan 2017.
 */

public class NewAdv {
    private String advTitle;
    private String advBnames;
    private String advPrice;
    private String advDiscount;
    private String advShopname;
    private String img1URL;
    private String img2URL;
    private String img3URL;
    private boolean cardStatus;

    public NewAdv(String advTitle, String advBnames, String advPrice, String advDiscount, String advShopname, String img1URL, String img2URL, String img3URL, boolean cardStatus) {
        this.advTitle = advTitle;
        this.advBnames = advBnames;
        this.advPrice = advPrice;
        this.advDiscount = advDiscount;
        this.advShopname = advShopname;
        this.img1URL = img1URL;
        this.img2URL = img2URL;
        this.img3URL = img3URL;
        this.cardStatus = cardStatus;
    }

    public NewAdv(String advTitle, String advBnames, String advPrice, String advDiscount, String img1URL, String img2URL, String img3URL, boolean cardStatus) {
        this.advTitle = advTitle;
        this.advBnames = advBnames;
        this.advPrice = advPrice;
        this.advDiscount = advDiscount;
        this.img1URL = img1URL;
        this.img2URL = img2URL;
        this.img3URL = img3URL;
        this.cardStatus = cardStatus;
    }

    public boolean isCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(boolean cardStatus) {
        this.cardStatus = cardStatus;
    }

    public String getImg1URL() {
        return img1URL;
    }

    public String getImg2URL() {
        return img2URL;
    }

    public String getImg3URL() {
        return img3URL;
    }

    public String getAdvShopname() {
        return advShopname;
    }

    public String getAdvTitle() {
        return advTitle;
    }

    public String getAdvBnames() {
        return advBnames;
    }

    public String getAdvPrice() {
        return advPrice;
    }

    public String getAdvDiscount() {
        return advDiscount;
    }
}
