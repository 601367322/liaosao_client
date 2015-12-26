package com.xl.bean;

import java.io.Serializable;

/**
 * Created by Shen on 2015/12/13.
 */
public class ImageBean implements Serializable {

    private int id;
    private String deviceId;
    private String path;
    private boolean checked;

    public ImageBean() {
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
