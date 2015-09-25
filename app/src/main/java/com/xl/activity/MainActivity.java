package com.xl.activity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.xl.activity.base.BaseActivity;
import com.xl.activity.chat.ChatActivity;
import com.xl.activity.chat.ChatListActivity_;
import com.xl.activity.girl.GirlChatActivity_;
import com.xl.activity.pay.PayActivity_;
import com.xl.activity.setting.HelpActivity_;
import com.xl.activity.setting.SettingActivity_;
import com.xl.activity.share.CommonShared;
import com.xl.application.AppClass;
import com.xl.bean.UserBean_6;
import com.xl.bean.UserTable_6;
import com.xl.custom.ResizeLayout;
import com.xl.db.ChatDao;
import com.xl.db.UserTableDao;
import com.xl.fragment.MainFragment_;
import com.xl.game.GameActivity_;
import com.xl.game.PinTuActivity_;
import com.xl.util.BroadCastUtil;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.StaticFactory;
import com.xl.util.StaticUtil;
import com.xl.util.ToastUtil;
import com.xl.util.URLS;
import com.xl.util.Utils;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;

import a.b.c.CommonManager;
import a.b.c.DynamicSdkManager;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends BaseActivity implements ResizeLayout.OnResizeListener, View.OnClickListener {

    @ViewById(R.id.navigation_drawer)
    public NavigationView mNavigationView;

    @ViewById
    public DrawerLayout drawer_layout;

    @ViewById
    public Toolbar toolbar;

    @OptionsMenuItem(R.id.menu_item_share)
    MenuItem shareItem;

    @ViewById
    public ResizeLayout resize_layout;

    MenuItem girl_god, message_history;

    TextView vip_text, sex_text, nickname_text;
    EditText nickname_edit;
    ImageView logo;

    ChatDao chatDao;
    UserTableDao userTableDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置开发者信息(appid, appsecret, 是否开启测试模式)
        CommonManager.getInstance(getApplicationContext()).init("f8e79d512282c364",
                "1b6279c5f1aa4dde", false);

        // 根据 AndroidManifest.xml 文件中的设置进行插屏及其他无积分类型广告的预加载
        DynamicSdkManager.getInstance(getApplicationContext()).initNormalAd();

        MobclickAgent.updateOnlineConfig(this);

        chatDao = ChatDao.getInstance(this);
        userTableDao = UserTableDao.getInstance(this);

    }

    protected void init() {

        ac.startService();

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_navigation_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        UmengUpdateAgent.update(this);
        UmengUpdateAgent.setUpdateListener(null);

        setupDrawerContent(mNavigationView);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            ViewParent parent = drawer_layout.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(drawer_layout);

                RelativeLayout layout = new RelativeLayout(this);
                layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                layout.setFitsSystemWindows(true);
                layout.addView(drawer_layout);

                ((ViewGroup) parent).addView(layout);
            }
        }

        initSource();

        resize_layout.setOnResizeListener(this);
    }


    private void setupDrawerContent(NavigationView navigationView) {

        girl_god = navigationView.getMenu().findItem(R.id.girl_god);
        message_history = navigationView.getMenu().findItem(R.id.message_history);

        vip_text = (TextView) drawer_layout.findViewById(R.id.vip_text);
        sex_text = (TextView) drawer_layout.findViewById(R.id.sex_text);
        nickname_text = (TextView) drawer_layout.findViewById(R.id.nickname);
        logo = (ImageView) drawer_layout.findViewById(R.id.logo);
        nickname_edit = (EditText) drawer_layout.findViewById(R.id.nickname_edit);
        nickname_text.setOnClickListener(this);
        logo.setOnClickListener(this);

        navigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawer_layout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.message_history:
                                if (ac.cs.getISVIP() == CommonShared.ON) {
                                    ChatListActivity_.intent(MainActivity.this).start();
                                } else {
                                    Utils.showVipDialog(MainActivity.this);
                                }
                                break;
                            case R.id.girl_god:
                                GirlChatActivity_.intent(MainActivity.this).start();
                                break;
                            case R.id.pay:
                                PayActivity_.intent(MainActivity.this).start();
                                break;
                            case R.id.game1:
                                PinTuActivity_.intent(MainActivity.this).start();
                                break;
                            case R.id.game2:
                                GameActivity_.intent(MainActivity.this).start();
                                break;
                            case R.id.announcement:
                                FeedbackAgent agent = new FeedbackAgent(MainActivity.this);
                                agent.startFeedbackActivity();
                                break;
                            case R.id.help:
                                HelpActivity_.intent(MainActivity.this).start();
                                break;
                            case R.id.setting:
                                SettingActivity_.intent(MainActivity.this).start();
                                break;
                        }
                        return true;
                    }
                });
    }

    @OptionsItem(R.id.menu_item_share)
    void share() {
        startActivity(Intent.createChooser(getDefaultIntent(), getString(R.string.app_name)));
    }

    private Intent getDefaultIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "寂寞了吗？来一发吧。");
        intent.putExtra(Intent.EXTRA_TEXT, "我在" + getString(R.string.app_name) + "，要不要一起啊~ http://t.cn/RZTbceg");
        intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ac.stopService();
    }


    @OptionsItem(android.R.id.home)
    public void menu_cliek() {
        drawer_layout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onResume() {
        super.onResume();
        setCount();
    }

    @Receiver(actions = {BroadCastUtil.REFRESHNEWMESSAGECOUNT, BroadCastUtil.NEWMESSAGE})
    void refreshMessageCount() {
        setCount();
    }

    @UiThread
    public void setCount() {
        if (chatDao == null) {
            return;
        }

        int managerCount = chatDao.getUnCount(AppClass.MANAGER, ac.deviceId);
        if (managerCount > 0) {
            girl_god.setTitle(getString(R.string.girl_god) + "(" + managerCount + "条未读)");
        } else {
            girl_god.setTitle(getString(R.string.girl_god));
        }

        int count = chatDao.getAllUnCount(ac.deviceId);

        if (count > 0) {
            message_history.setTitle(getString(R.string.message_history) + "(" + count + "条未读)");
        } else {
            message_history.setTitle(getString(R.string.message_history));
        }
    }


    public void initSource() {
        UserTable_6 ut = userTableDao.getUserTableByDeviceId(ac.deviceId);
        setHead(ut);

        if (ac.cs.getIsFirstStartApp() == CommonShared.ON || ut == null || ut.getBean() == null || ut.getBean().getSex() == null) {

            ac.httpClient.post(URLS.GETUSERINFO, ac.getRequestParams(), new JsonHttpResponseHandler(MainActivity.this, getString(R.string.synchronization), false) {

                @Override
                public void onSuccess(JSONObject jo) {
                    int status = jo.optInt(ResultCode.STATUS);
                    switch (status) {
                        case ResultCode.SUCCESS:

                            UserTable_6 ut = new Gson().fromJson(jo.optString(StaticUtil.CONTENT), new TypeToken<UserTable_6>() {
                            }.getType());

                            setHead(ut);

                            userTableDao.deleteUserByDeviceId(ut.getDeviceId());
                            userTableDao.create(ut);

                            if (ut.getBean().getSex() == null) {
                                Utils.showDialog(MainActivity.this, R.drawable.dialog_icon, R.string.yoursex, R.string.can_not_reset, R.string.boy, R.string.girl, R.string.close, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        uploadSex(2);
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        uploadSex(1);
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }, false, false);

                            } else {
                                ac.cs.setIsFirstStartApp(CommonShared.OFF);
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.container, new MainFragment_())
                                        .commitAllowingStateLoss();
                            }

                            break;
                        case ResultCode.FAIL:
                            ToastUtil.toast(MainActivity.this, getString(R.string.fuck_alien), R.drawable.wunai);
                            break;
                    }
                }

                @Override
                public void onFailure() {
                    Utils.showDialog(MainActivity.this, R.drawable.what_the_fucks, R.string.what_the_fuck, R.string.error_net, R.string.retry, null, R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            initSource();
                        }
                    }, null, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }, false, false);
                }
            });
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new MainFragment_())
                    .commit();

            ac.httpClient.post(URLS.GETUSERINFO, ac.getRequestParams(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject jo) {
                    int status = jo.optInt(ResultCode.STATUS);
                    switch (status) {
                        case ResultCode.SUCCESS:
                            UserTable_6 ut = new Gson().fromJson(jo.optString(StaticUtil.CONTENT), new TypeToken<UserTable_6>() {
                            }.getType());
                            setHead(ut);
                            break;
                    }
                }
            });
        }
    }

    public void setHead(UserTable_6 ut) {
        if (ut == null || ut.getBean() == null) {
            return;
        }
        if (ut.getBean().isGirl()) {
            vip_text.setText(getString(R.string.girl_god));
        } else if (ut.getBean().isVip()) {
            vip_text.setText("会员");
        } else {
            vip_text.setText("普通");
        }
        if (ut.getBean().getSex() == null) {
            sex_text.setText("未知性别");
        } else {
            sex_text.setText(ut.getBean().getSex() == 1 ? "女" : "男");
        }
        nickname_text.setText(ut.getBean().nickname);
        nickname_edit.setText(ut.getBean().nickname);

        ImageLoader.getInstance().displayImage(ut.getBean().logo, logo, Utils.options_default_logo);
    }

    public void uploadSex(final int sex) {
        RequestParams params = ac.getRequestParams();
        params.put("sex", sex);
        ac.httpClient.post(URLS.SETUSERDETAIL, params, new JsonHttpResponseHandler(MainActivity.this, getString(R.string.saving), false) {

            @Override
            public void onSuccess(JSONObject jo) {
                int status = jo.optInt(ResultCode.STATUS);
                switch (status) {
                    case ResultCode.SUCCESS:
                        ToastUtil.toast(MainActivity.this, getString(R.string.saving_success));
                        UserTable_6 ut = new Gson().fromJson(jo.optString(StaticUtil.CONTENT), new TypeToken<UserTable_6>() {
                        }.getType());

                        setHead(ut);

                        userTableDao.deleteUserByDeviceId(ut.getDeviceId());
                        userTableDao.create(ut);

                        ac.cs.setIsFirstStartApp(CommonShared.OFF);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new MainFragment_())
                                .commit();
                        break;
                    case ResultCode.FAIL:
                        onFailure();
                        break;
                }
            }

            @Override
            public void onFailure() {
                Utils.showDialog(MainActivity.this, R.drawable.what_the_fucks, R.string.what_the_fuck, R.string.saving_error, R.string.retry, null, R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadSex(sex);
                    }
                }, null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }, false, false);
            }

        });
    }

    @Override
    public void OnSoftPop(int height) {

    }

    @Override
    public void OnSoftClose(int height) {
        nickname_edit.setVisibility(View.GONE);
        nickname_text.setVisibility(View.VISIBLE);
        final UserTable_6 user = userTableDao.getUserTableByDeviceId(ac.deviceId);
        final String str = nickname_edit.getText().toString();
        if (!TextUtils.isEmpty(str)){
            if(!str.equals(user.getBean().nickname)){
                RequestParams params = ac.getRequestParams();
                params.put("nickname", str);
                ac.httpClient.post(URLS.SETUSERDETAIL,params,new JsonHttpResponseHandler(){

                    @Override
                    public void onStart() {
                        super.onStart();
                        nickname_text.setText(str);
                    }

                    @Override
                    public void onSuccessCode(JSONObject jo) {
                        super.onSuccessCode(jo);
                        UserBean_6 bean = user.getBean();
                        bean.nickname = str;
                        user.setBean(bean);
                        userTableDao.update(user);
                    }

                    @Override
                    public void onFailCode() {
                        super.onFailCode();
                        nickname_text.setText(user.getBean().nickname);
                    }
                });
            }
        }
    }

    @Override
    public void OnSoftChanegHeight(int height) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nickname:
                nickname_text.setVisibility(View.GONE);
                nickname_edit.setVisibility(View.VISIBLE);
                nickname_edit.setFocusable(true);
                nickname_edit.requestFocus();
                nickname_edit.setSelection(nickname_edit.getText().toString().length());
                Utils.openSoftKeyboard(nickname_edit);
                break;
            case R.id.logo:
                chosePic();
                break;
        }
    }

    String filename;

    public void chosePic(){
        new AlertDialog.Builder(this).setTitle(getString(R.string.dont_chose_fail)).setIcon(R.drawable.weisuo_yuan).setItems(new String[]{getString(R.string.Camera), getString(R.string.Album)}, new DialogInterface.OnClickListener() {
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
                            ToastUtil.toast(MainActivity.this, getString(R.string.please_check_sdcard), R.drawable.kiding);
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
                            ToastUtil.toast(MainActivity.this, getString(R.string.please_check_sdcard), R.drawable.kiding);
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
                Utils.downsize(filename, filename, this);
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
        ContentResolver resolver = getContentResolver();
        Uri imgUri = intent.getData();
        try {
            Cursor cursor = resolver.query(imgUri, null, null, null, null);
            cursor.moveToFirst();
            filename = cursor.getString(1);
            Utils.downsize(
                    filename,
                    filename = StaticFactory.APKCardPath
                            + new Date().getTime(), this);
            updateLogo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void updateLogo(){
        try {
            RequestParams params = ac.getRequestParams();
            params.put("file",new File(filename));

            ac.httpClient.post(URLS.UPLOADUSERLOGO, params, new JsonHttpResponseHandler() {

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
