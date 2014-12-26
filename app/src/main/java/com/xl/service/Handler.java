package com.xl.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xl.activity.R;
import com.xl.application.AppClass_;
import com.xl.bean.MessageBean;
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

    public Handler(Context context, Socket socket) {
        super();
        this.context = context;
        this.socket = socket;
        ac = (AppClass_) context.getApplicationContext();
        manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
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

    public void orderSendMessage(JSONObject jo) {
        MessageBean mb = new Gson().fromJson(jo.toString(), new TypeToken<MessageBean>() {
        }.getType());
        Intent intent = new Intent(BroadCastUtil.NEWMESSAGE);
        intent.putExtra("bean", mb);
        context.sendBroadcast(intent);
    }

    public void orderCloseChat(JSONObject jo){
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
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_icon).setContentTitle("您有一条新消息")
                .setOnlyAlertOnce(true)
                .setContentText("一位陌生人将与你聊天").setNumber(1).setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);
        Notification notification = null;
        if (SdkVersionHelper.getSdkInt() >= 16) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        manager.notify(0, notification);

        try {
            Intent i = new Intent(BroadCastUtil.STARTCHAT);
            i.putExtra(StaticUtil.OTHERDEVICEID, jo.getString(StaticUtil.OTHERDEVICEID));
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

}
