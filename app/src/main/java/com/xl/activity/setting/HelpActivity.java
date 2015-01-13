package com.xl.activity.setting;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
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

    SuperToast enoughToast,updateToast;
    @OptionsItem(R.id.update)
    void update(){
        if(Utils.isFastDoubleClick()){
            enoughToast = new SuperToast(HelpActivity.this);
            enoughToast.setDuration(SuperToast.Duration.LONG);
            enoughToast.setText("まるで加藤鹰手速よ！\n够了！");
            enoughToast.setIcon(R.drawable.kiding, SuperToast.IconPosition.LEFT);
            enoughToast.show();
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

        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                View view = updateMenu.getActionView();
                if (view != null) {
                    animator.end();
                    updateMenu.setActionView(null);
                }
                if (updateResponse != null && !updateResponse.hasUpdate) {
                    updateToast = new SuperToast(HelpActivity.this);
                    updateToast.setDuration(SuperToast.Duration.LONG);
                    updateToast.setText("もう最新バージョンました！\n已经是最新版了！");
                    updateToast.setIcon(R.drawable.last_version, SuperToast.IconPosition.LEFT);
                    updateToast.show();
                }
            }
        });
        UmengUpdateAgent.update(this);
    }
}
