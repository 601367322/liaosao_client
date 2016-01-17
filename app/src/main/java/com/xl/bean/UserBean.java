package com.xl.bean;

import android.text.TextUtils;

import com.xl.util.Utils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserBean implements Serializable {

    public Integer sex;
    public String lat;
    public String lng;
    public String province;
    public String city;
    public boolean vip;
    public String nickname;
    public String logo;
    public Long birthday;
    public String desc;
    public List<ImageBean> album;
    public Account account;

    public Account getAccount() {
        if(account == null){
            return new Account();
        }
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<ImageBean> getAlbum() {
        return album;
    }

    public void setAlbum(List<ImageBean> album) {
        this.album = album;
    }

    public static final String EMPTY = "未设置";

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public String getDesc() {
        if(TextUtils.isEmpty(desc)){
            return EMPTY;
        }
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isGirl() {
        return girl;
    }

    public void setGirl(boolean girl) {
        this.girl = girl;
    }

    public boolean girl;

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNickname() {
        if(TextUtils.isEmpty(nickname)){
            return EMPTY;
        }
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getAge() {
        int age = 0;
        if (null != birthday) {
            Calendar born = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            born.setTimeInMillis(birthday);
            age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
            if (now.get(Calendar.DAY_OF_YEAR) < born.get(Calendar.DAY_OF_YEAR)) {
                age -= 1;
            }
        }
        return age;
    }

    public String getFormatBirthday(){
        if(null!=birthday){
            return Utils.dateFormat_simple.format(new Date(birthday));
        }
        return EMPTY;
    }


}
