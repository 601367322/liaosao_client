package com.xl.activity.base;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;

@EActivity
public abstract class BaseBackActivity extends BaseActivity{

    @OptionsItem(android.R.id.home)
    public void homeClick(){
        finish();
    }

}
