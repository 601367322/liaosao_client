package com.xl.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v4.preference.PreferenceFragment;

import com.xl.activity.R;
import com.xl.activity.share.CommonShared;
import com.xl.application.AppClass;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;

/**
 * Created by sbb on 2015/1/22.
 */
@EFragment
public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    SwitchPreference sound, vibration, background;//, distance;

    @App
    AppClass ac;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferen_setting);
    }

    @AfterViews
    public void init() {
        sound = (SwitchPreference) findPreference("sound");
        vibration = (SwitchPreference) findPreference("vibration");
        background = (SwitchPreference) findPreference("background");

        sound.setOnPreferenceChangeListener(this);
        vibration.setOnPreferenceChangeListener(this);
        background.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (sound == preference) {
            if (((boolean) newValue)) {
                ac.cs.setSound(CommonShared.ON);
            } else {
                ac.cs.setSound(CommonShared.OFF);
            }
        } else if (vibration == preference) {
            if (((boolean) newValue)) {
                ac.cs.setVibration(CommonShared.ON);
            } else {
                ac.cs.setVibration(CommonShared.OFF);
            }
        } else if (background == preference) {
            if (((boolean) newValue)) {
                ac.cs.setBackgroundRun(CommonShared.ON);
            } else {
                ac.cs.setBackgroundRun(CommonShared.OFF);
            }
        }
        return true;
    }
}
