package com.xl.game;

import android.view.View;
import android.widget.RelativeLayout;

import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;

import net.imageloader.tools.br.imakdg;
import net.imageloader.tools.br.imandg;

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

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setTitle("玩儿蛋");
    }

    @Override
    protected void init() {
        gameView.setListener(new GameView.GameViewListener() {
            @Override
            public void onFinishListener() {
                hide_ll.setVisibility(View.VISIBLE);
            }
        });

        // 实例化LayoutParams(重要)
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        // 设置广告条的悬浮位置
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        // 实例化广告条
        imandg adView = new imandg(this, imakdg.FIT_SCREEN);
        // 调用Activity的addContentView函数

        content.addView(adView,layoutParams);
    }

    @Click
    void restart_btn(){
        gameView.reseat();
        hide_ll.setVisibility(View.GONE);
    }
}
