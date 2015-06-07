package com.xl.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Shen on 2015/6/7.
 */
@DatabaseTable(tableName = "BlackUser")
public class BlackUser implements Serializable {

    @DatabaseField(generatedId = true)
    public Integer id;

    @DatabaseField(unique = true)
    public String deviceId;

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
}
