package com.xl.db;

import android.content.Context;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.xl.bean.MessageBean;
import com.xl.util.LogUtil;

import java.util.List;

/**
 * Created by sbb on 2015/6/4.
 */
public class ChatDao extends BaseDao<MessageBean, Integer> {

    private static ChatDao instance;

    private ChatDao(Context context) {
        super(context);
    }

    public static synchronized ChatDao getInstance(Context context) {
        if (instance == null) {
            instance = new ChatDao(context.getApplicationContext());
        }
        return instance;
    }

    public void addMessage(String userId, MessageBean mb) {
        mb.setUserId(userId);
        dao.create(mb);
        LogUtil.d(mb.getId()+"");
    }

    public void updateMessage(MessageBean mb) {
        dao.update(mb);
    }

    public List<MessageBean> getHistoryMsg(String userId, String otherId, int lastId) {
        try {
            QueryBuilder builder = dao.queryBuilder().orderBy("id", false);
            Where where = builder.where();
            where.and(where.eq("userId", userId), where.or(where.eq("fromId", otherId), where.eq("toId", otherId)));
            if (lastId > -1) {
                where.and().lt("id", lastId);
            }
            builder.limit(15l);
            return builder.query();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
