package com.xl.activity.job;

import com.xl.activity.R;
import com.xl.activity.base.BaseAdapter;
import com.xl.api.BaseApi;
import com.xl.bean.ChatRoom;
import com.xl.fragment.BaseListFragment;
import com.xl.util.URLS;

import org.androidannotations.annotations.EFragment;

/**
 * Created by Shen on 2016/1/16.
 */
@EFragment(R.layout.fragment_list)
public class ChaterListFragment extends BaseListFragment<ChatRoom> {

    @Override
    public BaseAdapter<ChatRoom> getAdapter() {
        return new ChaterListAdapter(getActivity());
    }

    @Override
    public BaseApi getApi() {
        return new ChaterListApi(getActivity());
    }

    @Override
    public Class<?> getClazz() {
        return ChatRoom.class;
    }
}
