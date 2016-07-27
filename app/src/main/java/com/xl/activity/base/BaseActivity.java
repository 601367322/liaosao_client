
package com.xl.activity.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.umeng.analytics.MobclickAgent;
import com.xl.activity.R;
import com.xl.application.AM;
import com.xl.application.AppClass;
import com.xl.db.DBHelper;
import com.xl.util.SystemBarTintManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;

@EActivity
public abstract class BaseActivity extends AppCompatActivity {
    @App
    protected AppClass ac;

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AM.getActivityManager().pushActivity(this);
        super.onCreate(savedInstanceState);

        this.mContext = this;

        if(needTranslucentStatus()) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                setTranslucentStatus(true);
                SystemBarTintManager tintManager = new SystemBarTintManager(this);
                tintManager.setStatusBarTintEnabled(true);
                //使StatusBarTintView 和 actionbar的颜色保持一致，风格统一。
                tintManager.setStatusBarTintResource(R.color.holo_blue_light);
                // 设置状态栏的文字颜色
                tintManager.setStatusBarDarkMode(false, this);
            }
        }
    }

    public boolean needTranslucentStatus(){
        return true;
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @AfterViews
    protected abstract void init();

    Toast toast;

    public void toast(String str) {
        toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AM.getActivityManager().popActivity(this);
    }

    public void closeInput() {
        if (getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

        if(getActionBarTitle()!=-1)getSupportActionBar().setTitle(getActionBarTitle());
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public OrmLiteSqliteOpenHelper getHelper() {
        return OpenHelperManager.getHelper(this, DBHelper.class);
    }

    public int getActionBarTitle(){
        return -1;
    }
}
