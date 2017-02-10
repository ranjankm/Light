package com.rk.reachout;

/**
 * Created by Ranjan KM on 31 Jan 2017.
 */

class DealsAdv {
    private String advTitle;
    private String advBnames;
    private String advPrice;
    private String advDiscount;
    private String advValidity;
    private String advShopname;
    private String img1URL;
    private String img2URL;
    private String img3URL;
    private boolean cardStatus;
    private boolean timerStatus=false;

    DealsAdv(String advTitle, String advBnames, String advPrice, String advDiscount, String advValidity, String advShopname, String img1URL, String img2URL, String img3URL, boolean cardStatus) {
        this.advTitle = advTitle;
        this.advBnames = advBnames;
        this.advPrice = advPrice;
        this.advDiscount = advDiscount;
        this.advValidity = advValidity;
        this.advShopname = advShopname;
        this.img1URL = img1URL;
        this.img2URL = img2URL;
        this.img3URL = img3URL;
        this.cardStatus = cardStatus;
    }

    DealsAdv(String advTitle, String advBnames, String advPrice, String advDiscount, String advValidity, String img1URL, String img2URL, String img3URL, boolean cardStatus) {
        this.advTitle = advTitle;
        this.advBnames = advBnames;
        this.advPrice = advPrice;
        this.advDiscount = advDiscount;
        this.advValidity = advValidity;
        this.img1URL = img1URL;
        this.img2URL = img2URL;
        this.img3URL = img3URL;
        this.cardStatus = cardStatus;
    }

    public boolean isTimerStatus() {
        return timerStatus;
    }

    public void setTimerStatus(boolean timerStatus) {
        this.timerStatus = timerStatus;
    }

    boolean isCardStatus() {
        return cardStatus;
    }

    void setCardStatus(boolean cardStatus) {
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

    String getAdvShopname() {
        return advShopname;
    }

    String getAdvTitle() {
        return advTitle;
    }

    String getAdvBnames() {
        return advBnames;
    }

    String getAdvPrice() {
        return advPrice;
    }

    String getAdvDiscount() {
        return advDiscount;
    }

    String getAdvValidity() {
        return advValidity;
    }
}
