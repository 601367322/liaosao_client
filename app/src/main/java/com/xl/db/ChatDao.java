package com.xl.db;

import android.content.Context;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.xl.application.AppClass;
import com.xl.bean.MessageBean;
import com.xl.util.LogUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        LogUtil.d(mb.getId() + "");
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
            List<MessageBean> list = builder.query();

            for (MessageBean mb : list) {
                if (mb.getState() == 0) {
                    mb.setState(1);
                    dao.update(mb);
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteMessage(MessageBean mb) {
        dao.delete(mb);
    }

    public void deleteMessage(String deviceId) {
        try {
            DeleteBuilder builder = dao.deleteBuilder();
            Where where = builder.where();
            where.or(where.eq("fromId", deviceId), where.eq("toId", deviceId));
            dao.delete(builder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUnCount(String fromId, String toId) {
        Map<String, Object> map = new HashMap<>();
        map.put("fromId", fromId);
        map.put("toId", toId);
        map.put("state", 0);
        return dao.queryForFieldValues(map).size();
    }

    public int getAllUnCount(String deviceId) {
        try {
            QueryBuilder builder = dao.queryBuilder();
            Where where = builder.where();

            where.and(where.eq("toId", deviceId), where.eq("state", 0), where.ne("fromId", AppClass.MANAGER));
            return dao.query(builder.prepare()).size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
