package com.xl.activity.job;

import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;

import org.androidannotations.annotations.EActivity;

/**
 * Created by Shen on 2016/3/3.
 */
@EActivity(R.layout.activity_only_fragment)
public class ChaterRequestActivity extends BaseBackActivity {

    @Override
    protected void init() {
        getSupportFragmentManager().beginTransaction().replace(R.id.content, ChaterRequestFragment_.builder().build()).commitAllowingStateLoss();
    }
}
