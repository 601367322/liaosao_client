package com.xl.activity.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.gauss.recorder.MicRealTimeListenerSpeex;
import com.gauss.recorder.SpeexRecorder;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.xl.activity.R;
import com.xl.activity.base.BaseActivity;
import com.xl.bean.MessageBean;
import com.xl.util.BroadCastUtil;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.LogUtil;
import com.xl.util.StaticFactory;
import com.xl.util.StaticUtil;
import com.xl.util.URLS;
import com.xl.util.Utils;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

@EActivity(R.layout.chat_activity)
public class ChatActivity extends BaseActivity implements
		OnEditorActionListener {

	@ViewById
	EditText content_et;
	@ViewById
	ImageButton send_btn;
	@ViewById
	ListView listview;
	@Extra
	String deviceId;

	ChatAdapters adapter;

    AtomicBoolean changeing1 = new AtomicBoolean(false);
    AtomicBoolean changeing2 = new AtomicBoolean(false);

	protected void init() {

        setSwipeBackEnable(false);

		content_et.setOnEditorActionListener(this);

		adapter = new ChatAdapters(this, new ArrayList<String>());
        SwingBottomInAnimationAdapter t = new SwingBottomInAnimationAdapter(adapter,listview);
		listview.setAdapter(t);

        send_btn.setEnabled(false);

	}

    @Receiver(actions = BroadCastUtil.NEWMESSAGE)
    public void newMessage(Intent intent){
        MessageBean mb=(MessageBean) intent.getExtras().getSerializable("bean");
        adapter.getList().add(mb);
        adapter.notifyDataSetChanged();
    }

    @Receiver(actions = BroadCastUtil.CLOSECHAT)
    public void closeChat(Intent intent){
        String other=intent.getStringExtra(StaticUtil.DEVICEID);
        if(deviceId.equals(other)){
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
            if(changeing1.compareAndSet(false,true)) {
                send_btn.animate().alpha(1.0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        send_btn.setEnabled(true);
                        changeing1.compareAndSet(true,false);
                    }
                }).start();
            }
		} else if (text.toString().length() == 0 && send_btn.getAlpha() > 0.5f) {
            if(changeing2.compareAndSet(false, true)) {
                send_btn.animate().alpha(0.5f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        send_btn.setEnabled(false);
                        changeing2.compareAndSet(true,false);
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

        if(isFastDoubleClick()){
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
		rp.put("content", new Gson().toJson(new MessageBean(ac.deviceId, deviceId, context_str)).toString());
		rp.put("toId", deviceId);
		rp.put("fromId", ac.deviceId);
		ac.httpClient.post(URLS.SENDMESSAGE, rp, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject jo) {
//				Toast.makeText(ChatActivity.this, jo.toString(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStart() {
				MessageBean mb=new MessageBean(new Date().getTime()+"",ac.deviceId,deviceId,ac.deviceId,context_str,Utils.dateFormat.format(new Date()),"","",1);
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
    public void backItem(){
        showFinshDialog();
    }

    public void showFinshDialog(){
        new AlertDialog.Builder(this).setTitle(getString(R.string.kiding)).setIcon(R.drawable.dialog_icon).setMessage(getString(R.string.are_you_fuck_sure_do_not_chat)).setPositiveButton(getString(R.string.papa_do_not_chat),new DialogInterface.OnClickListener() {
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
    private static int MIX_TIME = 1; // 最短录制时间，单位秒，0为无时间限制，建议设为1

    @Touch(R.id.voice_btn)
    boolean voiceBtnTouch(View view,MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (recorderInstance==null||!recorderInstance.isRecording()) {

                    File file = new File(StaticFactory.APKCardPathChat);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    filename = StaticFactory.APKCardPathChat + String.valueOf((String
                            .valueOf(new Date().getTime()) + ".spx")
                            .hashCode());


                    recorderInstance = new SpeexRecorder(filename,new MicRealTimeListenerSpeex() {

                        @Override
                        public void getMicRealTimeSize(int size) {
                            voiceValue = size;
                        }
                    });
                    Thread th = new Thread(recorderInstance);
                    th.start();
                    recorderInstance.setRecording(true);

                    mythread();
                }

                break;
            case MotionEvent.ACTION_UP:
                imgHandle.sendEmptyMessage(0);
                break;
        }
        return false;
    }

    // 录音计时线程
    public void mythread() {
        recordThread = new Thread(ImgThread);
        recordThread.start();
    }

    private Runnable ImgThread = new Runnable() {

        public void run() {
            recodeTime = 0.0f;
            while (recorderInstance!=null&&recorderInstance.isRecording()) {
                if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
                    imgHandle.sendEmptyMessage(0);
                } else {
                    try {
                        Thread.sleep(200);
                        recodeTime += 0.2;
                        if (recorderInstance!=null&&recorderInstance.isRecording()) {
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
//        if (voiceValue >600) {
//            voicesize.setImageResource(R.drawable.hua7);
//        } else if (voiceValue > 500) {
//            voicesize.setImageResource(R.drawable.hua6);
//        } else if (voiceValue > 400) {
//            voicesize.setImageResource(R.drawable.hua5);
//        } else if (voiceValue > 300) {
//            voicesize.setImageResource(R.drawable.hua4);
//        } else if (voiceValue > 200) {
//            voicesize.setImageResource(R.drawable.hua3);
//        } else if (voiceValue > 100) {
//            voicesize.setImageResource(R.drawable.hua2);
//        } else if (voiceValue > 0) {
//            voicesize.setImageResource(R.drawable.hua1);
//        }
    }

    Handler imgHandle = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (recorderInstance!=null&&recorderInstance.isRecording()) {
                        recorderInstance.setRecording(false);
                        voiceValue = 0;
                        if (recodeTime < MIX_TIME) {
                            File o = new File(filename);
                            if (o.exists()) {
                                o.delete();
                            }
                        } else {
                        }
                    }
                    break;
                case 1:
                    setDialogImage();
                    break;
                default:
                    break;
            }

        }
    };

}
