package com.xl.activity.girl;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.activity.chat.ChatAdapters;
import com.xl.application.AppClass;
import com.xl.bean.MessageBean;
import com.xl.bean.UserBean_6;
import com.xl.custom.swipe.SwipeRefreshLayout;
import com.xl.db.ChatDao;
import com.xl.db.ChatlistDao;
import com.xl.db.UserTableDao;
import com.xl.recorder.FFmpegRecorderActivity_;
import com.xl.util.BroadCastUtil;
import com.xl.util.EventID;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.StaticUtil;
import com.xl.util.URLS;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

@EActivity(R.layout.girl_chat_activity)
public class GirlChatActivity extends BaseBackActivity implements
        SwipeRefreshLayout.OnRefreshListener {

    @ViewById
    RecyclerView listview;
    @ViewById
    SwipeRefreshLayout swipe;
    public static final int RADIO = 3;
    @SystemService
    NotificationManager notifManager;

    ChatAdapters adapter;

    UserTableDao userTableDao;
    UserBean_6 userBean;

    int lastId = -1; //已显示聊天记录

    String deviceId = AppClass.MANAGER;

    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userTableDao = UserTableDao.getInstance(this);
        userBean = userTableDao.getUserTableByDeviceId(ac.deviceId).getBean();
    }



    protected void init() {
        notifManager.cancel(deviceId.hashCode());

        adapter = new ChatAdapters(this, new ArrayList<MessageBean>());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listview.setLayoutManager(layoutManager);
        listview.setAdapter(adapter);

        EventBus.getDefault().register(this);

        swipe.setOnRefreshListener(this);

        refresh();

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
            new AlertDialog.Builder(GirlChatActivity.this).setIcon(R.drawable.beiju).setTitle(getString(R.string.beijua)).setMessage(getString(R.string.resend_message)).setPositiveButton(getString(R.string.resend_btn), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.getList().remove(mb);
                    ChatDao.getInstance(getApplicationContext()).deleteMessage(mb);
                    switch (mb.getMsgType()) {
                        case MessageBean.RADIO:
                        case MessageBean.VOICE:
                        case MessageBean.IMAGE:
                            filename = mb.getContent();
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

    @UiThread
    void notifyData() {
        adapter.notifyDataSetChanged();
        swipe.setRefreshing(false);
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
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


    @OnActivityResult(value = GirlChatActivity.RADIO)
    public void onRadioCallBack(int result, Intent data) {
        if (result == RESULT_OK) {
            filename = data.getStringExtra("filename");
            File fi = new File(filename);
            if (fi != null && fi.exists()) {
                sendFile(MessageBean.RADIO);
            }
            fi = null;
        }
    }

    @UiThread
    void sendFile(int type) {
        if (type == MessageBean.VOICE) {
            MobclickAgent.onEvent(GirlChatActivity.this, EventID.SEND_VOICE);
        } else if (type == MessageBean.IMAGE) {
            MobclickAgent.onEvent(GirlChatActivity.this, EventID.SEND_IMG);
        } else if (type == MessageBean.RADIO) {
            MobclickAgent.onEvent(GirlChatActivity.this, EventID.SEND_RADIO);
        }
        try {
            RequestParams rp = ac.getRequestParams();
            rp.put("file", new File(filename));
            rp.put("toId", deviceId);
            rp.put("msgType", type);
            rp.put("voiceTime", 0);
            rp.put("sex", userBean.getSex());
            final MessageBean mb = new MessageBean(ac.deviceId, deviceId, filename, "", "", type, 0, userBean.getSex());
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

    @Click
    public void radio_btn(){
        FFmpegRecorderActivity_.intent(this).startForResult(RADIO);
    }
}