package com.xl.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by sbb on 2015/5/6.
 */
@DatabaseTable(tableName = "ChatListBean")
public class ChatListBean implements Serializable {

    @DatabaseField(generatedId = true)
    private Integer  id;

    @DatabaseField(unique = true)
    private String deviceId;

    @DatabaseField
    private String content;

    @DatabaseField
    private Integer sex;

    private int num;

    private UserTable_6 friend;

    public ChatListBean() {
        super();
    }

    public ChatListBean(Integer id, String deviceId, String content,Integer sex) {
        this.id = id;
        this.deviceId = deviceId;
        this.sex = sex;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public UserTable_6 getFriend() {
        return friend;
    }

    public void setFriend(UserTable_6 friend) {
        this.friend = friend;
    }
}
