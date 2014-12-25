package com.xl.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.xl.bean.SharedBean.Bean;
import com.xl.db.DBHelper;
import com.xl.service.IPushService;
import com.xl.service.PushService;
import com.xl.service.PushService_;

import org.androidannotations.annotations.EApplication;

@EApplication
public class AppClass extends Application {
	
	public AsyncHttpClient httpClient;
	public String deviceId;

	public void onCreate() {
		if((deviceId=getBean().getDeviceId()).equals("")){
			TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
			getBean().setDeviceId((deviceId=tm.getDeviceId())).commit(this);
		}

		httpClient=new AsyncHttpClient();
		httpClient.setCookieStore(new PersistentCookieStore(this));
	};

	public RequestParams getRequestParams(){
		RequestParams rp=new RequestParams();
		rp.put("deviceId", deviceId);
		return rp;
	}
	
	public Bean getBean(){
		return OpenHelperManager.getHelper(this, DBHelper.class).getShared().getBean();
	}
	
	 /** 向Service发送Message的Messenger对象 */  
    IPushService mService = null;
  
    private ServiceConnection mConnection = new ServiceConnection() {  
        public void onServiceConnected(ComponentName className, IBinder service) {  
            mService = IPushService.Stub.asInterface(service);  
        }  
        /**异常时才会调用**/
        public void onServiceDisconnected(ComponentName className) {  
            mService = null;  
        }  
    };  
    
    public void startService(){
    	Intent i = new Intent(this,PushService_.class);
    	i.setAction(PushService.ACTION_START);
    	startService(i);
    	bindService(i, mConnection,  
                Context.BIND_AUTO_CREATE);  
    }
    
    public void stopService(){
    	if(mService!=null)
    		unbindService(mConnection);
    	mService=null;
		PushService.actionStop(this);
    }
    
    public boolean isOnline(){
    	try {
			if(mService==null){
				return false;
			}
			return mService.isConnected();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return false;
    }
    
}
