package com.xl.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import com.xl.application.AppClass;
import com.xl.util.LogUtil;
import com.xl.util.URLS;

@EService
public class PushService extends Service {

	@App
	AppClass ac;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.d("onStartCommand");
		LogUtil.d(ac.getBean().getNumber() + "");
		if (intent != null) {
			if (intent.getAction().equals(ACTION_STOP) == true) {
				stop();
				stopSelf();
			} else if (intent.getAction().equals(ACTION_START) == true)
				start();
			else if (intent.getAction().equals(ACTION_KEEPALIVE) == true)
				keepAlive();
			else if (intent.getAction().equals(ACTION_RECONNECT) == true)
				reconnectIfNecessary();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public static final String TAG = "KeepAliveService";


	private static final String HEARTBEATREQUEST = "#";
	private static final String HEARTBEATRESPONSE = "*";
	public static final String ACTION_START = "com.xl.keepalive.START";
	public static final String ACTION_STOP = "com.xl.keepalive.STOP";
	public static final String ACTION_KEEPALIVE = "com.xl.keepalive.KEEP_ALIVE";
	public static final String ACTION_RECONNECT = "com.xl.keepalive.RECONNECT";

	private ConnectivityManager mConnMan;
	private NotificationManager mNotifMan;

	private boolean mStarted;
	private ConnectionThread mConnection;

	private static final long KEEP_ALIVE_INTERVAL = 1000 * 15;

	public static final long INITIAL_RETRY_INTERVAL = 1000 * 5;
	private static final long MAXIMUM_RETRY_INTERVAL = 1000 * 60;

	@Pref
	ServicePref_ mPrefs;

	private static final int NOTIF_CONNECTED = 0;

	public static void actionStart(Context ctx) {
		Intent i = new Intent(ctx, PushService_.class);
		i.setAction(ACTION_START);
		ctx.startService(i);
	}

	public static void actionStop(Context ctx) {
		Intent i = new Intent(ctx, PushService_.class);
		i.setAction(ACTION_STOP);
		ctx.startService(i);
	}

	public static void actionPing(Context ctx) {
		Intent i = new Intent(ctx, PushService_.class);
		i.setAction(ACTION_KEEPALIVE);
		ctx.startService(i);
	}

	public static void actionReConnect(Context ctx) {
		Intent i = new Intent(ctx, PushService_.class);
		i.setAction(ACTION_RECONNECT);
		ctx.startService(i);
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mConnMan = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		mNotifMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		handleCrashedService();
	}

	private void handleCrashedService() {
		if (wasStarted() == true) {
			/*
			 * We probably didn't get a chance to clean up gracefully, so do it
			 * now.
			 */
			hideNotification();
			stopKeepAlives();

			/* Formally start and attempt connection. */
			start();
		}
	}

	@Override
	public void onDestroy() {
		log("Service destroyed (started=" + mStarted + ")");

		if (mStarted == true)
			stop();

	}

	private void log(String message) {
		LogUtil.d(TAG, message);

	}

	private boolean wasStarted() {
		return mPrefs.isStarted().get();
	}

	private void setStarted(boolean started) {
		mPrefs.isStarted().put(started);
		mStarted = started;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return stub;
	}

	private synchronized void start() {
		if (mStarted == true) {
			LogUtil.d(TAG, "Attempt to start connection that is already active");
			return;
		}

		mPrefs.clear();

		setStarted(true);

		registerReceiver(mConnectivityChanged, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));

		log("Connecting...");

		mConnection = new ConnectionThread(URLS.IP, URLS.PORT);
		mConnection.start();
	}

	private synchronized void stop() {
		if (mStarted == false) {
			LogUtil.d(TAG, "Attempt to stop connection not active.");
			return;
		}

		setStarted(false);

		unregisterReceiver(mConnectivityChanged);
		cancelReconnect();

		if (mConnection != null) {
			mConnection.abort();
			mConnection = null;
		}
	}

	private synchronized void keepAlive() {
		try {
			if (mStarted == true && mConnection != null)
				mConnection.sendKeepAlive();
		} catch (IOException e) {
		}
	}

	private void startKeepAlives() {
		Intent i = new Intent();
		i.setClass(this, PushService_.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.RTC, System.currentTimeMillis()
				+ KEEP_ALIVE_INTERVAL, KEEP_ALIVE_INTERVAL, pi);
	}

