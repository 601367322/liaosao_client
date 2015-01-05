package com.xl.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.xl.util.Utils;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName="MessageBean")
public class MessageBean implements Serializable{

	@DatabaseField(id=true)
	private Integer id;
	@DatabaseField(index=true)
	private String msgId;
	@DatabaseField(index=true)
	private String userId;
	@DatabaseField(index=true)
	private String toId;
	@DatabaseField(index=true)
	private String fromId;
	@DatabaseField
	private String content;
	@DatabaseField
	private String time;
	@DatabaseField
	private String nickName;
	@DatabaseField
	private String userLogo;
	@DatabaseField
	private Integer state;//0 未读  1 已读
    @DatabaseField
    private int msgType;
    @DatabaseField
    private int loading = 0;//0 正在进行 1完成 -1失败
    @DatabaseField
    private int voiceTime; //录音时间
    private boolean isPlaying; //正在播放

	public MessageBean() {
		super();
		// TODO Auto-generated constructor stub
	}

    public MessageBean(String fromId, String toId, String content) {
        this.fromId = fromId;
        this.toId = toId;
        this.content = content;
    }

    public MessageBean(String userId, String toId,
			String content, String nickName, String userLogo,int msgType) {
		super();
        this.msgType=msgType;
		this.msgId = new Date().getTime()+"";
		this.userId = userId;
		this.toId = toId;
		this.fromId = userId;
		this.content = content;
		this.time = Utils.dateFormat.format(new Date());
		this.nickName = nickName;
		this.userLogo = userLogo;
		this.state = 1;
	}

    public MessageBean(String userId, String toId,
                       String content, String nickName, String userLogo,int msgType,int voiceTime) {
        super();
        this.voiceTime=voiceTime;
        this.msgType=msgType;
        this.msgId = new Date().getTime()+"";
        this.userId = userId;
        this.toId = toId;
        this.fromId = userId;
        this.content = content;
        this.time = Utils.dateFormat.format(new Date());
        this.nickName = nickName;
        this.userLogo = userLogo;
        this.state = 1;
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
