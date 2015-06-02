package com.xl.fragment;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v4.preference.PreferenceFragment;

import com.xl.activity.R;
import com.xl.activity.share.CommonShared;
import com.xl.application.AppClass;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

/**
 * Created by sbb on 2015/1/22.
 */
@EFragment
public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

    SwitchPreference sound, vibration;//, distance;
    ListPreference sex;
    AppClass ac;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ac = (AppClass) getActivity().getApplicationContext();
        addPreferencesFromResource(R.xml.preferen_setting);
    }

    @AfterViews
    public void init(){
        sound = (SwitchPreference) findPreference("sound");
        vibration = (SwitchPreference) findPreference("vibration");
        sex = (ListPreference) findPreference("sex");
//        distance = (SwitchPreference) findPreference("distance");

        sound.setOnPreferenceChangeListener(this);
        vibration.setOnPreferenceChangeListener(this);
        sex.setOnPreferenceChangeListener(this);
//        distance.setOnPreferenceChangeListener(this);

        onPreferenceChange(sex, ac.cs.getSex());
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
        }
        else if (sex == preference) {
            sex.setSummary(getResources().getStringArray(R.array.sex_title)[Integer.valueOf(newValue.toString())]);
            ac.cs.setSex(Integer.valueOf(newValue.toString()));
        }
//        else if (distance == preference) {
//            if (((boolean) newValue)) {
//                ac.cs.setDistance(CommonShared.ON);
//            } else {
//                ac.cs.setDistance(CommonShared.OFF);
//            }
//        }
        return true;
    }
}
