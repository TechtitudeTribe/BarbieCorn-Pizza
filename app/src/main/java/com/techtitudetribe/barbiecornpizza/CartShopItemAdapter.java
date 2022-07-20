package com.techtitudetribe.barbiecornpizza;

public class CartShopItemAdapter {
    String itemNames, shopName, sellerId, deliveryCharge, upi;
    long count;

    public CartShopItemAdapter()
    {

    }

    public CartShopItemAdapter(String itemNames, String shopName, String sellerId, String deliveryCharge, String upi, long count) {
        this.itemNames = itemNames;
        this.shopName = shopName;
        this.sellerId = sellerId;
        this.deliveryCharge = deliveryCharge;
        this.upi = upi;
        this.count = count;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
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

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getItemNames() {
        return itemNames;
    }

    public void setItemNames(String itemNames) {
        this.itemNames = itemNames;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
