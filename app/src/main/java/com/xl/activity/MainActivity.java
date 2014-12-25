package com.xl.activity;

import android.support.v4.widget.DrawerLayout;

import com.xl.activity.base.BaseActivity;
import com.xl.fragment.MainFragment_;
import com.xl.fragment.NavigationDrawerFragment;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks  {

    @FragmentById(R.id.navigation_drawer)
    public NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected boolean canSwipe() {
        return false;
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
    public void onNavigationDrawerItemSelected(int position) {

    }
}
