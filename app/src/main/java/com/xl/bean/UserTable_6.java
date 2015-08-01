package com.xl.bean;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.xl.util.Utils;

/**
 * UserTable entity. @author MyEclipse Persistence Tools
 */
@DatabaseTable(tableName = "UserTable_6")
public class UserTable_6 implements java.io.Serializable {

    public static final String DEVICEID = "deviceId";

    // Fields
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(unique = true)
    private String deviceId;
    @DatabaseField(useGetSet = true)
    private String detail;

    private UserBean_6 bean;

    public UserBean_6 getBean() {
        if (bean == null && !TextUtils.isEmpty(detail)) {
            bean = Utils.jsonToBean(detail, UserBean_6.class);
        }
        return bean;
    }

    public void setBean(UserBean_6 bean) {
        this.bean = bean;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDetail() {
        if (TextUtils.isEmpty(detail) && bean != null) {
            return new Gson().toJson(bean);
        }
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
        bean = Utils.jsonToBean(detail, UserBean_6.class);
    }

}