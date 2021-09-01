package com.techtitudetribe.barbiecornpizza;

public class MenuItemAdapter {
    String image,name,description,price,medium,large,availability,mediumCheese, largeCheese,mediumVegTopping, largeVegTopping;

    public MenuItemAdapter()
    {

    }

    public MenuItemAdapter(String image, String name, String description, String price, String medium, String large, String availability, String mediumCheese, String largeCheese, String mediumVegTopping, String largeVegTopping) {
        this.image = image;
        this.name = name;
        this.description = description;
        this.price = price;
        this.medium = medium;
        this.large = large;
        this.availability = availability;
        this.mediumCheese = mediumCheese;
        this.largeCheese = largeCheese;
        this.mediumVegTopping = mediumVegTopping;
        this.largeVegTopping = largeVegTopping;
    }

    public String getMediumVegTopping() {
        return mediumVegTopping;
    }

    public void setMediumVegTopping(String mediumVegTopping) {
        this.mediumVegTopping = mediumVegTopping;
    }

    public String getLargeVegTopping() {
        return largeVegTopping;
    }

    public void setLargeVegTopping(String largeVegTopping) {
        this.largeVegTopping = largeVegTopping;
    }

    public String getMediumCheese() {
        return mediumCheese;
    }

    public void setMediumCheese(String mediumCheese) {
        this.mediumCheese = mediumCheese;
    }

    public String getLargeCheese() {
        return largeCheese;
    }

    public void setLargeCheese(String largeCheese) {
        this.largeCheese = largeCheese;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
