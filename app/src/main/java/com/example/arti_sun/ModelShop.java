package com.example.arti_sun;

public class ModelShop {
    private String uid, email,name,phnum,password, shopname, address,timestamp,ShopImg,shopOpen,role,online;

    public ModelShop() {
    }

    public ModelShop(String uid, String email, String name, String phnum, String password, String shopname, String address,String online, String timestamp, String ShopImg, String shopOpen, String role) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phnum = phnum;
        this.password = password;
        this.shopname = shopname;
        this.online = online;
        this.address = address;
        this.timestamp = timestamp;
        this.ShopImg = ShopImg;
        this.shopOpen = shopOpen;
        this.role = role;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhnum() {
        return phnum;
    }

    public void setPhnum(String phnum) {
        this.phnum = phnum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfileImage() {
        return ShopImg;
    }

    public void setProfileImage(String profileImage) {
        this.ShopImg = profileImage;
    }

    public String getShopOpen() {
        return shopOpen;
    }

    public void setShopOpen(String shopOpen) {
        this.shopOpen = shopOpen;
    }
}
