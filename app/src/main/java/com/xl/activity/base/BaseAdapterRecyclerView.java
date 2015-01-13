/*
package com.xl.activity.base;

import android.content.Context;

import java.util.List;

*/
/**
 * Created by Administrator on 2014/12/22.
 *//*

public abstract class BaseAdapterRecyclerView<E> extends HeaderRecyclerViewAdapterV2{

    public List<E> list;
    public Context context;

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

    public BaseAdapterRecyclerView(List<E> list, Context context){
        this.list=list;
        this.context=context;
    }

    @Override
    public int getBasicItemCount() {
        return list == null ? 0 : list.size();
    }
}
*/
