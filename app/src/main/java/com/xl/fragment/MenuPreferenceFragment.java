package com.xl.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;

import com.xl.activity.R;
import com.xl.activity.chat.ChatListActivity_;
import com.xl.activity.pay.PayActivity_;
import com.xl.activity.share.CommonShared;
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

        msgCount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(ac.cs.getISVIP()== CommonShared.ON){
                    ChatListActivity_.intent(MenuPreferenceFragment.this).start();
                }else{
                    new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.kiding)).setIcon(R.drawable.dialog_icon).setMessage("你并不是会员，哈哈哈哈哈哈！！！").setPositiveButton("成为会员", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PayActivity_.intent(MenuPreferenceFragment.this).start();
                        }
                    }).setNegativeButton("继续做屌丝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();
                }
                return false;
            }
        });
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
