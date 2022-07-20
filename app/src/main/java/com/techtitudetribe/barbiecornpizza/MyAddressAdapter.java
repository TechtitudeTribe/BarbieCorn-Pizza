package com.techtitudetribe.barbiecornpizza;

public class MyAddressAdapter {
    String address;
    long count;

    public MyAddressAdapter()
    {

    }

    public MyAddressAdapter(String address, long count) {
        this.address = address;
        this.count = count;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
