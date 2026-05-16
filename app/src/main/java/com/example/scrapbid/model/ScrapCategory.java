package com.example.scrapbid.model;

public class ScrapCategory {
    public int id;
    public String name;
    public String emoji;
    public double basePricePerKg;
    public String colorHex;

    public ScrapCategory() {}

    public ScrapCategory(int id, String name, String emoji, double basePricePerKg, String colorHex) {
        this.id = id;
        this.name = name;
        this.emoji = emoji;
        this.basePricePerKg = basePricePerKg;
        this.colorHex = colorHex;
    }
}
