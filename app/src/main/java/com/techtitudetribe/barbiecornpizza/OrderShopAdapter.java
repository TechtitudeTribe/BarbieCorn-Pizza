package com.techtitudetribe.barbiecornpizza;

public class OrderShopAdapter {
    String itemNames, itemPlacedDate, itemTotalAmount, itemStatus, itemDescription, address, orderType;
    String paymentMethod, userId, deliveryCharge, titleName, customerName, customerNumber, shopName, orderId;
    long count;

    public OrderShopAdapter()
    {

    }

    public OrderShopAdapter(String itemNames, String itemPlacedDate, String itemTotalAmount, String itemStatus, String itemDescription, String address, String orderType, String paymentMethod, String userId, String deliveryCharge, String titleName, String customerName, String customerNumber, String shopName, String orderId, long count) {
        this.itemNames = itemNames;
        this.itemPlacedDate = itemPlacedDate;
        this.itemTotalAmount = itemTotalAmount;
        this.itemStatus = itemStatus;
        this.itemDescription = itemDescription;
        this.address = address;
        this.orderType = orderType;
        this.paymentMethod = paymentMethod;
        this.userId = userId;
        this.deliveryCharge = deliveryCharge;
        this.titleName = titleName;
        this.customerName = customerName;
        this.customerNumber = customerNumber;
        this.shopName = shopName;
        this.orderId = orderId;
        this.count = count;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getItemNames() {
        return itemNames;
    }

    public void setItemNames(String itemNames) {
        this.itemNames = itemNames;
    }

    public String getItemPlacedDate() {
        return itemPlacedDate;
    }

    public void setItemPlacedDate(String itemPlacedDate) {
        this.itemPlacedDate = itemPlacedDate;
    }

    public String getItemTotalAmount() {
        return itemTotalAmount;
    }

    public void setItemTotalAmount(String itemTotalAmount) {
        this.itemTotalAmount = itemTotalAmount;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
