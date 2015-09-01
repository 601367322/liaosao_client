package com.xl.activity.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.umeng.analytics.MobclickAgent;
import com.xl.activity.R;
import com.xl.activity.share.CommonShared;
import com.xl.util.ToastUtil;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.UiThread;

import a.b.c.DynamicSdkManager;

@EActivity
public abstract class BaseBackActivity extends BaseActivity {

    @OptionsItem(android.R.id.home)
    public void homeClick() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
