package com.xl.activity.user;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xl.activity.R;
import com.xl.application.AppClass;
import com.xl.bean.UserBean_6;
import com.xl.bean.UserTable_6;
import com.xl.db.DBHelper;
import com.xl.util.StaticFactory;
import com.xl.util.Utils;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;

import java.util.Calendar;

/**
 * Created by Shen on 2015/12/5.
 */
@EFragment
public class EditUserInfoFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    Preference avatar, album, nickname, gender, age, area, desc, coin, zhifubao, weixin;

    @App
    AppClass ac;

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.edit_user_info);

        UserTable_6 user = DBHelper.userDao.getUserTableByDeviceId(ac.deviceId);

        avatar = findPreference(getString(R.string.preference_user_avatar));
        album = findPreference(getString(R.string.preference_user_avatar));
        nickname = findPreference(getString(R.string.preference_user_name));
        gender = findPreference(getString(R.string.preference_user_gender));
        age = findPreference(getString(R.string.preference_user_age));
        area = findPreference(getString(R.string.preference_user_area));
        desc = findPreference(getString(R.string.preference_user_description));
        coin = findPreference(getString(R.string.preference_user_coin));
        zhifubao = findPreference(getString(R.string.preference_user_zhifubao));
        weixin = findPreference(getString(R.string.preference_user_weixin));

        nickname.setOnPreferenceChangeListener(this);
        desc.setOnPreferenceChangeListener(this);
        zhifubao.setOnPreferenceChangeListener(this);
        weixin.setOnPreferenceChangeListener(this);

        if (user != null) {
            UserBean_6 bean = user.getBean();
            nickname.setSummary(bean.getNickname());
            gender.setSummary(bean.getSex() == 1 ? getString(R.string.girl) : getString(R.string.boy));
            area.setSummary(bean.getProvince() + "\t" + bean.getCity());
            age.setSummary(bean.getFormatBirthday());

            ImageLoader.getInstance().loadImage(bean.logo + StaticFactory._160x160, Utils.options_default_logo, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    avatar.setIcon(new BitmapDrawable(getResources(), loadedImage));
                }
            });
        }

        age.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Calendar calendar = Calendar.getInstance();

                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        Calendar selectCalender = Calendar.getInstance();
                        selectCalender.set(Calendar.YEAR, year);
                        selectCalender.set(Calendar.MONTH, month);
                        selectCalender.set(Calendar.DAY_OF_MONTH, day);

                        age.setSummary(Utils.dateFormat_simple.format(selectCalender.getTime()));

                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                try {
                    DatePicker picker = dialog.getDatePicker();
                    picker.setMaxDate(calendar.getTimeInMillis());

                    Calendar minCalender = Calendar.getInstance();
                    minCalender.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 80);
                    picker.setMinDate(minCalender.getTimeInMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                dialog.show();
                return false;
            }
        });
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        preference.setSummary(newValue.toString());
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        if (v != null) {
            ListView lv = (ListView) v.findViewById(android.R.id.list);
            lv.setPadding(0, 0, 0, 0);
        }
        return v;
    }
}
