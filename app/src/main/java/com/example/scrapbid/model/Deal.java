package com.example.scrapbid.model;

public class Deal {
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_COMPLETED = "COMPLETED";

    public int id;
    public int scrapId;
    public int userId;
    public int dealerId;
    public int bidId;
    public double finalPrice;
    public String status;
    public String createdAt;

    // Display info
    public String categoryName;
    public String categoryEmoji;
    public String categoryColorHex;
    public double scrapWeight;
    public String scrapAddress;
    public String userName;
    public String dealerName;
    public String dealerPhone;

    public Deal() {}
}
