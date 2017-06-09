package com.xl.game;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


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
    }

    @Click
    void restart_btn() {
        gameView.reseat();
        hide_ll.setVisibility(View.GONE);
    }


}
