package com.xl.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.xl.util.Utils;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "MessageBean")
public class MessageBean implements Serializable {

    @DatabaseField(generatedId = true)
    private Integer id;
    @Expose
    @DatabaseField(index = true)
    private String msgId;
    @Expose
    @DatabaseField(index = true)
    private String userId;
    @Expose
    @DatabaseField(index = true)
    private String toId;
    @Expose
    @DatabaseField(index = true)
    private String fromId;
    @Expose
    @DatabaseField
    private String content;
    @Expose
    private int sex = -1;
    @DatabaseField
    private String time;
    @DatabaseField
    private String nickName;
    @DatabaseField
    private String userLogo;
    @DatabaseField
    private int state;//0 未读  1 已读
    @Expose
    @DatabaseField
    private int msgType;
    @DatabaseField
    private int loading = 0;//0 正在进行 1完成 -1失败

    public ImageSize imageSize;

    public static class ImageSize {
        int width;
        int height;

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public RadioBean getRadioBean(){
        return new Gson().fromJson(content,new TypeToken<RadioBean>(){}.getType());
    }

    public void setRadioBean(RadioBean bean){
        this.content = new Gson().toJson(bean);
    }

    public static class RadioBean {

        public String thumb;
        public String file;

        public RadioBean(String thumb, String file) {
            this.thumb = thumb;
            this.file = file;
        }
    }

    public static final int TEXT = 0;
    public static final int VOICE = 1;
    public static final int IMAGE = 2;
    public static final int FACE = 3;
    public static final int RADIO = 4;
    public static final int RADIO_NEW = 5;

    public static final int LOADING_NODOWNLOAD = 0;
    public static final int LOADING_DOWNLOADING = 2;
    public static final int LOADING_DOWNLOADED = 1;
    public static final int LOADING_DOWNLOADFAIL = -1;

    @Expose
    @DatabaseField
    private int voiceTime; //录音时间

    private boolean isPlaying; //正在播放

    public MessageBean() {
        super();
    }

    public MessageBean(String fromId, String toId, String content, int msgType, int sex) {
        this.fromId = fromId;
        this.toId = toId;
        this.msgType = msgType;
        this.content = content;
        this.sex = sex;
        setLoading(LOADING_DOWNLOADING);
    }

    public MessageBean(String userId, String toId,
                       String content, String nickName, String userLogo, int msgType) {
        super();
        this.msgType = msgType;
        this.msgId = new Date().getTime() + "";
        this.userId = userId;
        this.toId = toId;
        this.fromId = userId;
        this.content = content;
        this.time = Utils.dateFormat.format(new Date());
        this.nickName = nickName;
        this.userLogo = userLogo;
        this.state = 1;
        setLoading(LOADING_DOWNLOADING);
    }

    public MessageBean(String userId, String toId,
                       String content, String nickName, String userLogo, int msgType, int voiceTime, int sex) {
        super();
        this.voiceTime = voiceTime;
        this.msgType = msgType;
        this.msgId = new Date().getTime() + "";
        this.userId = userId;
        this.toId = toId;
        this.fromId = userId;
        this.content = content;
        this.time = Utils.dateFormat.format(new Date());
        this.nickName = nickName;
        this.userLogo = userLogo;
        this.state = 1;
        this.sex = sex;
        setLoading(LOADING_DOWNLOADING);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserLogo() {
        return userLogo;
    }

    public void setUserLogo(String userLogo) {
        this.userLogo = userLogo;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(int voiceTime) {
        this.voiceTime = voiceTime;
    }

    public int getLoading() {
        return loading;
    }

    public void setLoading(int loading) {
        this.loading = loading;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }
}
