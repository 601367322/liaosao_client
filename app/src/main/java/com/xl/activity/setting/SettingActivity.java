package com.xl.activity.setting;

import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.fragment.SettingFragment_;

import org.androidannotations.annotations.EActivity;

/**
 * Created by sbb on 2015/1/21.
 */
@EActivity(R.layout.fragment_navigation_drawer)
public class SettingActivity extends BaseBackActivity {

    @Override
    protected void init() {
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_content, SettingFragment_.builder().build()).commitAllowingStateLoss();
    }
}
