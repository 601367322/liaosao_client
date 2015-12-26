package com.xl.activity.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.RequestParams;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.quan.lib_camera_video.MediaRecorderActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.activity.chat.adapter.ChatAdapters;
import com.xl.activity.share.CommonShared;
import com.xl.application.AppClass;
import com.xl.bean.BlackUser;
import com.xl.bean.MessageBean;
import com.xl.bean.UserBean;
import com.xl.bean.UserTable;
import com.xl.custom.swipe.SwipeRefreshLayout;
import com.xl.db.BlackDao;
import com.xl.db.ChatDao;
import com.xl.db.ChatlistDao;
import com.xl.db.UserTableDao;
import com.xl.radio.AmrEncodSender;
import com.xl.radio.AmrEngine;
import com.xl.radio.MicRealTimeListener;
import com.xl.util.BroadCastUtil;
import com.xl.util.EventID;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.LogUtil;
import com.xl.util.ResultCode;
import com.xl.util.SensitivewordFilter;
import com.xl.util.StaticFactory;
import com.xl.util.StaticUtil;
import com.xl.util.ToastUtil;
import com.xl.util.URLS;
import com.xl.util.Utils;

import net.google.niofile.st.SpotManager;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

@OptionsMenu(R.menu.chat_menu)
@EActivity(R.layout.chat_activity)
public class ChatActivity extends BaseBackActivity implements
        OnEditorActionListener, SensorEventListener, SwipeRefreshLayout.OnRefreshListener {

    @ViewById
    EditText content_et;
    @ViewById
    ImageButton send_btn;
    @ViewById
    RecyclerView listview;
    @Extra
    String deviceId = null;
    @ViewById
    SwipeRefreshLayout swipe;
    @ViewById
    ImageView fbiimg;
    @ViewById
    View chat_ll1, chat_ll2, voice_anim_view, chat_more_ll, add_btn, image_btn, face_btn;
    @ViewById
    TextView time_txt, send_voice_btn, cancle_btn;
    public static final int Album = 2, Camera = 1, RADIO = 3;
    @SystemService
    NotificationManager notifManager;
    @OptionsMenuItem(R.id.pingbi)
    MenuItem menuItem;
    @OptionsMenuItem(R.id.juli)
    MenuItem juli;
    @OptionsMenuItem(R.id.renzheng)
    MenuItem renzheng;
    @Extra
    boolean isGroup;
    @ViewById
    GridView face_grid;

    ChatAdapters adapter;

    UserTableDao userTableDao;
    UserBean userBean;

    AtomicBoolean changeing1 = new AtomicBoolean(false);
    AtomicBoolean changeing2 = new AtomicBoolean(false);

    int lastId = -1; //已显示聊天记录

    UserTable friend;

    SensitivewordFilter filter = new SensitivewordFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userTableDao = UserTableDao.getInstance(this);
        userBean = userTableDao.getUserTableByDeviceId(ac.deviceId).getBean();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (BlackDao.getInstance(this).isExists(deviceId) != null) {
            menuItem.setTitle(getString(R.string.yipingbi));
        } else {
            menuItem.setTitle(getString(R.string.pingbi));
        }
        if (ac.deviceId.equals(AppClass.MANAGER)) {
            renzheng.setVisible(true);
        } else {
            renzheng.setVisible(false);
        }
        if (isGroup) {
            menu.clear();
        }
        return super.onCreateOptionsMenu(menu);
    }

    protected void init() {
        //去掉通知栏
        notifManager.cancel(deviceId.hashCode());

        //设置键盘回车监听
        content_et.setOnEditorActionListener(this);

        //初始化数据源
        adapter = new ChatAdapters(this, new ArrayList<MessageBean>());

        UserTable friend = userTableDao.getUserTableByDeviceId(deviceId);
        if (friend != null) {
            adapter.setFriend(friend);
        }

        //设置数据源
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listview.setLayoutManager(layoutManager);
        listview.setAdapter(adapter);

        send_btn.setEnabled(false);

        //发送成功失败监听
        EventBus.getDefault().register(this);

        //弹出广告
        showScreenAd();

        //下拉刷新
        swipe.setOnRefreshListener(this);

        //开始刷新
        refresh();

        //获取对方用户信息
        checkVip();

        //如果是群聊
        if (isGroup) {
            chat_more_ll.setVisibility(View.GONE);
        }
    }

    public void checkVip() {
        ac.httpClient.post(URLS.GETUSERINFO, new RequestParams(StaticUtil.DEVICEID, deviceId), new JsonHttpResponseHandler() {

            @Override
            public void onSuccessCode(JSONObject jo) throws Exception {
                super.onSuccessCode(jo);
                friend = Utils.jsonToBean(jo.optString(ResultCode.CONTENT), UserTable.class);

                //设置标题和副标题
                String title = "";
                if (friend.getBean().isGirl()) {
                    title += getString(R.string.girl_god);
                } else {
                    if (friend.getBean() == null || friend.getBean().getSex() == null) {
                        title += "未知性别";
                    } else {
                        title += friend.getBean().getSex() == 1 ? "女" : "男";
                    }
                }
                if (friend.getBean().isVip()) {
                    title += "\t　会员";
                }
                getSupportActionBar().setSubtitle(title);
                if (!TextUtils.isEmpty(friend.getBean().getNickname())) {
                    getSupportActionBar().setTitle(friend.getBean().getNickname());
                }

                //设置显示距离
                if (ac.cs.getISVIP() == CommonShared.ON) {
                    if (!TextUtils.isEmpty(ac.cs.getLat()) && !TextUtils.isEmpty(friend.getBean().getLat())) {
                        setJuLi(Utils.getDistance(Double.valueOf(ac.cs.getLng()), Double.valueOf(ac.cs.getLat()), Double.valueOf(friend.getBean().getLng()), Double.valueOf(friend.getBean().getLat())) + "km");
                    }
                }

                //更新数据源
                adapter.setFriend(friend);
                adapter.notifyDataSetChanged();

                userTableDao.deleteUserByDeviceId(deviceId);
                userTableDao.create(friend);
            }

        });
    }

    @UiThread(delay = 200)
    public void setJuLi(String str) {
        juli.setTitle(str);
    }

    @OptionsItem(R.id.juli)
    public void juli() {
        if (ac.cs.getISVIP() == CommonShared.OFF) {
            Utils.showVipDialog(this);
        } else {
            checkVip();
        }
    }

    @OptionsItem(R.id.pingbi)
    public void pingbi() {
        BlackUser bean = BlackDao.getInstance(getApplicationContext()).isExists(deviceId);
        if (bean != null) {
            BlackDao.getInstance(getApplicationContext()).delete(bean);
            menuItem.setTitle(getString(R.string.pingbi));
        } else {
            BlackDao.getInstance(getApplicationContext()).add(deviceId);
            menuItem.setTitle(getString(R.string.yipingbi));
        }
    }

    @UiThread
    public void refresh() {
        swipe.setRefreshing(true);
    }

    @Override
    public void onRefresh() {
        getHistoryData(lastId == -1 ? true : false);
    }

    @Background
    public void getHistoryData(boolean toLast) {
        List<MessageBean> list = ChatDao.getInstance(getApplicationContext()).getHistoryMsg(ac.deviceId, deviceId, lastId);
        if (ac.cs.getISVIP() == CommonShared.OFF) {
            list = new ArrayList<>();
        }
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                adapter.addFirst(list.get(i));
                if (i == list.size() - 1) {
                    lastId = list.get(i).getId();
                }
            }
            notifyData();
            if (toLast) {
                scrollToLast();
            } else {
                scrollToPosition(list.size());
            }
        }
    }

    @Subscribe
    public void onEvent(final MessageBean mb) {
        if (!mb.getToId().equals(ac.deviceId)) {
            new AlertDialog.Builder(ChatActivity.this).setIcon(R.drawable.beiju).setTitle(getString(R.string.beijua)).setMessage(getString(R.string.resend_message)).setPositiveButton(getString(R.string.resend_btn), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.getList().remove(mb);
                    ChatDao.getInstance(getApplicationContext()).deleteMessage(mb);
                    switch (mb.getMsgType()) {
                        case MessageBean.TEXT:
                        case MessageBean.FACE:
                            sendText(mb.getContent(), mb.getMsgType());
                            break;
                        case MessageBean.RADIO:
                        case MessageBean.VOICE:
                        case MessageBean.IMAGE:
                            recodeTime = mb.getVoiceTime();
                            filename = mb.getContent();
                            sendFile(mb.getMsgType());
                            break;
                        case MessageBean.RADIO_NEW:
                            filename = mb.getRadioBean().file;
                            thumb = mb.getRadioBean().thumb;
                            sendFile(mb.getMsgType());
                            break;
                    }
                }
            }).setNegativeButton(getString(R.string.cancle_send_btn), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
        } else {
            mb.setLoading(MessageBean.LOADING_NODOWNLOAD);
            adapter.notifyDataSetChanged();
        }
    }

    @Receiver(actions = BroadCastUtil.NEWMESSAGE)
    public void newMessage(Intent intent) {
        MessageBean mb = (MessageBean) intent.getExtras().getSerializable(StaticUtil.BEAN);
        if (mb.getFromId().equals(deviceId)) {
            mb.setState(1);
            ChatDao.getInstance(getApplicationContext()).updateMessage(mb);
            notifManager.cancel(deviceId.hashCode());
            adapter.getList().add(mb);
            adapter.notifyDataSetChanged();
            scrollToLast();
        }
    }

    @Receiver(actions = BroadCastUtil.CLOSECHAT)
    public void closeChat(Intent intent) {
        String other = intent.getStringExtra(StaticUtil.DEVICEID);
        if (deviceId.equals(other)) {
            ToastUtil.toast(this, "他/她/它退出了聊天，不过你依然可以骚扰他/她/它……", R.drawable.be_alone);
            content_et.setHint("对方退出了聊天");
        }
    }

    @AfterTextChange
    void content_et(Editable text) {
        if (text.toString().length() > 0 && send_btn.getAlpha() < 1f) {
            if (changeing1.compareAndSet(false, true)) {
                send_btn.animate().alpha(1.0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        send_btn.setEnabled(true);
                        changeing1.compareAndSet(true, false);
                    }
                }).start();
            }
        } else if (text.toString().length() == 0 && send_btn.getAlpha() > 0.5f) {
            if (changeing2.compareAndSet(false, true)) {
                send_btn.animate().alpha(0.5f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        send_btn.setEnabled(false);
                        changeing2.compareAndSet(true, false);
                    }
                }).start();
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView tv, int arg1, KeyEvent arg2) {
        switch (arg1) {
            case EditorInfo.IME_ACTION_SEND:
                send_btn.performClick();
                break;
        }
        return true;
    }

    @Click
    void send_btn() {

        if (isFastDoubleClick()) {
            toast(getString(R.string.wait_a_moment));
            return;
        }

        final String context_str = content_et.getText().toString().trim();
        if (context_str.length() == 0) {
            toast(getString(R.string.you_fuck_do_not_write_send_jj));
            content_et.requestFocus();
            return;
        }
        content_et.setText("");

        MobclickAgent.onEvent(this, EventID.SEND_TEXT);
        sendText(context_str, 0);
    }

    void sendText(String context_str, int msgType) {

        if (OnlineConfigAgent.getInstance().getConfigParams(this, "key_filter").equals("on")) {
            context_str = filter.replaceSensitiveWord(context_str, SensitivewordFilter.minMatchTYpe, "*");
        }

        RequestParams rp = ac.getRequestParams();
        final MessageBean mb = new MessageBean(ac.deviceId, deviceId, context_str, msgType, userBean.getSex());
        rp.put("content", new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation().create().toJson(mb).toString());
        rp.put("sex", userBean.getSex());
        ac.httpClient.post(URLS.SENDMESSAGE, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jo) {
                try {
                    int status = jo.getInt(ResultCode.STATUS);
                    switch (status) {
                        case ResultCode.SUCCESS:
                            mb.setLoading(MessageBean.LOADING_DOWNLOADED);
                            notifyData();
                            if (jo.has(ResultCode.INFO)) {
                                if (jo.optInt(ResultCode.INFO) == ResultCode.DISCONNECT) {
                                    Intent intent = new Intent(BroadCastUtil.CLOSECHAT);
                                    intent.putExtra(StaticUtil.DEVICEID, deviceId);
                                    sendBroadcast(intent);
                                }
                            }
                            break;
                        case ResultCode.FAIL:
                            mb.setLoading(MessageBean.LOADING_DOWNLOADFAIL);
                            notifyData();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    ChatDao.getInstance(getApplicationContext()).updateMessage(mb);
                }
            }

            @Override
            public void onStart() {
                adapter.getList().add(mb);
                adapter.notifyDataSetChanged();

                ChatlistDao.getInstance(getApplicationContext()).addChatListBean(mb, deviceId);
                ChatDao.getInstance(getApplicationContext()).addMessage(ac.deviceId, mb);

                final float x = send_btn.getX();
                send_btn.animate().translationX(send_btn.getWidth()).setInterpolator(new AccelerateInterpolator()).setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        send_btn.setEnabled(false);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        send_btn.setX(x - send_btn.getWidth());
                        send_btn.animate().translationX(0).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                changeing2.compareAndSet(true, false);
                            }
                        }).start();
                        send_btn.setAlpha(0.5f);
                    }
                }).start();
                scrollToLast();
            }

            @Override
            public void onFailure() {
                mb.setLoading(MessageBean.LOADING_DOWNLOADFAIL);
                ChatDao.getInstance(getApplicationContext()).updateMessage(mb);
                notifyData();
            }
        });
    }

    @UiThread
    void notifyData() {
        adapter.notifyDataSetChanged();
        swipe.setRefreshing(false);
    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    @Override
    public void onBackPressed() {
        // 如果有需要，可以点击后退关闭插播广告。
        if (SpotManager.getInstance(getApplicationContext()).disMiss()) {
            // 弹出退出窗口，可以使用自定义退屏弹出和回退动画,参照demo,若不使用动画，传入-1
            return;
        }
        if (!closeGridView())
            showFinshDialog();
    }

    @Override
    public void homeClick() {
        showFinshDialog();
    }

    public void showFinshDialog() {
        new AlertDialog.Builder(this).setTitle(getString(R.string.kiding)).setIcon(R.drawable.dialog_icon).setMessage(getString(R.string.are_you_fuck_sure_do_not_chat)).setPositiveButton(getString(R.string.papa_do_not_chat), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

                ac.httpClient.get(URLS.CLOSECHAT, ac.getRequestParams(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject jo) {
                        LogUtil.d(jo.toString());
                    }
                });
            }
        }).setNegativeButton(getString(R.string.not_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    AmrEncodSender recorderInstance;
    String filename, thumb;
    Thread recordThread;//动画线程
    float recodeTime = 0.0f; // 录音的时间
    int voiceValue = 0; // 麦克风获取的音量值
    private static int MAX_TIME = 60; // 最长录制时间，单位秒，0为无时间限制
    private static int MIN_TIME = 1; // 最短录制时间，单位秒，0为无时间限制，建议设为1

    @Click(R.id.voice_btn)
    void voiceBtnClick() {
        PlayVoice.getInstance(adapter).stopArm(null);
        closeInput();
        closeMore(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                chat_ll1.animate().translationY(-chat_ll1.getHeight()).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) chat_ll2.getLayoutParams();
                        ValueAnimator whxyBouncer = ObjectAnimator.ofInt(0, (int) getResources().getDimension(R.dimen.chat_bottom_voice_height)).setDuration(500);
                        whxyBouncer.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (recorderInstance == null || !AmrEngine.getSingleEngine().isRecordRunning()) {

                                    File file = new File(StaticFactory.APKCardPathChat);
                                    if (!file.exists()) {
                                        file.mkdirs();
                                    }
                                    filename = StaticFactory.APKCardPathChat + String.valueOf((String
                                            .valueOf(new Date().getTime()) + ".spx")
                                            .hashCode());

                                    recorderInstance = new AmrEncodSender(filename, new MicRealTimeListener() {

                                        @Override
                                        public void getMicRealTimeSize(int size) {
                                            voiceValue = size;
                                        }
                                    });
                                    Thread th = new Thread(recorderInstance);
                                    th.start();
                                    AmrEngine.getSingleEngine().startRecording();
                                    updateTimeText();
                                    mythread();
                                }
                            }
                        });
                        whxyBouncer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                layoutParams.height = (int) animation.getAnimatedValue();
                                chat_ll2.setLayoutParams(layoutParams);
                            }
                        });
                        whxyBouncer.start();
                    }
                }).setDuration(200).start();
            }
        });
    }

    @Click(R.id.cancle_btn)
    void cancleBtnClick() {
        closeVoicePanl(cancle_btn, 3);
    }

    void cancleAnim(final AnimatorListenerAdapter adapter1) {
        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) chat_ll2.getLayoutParams();
        ValueAnimator whxyBouncer = ObjectAnimator.ofInt(layoutParams.height, 0).setDuration(500);
        whxyBouncer.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ViewPropertyAnimator animator = chat_ll1.animate().translationY(0).setDuration(200);
                if (adapter1 != null) {
                    animator.setListener(adapter1);
                } else {
                    animator.setListener(null);
                }
                animator.start();
            }
        });
        whxyBouncer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutParams.height = (int) animation.getAnimatedValue();
                chat_ll2.setLayoutParams(layoutParams);
            }
        });
        whxyBouncer.start();
    }

    @Click(R.id.send_voice_btn)
    void sendVoiceBtnClick() {
        closeVoicePanl(send_voice_btn, 0);
    }

    void closeVoicePanl(TextView btn, final int message) {
        voice_anim_view.setVisibility(View.VISIBLE);
        voice_anim_view.setX(btn.getX());
        voice_anim_view.setY(btn.getY());
        AmrEngine.getSingleEngine().stopRecording();
        float width = (getResources().getDisplayMetrics().widthPixels / getResources().getDimensionPixelSize(R.dimen.voice_btn_width)) * 2.5f;
        voice_anim_view.animate().scaleY(width).scaleX(width).setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cancleAnim(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        imgHandle.sendEmptyMessage(message);
                    }
                });
            }
        }).start();
    }

    // 录音计时线程
    public void mythread() {
        recordThread = new Thread(ImgThread);
        recordThread.start();
    }

    private Runnable ImgThread = new Runnable() {

        public void run() {
            recodeTime = 0.0f;
            while (recorderInstance != null && AmrEngine.getSingleEngine().isRecordRunning()) {
                if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
                    AmrEngine.getSingleEngine().stopRecording();
                    handle.post(new Runnable() {
                        @Override
                        public void run() {
                            send_voice_btn.performClick();
                        }
                    });
                } else {
                    try {
                        Thread.sleep(200);
                        recodeTime += 0.2;
                        if (recorderInstance != null && AmrEngine.getSingleEngine().isRecordRunning()) {
                            // voiceValue = mr.getAmplitude();
                            imgHandle.sendEmptyMessage(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    // 录音Dialog图片随声音大小切换
    void setDialogImage() {
//        ball_view.setmHeight(voiceValue);
    }

    int allTime = 0;

    void updateTimeText() {
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AmrEngine.getSingleEngine().isRecordRunning() && allTime < MAX_TIME) {
                    time_txt.setText(String.valueOf(++allTime) + "''");
                    updateTimeText();
                }
            }
        }, 1000);
    }

    Handler handle = new Handler();
    Handler imgHandle = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    revert();
                    if (recodeTime < MIN_TIME) {
                        File o = new File(filename);
                        if (o.exists()) {
                            o.delete();
                        }

                        ToastUtil.toast(ChatActivity.this, getString(R.string.your_JJ_so_short), R.drawable.weisuo);
                    } else {
                        sendFile(MessageBean.VOICE);
                    }
                    break;
                case 3:
                    revert();
                    File o = new File(filename);
                    if (o.exists()) {
                        o.delete();
                    }
                    break;
                case 1:
                    setDialogImage();
                    break;
                default:
                    break;
            }
            super.dispatchMessage(msg);
        }
    };

    void revert() {
        //还原动画
        voice_anim_view.setVisibility(View.GONE);
        voice_anim_view.setScaleX(1f);
        voice_anim_view.setScaleY(1f);
        voiceValue = 0;//还原录音大小
//        ball_view.setmHeight(voiceValue);//还原球动画
        allTime = 0;//还原录音时间
        time_txt.setText(allTime + "''");
    }

    @Override
    public void onPause() {
        super.onPause();
        PlayVoice.getInstance(adapter).stopArm(null);
        mSensorManager.unregisterListener(this);
    }

    SensorManager mSensorManager;
    Sensor mSensor;
    AudioManager audioManager;

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (audioManager == null) {
            audioManager = (AudioManager) this
                    .getSystemService(Context.AUDIO_SERVICE);
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        notifManager.cancel(deviceId.hashCode());

        //更新聊天列表
        Intent intent = new Intent(BroadCastUtil.REFRESHCHATLIST);
        intent.putExtra(StaticUtil.DEVICEID, deviceId);
        sendBroadcast(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float range = event.values[0];
        if (range >= mSensor.getMaximumRange() && audioManager.getMode() != audioManager.MODE_NORMAL) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            PlayVoice.getInstance(adapter).replay();
        } else if (range < mSensor.getMaximumRange() && audioManager.getMode() != audioManager.MODE_IN_CALL) {
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            PlayVoice.getInstance(adapter).replay();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Click
    void add_btn(final View view) {
        closeGridView();
        closeMore(null);
    }

    void closeMore(AnimatorListenerAdapter listener) {
        final ValueAnimator animator = ObjectAnimator.ofInt(40, 200);
        animator.setDuration(200);
        final LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) chat_more_ll.getLayoutParams();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutParams.width = Utils.dip2px(getApplicationContext(), (int) animation.getAnimatedValue());
                chat_more_ll.setLayoutParams(layoutParams);
            }
        });
        if (add_btn.getRotation() == 0) {
            add_btn.animate().rotation(225f).setDuration(200).start();
            animator.start();
        } else {
            animator.setIntValues(160, 40);
            if (listener != null) {
                animator.addListener(listener);
            }
            animator.start();
            add_btn.animate().rotation(0f).setDuration(200).start();
        }
    }

    @Click
    void radio_btn() {
        closeMore(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent i = new Intent(ChatActivity.this, MediaRecorderActivity.class);
                startActivityForResult(i, RADIO);
            }
        });

    }

    @OnActivityResult(value = ChatActivity.RADIO)
    public void onRadioCallBack(int result, Intent data) {
        if (result == RESULT_OK) {
            filename = data.getStringExtra("file");
            thumb = data.getStringExtra("thumb");
            File fi = new File(filename);
            if (fi != null && fi.exists()) {
                fi = new File(thumb);
                if (fi != null && fi.exists()) {
                    sendFile(MessageBean.RADIO_NEW);
                }else{
                    ToastUtil.toast(ChatActivity.this, getString(R.string.your_JJ_so_short), R.drawable.weisuo);
                }
            }
            fi = null;
        }
    }

    @Click
    void image_btn() {
        closeMore(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                new AlertDialog.Builder(ChatActivity.this).setTitle(getString(R.string.dont_chose_fail)).setIcon(R.drawable.weisuo_yuan).setItems(new String[]{getString(R.string.Camera), getString(R.string.Album)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (Utils.existSDcard()) {
                                    Intent intent = new Intent(); // 调用照相机
                                    String messagepath = StaticFactory.APKCardPathChat;
                                    File fa = new File(messagepath);
                                    if (!fa.exists()) {
                                        fa.mkdirs();
                                    }
                                    filename = messagepath + new Date().getTime();// 图片路径
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(new File(filename)));
                                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(intent, Camera);
                                } else {
                                    ToastUtil.toast(ChatActivity.this, getString(R.string.please_check_sdcard), R.drawable.kiding);
                                }
                                break;
                            case 1:
                                if (Utils.existSDcard()) {
                                    Intent intent = new Intent();
                                    String messagepath = StaticFactory.APKCardPathChat;
                                    File fa = new File(messagepath);
                                    if (!fa.exists()) {
                                        fa.mkdirs();
                                    }
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intent, Album);
                                } else {
                                    ToastUtil.toast(ChatActivity.this, getString(R.string.please_check_sdcard), R.drawable.kiding);
                                }
                                break;
                        }
                    }
                }).create().show();
            }
        });
    }

    @OnActivityResult(value = ChatActivity.Camera)
    @Background
    void onCameraResult() {
        if (filename != null) {
            File fi = new File(filename);
            if (fi != null && fi.exists()) {
                Utils.downsize(filename, filename, this);
                sendFile(MessageBean.IMAGE);
            }
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
            sendFile(MessageBean.IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    void sendFile(int type) {
        if (type == MessageBean.VOICE) {
            MobclickAgent.onEvent(ChatActivity.this, EventID.SEND_VOICE);
        } else if (type == MessageBean.IMAGE) {
            MobclickAgent.onEvent(ChatActivity.this, EventID.SEND_IMG);
        } else if (type == MessageBean.RADIO || type == MessageBean.RADIO_NEW) {
            MobclickAgent.onEvent(ChatActivity.this, EventID.SEND_RADIO);
        }
        try {

            String content = filename;

            RequestParams rp = ac.getRequestParams();
            rp.put("file", new File(filename));
            if (type == MessageBean.RADIO_NEW) {
                rp.put("thumb", new File(thumb));
                content = new Gson().toJson(new MessageBean.RadioBean(thumb, filename));
            }
            rp.put("toId", deviceId);
            rp.put("msgType", type);
            rp.put("voiceTime", (int) recodeTime);
            rp.put("sex", userBean.getSex());

            final MessageBean mb = new MessageBean(ac.deviceId, deviceId, content, "", "", type, (int) recodeTime, userBean.getSex());
            ac.httpClient.post(URLS.UPLOADVOICEFILE, rp, new JsonHttpResponseHandler() {

                @Override
                public void onStart() {
                    adapter.getList().add(mb);
                    adapter.notifyDataSetChanged();

                    ChatlistDao.getInstance(getApplicationContext()).addChatListBean(mb, deviceId);
                    ChatDao.getInstance(getApplicationContext()).addMessage(ac.deviceId, mb);

                    scrollToLast();
                }

                @Override
                public void onSuccess(JSONObject jo) {
                    try {
                        int status = jo.getInt(ResultCode.STATUS);
                        switch (status) {
                            case ResultCode.SUCCESS:
                                mb.setLoading(MessageBean.LOADING_DOWNLOADED);
                                notifyData();
                                if (jo.has(ResultCode.INFO)) {
                                    if (jo.optInt(ResultCode.INFO) == ResultCode.DISCONNECT) {
                                        Intent intent = new Intent(BroadCastUtil.CLOSECHAT);
                                        intent.putExtra(StaticUtil.DEVICEID, deviceId);
                                        sendBroadcast(intent);
                                    }
                                }
                                break;
                            case ResultCode.FAIL:
                                mb.setLoading(MessageBean.LOADING_DOWNLOADFAIL);
                                notifyData();
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mb.setLoading(MessageBean.LOADING_DOWNLOADFAIL);
                        notifyData();
                    } finally {
                        ChatDao.getInstance(getApplicationContext()).updateMessage(mb);
                    }
                }

                @Override
                public void onFailure() {
                    mb.setLoading(MessageBean.LOADING_DOWNLOADFAIL);
                    ChatDao.getInstance(getApplicationContext()).updateMessage(mb);
                    notifyData();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<Integer> lstImageItem = null;

    @Click(R.id.face_btn)
    void faceBtnClick() {
        closeInput();
        closeMore(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                face_grid.setVisibility(View.VISIBLE);
                lstImageItem = new ArrayList<>();
                for (int i = 1; i <= 25; i++) {
                    lstImageItem.add(getResources().getIdentifier("face_" + i, "drawable", getPackageName()));
                }
                FaceAdapter saImageItems = new FaceAdapter(ChatActivity.this);
                saImageItems.setList(lstImageItem);
                final SwingBottomInAnimationAdapter t1 = new SwingBottomInAnimationAdapter(saImageItems);
                t1.setAbsListView(face_grid);
                t1.getViewAnimator().setInitialDelayMillis(0);
                t1.getViewAnimator().setAnimationDelayMillis(25);
                t1.getViewAnimator().setAnimationDurationMillis(100);
                face_grid.setAdapter(t1);
                face_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MobclickAgent.onEvent(ChatActivity.this, EventID.SEND_FACE);
                        sendText("face_" + (position + 1), 3);
                        closeGridView();
                    }
                });
            }
        });


    }


    @Click(R.id.content_et)
    void contentETClick() {
        closeGridView();
    }

    boolean closeGridView() {
        if (face_grid.getChildCount() > 0) {
            for (int i = 0; i < face_grid.getChildCount(); i++) {
                ValueAnimator animator = ObjectAnimator.ofFloat(face_grid.getChildAt(face_grid.getChildCount() - 1 - i), "translationY", 0, face_grid.getMeasuredHeight() >> 1);
                ValueAnimator animator1 = ObjectAnimator.ofFloat(face_grid.getChildAt(face_grid.getChildCount() - 1 - i), "alpha", 1, 0);
                AnimatorSet set = new AnimatorSet();
                set.setDuration(100);
                set.playTogether(animator, animator1);
                set.setStartDelay(i * 25);
                if (i == face_grid.getChildCount() - 1) {
                    set.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            lstImageItem.clear();
                            SwingBottomInAnimationAdapter adapter1 = (SwingBottomInAnimationAdapter) face_grid.getAdapter();
                            adapter1.notifyDataSetChanged();
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                set.start();
            }
            return true;
        }
        return false;
    }


    @Receiver(actions = BroadCastUtil.DISCONNECT)
    void disconnect() {
        ToastUtil.toast(ChatActivity.this, getString(R.string.guaiwolo), R.drawable.wunai);
    }

    @Touch
    boolean listview() {
        closeInput();
        closeGridView();
        return false;
    }

    @UiThread
    void scrollToLast() {
        if (adapter.getItemCount() > 0) {
            listview.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }

    @UiThread
    void scrollToPosition(int from) {
        listview.scrollToPosition(from);
        listview.smoothScrollToPosition(0);
    }

    @OptionsItem
    public void renzheng() {
        RequestParams params = ac.getRequestParams();
        params.put("deviceId", deviceId);
        params.put("month", 3);
        params.put("girl", "true");
        ac.httpClient.post(URLS.SETVIP, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jo) {
                super.onSuccess(jo);
                ToastUtil.toast(ChatActivity.this, jo.toString());
            }
        });
    }
}