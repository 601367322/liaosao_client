package com.xl.db;

import android.content.Context;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.Where;
import com.xl.bean.UnSuccessOrder;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shen on 2016/2/25.
 */
public class OrderDao extends BaseDao<UnSuccessOrder, Integer> {


    public OrderDao(Context context) {
        super(context);
    }

    private static OrderDao instance;

    public static synchronized OrderDao getInstance(Context context) {
        if (instance == null) {
            instance = new OrderDao(context.getApplicationContext());
        }
        return instance;
    }

    public void delete(UnSuccessOrder bean) {
        dao.delete(bean);
    }

    public void delete(String orderId) {
        try {
            DeleteBuilder builder = dao.deleteBuilder();
            Where where = builder.where();
            where.eq("orderId", orderId);
            builder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void add(String order) {
        UnSuccessOrder bean = new UnSuccessOrder();
        bean.orderId = order;
        dao.create(bean);
    }

    public UnSuccessOrder getFirstOrder() {
        try {
            UnSuccessOrder order = dao.queryForFirst(dao.queryBuilder().prepare());
            return order;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public UnSuccessOrder getOrder(String orderId) {
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        List<UnSuccessOrder> list = dao.queryForFieldValues(map);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
