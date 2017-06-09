package com.xl.activity.setting;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.xl.activity.R;
import com.xl.activity.base.BaseActivity;

import org.androidannotations.annotations.EActivity;

import java.util.concurrent.Callable;

/**
 * Created by bingbing on 2017/6/9.
 */
@EActivity(R.layout.activity_only_fragment)
public class FeedBackActivity extends BaseActivity {

    @Override
    protected void init() {

        getSupportActionBar().hide();

        FeedbackAPI.setFeedbackFragment(new Callable() {
            @Override
            public Object call() throws Exception {
                getSupportFragmentManager().beginTransaction().replace(R.id.content, FeedbackAPI.getFeedbackFragment()).commitAllowingStateLoss();
                return null;
            }
        },null);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FeedbackAPI.cleanFeedbackFragment();
    }
}
