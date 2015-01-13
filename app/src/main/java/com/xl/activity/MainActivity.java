package com.xl.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.xl.activity.base.BaseActivity;
import com.xl.fragment.MainFragment_;
import com.xl.fragment.NavigationDrawerFragment;
import com.xl.util.StaticFactory;
import com.xl.util.Utils;

import net.youmi.android.AdManager;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    @FragmentById(R.id.navigation_drawer)
    public NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected boolean canSwipe() {
        return false;
    }

    @OptionsMenuItem(R.id.menu_item_share)
    MenuItem shareItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdManager.getInstance(this).init("f8e79d512282c364", "1b6279c5f1aa4dde", false);
    }

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
        String img = "ic_launcher.png";
        File f = new File(StaticFactory.APKCardPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        f = new File(f, img.hashCode() + "");
        try {
            InputStream i = getAssets().open(img);
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(i, os);
            os.close();
            i.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (f.exists()) {
            intent.setType("image/*");
            Uri uri = Uri.fromFile(f);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
        }
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "寂寞了吗？来一发吧。");
        intent.putExtra(Intent.EXTRA_TEXT, "我在"+getString(R.string.app_name)+"，要不要一起啊~");
        intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name));
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
