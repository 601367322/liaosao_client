package com.xl.activity.user;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;

import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xl.activity.R;
import com.xl.activity.chat.ChatActivity;
import com.xl.application.AppClass;
import com.xl.bean.Account;
import com.xl.bean.UserBean;
import com.xl.bean.UserTable;
import com.xl.db.DBHelper;
import com.xl.post.GetUserAccount;
import com.xl.post.GetUserInfo;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.StaticFactory;
import com.xl.util.ToastUtil;
import com.xl.util.URLS;
import com.xl.util.Utils;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Shen on 2015/12/5.
 */
@EFragment
public class EditUserInfoFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    Preference avatar, album, gender, age, area, coin;
    EditTextPreference nickname, desc, zhifubao, weixin;
    @App
    AppClass ac;

    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.edit_user_info);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.edit_user_info, false);

        avatar = findPreference(getString(R.string.preference_user_avatar));
        album = findPreference(getString(R.string.preference_user_avatar));
        nickname = (EditTextPreference) findPreference(getString(R.string.preference_user_name));
        gender = findPreference(getString(R.string.preference_user_gender));
        age = findPreference(getString(R.string.preference_user_age));
        area = findPreference(getString(R.string.preference_user_area));
        desc = (EditTextPreference) findPreference(getString(R.string.preference_user_description));
        coin = findPreference(getString(R.string.preference_user_coin));
        zhifubao = (EditTextPreference) findPreference(getString(R.string.preference_user_zhifubao));
        weixin = (EditTextPreference) findPreference(getString(R.string.preference_user_weixin));

        nickname.setOnPreferenceChangeListener(this);
        desc.setOnPreferenceChangeListener(this);
        zhifubao.setOnPreferenceChangeListener(this);
        weixin.setOnPreferenceChangeListener(this);
        avatar.setOnPreferenceClickListener(this);
        age.setOnPreferenceClickListener(this);

        refreshUI();

        getUserInfo();
    }

    public void getUserInfo() {
        new GetUserInfo(getActivity(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccessCode(JSONObject jo) throws Exception {
                refreshUI();
            }
        });
        new GetUserAccount(getActivity(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccessCode(JSONObject jo) throws Exception {
                super.onSuccessCode(jo);
                refreshAccountUI(Utils.jsonToBean(jo.getString(ResultCode.CONTENT), Account.class));
            }
        });
    }

    public void refreshUI() {
        UserTable user = DBHelper.userDao.getUserTableByDeviceId(ac.deviceId);
        if (user != null) {
            UserBean bean = user.getBean();
            nickname.setSummary(bean.getNickname());
            nickname.setText(bean.nickname);
            gender.setSummary(bean.getSex() == 1 ? getString(R.string.girl) : getString(R.string.boy));
            area.setSummary(bean.getProvince() + "\t" + bean.getCity());
            age.setSummary(bean.getFormatBirthday());
            desc.setSummary(bean.getDesc());
            desc.setText(bean.desc);
            //头像
            setLogo(bean.logo);
        }
    }

    public void refreshAccountUI(Account account) {
        weixin.setSummary(account.getWeixin());
        weixin.setText(account.weixin);
        zhifubao.setSummary(account.getZhifubao());
        zhifubao.setText(account.zhifubao);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        preference.setSummary(newValue.toString());
        if (preference instanceof EditTextPreference) {
            ((EditTextPreference) preference).setText(newValue.toString());
        }
        UserBean user = new UserBean();
        if (preference.getKey().equals(getString(R.string.preference_user_name))) {
            user.nickname = newValue.toString();
        }
        if (preference.getKey().equals(getString(R.string.preference_user_description))) {
            user.desc = newValue.toString();
        }
        Account account = null;
        if (preference.getKey().equals(getString(R.string.preference_user_zhifubao))) {
            account = new Account();
            account.zhifubao = newValue.toString();
        }
        if (preference.getKey().equals(getString(R.string.preference_user_weixin))) {
            account = new Account();
            account.weixin = newValue.toString();
        }
        if (account != null) {
            updateUserAccount(account);
        } else {
            updateUserInfo(user);
        }
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

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.preference_user_age))) {
            onAgeClick();
        } else if (preference.getKey().equals(getString(R.string.preference_user_avatar))) {
            chosePic();
        }
        return false;
    }

    public void onAgeClick() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {

                Calendar selectCalender = Calendar.getInstance();
                selectCalender.set(Calendar.YEAR, year);
                selectCalender.set(Calendar.MONTH, month);
                selectCalender.set(Calendar.DAY_OF_MONTH, day);

                age.setSummary(Utils.dateFormat_simple.format(selectCalender.getTime()));

                UserBean user = new UserBean();
                user.birthday = selectCalender.getTimeInMillis();
                updateUserInfo(user);

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
    }

    public void updateUserInfo(UserBean updateUser) {
        if (updateUser != null) {
            RequestParams params = ac.getRequestParams();
            if (!TextUtils.isEmpty(updateUser.nickname)) {
                params.put("nickname", updateUser.nickname);
            }
            if (updateUser.birthday != null) {
                params.put("birthday", updateUser.birthday);
            }
            if (!TextUtils.isEmpty(updateUser.desc)) {
                params.put("desc", updateUser.desc);
            }
            ac.httpClient.post(URLS.SETUSERDETAIL, params, new JsonHttpResponseHandler(getActivity(), getString(R.string.loading)) {

                @Override
                public void onSuccessCode(JSONObject jo) throws Exception {
                    super.onSuccessCode(jo);
                    UserTable ut = Utils.jsonToBean(jo.getString(ResultCode.CONTENT), UserTable.class);
                    DBHelper.userDao.updateUser(ut);
                    ToastUtil.toast(getActivity(), getString(R.string.edit_success));
                }
            });
        }
    }

    /**
     * 更新用户账户信息
     *
     * @param updateAccount
     */
    public void updateUserAccount(Account updateAccount) {
        if (updateAccount != null) {
            RequestParams params = ac.getRequestParams();
            if (!TextUtils.isEmpty(updateAccount.zhifubao)) {
                params.put("zhifubao", updateAccount.zhifubao);
            }
            if (!TextUtils.isEmpty(updateAccount.weixin)) {
                params.put("weixin", updateAccount.weixin);
            }

            ac.httpClient.post(URLS.UPDATEACCOUNT, params, new JsonHttpResponseHandler(getActivity(), getString(R.string.loading)) {

                @Override
                public void onSuccessCode(JSONObject jo) throws Exception {
                    super.onSuccessCode(jo);
                    ToastUtil.toast(getActivity(), getString(R.string.edit_success));
                }
            });
        }
    }

    String filename;

    public void chosePic() {
        new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.dont_chose_fail)).setIcon(R.drawable.weisuo_yuan).setItems(new String[]{getString(R.string.Camera), getString(R.string.Album)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (Utils.existSDcard()) {
                            Intent intent = new Intent(); // 调用照相机
                            String messagepath = StaticFactory.APKCardPathLOGO;
                            File fa = new File(messagepath);
                            if (!fa.exists()) {
                                fa.mkdirs();
                            }
                            filename = messagepath + new Date().getTime();// 图片路径
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(new File(filename)));
                            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, ChatActivity.Camera);
                        } else {
                            ToastUtil.toast(getActivity(), getString(R.string.please_check_sdcard), R.drawable.kiding);
                        }
                        break;
                    case 1:
                        if (Utils.existSDcard()) {
                            Intent intent = new Intent();
                            String messagepath = StaticFactory.APKCardPathLOGO;
                            File fa = new File(messagepath);
                            if (!fa.exists()) {
                                fa.mkdirs();
                            }
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, ChatActivity.Album);
                        } else {
                            ToastUtil.toast(getActivity(), getString(R.string.please_check_sdcard), R.drawable.kiding);
                        }
                        break;
                }
            }
        }).create().show();
    }

    @OnActivityResult(value = ChatActivity.Camera)
    @Background
    void onCameraResult() {
        if (filename != null) {
            File fi = new File(filename);
            if (fi != null && fi.exists()) {
                Utils.downsize(filename, filename, getActivity());
            }
            updateLogo();
            fi = null;
        }
    }

    @OnActivityResult(value = ChatActivity.Album)
    @Background
    void onAlbumResult(Intent intent) {
        if (intent == null) {
            return;
        }
        ContentResolver resolver = getActivity().getContentResolver();
        Uri imgUri = intent.getData();
        try {
            Cursor cursor = resolver.query(imgUri, null, null, null, null);
            cursor.moveToFirst();
            filename = cursor.getString(1);
            Utils.downsize(
                    filename,
                    filename = StaticFactory.APKCardPath
                            + new Date().getTime(), getActivity());
            updateLogo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void updateLogo() {
        try {
            RequestParams params = ac.getRequestParams();
            params.put("file", new File(filename));

            ac.httpClient.post(URLS.UPLOADUSERLOGO, params, new JsonHttpResponseHandler(getActivity(), getString(R.string.loading)) {

                @Override
                public void onStart() {
                    super.onStart();
                    avatar.setIcon(new BitmapDrawable(getResources(), filename));
                }

                @Override
                public void onSuccessCode(JSONObject jo) throws Exception {
                    super.onSuccessCode(jo);
                    String logo = jo.getString("logo");

                    //更新数据库
                    UserTable ut = DBHelper.userDao.getUserTableByDeviceId(ac.deviceId);
                    UserBean ub = ut.getBean();
                    ub.setLogo(logo);
                    ut.setBean(ub);
                    DBHelper.userDao.update(ut);
                    ToastUtil.toast(getActivity(), getString(R.string.upload_succes));
                    //刷新UI
                    setLogo(logo);
                }

                @Override
                public void onFailCode(JSONObject jo) {
                    super.onFailCode(jo);
                    avatar.setIcon(R.drawable.default_logo);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLogo(String logo) {
        //头像
        ImageLoader.getInstance().loadImage(logo + StaticFactory._160x160, Utils.options_default_logo, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                avatar.setIcon(new BitmapDrawable(getResources(), loadedImage));
            }
        });
    }
}
