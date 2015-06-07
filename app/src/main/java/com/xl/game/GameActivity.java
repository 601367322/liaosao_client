package com.xl.game;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;


import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_game)
public class GameActivity extends BaseBackActivity{

    @ViewById
    GameView gameView;

    @ViewById
    RelativeLayout content;
    @ViewById
    View hide_ll,restart_btn;
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
                tv_names.setText("恭喜你获得\""+names+"\"称号,祝你早日升职加薪，当上总经理，迎娶白富美，走上人生巅峰~~~");
                hide_ll.setVisibility(View.VISIBLE);
            }
        });

        // 实例化LayoutParams(重要)
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        // 设置广告条的悬浮位置
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        // 实例化广告条
        AdView adView = new AdView(this, AdSize.FIT_SCREEN);

        // 将广告条加入到布局中
        content.addView(adView,layoutParams);

        showScreenAd();
    }

    @Click
    void restart_btn(){
        gameView.reseat();
        hide_ll.setVisibility(View.GONE);
    }

}
