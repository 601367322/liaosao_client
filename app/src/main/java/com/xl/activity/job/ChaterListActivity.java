package com.xl.activity.job;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_chater_list)
public class ChaterListActivity extends BaseBackActivity {

    @ViewById
    Toolbar toolbar;
    @ViewById
    FloatingActionButton fab;

    @Override
    protected void init() {
        setSupportActionBar(toolbar);
    }

    @Click
    public void fab(View view){

    }
}
