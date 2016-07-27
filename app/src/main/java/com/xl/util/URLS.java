package com.xl.util;

public class URLS {

    public static final String IP = "123.56.114.98";
    public static final String URL = "http://" + IP + "/";

    //        public static final String IP="172.16.24.209";
//    public static final String IP = "192.168.1.101";
//    public static final String URL = "http://" + IP + ":8080/";

    public static final int PORT = 8181;
    public static final String LAST = ".do";

    public static final String SENDMESSAGE = URL + "b/sendmessage" + LAST;
    public static final String JOINQUEUE = URL + "b/joinqueue" + LAST;
    public static final String JOINQUEUEVIP = URL + "b/joinqueuevip" + LAST;
    public static final String EXITQUEUE = URL + "b/exitqueue" + LAST;
    public static final String CLOSECHAT = URL + "b/closechat" + LAST;
    public static final String UPLOADVOICEFILE = URL + "b/uploadfile" + LAST;
    public static final String DOWNLOADFILE = URL + "b/download/";
    public static final String GETUNLINEMESSAGE = URL + "b/getallmessage" + LAST;
    public static final String ISVIP = URL + "b/isvip" + LAST;
    public static final String SETUSERDETAIL = URL + "b/setuserdetail" + LAST;

    public static final String GETUSERINFO = URL + "a/getuserinfo" + LAST;

    public static final String SETVIP = URL + "b/setvip" + LAST;

    public static final String CONNECT_GROUP = URL + "group/groupchat" + LAST;

    /**
     * 上传头像
     */
    public static final String UPLOADUSERLOGO = URL + "user/uploadlogo" + LAST;
    /**
     * 上传相册
     */
    public static final String UPLOADUSERALBUM = URL + "user/uploadalbum" + LAST;

    /**
     * 冲会员
     */
    public static final String PAY = URL + "user/pay" + LAST;
    /**
     * 冲钱
     */
    public static final String PAY_MONEY = URL + "user/paymoney" + LAST;
    /**
     * 查询VIP详情
     */
    public static final String VIPDETAIL = URL + "user/vipdetail" + LAST;
    /**
     * 删除相册
     */
    public static final String DELETEALBUM = URL + "user/deletealbum" + LAST;
    /**
     * 设置账户
     */
    public static final String UPDATEACCOUNT = URL + "account/setaccount" + LAST;
    /**
     * 获取账户
     */
    public static final String GETACCOUNT = URL + "account/getaccount" + LAST;
    /**
     * 创建聊天室
     */
    public static final String CREATE_CHAT_ROOM = URL + "chat/createroom" + LAST;
    /**
     * 获取聊天室列表
     */
    public static final String GET_CHAT_ROOM_LIST = URL + "chat/roomlist" + LAST;
    /**
     * 聊天请求列表
     */
    public static final String GET_CHAT_REQUEST_LIST = URL + "chat/getchatrequestlist" + LAST;
    /**
     * 购买请求
     */
    public static final String SEND_CHAT_REQUEST = URL + "chat/sendchatrequest" + LAST;
    /**
     * 删除聊天室
     */
    public static final String DELETE_CHAT_ROOM = URL + "chat/deleteroom" + LAST;
    /**
     * 交易详情列表
     */
    public static final String GET_MY_PAY_DETAIL_LIST = URL + "user/paydetaillist" + LAST;
    /**
     * 提现
     */
    public static final String PAY_TIXIAN = URL + "user/paytixian" + LAST;
}
