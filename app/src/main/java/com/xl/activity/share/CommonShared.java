package com.xl.activity.share;

import android.content.Context;

public class CommonShared {
    private SharedDataUtil sp;
    private SharedDataUtil.SharedDataEditor editor;
    private final String SOUND = "sound"; //开启声音
    private final String VIBRATION = "vibration";//开启震动
    private final String DISTANCE = "distance";//开启定位
    public final String LAT = "lat";
    public final String LNG = "lng";
    private final String CITY = "city";// 定位的城市
    private final String PROVINCE = "province";
    private final String SHOWDRAWER = "showdrawer";//是否自动弹出左侧菜单
    private final String MESSAGECOUNT = "messagecount";//未读消息数
    private final String FBI = "fbi";//是否第一次显示安全警告
    private final String ISVIP = "isvip";//是否是会员

    private final String VIP_ORDER = "vip_order";//充值会员的订单号

    private final String ISMANAGER = "ismanager";

    private String IsFirstStartApp = "isfirststartapp";

    public static final int ON = 1;
    public static final int OFF = 0;


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

    public void setCITY(String str) {
        editor.putString(CITY, str);
        editor.commit();
    }

    public String getCITY() {
        return sp.getString(CITY, "");
    }

    public void setPROVINCE(String str) {
        editor.putString(PROVINCE, str);
        editor.commit();
    }

    public String getPROVINCE() {
        return sp.getString(PROVINCE, "");
    }

    public void setShowDrawer(int on) {
        editor.putInt(SHOWDRAWER, on);
        editor.commit();
    }

    public int getShowDrawer() {
        return sp.getInt(SHOWDRAWER, OFF);
    }

    public void setFBI(int on) {
        editor.putInt(FBI, on);
        editor.commit();
    }

    public int getFBI() {
        return sp.getInt(FBI, ON);
    }

    public void setISVIP(int on) {
        editor.putInt(ISVIP, on);
        editor.commit();
    }

    public int getISVIP() {
        if (getISMANAGER() == ON) {
            return ON;
        }
        return sp.getInt(ISVIP, OFF);
    }


    public int getIsFirstStartApp() {
        return sp.getInt(IsFirstStartApp, ON);
    }

    public void setIsFirstStartApp(int on) {
        editor.putInt(IsFirstStartApp, on);
        editor.commit();
    }

    public int getISMANAGER() {
        return sp.getInt(ISMANAGER, OFF);
    }

    public void setISMANAGER(int on) {
        editor.putInt(ISMANAGER, on);
        editor.commit();
    }

    public void setVipOrder(String order) {
        editor.putString(VIP_ORDER, order);
        editor.commit();
    }

    public String getVipOrder() {
        return sp.getString(VIP_ORDER, null);
    }
}
