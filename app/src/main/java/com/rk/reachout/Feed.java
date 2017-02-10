package com.rk.reachout;

/**
 * Created by Ranjan KM on 22 Jan 2017.
 */

class Feed {
    private String feedname;
    private String feeddate;

    public Feed(String feedname, String feeddate) {
        this.feedname = feedname;
        this.feeddate = feeddate;
    }

    public String getFeedName() {
        return feedname;
    }

    public String getFeedDate() {
        return feeddate;
    }
}
