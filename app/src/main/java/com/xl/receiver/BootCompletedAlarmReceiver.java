package com.xl.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xl.util.LogUtil;

import org.androidannotations.annotations.EReceiver;

/**
 * 卡机时
 */
@EReceiver
public class BootCompletedAlarmReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtil.d(intent.getAction());
		AlarmReceiver.actionStartConnectAlarm(context);
	}
}
