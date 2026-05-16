package com.example.scrapbid.model;

public class Bid {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_REJECTED = "REJECTED";

    public int id;
    public int scrapId;
    public int dealerId;
    public String dealerName;
    public String dealerPhone;
    public double bidPrice;
    public String note;
    public String status;
    public String createdAt;

    // Scrap info (for dealer's bid list)
    public String scrapCategoryName;
    public String scrapCategoryEmoji;
    public double scrapWeight;
    public String scrapAddress;
    public String scrapStatus;

    public Bid() {}
}
