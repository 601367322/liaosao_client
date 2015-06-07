package com.xl.db;

import android.content.Context;

import com.xl.bean.BlackUser;
import com.xl.util.StaticUtil;

import java.util.List;

/**
 * Created by Shen on 2015/6/7.
 */
public class BlackDao extends BaseDao<BlackUser, Integer> {

    private static BlackDao instance;

    private BlackDao(Context context) {
        super(context);
    }

    public static synchronized BlackDao getInstance(Context context) {
        if (instance == null) {
            instance = new BlackDao(context.getApplicationContext());
        }
        return instance;
    }

    public BlackUser isExists(String deviceId) {
        List<BlackUser> list = dao.queryForEq(StaticUtil.DEVICEID, deviceId);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public void delete(BlackUser bean){
        dao.delete(bean);
    }

    public void add(String deviceId){
        BlackUser bean = new BlackUser();
        bean.deviceId = deviceId;
        dao.create(bean);
    }
}
