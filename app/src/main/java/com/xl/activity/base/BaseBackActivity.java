package com.xl.activity.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.xl.activity.R;
import com.xl.util.ToastUtil;

import net.youmi.android.spot.SpotDialogListener;
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
        SpotManager.getInstance(getApplicationContext()).setSpotOrientation(
                SpotManager.ORIENTATION_LANDSCAPE);
        SpotManager.getInstance(getApplicationContext()).setAnimationType(SpotManager.ANIM_ADVANCE);
    }

    int failNums = 0;

    public void showScreenAd() {
        if (MobclickAgent.getConfigParams(this, "ad_show").equals("on")) {
            adHandler.sendEmptyMessageDelayed(1, 25 * 1000);
            adHandler.sendEmptyMessageDelayed(2, 30 * 1000);
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
                        SpotManager.getInstance(getApplicationContext()).showSpotAds(BaseBackActivity.this, new SpotDialogListener() {
                            @Override
                            public void onShowSuccess() {
                                Log.i("Youmi", "onShowSuccess");
                            }

                            @Override
                            public void onShowFailed() {
                                Log.i("Youmi", "onShowFailed");
                                if (failNums < 3) {
                                    failNums++;
                                    showScreenAdFail();
                                }
                            }

                            @Override
                            public void onSpotClosed() {
                                Log.e("sdkDemo", "closed");
                            }
                        });
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
    protected void onStop() {
        // 如果不调用此方法，则按home键的时候会出现图标无法显示的情况。
        SpotManager.getInstance(getApplicationContext()).onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        SpotManager.getInstance(getApplicationContext()).onDestroy();
        super.onDestroy();
        adHandler.removeMessages(1);
        adHandler.removeMessages(2);
    }
}
