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

    int failNums = 0;

    public void showScreenAd() {
        if (MobclickAgent.getConfigParams(this, "ad_show").equals("on")) {
            if(ac.cs.getISVIP() == CommonShared.OFF) {
                adHandler.sendEmptyMessageDelayed(1, 25 * 1000);
                adHandler.sendEmptyMessageDelayed(2, 30 * 1000);
            }
        }
    }

    Handler adHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (!isFinishing()) {
                        ToastUtil.toast(BaseBackActivity.this, getString(R.string.go_away_ad), R.drawable.stop);
                    }
                    break;
                case 2:
                    if (!isFinishing()) {
                        DynamicSdkManager.getInstance(getApplicationContext()).showSpot(BaseBackActivity.this);
                    }
                    break;
            }
            super.dispatchMessage(msg);
        }
    };

    @UiThread
    public void showScreenAdFail() {
        ToastUtil.toast(this, getString(R.string.i_will_come_back), R.drawable.weisuo);
        showScreenAd();
    }

    @Override
    protected void onDestroy() {
        DynamicSdkManager.getInstance(getApplicationContext()).onDestroy(this);
        super.onDestroy();
        adHandler.removeMessages(1);
        adHandler.removeMessages(2);
    }
}
