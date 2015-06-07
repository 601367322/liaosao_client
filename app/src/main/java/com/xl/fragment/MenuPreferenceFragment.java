package com.xl.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.support.v4.preference.PreferenceFragment;

import com.xl.activity.R;
import com.xl.application.AppClass;
import com.xl.db.ChatDao;
import com.xl.util.BroadCastUtil;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;

/**
 * Created by sbb on 2015/5/5.
 */
@EFragment
public class MenuPreferenceFragment extends PreferenceFragment {

    @App
    AppClass ac;


    PreferenceScreen msgCount;

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.menu_preference_fragment);

        msgCount = (PreferenceScreen) findPreference("msgCount");
    }

    @Receiver(actions = {BroadCastUtil.NEWMESSAGE, BroadCastUtil.REFRESHNEWMESSAGECOUNT})
    public void newMessage(Intent intent) {
        getMsgCount();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMsgCount();
    }

    public void getMsgCount() {
        int count = ChatDao.getInstance(getActivity()).getAllUnCount(ac.deviceId);
        msgCount.setSummary(count + "条未读消息");
    }
}
