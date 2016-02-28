package com.xl.activity.pay;

import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;

import org.androidannotations.annotations.EActivity;

/**
 * Created by Shen on 2016/2/28.
 */
@EActivity(R.layout.activity_only_fragment)
public class PayDetailActivity extends BaseBackActivity {

    @Override
    protected void init() {
        getSupportFragmentManager().beginTransaction().replace(R.id.content,PayDetailFragment_.builder().build()).commitAllowingStateLoss();
    }
}
