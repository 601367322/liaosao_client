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
import com.xl.activity.R;
import com.xl.activity.chat.ChatActivity_;
import com.xl.activity.girl.GirlChatActivity_;
import com.xl.activity.share.CommonShared;
import com.xl.application.AppClass;
import com.xl.application.AppClass_;
import com.xl.bean.MessageBean;
import com.xl.db.BlackDao;
import com.xl.db.ChatDao;
import com.xl.db.ChatlistDao;
import com.xl.db.DBHelper;
import com.xl.util.BroadCastUtil;
import com.xl.util.LogUtil;
import com.xl.util.StaticUtil;

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
        try {
            OrmLiteSqliteOpenHelper helper = OpenHelperManager.getHelper(context, DBHelper.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startConnect() {
        try {
            JSONObject jo = new JSONObject();
            jo.put(StaticUtil.ORDER, StaticUtil.ORDER_CONNECT);
            jo.put(StaticUtil.DEVICEID, ac.deviceId);
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

        MessageBean mb = new Gson().fromJson(jo.toString(), new TypeToken<MessageBean>() {
        }.getType());

        if (BlackDao.getInstance(context).isExists(mb.getFromId()) != null) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_icon).setContentTitle(context.getString(R.string.u_have_a_new_message))
                .setTicker(context.getString(R.string.u_have_a_new_message))
                .setPriority(Notification.PRIORITY_HIGH)
                .setOnlyAlertOnce(false).setAutoCancel(true);

        builder.setContentText(getMsgContentType(mb));

        switch (mb.getMsgType()) {
            case MessageBean.IMAGE:
            case MessageBean.VOICE:
            case MessageBean.RADIO:
                mb.setLoading(MessageBean.LOADING_NODOWNLOAD);
                break;
        }

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
        PendingIntent contentIntent;
        if (mb.getFromId().equals(AppClass.MANAGER)) {
            contentIntent = PendingIntent.getActivity(context, 0,
                    GirlChatActivity_.intent(context).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).get(), PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            contentIntent = PendingIntent.getActivity(context, 0,
                    ChatActivity_.intent(context).deviceId(mb.getFromId()).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).get(), PendingIntent.FLAG_UPDATE_CURRENT);
        }
        builder.setContentIntent(contentIntent);
        builder.setPriority(Notification.PRIORITY_HIGH);
        Notification notification = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        manager.notify(mb.getFromId().hashCode(), notification);


        Intent intent = new Intent(BroadCastUtil.NEWMESSAGE);
        intent.putExtra(StaticUtil.BEAN, mb);
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
                contentText = "[文字]";
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
            case MessageBean.RADIO:
            case MessageBean.RADIO_NEW:
                contentText = "[视频]";
                break;
        }
        return contentText;
    }

}
