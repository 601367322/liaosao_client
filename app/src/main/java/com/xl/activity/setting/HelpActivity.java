package com.xl.activity.setting;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.util.ToastUtil;
import com.xl.util.Utils;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;

/**
 * Created by sbb on 2015/1/13.
 */
@EActivity(R.layout.activity_help)
@OptionsMenu(R.menu.help_menu)
public class HelpActivity extends BaseBackActivity{

    @OptionsMenuItem(R.id.update)
    MenuItem updateMenu;


    @Override
    protected void init() {

    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(getString(R.string.help_and_shuoming));
    }

    @OptionsItem(R.id.update)
    void update(){
        if(Utils.isFastDoubleClick()){
            ToastUtil.toast(this, getString(R.string.jatengying), R.drawable.kiding);
            return;
        }

        //这里使用一个ImageView设置成MenuItem的ActionView，这样我们就可以使用这个ImageView显示旋转动画了
        ImageView refreshActionView = (ImageView) getLayoutInflater().inflate(R.layout.action_loading_view, null);
        updateMenu.setActionView(refreshActionView);
        final ValueAnimator animator = ObjectAnimator.ofFloat(refreshActionView, "rotation", 0f, 360f);
        animator.setRepeatCount(-1);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(1000);
        animator.start();

    }
}
