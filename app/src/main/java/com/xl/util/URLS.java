package com.xl.util;

public class URLS {

    public static final String IP = "123.56.114.98";
    public static final String URL = "http://" + IP + "/";

    //        public static final String IP="172.16.24.209";
//    public static final String IP = "192.168.0.100";
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

    public static final String UPLOADUSERLOGO = URL + "user/uploadlogo" + LAST;

    public static final String PAY = URL + "user/pay" + LAST;
    /**
     * 查询VIP详情
     */
    public static final String VIPDETAIL = URL + "user/vipdetail" + LAST;
}
