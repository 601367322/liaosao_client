package com.xl.activity.job;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.custom.swipe.SwipeRefreshLayout;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_chater_list)
public class ChaterListActivity extends BaseBackActivity {

    @ViewById
    FloatingActionButton fab;

    @Override
    protected void init() {
        getSupportFragmentManager().beginTransaction().replace(R.id.content, ChaterListFragment_.builder().build()).commitAllowingStateLoss();
    }

    @Click
    public void fab(View view) {
        CreateChatRoomActivity_.intent(this).start();
    }

}
