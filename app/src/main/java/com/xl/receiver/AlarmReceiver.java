package com.xl.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.xl.application.AppClass;
import com.xl.service.PushService;
import com.xl.service.PushService_;
import com.xl.util.BroadCastUtil;
import com.xl.util.LogUtil;
import com.xl.util.Utils;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EReceiver;

@EReceiver
public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @App
    AppClass ac;

    public void onReceive(Context context, Intent intent) {
        LogUtil.d(intent.getAction());
        try {
            //如果service没有运行
            if (!Utils.isServiceRunning(context, PushService_.class.getName())) {
                ac.startService();
            } else if (ac.mService == null) {//如果没有绑定
                ac.bindServices();
            } else if (!ac.isOnline()) {//如果断线了
                PushService.actionReConnect(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Utils.setAlarmTime(context, System.currentTimeMillis() + BroadCastUtil.CHECKCONNECT, BroadCastUtil.ACTION_CHECKCONNECT, BroadCastUtil.CHECKCONNECT);
        }
    }

    public static void actionStartConnectAlarm(Context context) {
        Utils.setAlarmTime(context, System.currentTimeMillis(), BroadCastUtil.ACTION_CHECKCONNECT, BroadCastUtil.CHECKCONNECT);
    }

    public static void actionStopConnectAlarm(Context context) {
        Utils.canalAlarm(context, BroadCastUtil.ACTION_CHECKCONNECT);
    }
}