package com.xl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.xl.activity.base.BaseActivity;
import com.xl.activity.setting.HelpActivity_;
import com.xl.activity.setting.SettingActivity_;
import com.xl.fragment.MainFragment_;
import com.xl.fragment.NavigationDrawerFragment;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;

import a.b.c.CommonManager;
import a.b.c.DynamicSdkManager;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    @FragmentById(R.id.navigation_drawer)
    public NavigationDrawerFragment mNavigationDrawerFragment;

//    @Override
//    protected boolean canSwipe() {
//        return false;
//    }

    @OptionsMenuItem(R.id.menu_item_share)
    MenuItem shareItem;

//    GDLocation location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置开发者信息(appid, appsecret, 是否开启测试模式)
        CommonManager.getInstance(getApplicationContext()).init("f8e79d512282c364",
                "1b6279c5f1aa4dde", false);

// 根据 AndroidManifest.xml 文件中的设置进行插屏及其他无积分类型广告的预加载
        DynamicSdkManager.getInstance(getApplicationContext()).initNormalAd();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        MobclickAgent.updateOnlineConfig(this);

//        location = new GDLocation(this, this, true);
    }

    protected void init() {
//        setSwipeBackEnable(false);

        ac.startService();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new MainFragment_())
                .commit();

        UmengUpdateAgent.update(this);
        UmengUpdateAgent.setUpdateListener(null);

    }


    @OptionsItem(R.id.menu_item_share)
    void share() {
        startActivity(Intent.createChooser(getDefaultIntent(), getString(R.string.app_name)));
    }

    @OptionsItem(R.id.commit)
    void commit() {
        FeedbackAgent agent = new FeedbackAgent(this);
        agent.startFeedbackActivity();
    }

    @OptionsItem(R.id.setting)
    void setting() {
        SettingActivity_.intent(this).start();
    }

    @OptionsItem(R.id.help)
    void help() {
        HelpActivity_.intent(this).start();
    }

    private Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "寂寞了吗？来一发吧。");
        intent.putExtra(Intent.EXTRA_TEXT, "我在" + getString(R.string.app_name) + "，要不要一起啊~ http://t.cn/RZTbceg");
        intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ac.stopService();
    }

}
