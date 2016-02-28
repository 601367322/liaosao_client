package com.xl.activity.pay;

import com.xl.activity.R;
import com.xl.activity.base.BaseAdapter;
import com.xl.api.BaseApi;
import com.xl.bean.Pay;
import com.xl.fragment.BaseListFragment;

import org.androidannotations.annotations.EFragment;

/**
 * Created by Shen on 2016/2/28.
 */
@EFragment(R.layout.fragment_list)
public class PayDetailFragment extends BaseListFragment<Pay> {

    @Override
    public BaseAdapter<Pay> getAdapter() {
        return new PayDetailAdapter(getActivity());
    }

    @Override
    public BaseApi getApi() {
        return new PayDetailApi(getActivity());
    }

    @Override
    public Class<?> getClazz() {
        return Pay.class;
    }
}
