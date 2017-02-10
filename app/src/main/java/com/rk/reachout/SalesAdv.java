package com.rk.reachout;

/**
 * Created by Ranjan KM on 31 Jan 2017.
 */

class SalesAdv {
    private String advBnames;
    private String advSname;
    private String advDiscount;
    private String advValidity;
    private String advShopname;
    private String img1URL;
    private String img2URL;
    private String img3URL;
    private boolean cardStatus;

    SalesAdv(String advSname, String advBnames, String advDiscount, String advValidity, String advShopname, String img1URL, String img2URL, String img3URL, boolean cardStatus) {

        this.advBnames = advBnames;
        this.advSname = advSname;
        this.advDiscount = advDiscount;
        this.advValidity = advValidity;
        this.advShopname = advShopname;
        this.img1URL = img1URL;
        this.img2URL = img2URL;
        this.img3URL = img3URL;
        this.cardStatus = cardStatus;
    }

    SalesAdv(String advSname, String advBnames, String advDiscount, String advValidity, String img1URL, String img2URL, String img3URL, boolean cardStatus) {
        this.advBnames = advBnames;
        this.advSname = advSname;
        this.advDiscount = advDiscount;
        this.advValidity = advValidity;
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

    String getImg1URL() {
        return img1URL;
    }

    String getImg2URL() {
        return img2URL;
    }

    String getImg3URL() {
        return img3URL;
    }

    String getAdvShopname() {
        return advShopname;
    }

    String getAdvBnames() {
        return advBnames;
    }

    String getAdvSname() {
        return advSname;
    }

    String getAdvDiscount() {
        return advDiscount;
    }

    String getAdvValidity() {
        return advValidity;
    }
}
