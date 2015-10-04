package com.xl.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xl.activity.chat.ChatActivity_;
import com.xl.util.BroadCastUtil;
import com.xl.util.LogUtil;
import com.xl.util.StaticUtil;

import org.androidannotations.annotations.EReceiver;

@EReceiver
public class XLReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtil.d(intent.getAction());
		if(intent.getAction().equals(BroadCastUtil.STARTCHAT)){
			try {
				ChatActivity_.IntentBuilder_ builder_ = ChatActivity_.intent(context).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).deviceId(intent.getExtras().getString(StaticUtil.OTHERDEVICEID));
				builder_.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
