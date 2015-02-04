package com.xl.activity.base;

import android.util.Log;

import com.github.johnpersano.supertoasts.SuperToast;
import com.xl.activity.R;

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

    private boolean isPause = false;

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean isPause) {
        this.isPause = isPause;
    }

    @Override
    public void onPause() {
        super.onPause();
        setPause(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isScreenMustShow && isPause()) {
            setPause(false);
            showScreenAd();
        }
    }

    boolean isScreenMustShow = false;
    int failNums = 0;

    public void showScreenAd() {
        isScreenMustShow = true;
        showScreenAdBefore();
        showScreenAdNow();
    }

    @UiThread(delay = 25 * 1000)
    public void showScreenAdBefore() {
        if (!isPause()) {
            SuperToast superToast = new SuperToast(this);
            superToast.setText("闪开！我要弹广告啦！！！");
            superToast.setDuration(SuperToast.Duration.LONG);
            superToast.setIcon(R.drawable.stop, SuperToast.IconPosition.LEFT);
            superToast.show();
        }
    }

    @UiThread(delay = 30 * 1000)
    public void showScreenAdNow() {
        if (!isPause()) {
            imbzdg.isaypl(BaseBackActivity.this).iscxpl(
                    BaseBackActivity.this, new imbydg() {
                        @Override
                        public void isbqpl() {
                            Log.i("YoumiAdDemo", "展示成功");
                            isScreenMustShow = true;
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
        SuperToast superToast = new SuperToast(this);
        superToast.setText("广告弹出失败。。。我还会再来的。。");
        superToast.setDuration(SuperToast.Duration.LONG);
        superToast.setIcon(R.drawable.weisuo, SuperToast.IconPosition.LEFT);
        superToast.show();
        showScreenAd();
    }
}
