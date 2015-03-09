package com.xl.activity.setting;

import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.xl.activity.R;
import com.xl.activity.share.CommonShared;
import com.xl.application.AppClass;

/**
 * Created by sbb on 2015/1/21.
 */
public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    SwitchPreference sound, vibration;//, distance;
//    ListPreference sex;

    AppClass ac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ac = (AppClass) getApplicationContext();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Toolbar bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.preferences_toolbar, root, false);
            root.addView(bar, 0); // insert at top
        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);

            root.removeAllViews();

            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.preferences_toolbar, root, false);
            root.addView(bar);

            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            } else {
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);

            root.addView(content);
        }

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addPreferencesFromResource(R.xml.preferen_setting);

        sound = (SwitchPreference) findPreference("sound");
        vibration = (SwitchPreference) findPreference("vibration");
//        sex = (ListPreference) findPreference("sex");
//        distance = (SwitchPreference) findPreference("distance");

        sound.setOnPreferenceChangeListener(this);
        vibration.setOnPreferenceChangeListener(this);
//        sex.setOnPreferenceChangeListener(this);
//        distance.setOnPreferenceChangeListener(this);

//        onPreferenceChange(sex, ac.cs.getSex());
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
//        else if (sex == preference) {
//            sex.setSummary(getResources().getStringArray(R.array.sex_title)[Integer.valueOf(newValue.toString())]);
//            ac.cs.setSex(Integer.valueOf(newValue.toString()));
//        } else if (distance == preference) {
//            if (((boolean) newValue)) {
//                ac.cs.setDistance(CommonShared.ON);
//            } else {
//                ac.cs.setDistance(CommonShared.OFF);
//            }
//        }
        return true;
    }
}
