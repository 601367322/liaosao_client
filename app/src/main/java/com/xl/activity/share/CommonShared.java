package com.xl.activity.share;

import android.content.Context;

public class CommonShared {
    private SharedDataUtil sp;
    private SharedDataUtil.SharedDataEditor editor;
    private final String SOUND = "sound";
    private final String VIBRATION = "vibration";
    private final String DISTANCE = "distance";
    private final String SEX = "sex";
    public final String LAT = "lat";
    public final String LNG = "lng";
    private final String LOCATION = "location";// 定位的城市
    private final String AREA = "area";//区域
    private final String SHOWDRAWER = "showdrawer";
    private final String MESSAGECOUNT = "messagecount";//未读消息数

    public static final int ON = 1;
    public static final int OFF = 0;

    private final String HADSETSEX = "had_set_sex";

    public CommonShared(Context context) {
        sp = SharedDataUtil.getInstance(context);
        editor = sp.getSharedDataEditor();
    }

    public void setSound(int s) {
        editor.putInt(SOUND, s);
        editor.commit();
    }

    public int getSound() {
        return sp.getInt(SOUND, ON);
    }

    public void setVibration(int s) {
        editor.putInt(VIBRATION, s);
        editor.commit();
    }

    public int getDistance() {
        return sp.getInt(DISTANCE, ON);
    }

    public void setDistance(int s) {
        editor.putInt(DISTANCE, s);
        editor.commit();
    }

    public int getVibration() {
        return sp.getInt(VIBRATION, ON);
    }

    public void setSex(int s) {
        editor.putInt(SEX, s);
        editor.commit();
    }

    public int getSex() {
        return sp.getInt(SEX, 0);
    }

    public void setHadSex(int s) {
        editor.putInt(HADSETSEX, s);
        editor.commit();
    }

    public int getHadSex() {
        return sp.getInt(HADSETSEX, OFF);
    }

    public String getLat() {
        return sp.getString(LAT, "");
    }

    public void setLat(String str) {
        editor.putString(LAT, str);
        editor.commit();
    }

    public String getLng() {
        return sp.getString(LNG, "");
    }

    public void setLng(String str) {
        editor.putString(LNG, str);
        editor.commit();
    }

    public void setLocation(String str) {
        editor.putString(LOCATION, str);
        editor.commit();
    }

    public String getLocation() {
        return sp.getString(LOCATION, "二次元世界");
    }

    public void setArea(String str) {
        editor.putString(AREA, str);
        editor.commit();
    }

    public String getArea() {
        return getLocation() + sp.getString(AREA, getLocation() + "");
    }

    public void setShowDrawer(int on) {
        editor.putInt(SHOWDRAWER, on);
        editor.commit();
    }

    public int getShowDrawer() {
        return sp.getInt(SHOWDRAWER, OFF);
    }

    public void setMessageCount(int on) {
        editor.putInt(MESSAGECOUNT, on);
        editor.commit();
    }

    public int getMessageCount() {
        return sp.getInt(MESSAGECOUNT, 0);
    }
}
