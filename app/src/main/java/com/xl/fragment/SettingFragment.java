package com.xl.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.xl.activity.R;

/**
 * Created by sbb on 2015/1/22.
 */
public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.getPreferenceManager().setSharedPreferencesName(PREF_NAME);

        addPreferencesFromResource(R.xml.preferen_setting);
    }
}
