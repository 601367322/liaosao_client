package com.xl.activity.chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.xl.activity.R;
import com.xl.activity.base.BaseActivity;
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

	protected void init() {
		content_et.setOnEditorActionListener(this);

		adapter = new ChatAdapters(this, new ArrayList<String>());
		listview.setAdapter(adapter);
		
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
			ValueAnimator va = ObjectAnimator.ofFloat(send_btn, "alpha", 0.5f,
					1.0f).setDuration(200);
			va.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					send_btn.setEnabled(true);
				}
			});
			va.start();
		} else if (text.toString().length() == 0 && send_btn.getAlpha() > 0.5f) {
			ValueAnimator va = ObjectAnimator.ofFloat(send_btn, "alpha", 1.0f,
					0.5f).setDuration(200);
			va.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation) {
					send_btn.setEnabled(false);
				}
			});
			va.start();
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
		final String context_str = content_et.getText().toString().trim();
		if (context_str.length() == 0) {
			toast("请输入内容");
			content_et.requestFocus();
			return;
		}

        //LogUtil.d(new Gson().toJson(new MessageBean(ac.deviceId, deviceId, context_str)).toString());

		RequestParams rp = ac.getRequestParams();
		rp.put("content", new Gson().toJson(new MessageBean(ac.deviceId, deviceId, context_str)).toString());
		rp.put("toId", deviceId);
		rp.put("fromId", ac.deviceId);
		ac.httpClient.post(URLS.SENDMESSAGE, rp, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject jo) {
				Toast.makeText(ChatActivity.this, jo.toString(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStart() {
				MessageBean mb=new MessageBean(new Date().getTime()+"",ac.deviceId,deviceId,ac.deviceId,context_str,Utils.dateFormat.format(new Date()),"","",1);
				adapter.getList().add(mb);
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure() {
			}
		});
	}
	
}
