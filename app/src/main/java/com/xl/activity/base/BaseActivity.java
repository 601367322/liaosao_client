package com.xl.activity.base;

import android.os.Bundle;
import android.widget.Toast;

import com.xl.application.AM;
import com.xl.application.AppClass;

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
