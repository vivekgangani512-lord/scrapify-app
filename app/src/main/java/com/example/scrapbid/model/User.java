package com.example.scrapbid.model;

public class User {
    public static final String ROLE_USER = "USER";
    public static final String ROLE_DEALER = "DEALER";

    public int id;
    public String name;
    public String phone;
    public String password;
    public String role;
    public String address;
    public String createdAt;

    public User() {}

    public User(int id, String name, String phone, String role, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.address = address;
    }

    public boolean isDealer() {
        return ROLE_DEALER.equals(role);
    }
}
