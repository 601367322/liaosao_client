package com.xl.activity;

import android.os.Handler;
import android.widget.Button;

import com.xl.activity.base.BaseActivity;
import com.xl.activity.chat.ChatActivity_;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.StaticUtil;
import com.xl.util.URLS;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.main)
public class MainActivity extends BaseActivity {
	@ViewById
	Button startBtn;
	@ViewById
	Button stopBtn;
	@ViewById
	Button isOnline;

	Handler handler=new Handler();

	protected void init(){
		ac.startService();
	}

	@Click
	void startBtn(){
		ac.startService();
	}

	@Click
	void connect(){
		ac.httpClient.post(URLS.JOINQUEUE,ac.getRequestParams(),new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jo) {
				try {
					int status=jo.getInt(ResultCode.STATUS);
					switch (status) {
					case ResultCode.SUCCESS:
						String deviceId=jo.getString(StaticUtil.OTHERDEVICEID);
						ChatActivity_.intent(MainActivity.this).deviceId(deviceId).start();
						break;
					case ResultCode.LOADING:
						toast("排队中,请等待");
						break;
					}
				} catch (Exception e) {
				}
			}
		});
	}

	public void checkOnline(){
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(ac.isOnline()){
					ChatActivity_.intent(MainActivity.this).start();
				}else{
					checkOnline();
				}
			}
		},500);
	}

	int i=1;
	@Click
	void isOnline(){
		isOnline.setText(ac.isOnline()+"");
		ac.getBean().setNumber(i++).commit(this);
//		LogUtil.d(ac.getBean().getNumber()+"");
//		
//		PushService_.actionPing(this);
	}
	@Click
	void stopBtn(){
		ac.stopService();
	}
}
