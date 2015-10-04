package com.xl.activity.chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.application.AppClass;
import com.xl.bean.ChatListBean;
import com.xl.bean.MessageBean;
import com.xl.custom.swipe.SwipeRefreshLayout;
import com.xl.db.ChatlistDao;
import com.xl.util.BroadCastUtil;
import com.xl.util.StaticUtil;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ItemLongClick;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by sbb on 2015/5/6.
 */
@OptionsMenu(R.menu.chatlist_menu)
@EActivity(R.layout.activity_chat_list)
public class ChatListActivity extends BaseBackActivity implements SwipeRefreshLayout.OnRefreshListener {

    @ViewById
    ListView listview;

    ChatListAdapters adapter;

    @ViewById
    SwipeRefreshLayout refresh;

    @Override
    protected void init() {
        refresh.setOnRefreshListener(this);
        refresh();
    }

    @UiThread
    public void refresh() {
        refresh.setRefreshing(true);
    }

    @Override
    public int getActionBarTitle() {
        return R.string.chat_list_title;
    }

    @ItemClick
    public void listview(int position) {
        if (adapter != null) {
            ChatListBean bean = adapter.getItem(position);
            ChatActivity_.intent(this).deviceId(bean.getDeviceId()).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();
        }
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ChatListBean> list = ChatlistDao.getInstance(getApplicationContext()).queryForAll();
                complete(adapter = new ChatListAdapters(list, ChatListActivity.this));
            }
        }).start();
    }

    @UiThread
    public void complete(BaseAdapter adapter) {
        listview.setAdapter(adapter);
        refresh.setRefreshing(false);
    }

    //刷新列表
    @Receiver(actions = {BroadCastUtil.NEWMESSAGE, BroadCastUtil.REFRESHCHATLIST})
    public void newMessage(Intent intent) {
        String deviceId = null;
        if (intent.getAction().equals(BroadCastUtil.NEWMESSAGE)) {
            MessageBean mb = (MessageBean) intent.getExtras().getSerializable(StaticUtil.BEAN);
            deviceId = mb.getFromId();
        } else if (intent.getAction().equals(BroadCastUtil.REFRESHCHATLIST)) {
            deviceId = intent.getStringExtra(StaticUtil.DEVICEID);
        }

        if (deviceId.equals(AppClass.MANAGER)) {
            return;
        }

        if (adapter != null) {
            List<ChatListBean> list = adapter.getList();
            boolean has = false;
            for (ChatListBean bean : list) {
                if (bean.getDeviceId().equals(deviceId)) {
                    has = true;
                    ChatListBean temp = ChatlistDao.getInstance(getApplicationContext()).queryBeanForDeviceId(bean.getDeviceId());
                    if (temp != null) {
                        bean.setContent(temp.getContent());
                        bean.setNum(temp.getNum());
                        adapter.notifyDataSetChanged();
                    }
                }
            }
            if (!has) {
                ChatListBean temp = ChatlistDao.getInstance(getApplicationContext()).queryBeanForDeviceId(deviceId);
                if (temp != null) {
                    adapter.addFirst(temp);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @OptionsItem
    public void clear() {
        new AlertDialog.Builder(this).setTitle(getString(R.string.kiding)).setIcon(R.drawable.dialog_icon).setMessage(getString(R.string.are_you_sure_clear_all)).setPositiveButton(getString(R.string.papa_do_not_chat), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ChatlistDao.getInstance(getApplicationContext()).deleteAll();
                adapter.clear();
            }
        }).setNegativeButton(getString(R.string.not_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }

    @ItemLongClick(R.id.listview)
    public void listview_long(final int position) {
        new AlertDialog.Builder(this).setTitle(getString(R.string.kiding)).setIcon(R.drawable.dialog_icon).setMessage(getString(R.string.are_you_sure_clear_this)).setPositiveButton(getString(R.string.papa_do_not_chat), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ChatlistDao.getInstance(getApplicationContext()).deleteById(adapter.getItem(position));
                adapter.remove(position);
            }
        }).setNegativeButton(getString(R.string.not_sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }
}
