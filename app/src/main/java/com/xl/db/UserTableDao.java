package com.xl.db;

import android.content.Context;

import com.j256.ormlite.stmt.PreparedDelete;
import com.xl.bean.UserTable;
import com.xl.util.LogUtil;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Shen on 2015/7/25.
 */
public class UserTableDao extends BaseDao<UserTable, Integer> {

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

    public UserTable getUserTableByDeviceId(String deviceId) {
        List<UserTable> list = dao.queryForEq(UserTable.DEVICEID, deviceId);
        LogUtil.d("getUserTableByDeviceId:\t"+list.size());
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public void deleteUserByDeviceId(String deviceId){
        try {
            dao.delete((PreparedDelete<UserTable>) dao.deleteBuilder().where().eq(UserTable.DEVICEID, deviceId).prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(UserTable user){
        deleteUserByDeviceId(user.getDeviceId());
        create(user);
    }
}
