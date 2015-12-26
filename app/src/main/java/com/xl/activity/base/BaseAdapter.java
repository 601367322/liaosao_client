package com.xl.activity.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xl.application.AppClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.ButterKnife;

public abstract class BaseAdapter<E> extends android.widget.BaseAdapter {

    public List<E> list = new ArrayList<>();
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
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    public void add(List<E> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void add(int position, E bean) {
        if (list != null) {
            list.add(position, bean);
        }
    }

    public void add(E bean) {
        if (list != null) {
            list.add(bean);
        }
    }

    public BaseAdapter(Context context) {
        this.context = context;
        if (context != null) {
            ac = (AppClass) context.getApplicationContext();
        }
    }

    public void remove(int position) {
        if (list != null && list.size() > position) {
            list.remove(position);
        }
    }

    public void remove(E object) {
        if (list != null) {
            list.remove(object);
        }
    }

    public void clear() {
        if (list != null) {
            list.clear();
        }
    }

    public void removeAll(Collection<E> list) {
        if (this.list != null && list != null) {
            this.list.removeAll(list);
        }
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(getConvertView(position), parent, false);
            holder = getHolder(convertView);
        } else {
            holder = (BaseHolder) convertView.getTag();
        }
        holder.bind(getItem(position), position);
        return convertView;
    }

    public abstract BaseHolder getHolder(View view);

    public abstract int getConvertView(int position);

    public abstract class BaseHolder {

        public BaseHolder(View view) {

            ButterKnife.bind(this, view);

            view.setTag(this);
        }

        public abstract void bind(E bean, int position);
    }

}