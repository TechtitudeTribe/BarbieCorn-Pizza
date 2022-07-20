package com.techtitudetribe.barbiecornpizza;

public class AboutUsAdapter {
    String appIcon;
    long count;

    public AboutUsAdapter() {

    }

    public AboutUsAdapter(String appIcon, long count) {
        this.appIcon = appIcon;
        this.count = count;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

}
