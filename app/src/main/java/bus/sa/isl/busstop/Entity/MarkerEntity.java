package bus.sa.isl.busstop.Entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by alstn on 2017-07-31.
 */

public class MarkerEntity {
    private double latitude;
    private double longitude;
    private String markerName;


    public MarkerEntity(double latitude, double longitude, String markerName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.markerName = markerName;
    }




    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getMarkerName() {
        return markerName;
    }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }


}