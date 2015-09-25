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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
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

import org.androidannotations.annotations.EApplication;

import a.b.c.DynamicSdkManager;

@EApplication
public class AppClass extends Application {

    public AsyncHttpClient httpClient;
    public String deviceId;
    public CommonShared cs;

    public static final String MANAGER = "manager";

    public static final DisplayImageOptions options_no_default = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisk(true).build();

    public static Context context;

    public void onCreate() {
//		LeakCanary.install(this);

        try {
            DynamicSdkManager.onCreate(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        cs = new CommonShared(this);

        initDeviceid();

        httpClient = new AsyncHttpClient();
        httpClient.setCookieStore(new PersistentCookieStore(this));
        httpClient.setURLEncodingEnabled(false);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(
                options_no_default).build();
        L.writeLogs(false);
        L.writeDebugLogs(false);
        ImageLoader.getInstance().init(config);

        super.onCreate();

        this.context = getApplicationContext();
    }

    public void initDeviceid(){
        if (cs.getISMANAGER() == CommonShared.ON) {
            deviceId = MANAGER;
        }else {
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

    public void startService() {
        Intent i = new Intent(this, PushService_.class);
        i.setAction(PushService.ACTION_START);
        startService(i);
        bindService(i, mConnection,
                Context.BIND_AUTO_CREATE);
    }

    public void stopService() {
        if (mService != null)
            unbindService(mConnection);
        mService = null;
        PushService.actionStop(this);
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
