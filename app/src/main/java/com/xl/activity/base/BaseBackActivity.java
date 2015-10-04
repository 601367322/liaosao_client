package com.xl.activity.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.umeng.onlineconfig.OnlineConfigAgent;
import com.xl.activity.R;
import com.xl.activity.share.CommonShared;
import com.xl.util.ToastUtil;

import net.youmi.android.spot.SpotManager;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.UiThread;

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
        if (OnlineConfigAgent.getInstance().getConfigParams(this, "ad_show").equals("on")) {
            if (ac.cs.getISVIP() == CommonShared.OFF) {
                SpotManager.getInstance(getApplicationContext()).loadSpotAds();
                SpotManager.getInstance(getApplicationContext()).setSpotOrientation(SpotManager.ORIENTATION_PORTRAIT);
                SpotManager.getInstance(getApplicationContext()).setAnimationType(SpotManager.ANIM_ADVANCE);
                adHandler.sendEmptyMessageDelayed(1, 15 * 1000);
                adHandler.sendEmptyMessageDelayed(2, 20 * 1000);
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
                        SpotManager.getInstance(getApplicationContext()).showSpotAds(BaseBackActivity.this);
//                        DynamicSdkManager.getInstance(getApplicationContext()).showSpot(BaseBackActivity.this);
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
//        DynamicSdkManager.getInstance(getApplicationContext()).onDestroy(this);
        super.onDestroy();
        SpotManager.getInstance(getApplicationContext()).onDestroy();
        adHandler.removeMessages(1);
        adHandler.removeMessages(2);
    }
}
