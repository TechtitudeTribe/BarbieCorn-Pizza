package com.techtitudetribe.barbiecornpizza;

public class ToppingAdapter {
    String itemName, itemImage;

    public ToppingAdapter ()
    {

    }

    public ToppingAdapter(String itemName, String itemImage) {
        this.itemName = itemName;
        this.itemImage = itemImage;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }
}
