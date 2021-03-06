package com.xl.db;

import android.content.Context;

import com.j256.ormlite.stmt.QueryBuilder;
import com.xl.application.AppClass;
import com.xl.bean.ChatListBean;
import com.xl.bean.MessageBean;
import com.xl.bean.UserTable;
import com.xl.service.Handler;
import com.xl.util.StaticUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbb on 2015/6/4.
 */
public class ChatlistDao extends BaseDao<ChatListBean, Integer> {

    private static ChatlistDao instance;
    private AppClass ac;

    private ChatlistDao(Context context) {
        super(context);
        this.ac = (AppClass) context.getApplicationContext();
    }

    public static synchronized ChatlistDao getInstance(Context context) {
        if (instance == null) {
            instance = new ChatlistDao(context.getApplicationContext());
        }
        return instance;
    }

    public void addChatListBean(MessageBean mb, String deviceId) {
        ChatListBean chatListBean = null;
        try {

            List<ChatListBean> list = dao.queryForEq("deviceId", deviceId);
            if (list.size() > 0) {
                chatListBean = list.get(0);
            }
            if (chatListBean == null) {
                chatListBean = new ChatListBean();
            }

            chatListBean.setContent(Handler.getMsgContentType(mb));
            chatListBean.setDeviceId(deviceId);

            dao.createOrUpdate(chatListBean);//更新聊天记录列表
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ChatListBean> queryForAll() {
        List<ChatListBean> list = new ArrayList<>();
        try {
            QueryBuilder builder = dao.queryBuilder();
            builder.where().ne("deviceId", AppClass.MANAGER);
            builder.orderBy("id", false);
            list = dao.query(builder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (ChatListBean bean : list) {
            int count = ChatDao.getInstance(context).getUnCount(bean.getDeviceId(), ac.deviceId);
            bean.setNum(count);

            UserTable ut = UserTableDao.getInstance(context).getUserTableByDeviceId(bean.getDeviceId());
            bean.setFriend(ut);
        }
        return list;
    }

    public ChatListBean queryBeanForDeviceId(String deviceId) {
        List<ChatListBean> list = dao.queryForEq(StaticUtil.DEVICEID, deviceId);
        if (list.size() > 0) {
            ChatListBean bean = list.get(0);
            int count = ChatDao.getInstance(context).getUnCount(bean.getDeviceId(), ac.deviceId);
            bean.setNum(count);

            UserTable ut = UserTableDao.getInstance(context).getUserTableByDeviceId(bean.getDeviceId());
            bean.setFriend(ut);
            return bean;
        }
        return null;
    }

    public void deleteAll() {
        DBHelper.clearTable(context, ChatListBean.class);
        DBHelper.clearTable(context, MessageBean.class);
    }

    public void deleteById(ChatListBean bean) {
        dao.delete(bean);
        ChatDao.getInstance(context).deleteMessage(bean.getDeviceId());
    }
}
