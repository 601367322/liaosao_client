package com.xl.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.xl.activity.R;
import com.xl.activity.share.CommonShared;
import com.xl.application.AppClass_;
import com.xl.bean.ChatListBean;
import com.xl.bean.MessageBean;
import com.xl.db.ChatDao;
import com.xl.db.ChatlistDao;
import com.xl.db.DBHelper;
import com.xl.util.BroadCastUtil;
import com.xl.util.LogUtil;
import com.xl.util.StaticUtil;

import org.androidannotations.api.SdkVersionHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;

public class Handler {

    private Context context;
    private Socket socket;
    private AppClass_ ac;
    private NotificationManager manager;
    private Dao<ChatListBean, Integer> chatListDao;

    public Handler(Context context, Socket socket) {
        super();
        this.context = context;
        this.socket = socket;
        ac = (AppClass_) context.getApplicationContext();
        manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            OrmLiteSqliteOpenHelper helper = OpenHelperManager.getHelper(context, DBHelper.class);
            chatListDao = helper.getDao(ChatListBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startConnect() {
        try {
            JSONObject jo = new JSONObject();
            jo.put(StaticUtil.ORDER, StaticUtil.ORDER_CONNECT);
            jo.put(StaticUtil.DEVICEID, ac.getBean().getDeviceId());
            sendMessage(jo.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void messageReceived(String message) {
        try {
            JSONObject jo = new JSONObject(message);
            int order = jo.getInt(StaticUtil.ORDER);
            switch (order) {
                case StaticUtil.ORDER_CONNECT_CHAT:
                    orderConnectChat(jo);
                    break;
                case StaticUtil.ORDER_SENDMESSAGE:
                    orderSendMessage(jo);
                    break;
                case StaticUtil.ORDER_CLOSE_CHAT:
                    orderCloseChat(jo);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void orderSendMessage(JSONObject jo) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_icon).setContentTitle("您有一条新消息")
                .setOnlyAlertOnce(false).setNumber(1).setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);
        MessageBean mb = new Gson().fromJson(jo.toString(), new TypeToken<MessageBean>() {
        }.getType());

        builder.setContentText(getMsgContentType(mb));

        ChatlistDao.getInstance(context).addChatListBean(mb, mb.getFromId());
        ChatDao.getInstance(context).addMessage(ac.deviceId, mb);

        if (ac.cs.getSound() == CommonShared.ON && ac.cs.getVibration() == CommonShared.ON) {
            builder.setDefaults(Notification.DEFAULT_ALL);
        } else if (ac.cs.getSound() == CommonShared.ON && ac.cs.getVibration() == CommonShared.OFF) {
            builder.setDefaults(Notification.DEFAULT_SOUND);
        } else if (ac.cs.getSound() == CommonShared.OFF && ac.cs.getVibration() == CommonShared.ON) {
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
        }
        if (mb.getMsgType() == MessageBean.TEXT) {
            builder.setContentText(mb.getContent());
        }
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        Notification notification = null;
        if (SdkVersionHelper.getSdkInt() >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        manager.notify(0, notification);


        Intent intent = new Intent(BroadCastUtil.NEWMESSAGE);
        intent.putExtra("bean", mb);
        context.sendBroadcast(intent);
    }

    public void orderCloseChat(JSONObject jo) {
        try {
            Intent intent = new Intent(BroadCastUtil.CLOSECHAT);
            intent.putExtra(StaticUtil.DEVICEID, jo.getString(StaticUtil.DEVICEID));
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public void orderConnectChat(JSONObject jo) {
        try {
            Intent i = new Intent(BroadCastUtil.STARTCHAT);
            i.putExtra(StaticUtil.OTHERDEVICEID, jo.getString(StaticUtil.OTHERDEVICEID));
            if (jo.has(StaticUtil.SEX)) {
                i.putExtra(StaticUtil.SEX, jo.getInt(StaticUtil.SEX));
            }
            if (jo.has(StaticUtil.LAT)) {
                i.putExtra(StaticUtil.LAT, jo.getString(StaticUtil.LAT));
                i.putExtra(StaticUtil.LNG, jo.getString(StaticUtil.LNG));
            }
            context.sendBroadcast(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String str) {
        try {
            Socket s = socket;
            str += "\n";
            s.getOutputStream().write(str.getBytes());
            LogUtil.d(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getMsgContentType(MessageBean mb) {
        String contentText = "";
        switch (mb.getMsgType()) {
            case MessageBean.TEXT:
                contentText = mb.getContent();
                break;
            case MessageBean.FACE:
                contentText = "[表情]";
                break;
            case MessageBean.IMAGE:
                contentText = "[图片]";
                break;
            case MessageBean.VOICE:
                contentText = "[语音]";
                break;
        }
        return contentText;
    }

}
