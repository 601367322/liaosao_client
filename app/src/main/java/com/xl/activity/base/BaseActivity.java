package com.xl.activity.base;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.xl.application.AM;
import com.xl.application.AppClass;
import com.xl.util.UIUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

@EActivity
public abstract class BaseActivity extends SwipeBackActivity{
    @App
    protected AppClass ac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AM.getActivityManager().pushActivity(this);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //UIUtils.setSystemBarTintColor(this);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            UIUtils.setSystemBarTintColor(this);

        }
    }

    @AfterViews
    protected abstract void init();

    Toast toast;
    public void toast(String str){
        toast=Toast.makeText(this, str, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        AM.getActivityManager().popActivity(this);
    }
}
