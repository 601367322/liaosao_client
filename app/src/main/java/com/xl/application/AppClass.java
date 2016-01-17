package com.xl.application;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;
import com.xl.activity.share.CommonShared;
import com.xl.bean.SharedBean.Bean;
import com.xl.db.DBHelper;
import com.xl.service.IPushService;
import com.xl.service.PushService;
import com.xl.service.PushService_;
import com.xl.util.DeviceUuidFactory;
import com.xl.util.Utils;

import org.androidannotations.annotations.EApplication;

@EApplication
public class AppClass extends Application {

    public AsyncHttpClient httpClient;
    public String deviceId;
    public CommonShared cs;

    public static final String MANAGER = "manager";

    public static Context context;

    public void onCreate() {
        super.onCreate();

        DBHelper helper = new DBHelper(this);
        helper.init(this);

        cs = new CommonShared(this);

        initDeviceid();

        httpClient = new AsyncHttpClient();
        httpClient.setCookieStore(new PersistentCookieStore(this));
        httpClient.setURLEncodingEnabled(false);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(
                Utils.options_no_default).build();
        L.writeLogs(false);
        L.writeDebugLogs(false);
        ImageLoader.getInstance().init(config);

        this.context = getApplicationContext();
    }

    public void initDeviceid() {
        if (cs.getISMANAGER() == CommonShared.ON) {
            deviceId = MANAGER;
        } else {
            if ((deviceId = getBean().getDeviceId()).equals("")) {
                getBean().setDeviceId((deviceId = new DeviceUuidFactory(this).getDeviceUuid().toString())).commit(this);
            }
        }
    }

    public RequestParams getRequestParams() {
        RequestParams rp = new RequestParams();
        rp.put("deviceId", deviceId);
        return rp;
    }

    public Bean getBean() {
        return OpenHelperManager.getHelper(this, DBHelper.class).getShared().getBean();
    }

    /**
     * 向Service发送Message的Messenger对象
     */
    public IPushService mService = null;

    public CounterServiceConnection mConnection = null;

    public class CounterServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IPushService.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    }

    public void startService() {
        //开启service并且绑定service
        bindService(PushService.actionStart(this), mConnection = new CounterServiceConnection(),
                Context.BIND_AUTO_CREATE);
    }

    public void stopService() {
        //先取消绑定service
        if (mConnection != null)
            unbindService(mConnection);
        mConnection = null;
        mService = null;
        //关闭service
        PushService.actionStop(this);
    }

    public void bindServices() {
        //单独绑定service
        Intent i = new Intent(this, PushService_.class);
        if (mConnection == null)
            mConnection = new CounterServiceConnection();
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }

    public boolean isOnline() {
        try {
            if (mService == null) {
                return false;
            }
            return mService.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