	private void stopKeepAlives() {
		Intent i = new Intent();
		i.setClass(this, PushService_.class);
		i.setAction(ACTION_KEEPALIVE);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.cancel(pi);
	}

	public void scheduleReconnect(long startTime) {
		long interval = mPrefs.retryInterval().get();

		long now = System.currentTimeMillis();
		long elapsed = now - startTime;

		if (elapsed < interval)
			interval = Math.min(interval + INITIAL_RETRY_INTERVAL,
					MAXIMUM_RETRY_INTERVAL);
		else
			interval = INITIAL_RETRY_INTERVAL;

		log("Rescheduling connection in " + interval + "ms.");

		mPrefs.retryInterval().put(interval);

		Intent i = new Intent();
		i.setClass(this, PushService_.class);
		i.setAction(ACTION_RECONNECT);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.set(AlarmManager.RTC, now + interval, pi);
	}

	public void cancelReconnect() {
		Intent i = new Intent();
		i.setClass(this, PushService_.class);
		i.setAction(ACTION_RECONNECT);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmMgr.cancel(pi);
	}

	private synchronized void reconnectIfNecessary() {
		if (mStarted == true && mConnection == null) {
			log("Reconnecting...");

			mConnection = new ConnectionThread(URLS.IP, URLS.PORT);
			mConnection.start();
		}
	}

	private BroadcastReceiver mConnectivityChanged = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				log("网络状态已经改变");
				ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = connectivityManager.getActiveNetworkInfo();
				if (info != null && info.isAvailable()) {
					String name = info.getTypeName();
					log("当前网络名称：" + name);
					reconnectIfNecessary();
				} else {
					log("没有可用网络");
				}
			}
		}
	};

	private void hideNotification() {
		mNotifMan.cancel(NOTIF_CONNECTED);
	}

	private class ConnectionThread extends Thread {
		private final Socket mSocket;
		private final String mHost;
		private final int mPort;

		private volatile boolean mAbort = false;

		public ConnectionThread(String host, int port) {
			mHost = host;
			mPort = port;
			mSocket = new Socket();
		}

		public boolean isConnected() {
			return mSocket.isConnected();
		}

		private boolean isNetworkAvailable() {
			NetworkInfo info = mConnMan.getActiveNetworkInfo();
			if (info == null)
				return false;

			return info.isConnected();
		}

		public void run() {
			Socket s = mSocket;

			long startTime = System.currentTimeMillis();

			try {
				s.connect(new InetSocketAddress(mHost, mPort), 20000);

				log("Connection established to " + s.getInetAddress() + ":"
						+ mPort);

				startKeepAlives();

				// /*InputStream in = s.getInputStream();*/
				BufferedReader in = new BufferedReader(new InputStreamReader(
						s.getInputStream()));

				Handler handler=new Handler(PushService.this, mSocket);
				
				handler.startConnect();
				
				String str = null;
				while ((str = in.readLine()) != null) {
					log(str);
					if (str.equals(HEARTBEATREQUEST)
							|| str.equals(HEARTBEATRESPONSE)) {
						continue;
					}
					handler.messageReceived(str);
				}

				if (mAbort == false)
					log("Server closed connection unexpectedly.");
			} catch (IOException e) {
				log("Unexpected I/O error: " + e.toString());
			} finally {
				stopKeepAlives();
				hideNotification();

				if (mAbort == true)
					log("Connection aborted, shutting down.");
				else {
					try {
						s.close();
					} catch (IOException e) {
					}

					synchronized (PushService.this) {
						mConnection = null;
					}

					if (isNetworkAvailable() == true)
						scheduleReconnect(startTime);
				}
			}
		}

		public void sendKeepAlive() throws IOException {
			Socket s = mSocket;
			s.getOutputStream().write((HEARTBEATREQUEST + "\n").getBytes());

			log("Keep-alive sent.");
		}

		public void abort() {
			log("Connection aborting.");

			mAbort = true;

			try {
				mSocket.shutdownOutput();
			} catch (IOException e) {
			}

			try {
				mSocket.shutdownInput();
			} catch (IOException e) {
			}

			try {
				mSocket.close();
			} catch (IOException e) {
			}

			while (true) {
				try {
					join();
					break;
				} catch (InterruptedException e) {
				}
			}
		}
	}

	IPushService.Stub stub = new IPushService.Stub() {
		public boolean isConnected() {
			if (mStarted && mConnection != null && mConnection.isConnected()) {
				return true;
			}
			return false;
		}
	};
}