package com.xl.util;

public class BroadCastUtil {
	public static final String STARTSERVICE="com.xl.broadcast.startservice";
	
	public static final String STARTCHAT="com.xl.broadcast.startchat";
	
	public static final String NEWMESSAGE="com.xl.broadcast.newmessage";

    public static final String CLOSECHAT="com.xl.broadcast.closechat";

    public static final String DISCONNECT="com.xl.broadcast.disconnect";

	public static final String REFRESHCHATLIST = "com.xl.broadcast.refreshchatlist";

	public static final String REFRESHNEWMESSAGECOUNT ="com.xl.broadcast.newmessagecount";

	public static final String OPENLEFTMENU ="com.xl.broadcast.openleftmenu";

	//检查socket有无连接
	public static final String ACTION_CHECKCONNECT="com.xl.broadcast.checkconnect";

	public static final int CHECKCONNECT = 15 * 1000;
}
