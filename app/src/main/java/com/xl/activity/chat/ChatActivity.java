package com.xl.activity.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.xl.activity.R;
import com.xl.activity.base.BaseActivity;
import com.xl.anim.TranAnimationAdapter;
import com.xl.bean.MessageBean;
import com.xl.util.BroadCastUtil;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.URLS;
import com.xl.util.Utils;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

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
		content_et.setOnEditorActionListener(this);

		adapter = new ChatAdapters(this, new ArrayList<String>());
        TranAnimationAdapter t = new TranAnimationAdapter(adapter,listview);
		listview.setAdapter(t);

        send_btn.setEnabled(false);
	}
	
    @Receiver(actions = BroadCastUtil.NEWMESSAGE)
    public void newMessage(Intent intent){
        MessageBean mb=(MessageBean) intent.getExtras().getSerializable("bean");
        adapter.getList().add(mb);
        adapter.notifyDataSetChanged();
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
            toast("太快了，休息一下~");
            return;
        }

		final String context_str = content_et.getText().toString().trim();
		if (context_str.length() == 0) {
			toast("请输入内容");
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
	
}
