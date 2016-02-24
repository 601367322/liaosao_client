package com.xl.activity.job;

import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_chater_list)
public class ChaterListActivity extends BaseBackActivity {

    public static final int CreateResponseCode = 11;

    @ViewById
    FloatingActionButton fab;

    ChaterListFragment fragment;

    @Override
    protected void init() {
        getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment = ChaterListFragment_.builder().build()).commitAllowingStateLoss();
    }

    @Click
    public void fab(View view) {
        CreateChatRoomActivity_.intent(this).start();
    }


    @OnActivityResult(value = CreateResponseCode)
    public void onCreateSuccess(int result) {
        if (result == RESULT_OK && fragment != null) {
            fragment.swipeRefresh();
        }
    }
}
