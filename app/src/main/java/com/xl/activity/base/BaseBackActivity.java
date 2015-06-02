package com.xl.activity.base;

import android.util.Log;

import com.xl.activity.R;
import com.xl.util.ToastUtil;

import net.imageloader.tools.st.imbydg;
import net.imageloader.tools.st.imbzdg;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.UiThread;

@EActivity
public abstract class BaseBackActivity extends BaseActivity {

    @OptionsItem(android.R.id.home)
    public void homeClick() {
        finish();
    }

    int failNums = 0;

    public void showScreenAd() {
        showScreenAdBefore();
        showScreenAdNow();
    }

    @UiThread(delay = 25 * 1000)
    public void showScreenAdBefore() {
        if(!isFinishing()) {
            ToastUtil.toast(this,getString(R.string.go_away_ad),R.drawable.stop);
        }
    }

    @UiThread(delay = 30 * 1000)
    public void showScreenAdNow() {
        if(!isFinishing()) {
            imbzdg.isaypl(BaseBackActivity.this).iscxpl(
                    BaseBackActivity.this, new imbydg() {
                        @Override
                        public void isbqpl() {
                            Log.i("YoumiAdDemo", "展示成功");
                        }

                        @Override
                        public void isbppl() {
                            Log.i("YoumiAdDemo", "展示失败");
                            if (failNums < 3) {
                                failNums++;
                                showScreenAdFail();
                            }
                        }

                        @Override
                        public void isbrpl() {
                            Log.i("YoumiAdDemo", "展示关闭");
                        }

                    });
        }
    }

    @UiThread
    public void showScreenAdFail() {
        ToastUtil.toast(this,getString(R.string.i_will_come_back),R.drawable.weisuo);
        showScreenAd();
    }
}
