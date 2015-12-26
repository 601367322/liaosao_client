package com.xl.bean;

import android.text.TextUtils;

/**
 * Created by Shen on 2015/12/20.
 */
public class Account {
    private int id;
    private String deviceId;
    public String zhifubao;
    public String weixin;
    private double coin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getZhifubao() {
        if (TextUtils.isEmpty(zhifubao)) {
            return UserBean.EMPTY;
        }
        return zhifubao;
    }

    public void setZhifubao(String zhifubao) {
        this.zhifubao = zhifubao;
    }

    public String getWeixin() {
        if (TextUtils.isEmpty(weixin)) {
            return UserBean.EMPTY;
        }
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }
    public double getCoin() {
        return coin;
    }

    public void setCoin(Double coin) {
        this.coin = coin;
    }

}
