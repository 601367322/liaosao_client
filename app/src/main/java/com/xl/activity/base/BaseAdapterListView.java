package com.xl.activity.base;

import android.content.Context;
import android.widget.BaseAdapter;

import com.xl.application.AppClass;

import java.util.List;

/**
 * Created by Administrator on 2014/12/22.
 */
public abstract class BaseAdapterListView<E> extends BaseAdapter {

    public List<E> list;
    public Context context;
    public AppClass ac;

    public E getItem(int position) {
        if (list != null && list.size() > 0 && position < list.size()) {
            return list.get(position);
        }
        return null;
    }

    public void setList(List<E> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<E> getList() {
        return list;
    }

    public BaseAdapterListView(List<E> list, Context context) {
        this.list = list;
        this.context = context;
        ac = (AppClass) context.getApplicationContext();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addFirst(E object) {
        this.list.add(0, object);
    }

}
