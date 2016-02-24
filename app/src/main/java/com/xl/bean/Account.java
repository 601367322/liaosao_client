package com.xl.bean;

import android.text.TextUtils;

/**
 * Created by Shen on 2015/12/20.
 */
public class Account {
    private int id;
    private String deviceId;
    public String zhifubao;
    private double coin;
    private double coldCoin;

    public double getColdCoin() {
        return coldCoin;
    }

    public void setColdCoin(double coldCoin) {
        this.coldCoin = coldCoin;
    }

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

    public double getCoin() {
        return coin;
    }

    public void setCoin(Double coin) {
        this.coin = coin;
    }

}
