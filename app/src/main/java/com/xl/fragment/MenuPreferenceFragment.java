package com.xl.fragment;


import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;

import com.xl.activity.R;
import com.xl.application.AppClass;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;

/**
 * Created by sbb on 2015/5/5.
 */
@EFragment
public class MenuPreferenceFragment extends PreferenceFragment {

    @App
    AppClass ac;

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.menu_preference_fragment);
    }
}
