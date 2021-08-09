package com.techtitudetribe.barbiecornpizza;

public class ShopDetailsAdapter {
    String shopName, shopSchedule, shopContact, shopStatus, shopFrontImage,userId, deliveryCharge, upi, exclusiveOffersStatus;

    public ShopDetailsAdapter() {

    }

    public ShopDetailsAdapter(String shopName, String shopSchedule, String shopContact,String shopStatus, String shopFrontImage, String userId, String deliveryCharge, String upi, String exclusiveOffersStatus) {
        this.shopName = shopName;
        this.shopSchedule = shopSchedule;
        this.shopContact = shopContact;
        this.shopStatus = shopStatus;
        this.shopFrontImage = shopFrontImage;
        this.userId = userId;
        this.deliveryCharge = deliveryCharge;
        this.upi = upi;
        this.exclusiveOffersStatus = exclusiveOffersStatus;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public String getUpi() {
        return upi;
    }

    public void setUpi(String upi) {
        this.upi = upi;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopSchedule() {
        return shopSchedule;
    }

    public void setShopSchedule(String shopSchedule) {
        this.shopSchedule = shopSchedule;
    }

    public String getShopContact() {
        return shopContact;
    }

    public void setShopContactNumber(String shopContactNumber) {
        this.shopContact = shopContact;
    }

    public String getShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(String shopStatus) {
        this.shopStatus = shopStatus;
    }

    public String getShopFrontImage() {
        return shopFrontImage;
    }

    public void setShopFrontImage(String shopFrontImage) {
        this.shopFrontImage = shopFrontImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getExclusiveOffersStatus() {
        return exclusiveOffersStatus;
    }

    public void setExclusiveOffersStatus(String exclusiveOffersStatus) {
        this.exclusiveOffersStatus = exclusiveOffersStatus;
    }
}
