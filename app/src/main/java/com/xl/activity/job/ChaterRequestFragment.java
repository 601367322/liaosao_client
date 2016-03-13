package com.xl.activity.job;

import com.xl.activity.R;
import com.xl.activity.base.BaseAdapter;
import com.xl.api.BaseApi;
import com.xl.bean.ChatRoomRequest;
import com.xl.fragment.BaseListFragment;

import org.androidannotations.annotations.EFragment;

/**
 * Created by Shen on 2016/3/3.
 */
@EFragment(R.layout.fragment_list)
public class ChaterRequestFragment extends BaseListFragment<ChatRoomRequest> {

    @Override
    public BaseAdapter<ChatRoomRequest> getAdapter() {
        return new ChaterRequestAdapter(getActivity());
    }

    @Override
    public BaseApi getApi() {
        return new ChaterListApi(getActivity());
    }

    @Override
    public Class<?> getClazz() {
        return ChatRoomRequest.class;
    }

}
