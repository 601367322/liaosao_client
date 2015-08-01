package com.xl.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.xl.application.AppClass;
import com.xl.util.Utils;

import java.text.DecimalFormat;

public class GDLocation implements AMapLocationListener {

    private ILocationImpl locationListener;

    public void setLocationListener(ILocationImpl locationListener) {
        this.locationListener = locationListener;
    }

    private LocationManagerProxy mAMapLocationManager;
    private AppClass ac;
    private Context context;

    public GDLocation(Context context, ILocationImpl listener, boolean autoStart) {
        this.ac = (AppClass) context.getApplicationContext();
        this.context = context;
        this.locationListener = listener;
        if (autoStart)
            startLocation();
    }

    public void startLocation() {
        if (Utils.isFastLocation()) {
            if (locationListener != null) {
                locationListener.onLocationSuccess();
            }
            return;
        } else {
            mAMapLocationManager = LocationManagerProxy.getInstance(context);
            mAMapLocationManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, -1, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        deactivate();
        if (aLocation == null
                || aLocation.getAMapException().getErrorCode() != 0) {
            if (locationListener != null)
                locationListener.onLocationFail();
            return;
        }
        DecimalFormat df = new DecimalFormat("#.##########");
        if (!df.format(aLocation.getLatitude()).equals("0"))
            ac.cs.setLat(df.format(aLocation.getLatitude()));
        if (!df.format(aLocation.getLongitude()).equals("0"))
            ac.cs.setLng(df.format(aLocation.getLongitude()));
        if (aLocation.getProvince() != null)
            ac.cs.setCITY(aLocation.getProvince());
        if (aLocation.getCity() != null)
            ac.cs.setPROVINCE(aLocation.getCity());
        Utils.locationTime = System.currentTimeMillis();
        if (locationListener != null)
            locationListener.onLocationSuccess();

    }

    public void deactivate() {
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;
    }

    public void destory() {
        deactivate();
    }
}
