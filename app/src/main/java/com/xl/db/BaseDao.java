package com.xl.db;

import android.content.Context;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.lang.reflect.ParameterizedType;

/**
 * Created by sbb on 2015/6/4.
 */
public class BaseDao<T, ID> {

    public RuntimeExceptionDao<T, ID> dao;

    public Context context;

    public BaseDao(Context context) {
        this.context = context;
        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        dao = DBHelper.getDao_(context, entityClass);
    }

    public void delete(T bean) {
        dao.delete(bean);
    }

    public void create(T bean) {
        dao.create(bean);
    }

    public void update(T bean) {
        dao.update(bean);
    }

    public void createOrUpdate(T bean){
        dao.createOrUpdate(bean);
    }
}
