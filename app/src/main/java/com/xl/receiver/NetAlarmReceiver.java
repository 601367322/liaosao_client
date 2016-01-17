package com.xl.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xl.util.LogUtil;

import org.androidannotations.annotations.EReceiver;

/**
 * 网络状态改变时
 */
@EReceiver
public class NetAlarmReceiver extends BroadcastReceiver {

    public static final String TAG = "BootCompletedAlarmReceiver";

    public void onReceive(Context context, Intent intent) {
        LogUtil.d(intent.getAction());
        AlarmReceiver.actionStartConnectAlarm(context);
    }
}