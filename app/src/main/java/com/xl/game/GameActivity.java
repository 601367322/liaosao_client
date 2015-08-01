package com.xl.game;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import a.b.c.DynamicSdkManager;


@EActivity(R.layout.activity_game)
public class GameActivity extends BaseBackActivity {

    @ViewById
    GameView gameView;

    @ViewById
    RelativeLayout content;
    @ViewById
    View hide_ll, restart_btn;
    @ViewById
    TextView tv_names;

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setTitle("玩儿蛋");
    }

    @Override
    protected void init() {
        gameView.setListener(new GameView.GameViewListener() {
            @Override
            public void onFinishListener(String names) {
                tv_names.setText("恭喜你获得\"" + names + "\"称号,祝你早日升职加薪，当上总经理，迎娶白富美，走上人生巅峰~~~");
                hide_ll.setVisibility(View.VISIBLE);
            }
        });
        if (MobclickAgent.getConfigParams(this, "ad_show").equals("on")) {
            // 实例化LayoutParams(重要)
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            // 设置广告条的悬浮位置
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            View banner = DynamicSdkManager.getInstance(ac).getBanner(this);
            if(banner!=null){
                content.addView(banner,layoutParams);
            }
        }
        showScreenAd();
    }

    @Click
    void restart_btn() {
        gameView.reseat();
        hide_ll.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {
        // 如果有需要，可以点击后退关闭插播广告。
        if (!DynamicSdkManager.getInstance(getApplicationContext()).disMiss(this)) {
            // 弹出退出窗口，可以使用自定义退屏弹出和回退动画,参照demo,若不使用动画，传入-1
            super.onBackPressed();
        }
    }
}
