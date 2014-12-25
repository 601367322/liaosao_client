package com.xl.receiver;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xl.application.AppClass;

@EReceiver
public class BootCompletedAlarmReceiver extends BroadcastReceiver{

	@App
	AppClass ac;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		ac.startService();
	}
}
