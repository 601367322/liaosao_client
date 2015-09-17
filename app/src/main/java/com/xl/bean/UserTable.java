package com.xl.bean;

import android.text.TextUtils;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.xl.util.Utils;

/**
 * UserTable entity. @author MyEclipse Persistence Tools
 */
@Deprecated
@DatabaseTable(tableName = "UserTable")
public class UserTable implements java.io.Serializable {

    public static final String DEVICEID = "deviceId";

    // Fields
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(unique = true)
    private String deviceId;

    private String detail;

    @DatabaseField(dataType = DataType.SERIALIZABLE, useGetSet = true)
    private UserBean bean;

    public UserBean getBean() {
        if (bean == null && !TextUtils.isEmpty(detail)) {
            bean = Utils.jsonToBean(detail, UserBean.class);
        }
        return bean;
    }

    public void setBean(UserBean bean) {
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
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
        bean = Utils.jsonToBean(detail, UserBean.class);
    }

}