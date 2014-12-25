package com.xl.bean;

import java.io.Serializable;

import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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
	public MessageBean() {
		super();
		// TODO Auto-generated constructor stub
	}

    public MessageBean(String fromId, String toId, String content) {
        this.fromId = fromId;
        this.toId = toId;
        this.content = content;
    }

    public MessageBean(String msgId, String userId, String toId, String fromId,
			String content, String time, String nickName, String userLogo,
			int state) {
		super();
		this.msgId = msgId;
		this.userId = userId;
		this.toId = toId;
		this.fromId = fromId;
		this.content = content;
		this.time = time;
		this.nickName = nickName;
		this.userLogo = userLogo;
		this.state = state;
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
	
}
