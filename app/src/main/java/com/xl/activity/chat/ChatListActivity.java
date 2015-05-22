package com.xl.activity.chat;

import android.widget.BaseAdapter;
import android.widget.ListView;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.bean.ChatListBean;
import com.xl.custom.swipe.SwipeRefreshLayout;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by sbb on 2015/5/6.
 */
@EActivity(R.layout.activity_chat_list)
public class ChatListActivity extends BaseBackActivity implements SwipeRefreshLayout.OnRefreshListener {

    RuntimeExceptionDao<ChatListBean, Integer> chatListDao;

    @ViewById
    ListView listview;

    @ViewById
    SwipeRefreshLayout refresh;

    @Override
    protected void init() {
        refresh.setOnRefreshListener(this);
        refresh();
        chatListDao = getHelper().getRuntimeExceptionDao(ChatListBean.class);
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
    public void listview() {
        toast("123123");
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ChatListBean> list = chatListDao.queryForAll();
                SwingBottomInAnimationAdapter t = new SwingBottomInAnimationAdapter(new ChatListAdapters(list, ChatListActivity.this));
                t.setmGridViewPossiblyMeasuring(false);
                t.setAbsListView(listview);
                complete(t);
            }
        }).start();
    }

    @UiThread
    public void complete(BaseAdapter adapter){
        listview.setAdapter(adapter);
        refresh.setRefreshing(false);
    }

}
