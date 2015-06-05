package com.xl.db;

import android.content.Context;

import com.xl.bean.ChatListBean;
import com.xl.bean.MessageBean;
import com.xl.service.Handler;

import java.util.List;

/**
 * Created by sbb on 2015/6/4.
 */
public class ChatlistDao extends BaseDao<ChatListBean, Integer> {

    private static ChatlistDao instance;

    private ChatlistDao(Context context) {
        super(context);
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
}
