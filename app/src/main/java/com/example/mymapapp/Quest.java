package com.example.mymapapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Quest {

    public String tag;
    public boolean readed;
    public boolean selected;

    public MarkerOptions marker;

    public Quest(double lat, double lon, String tag, boolean readed, boolean selected) {
        this.tag = tag;
        this.readed = readed;
        this.selected = selected;
        marker = new MarkerOptions().position(new LatLng(lat, lon)).title(getHeading(tag));
    }

    public String getHeading(String tag) {
        switch (tag) {
            case "basketball":
                return "Сонный баскетбол";
            case "park":
                return "Старый бункер";
            case "plane":
                return "Старая площадка";
            case "skateboard":
                return "Скейтер";
            default:
                return "1";
        }
    }

    public String getTagButtonOne(String tag) {
        return tag + "one";
    }

    public String getTagButtonTwo(String tag) {
        return tag + "two";
    }

}