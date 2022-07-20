package com.techtitudetribe.barbiecornpizza;

public class ProfileAdapter {
    String Name, Email, ContactNumber, Address;

    public ProfileAdapter()
    {

    }

    public ProfileAdapter(String name, String email, String contactNumber, String address) {
        Name = name;
        Email = email;
        ContactNumber = contactNumber;
        Address = address;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }
}
