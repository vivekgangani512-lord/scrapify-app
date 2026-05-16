package com.example.scrapbid.model;

public class Scrap {
    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_BIDDING = "BIDDING";
    public static final String STATUS_DEALT = "DEALT";

    public int id;
    public int userId;
    public int categoryId;
    public String categoryName;
    public String categoryEmoji;
    public String categoryColorHex;
    public double categoryBasePrice;
    public String userName;
    public String title;
    public double weight;
    public double askingPrice;
    public String address;
    public String description;
    public String status;
    public String createdAt;
    public int bidCount;

    public Scrap() {}

    public double suggestedPrice() {
        return weight * categoryBasePrice;
    }
}
