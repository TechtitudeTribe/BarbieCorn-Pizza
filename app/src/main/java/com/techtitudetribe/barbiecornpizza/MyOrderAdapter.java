package com.techtitudetribe.barbiecornpizza;

public class MyOrderAdapter {
    String itemNames, itemNumber, itemPlacedDate, itemTotalAmount, itemStatus, itemDescription, address, orderType, paymentMethod, userId, orderId;
    long count;

    public MyOrderAdapter()
    {

    }

    public MyOrderAdapter(String itemNames, String itemNumber, String itemPlacedDate, String itemTotalAmount, String itemStatus, String itemDescription, String address, String orderType, String paymentMethod, String userId, String orderId, long count) {
        this.itemNames = itemNames;
        this.itemNumber = itemNumber;
        this.itemPlacedDate = itemPlacedDate;
        this.itemTotalAmount = itemTotalAmount;
        this.itemStatus = itemStatus;
        this.itemDescription = itemDescription;
        this.address = address;
        this.orderType = orderType;
        this.paymentMethod = paymentMethod;
        this.userId = userId;
        this.orderId = orderId;
        this.count = count;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getItemNames() {
        return itemNames;
    }

    public void setItemNames(String itemNames) {
        this.itemNames = itemNames;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
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

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

}
