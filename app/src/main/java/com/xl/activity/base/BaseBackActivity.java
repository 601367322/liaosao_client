package com.xl.activity.base;

import android.os.Bundle;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;

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
