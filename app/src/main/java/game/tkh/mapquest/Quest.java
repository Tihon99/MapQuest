package game.tkh.mapquest;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Quest {

    public String tag;
    public boolean readed;
    public boolean selected;
    public MarkerOptions marker;
    double lat,  lon;
    public int position;

    public Quest(double lat, double lon, String tag, boolean readed, boolean selected, int position) {
        this.tag = tag;
        this.readed = readed;
        this.selected = selected;
        this.lat = lat;
        this.lon = lon;
        this.position = position;
        marker = new MarkerOptions().position(new LatLng(lat, lon)).title(getHeading(tag));
    }

    public String getHeading(String tag) {
        switch (tag.replaceAll("[^A-Za-z]","")) {
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