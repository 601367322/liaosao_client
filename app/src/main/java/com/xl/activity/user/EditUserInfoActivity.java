package com.xl.activity.user;

import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;

import org.androidannotations.annotations.EActivity;

/**
 * Created by Shen on 2015/12/5.
 */
@EActivity(R.layout.activity_only_fragment)
public class EditUserInfoActivity extends BaseBackActivity {

    @Override
    protected void init() {
        getSupportFragmentManager().beginTransaction().replace(R.id.content,EditUserInfoFragment_.builder().build()).commitAllowingStateLoss();
    }
}
