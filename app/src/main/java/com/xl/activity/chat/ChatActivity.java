package com.xl.activity.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.gauss.recorder.MicRealTimeListenerSpeex;
import com.gauss.recorder.SpeexRecorder;
import com.github.johnpersano.supertoasts.SuperToast;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.RequestParams;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.xl.activity.R;
import com.xl.activity.base.BaseActivity;
import com.xl.bean.MessageBean;
import com.xl.custom.MyAnimationView;
import com.xl.util.BroadCastUtil;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.LogUtil;
import com.xl.util.StaticFactory;
import com.xl.util.StaticUtil;
import com.xl.util.URLS;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

@EActivity(R.layout.chat_activity)
public class ChatActivity extends BaseActivity implements
        OnEditorActionListener,SensorEventListener {

    @ViewById
    EditText content_et;
    @ViewById
    ImageButton send_btn;
    @ViewById
    ListView listview;
    @Extra
    String deviceId = "A00000443A4BE6";
    @ViewById
    MyAnimationView ball_view;
    @ViewById
    View chat_ll1, chat_ll2,voice_anim_view;
    @ViewById
    TextView time_txt,send_voice_btn,cancle_btn;

    ChatAdapters adapter;

    AtomicBoolean changeing1 = new AtomicBoolean(false);
    AtomicBoolean changeing2 = new AtomicBoolean(false);

    protected void init() {

        ac.startService();

        if(ac.deviceId.equals("A00000443A4BE6")){
            deviceId="000000000000000";
        }

        setSwipeBackEnable(false);

        content_et.setOnEditorActionListener(this);

        adapter = new ChatAdapters(this, new ArrayList<String>());
        SwingBottomInAnimationAdapter t = new SwingBottomInAnimationAdapter(adapter, listview);
        t.setmGridViewPossiblyMeasuring(false);
        listview.setAdapter(t);

        send_btn.setEnabled(false);


    }

    @Receiver(actions = BroadCastUtil.NEWMESSAGE)
    public void newMessage(Intent intent) {
        MessageBean mb = (MessageBean) intent.getExtras().getSerializable("bean");
        adapter.getList().add(mb);
        adapter.notifyDataSetChanged();
    }

    @Receiver(actions = BroadCastUtil.CLOSECHAT)
    public void closeChat(Intent intent) {
        String other = intent.getStringExtra(StaticUtil.DEVICEID);
        if (deviceId.equals(other)) {
            content_et.setText("");
            content_et.setHint("对方觉得你的脸不行，已退出聊天！");
            content_et.setEnabled(false);
            send_btn.setEnabled(false);
            send_btn.setAlpha(0.5f);
            closeInput();
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

        RequestParams rp = ac.getRequestParams();
        rp.put("content", new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation().create().toJson(new MessageBean(ac.deviceId, deviceId, context_str)).toString());
        rp.put("toId", deviceId);
        rp.put("fromId", ac.deviceId);
        ac.httpClient.post(URLS.SENDMESSAGE, rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jo) {
//				Toast.makeText(ChatActivity.this, jo.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStart() {
                MessageBean mb = new MessageBean(ac.deviceId, deviceId,context_str,"", "", 0);
                adapter.getList().add(mb);
                adapter.notifyDataSetChanged();

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
                                send_btn.setEnabled(true);
                            }
                        }).start();
                    }
                }).start();

            }

            @Override
            public void onFailure() {
            }
        });
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
        showFinshDialog();
    }

    @OptionsItem(android.R.id.home)
    public void backItem() {
        showFinshDialog();
    }

    public void showFinshDialog() {
        new AlertDialog.Builder(this).setTitle(getString(R.string.kiding)).setIcon(R.drawable.dialog_icon).setMessage(getString(R.string.are_you_fuck_sure_do_not_chat)).setPositiveButton(getString(R.string.papa_do_not_chat), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scrollToFinishActivity();

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

    SpeexRecorder recorderInstance;
    String filename;
    Thread recordThread;//动画线程
    float recodeTime = 0.0f; // 录音的时间
    int voiceValue = 0; // 麦克风获取的音量值
    private static int MAX_TIME = 60; // 最长录制时间，单位秒，0为无时间限制
    private static int MIN_TIME = 1; // 最短录制时间，单位秒，0为无时间限制，建议设为1

    @Click(R.id.voice_btn)
    void voiceBtnClick() {
        closeInput();
        chat_ll1.animate().translationY(-chat_ll1.getHeight()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) chat_ll2.getLayoutParams();
                ValueAnimator whxyBouncer = ObjectAnimator.ofInt(0, (int) getResources().getDimension(R.dimen.chat_bottom_voice_height)).setDuration(500);
                whxyBouncer.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (recorderInstance == null || !recorderInstance.isRecording()) {

                            File file = new File(StaticFactory.APKCardPathChat);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            filename = StaticFactory.APKCardPathChat + String.valueOf((String
                                    .valueOf(new Date().getTime()) + ".spx")
                                    .hashCode());

                            recorderInstance = new SpeexRecorder(filename, new MicRealTimeListenerSpeex() {

                                @Override
                                public void getMicRealTimeSize(int size) {
                                    voiceValue = size;
                                }
                            });
                            Thread th = new Thread(recorderInstance);
                            th.start();
                            recorderInstance.setRecording(true);
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
                if(adapter1!=null){
                    animator.setListener(adapter1);
                }else{
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
        closeVoicePanl(send_voice_btn,0);
    }

    void closeVoicePanl(TextView btn,final int message){
        voice_anim_view.setVisibility(View.VISIBLE);
        voice_anim_view.setX(btn.getX());
        voice_anim_view.setY(btn.getY());
        recorderInstance.setRecording(false);
        float width =(getResources().getDisplayMetrics().widthPixels/getResources().getDimensionPixelSize(R.dimen.voice_btn_width))*2.5f;
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
            while (recorderInstance != null && recorderInstance.isRecording()) {
                if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
                    recorderInstance.setRecording(false);
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
                        if (recorderInstance != null && recorderInstance.isRecording()) {
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
        ball_view.setmHeight(voiceValue);
    }

    int allTime = 0;

    void updateTimeText() {
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (recorderInstance.isRecording() && allTime < MAX_TIME) {
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

                        SuperToast superToast = new SuperToast(ChatActivity.this);
                        superToast.setDuration(SuperToast.Duration.LONG);
                        superToast.setText(getString(R.string.your_JJ_so_short));
                        superToast.setIcon(R.drawable.weisuo, SuperToast.IconPosition.LEFT);
                        superToast.show();
                    } else {
                        try {
                            RequestParams rp = ac.getRequestParams();
                            rp.put("file", new File(filename));
                            rp.put("toId",deviceId);
                            final MessageBean mb = new MessageBean(ac.deviceId, deviceId, filename, "", "",1,(int)recodeTime);
                            ac.httpClient.post(URLS.UPLOADVOICEFILE, rp, new JsonHttpResponseHandler() {

                                @Override
                                public void onStart() {
                                    adapter.getList().add(mb);
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onSuccess(JSONObject jo) {
                                    mb.setLoading(1);
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onFailure() {
                                    adapter.getList().remove(mb);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    void revert(){
        //还原动画
        voice_anim_view.setVisibility(View.GONE);
        voice_anim_view.setScaleX(1f);
        voice_anim_view.setScaleY(1f);
        voiceValue = 0;//还原录音大小
        ball_view.setmHeight(voiceValue);//还原球动画
        allTime = 0;//还原录音时间
        time_txt.setText(allTime+"''");
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.stopArm();
        mSensorManager.unregisterListener(this);
    }

    SensorManager mSensorManager;
    Sensor mSensor;
    AudioManager audioManager;

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(audioManager==null){
            audioManager = (AudioManager) this
                    .getSystemService(Context.AUDIO_SERVICE);
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float range = event.values[0];
        if (range >= mSensor.getMaximumRange() && audioManager.getMode() != audioManager.MODE_NORMAL) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            adapter.replay();
        } else if(range < mSensor.getMaximumRange() && audioManager.getMode() != audioManager.MODE_IN_CALL){
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            adapter.replay();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
