package com.xl.db;

import android.content.Context;

import com.j256.ormlite.stmt.PreparedDelete;
import com.xl.bean.UserTable_6;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Shen on 2015/7/25.
 */
public class UserTableDao extends BaseDao<UserTable_6, Integer> {

    private static UserTableDao instance;

    public UserTableDao(Context context) {
        super(context);
    }

    public static synchronized UserTableDao getInstance(Context context) {
        if (instance == null) {
            instance = new UserTableDao(context.getApplicationContext());
        }
        return instance;
    }

    public UserTable_6 getUserTableByDeviceId(String deviceId) {
        List<UserTable_6> list = dao.queryForEq(UserTable_6.DEVICEID, deviceId);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public void deleteUserByDeviceId(String deviceId){
        try {
            dao.delete((PreparedDelete<UserTable_6>) dao.deleteBuilder().where().eq(UserTable_6.DEVICEID, deviceId).prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
