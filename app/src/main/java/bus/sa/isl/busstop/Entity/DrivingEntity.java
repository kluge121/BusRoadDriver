package bus.sa.isl.busstop.Entity;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by alstn on 2017-08-07.
 */

public class DrivingEntity implements Serializable {

    private String strBusStopName;
    private int IsLocation;
    private int IsReserve;
    private int itemType;
    private double latitude;
    private double longitude;

    public DrivingEntity(String strBusStopName, int bIsLocation, int bIsReserve, int itemType) {
        this.strBusStopName = strBusStopName;
        this.IsLocation = bIsLocation;
        this.IsReserve = bIsReserve;
        this.itemType = itemType;
    }

    public DrivingEntity(String strBusStopName, int bIsLocation, int bIsReserve, int itemType, double latitude, double longitude) {
        this.strBusStopName = strBusStopName;
        this.IsLocation = bIsLocation;
        this.IsReserve = bIsReserve;
        this.itemType = itemType;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getItemType() {

        return itemType;
    }

    public void setStrBusStopName(String strBusStopName) {
        this.strBusStopName = strBusStopName;
    }

    public void setIsLocation(int isLocation) {
        IsLocation = isLocation;
    }

    public void setIsReserve(int isReserve) {
        IsReserve = isReserve;
    }

    public int getIsLocation() {

        return IsLocation;
    }

    public int getIsReserve() {
        return IsReserve;
    }

    public String getStrBusStopName() {
        return strBusStopName;
    }


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {

        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


    public LatLng makeLatLng() {
        return new LatLng(getLatitude(), getLongitude());
    }


}
