package com.xl.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmob.pay.tool.BmobPay;
import com.github.johnpersano.supertoasts.SuperToast;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.fb.FeedbackAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.update.UmengUpdateAgent;
import com.xl.activity.base.BaseActivity;
import com.xl.activity.chat.ChatListActivity_;
import com.xl.activity.girl.GirlChatActivity_;
import com.xl.activity.pay.PayActivity_;
import com.xl.activity.setting.HelpActivity_;
import com.xl.activity.setting.SettingActivity_;
import com.xl.activity.share.CommonShared;
import com.xl.activity.user.EditUserInfoActivity_;
import com.xl.application.AppClass;
import com.xl.bean.UserTable;
import com.xl.db.ChatDao;
import com.xl.db.DBHelper;
import com.xl.db.UserTableDao;
import com.xl.fragment.MainFragment_;
import com.xl.game.GameActivity_;
import com.xl.game.PinTuActivity_;
import com.xl.post.GetUserInfo;
import com.xl.receiver.AlarmReceiver;
import com.xl.util.BroadCastUtil;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.StaticFactory;
import com.xl.util.ToastUtil;
import com.xl.util.URLS;
import com.xl.util.Utils;

import net.google.niofile.AdManager;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends BaseActivity implements View.OnClickListener {

    @ViewById(R.id.navigation_drawer)
    public NavigationView mNavigationView;

    @ViewById
    public DrawerLayout drawer_layout;

    @ViewById
    public Toolbar toolbar;

    @OptionsMenuItem(R.id.menu_item_share)
    MenuItem shareItem;

    MenuItem girl_god, message_history;

    TextView vip_text, sex_text, nickname_text;
    ImageView userlogo;

    ChatDao chatDao;
    UserTableDao userTableDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*try {
            DynamicSdkManager.onCreate(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        // 设置开发者信息(appid, appsecret, 是否开启测试模式)
        CommonManager.getInstance(getApplicationContext()).init("f8e79d512282c364",
                "1b6279c5f1aa4dde", false);

        // 根据 AndroidManifest.xml 文件中的设置进行插屏及其他无积分类型广告的预加载
        DynamicSdkManager.getInstance(getApplicationContext()).initNormalAd();*/

        AdManager.getInstance(this).init("f8e79d512282c364", "1b6279c5f1aa4dde", false);

        OnlineConfigAgent.getInstance().updateOnlineConfig(this);

        chatDao = ChatDao.getInstance(this);
        userTableDao = UserTableDao.getInstance(this);

        BmobPay.init(getApplicationContext(), "2c9f0c5fbeb32f1b1bce828d29514f5d");

        testRecoding();//获取录音请求
    }

    @Background
    public void testRecoding() {
        AudioRecord mAudioRecorder = null;
        try {
            int SampleRateInHz = 8000;

            int min = AudioRecord.getMinBufferSize(SampleRateInHz,
                    AudioFormat.CHANNEL_IN_DEFAULT,
                    AudioFormat.ENCODING_PCM_16BIT);

            int frameSize = Math.max(2560, min) * 4;

            mAudioRecorder = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SampleRateInHz,
                    AudioFormat.CHANNEL_IN_DEFAULT,
                    AudioFormat.ENCODING_PCM_16BIT,
                    frameSize);
            mAudioRecorder.startRecording();
            Thread.sleep(1000);
        } catch (Throwable t) {
            t.printStackTrace();
            toashLiangchen();
        } finally {
            try {
                mAudioRecorder.stop();
                mAudioRecorder.release();
                mAudioRecorder = null;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @UiThread
    public void toashLiangchen() {
        ToastUtil.toast(MainActivity.this, getString(R.string.unable_recoding_liangchen), R.drawable.eat_shit, SuperToast.Duration.LONG);
    }

    @Override
    public boolean needTranslucentStatus() {
        return false;
    }

    protected void init() {

        AlarmReceiver.actionStartConnectAlarm(this);

        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_navigation_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        UmengUpdateAgent.update(this);
        UmengUpdateAgent.setUpdateListener(null);

        setupDrawerContent(mNavigationView);

        initSource();

        checkVip();
    }

    public void checkVip() {
        if (!TextUtils.isEmpty(ac.cs.getVipOrder())) {
            RequestParams params = ac.getRequestParams();
            params.put("orderId", ac.cs.getVipOrder());
            ac.httpClient.post(URLS.PAY, params, new JsonHttpResponseHandler(mContext, "正在完成充值") {

                @Override
                public void onSuccess(JSONObject jo) {
                    super.onSuccess(jo);

                    ac.cs.setVipOrder(null);
                }

                @Override
                public void onSuccessCode(JSONObject jo) throws Exception {
                    super.onSuccessCode(jo);

                    ToastUtil.toast(mContext, "充值成功，请重启软件");
                }

            });
        }
    }


    private void setupDrawerContent(NavigationView navigationView) {

        girl_god = navigationView.getMenu().findItem(R.id.girl_god);
        message_history = navigationView.getMenu().findItem(R.id.message_history);

        vip_text = (TextView) drawer_layout.findViewById(R.id.vip_text);
        sex_text = (TextView) drawer_layout.findViewById(R.id.sex_text);
        nickname_text = (TextView) drawer_layout.findViewById(R.id.nickname);
        userlogo = (ImageView) drawer_layout.findViewById(R.id.logo);
        nickname_text.setOnClickListener(this);
        userlogo.setOnClickListener(this);

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
        UserTable ut = userTableDao.getUserTableByDeviceId(ac.deviceId);
        setHead(ut);

        if (ac.cs.getIsFirstStartApp() == CommonShared.ON || ut == null || ut.getBean() == null || ut.getBean().getSex() == null) {

            ac.httpClient.post(URLS.GETUSERINFO, ac.getRequestParams(), new JsonHttpResponseHandler(MainActivity.this, getString(R.string.synchronization), false) {

                @Override
                public void onSuccess(JSONObject jo) {
                    int status = jo.optInt(ResultCode.STATUS);
                    switch (status) {
                        case ResultCode.SUCCESS:

                            UserTable ut = Utils.jsonToBean(jo.optString(ResultCode.CONTENT), UserTable.class);

                            setHead(ut);

                            userTableDao.updateUser(ut);

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

            new GetUserInfo(this, new JsonHttpResponseHandler() {
                @Override
                public void onSuccessCode(JSONObject jo) throws Exception {
                    UserTable ut = DBHelper.userDao.getUserTableByDeviceId(ac.deviceId);

                    setHead(ut);
                }
            });
        }
    }

    public void setHead(UserTable ut) {
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

        ImageLoader.getInstance().displayImage(ut.getBean().logo + StaticFactory._160x160, userlogo, Utils.options_default_logo);
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
                        UserTable ut = Utils.jsonToBean(jo.optString(ResultCode.CONTENT), UserTable.class);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nickname:
                EditUserInfoActivity_.intent(this).start();
                break;
            case R.id.logo:
                EditUserInfoActivity_.intent(this).start();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ac.cs.getBackgroundRun() == CommonShared.OFF) {
            AlarmReceiver.actionStopConnectAlarm(this);
            ac.stopService();
        }
    }
}
