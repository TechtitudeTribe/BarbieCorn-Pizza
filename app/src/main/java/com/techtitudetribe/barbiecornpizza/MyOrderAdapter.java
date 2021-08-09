package com.techtitudetribe.barbiecornpizza;

public class MyOrderAdapter {
    String itemNames, itemNumber, itemPlacedDate, itemTotalAmount;
    long count;

    public MyOrderAdapter()
    {

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
