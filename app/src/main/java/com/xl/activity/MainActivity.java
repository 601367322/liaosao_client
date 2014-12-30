package com.xl.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.xl.activity.base.BaseActivity;
import com.xl.fragment.MainFragment_;
import com.xl.fragment.NavigationDrawerFragment;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks  {

    @FragmentById(R.id.navigation_drawer)
    public NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected boolean canSwipe() {
        return false;
    }

    @OptionsMenuItem(R.id.menu_item_share)
    MenuItem shareItem;

    protected void init() {
        setSwipeBackEnable(false);

        ac.startService();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new MainFragment_())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ShareActionProvider mShareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(shareItem);
        mShareActionProvider.setShareIntent(getDefaultIntent());
        return super.onCreateOptionsMenu(menu);
    }

    private Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        Uri uri = Uri.fromFile(getFileStreamPath("Share.png"));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "寂寞了吗？来一发吧。");
        intent.putExtra(Intent.EXTRA_TEXT, "我在聊骚，要不要一起啊~");
        intent.putExtra(Intent.EXTRA_TITLE, "聊骚");
        return intent;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }
}
