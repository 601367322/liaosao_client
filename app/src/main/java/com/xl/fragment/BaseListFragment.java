package com.xl.fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import com.xl.activity.R;
import com.xl.activity.base.BaseAdapter;
import com.xl.activity.base.BaseFragment;
import com.xl.api.BaseApi;
import com.xl.custom.swipe.SwipeRefreshLayout;
import com.xl.location.GDLocation;
import com.xl.location.ILocationImpl;
import com.xl.pull.XListView;
import com.xl.util.ACache;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.ToastUtil;
import com.xl.util.Utils;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;

/**
 * Created by sbb on 2015/5/14.
 */
@EFragment(R.layout.fragment_list)
public abstract class BaseListFragment<T> extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, XListView.IXListViewListener, ILocationImpl {

    @ViewById
    public XListView listview;
    @ViewById
    public SwipeRefreshLayout swipe_layout;
    @ViewById
    public RelativeLayout parent;

    public BaseAdapter<T> adapter;

    public View emptyView;

    public int currentPage = 0;

    public GDLocation location;

    public BaseApi api;

    @Override
    public void init() {

        location = new GDLocation(getActivity(), this, false);

        if (getEmptyView() != -1) {
            emptyView = LayoutInflater.from(getActivity()).inflate(getEmptyView(), (ViewGroup) listview.getParent(), false);
            ((ViewGroup) listview.getParent().getParent()).addView(emptyView);
            emptyView.setVisibility(View.GONE);
        }

        listview.setPage(-1);

        listview.setXListViewListener(this);

        initListView();

        listview.setAdapter(adapter = getAdapter());

        swipe_layout.setOnRefreshListener(this);

        if (needCache()) {
            ACache cache = ACache.get(getActivity());
            JSONObject jo = cache.getAsJSONObject(getCacheKey());
            if (jo != null) {
                setJsonData(jo, true);
            }
        }

        if (autoRefresh()) {
            swipeRefresh();
        }
    }

    public boolean autoRefresh() {
        return true;
    }

    @Override
    public void onRefresh() {
        currentPage = 0;
        listview.setSelection(0);
        onPullToRefresh();
    }

    @Override
    public void onLoadMore() {
        onPullToRefresh();
    }

    private void onPullToRefresh() {
        if (needLocation() && currentPage == 0) {
            location.startLocation();
        } else {
            httpPost();
        }
    }

    public void httpPost() {
        api = getApi();
        api.setPage(currentPage);
        initParams();
        ac.httpClient.post(api.getUrl(), api.getParams(), new JsonHttpResponseHandler(getActivity()) {
            @Override
            public void onSuccessCode(JSONObject jo) throws Exception {

                if (needCache() && currentPage == 0) {
                    ACache.get(getActivity()).put(getCacheKey(), jo);
                }

                setJsonData(jo, false);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                onFinshRefreshUI();
            }

            @Override
            public void onFailCode(JSONObject jo) {
                super.onFailCode(jo);
                onFailCallBack();
            }

            @Override
            public void onFailure() {
                super.onFailure();
                onFailCallBack();
            }
        });
    }

    public void setJsonData(JSONObject jo, boolean cache) {
        try {
            onSuccessCallBack(jo);


            List list = Utils.jsonToList(jo.getString(ResultCode.CONTENT), getClazz());
            onSuccessRefreshUI(jo, list, cache);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread(delay = 100l)
    public void swipeRefresh() {
        swipe_layout.setRefreshing(true);
    }

    public int getEmptyView() {
        return R.layout.default_empty_view;
    }

    public abstract BaseAdapter<T> getAdapter();

    /**
     * 刷新list
     *
     * @param list
     * @param cache 如果是缓存数据，不改变页数
     */
    public void onSuccessRefreshUI(JSONObject jo, List list, boolean cache) {
        if (list != null) {
            if (currentPage == 0) {
                adapter.setList(list);
            } else {
                adapter.add(list);
            }

            if (!cache) {
                currentPage++;
            }
        }
        if (!cache) {
            if (list != null && list.size() <= 20) {
                listview.setPage(-1);
            } else {
                listview.setPage(currentPage);
            }
            showHideEmptyView();
        }
    }

    public void showHideEmptyView() {
        if (emptyView != null) {
            if (adapter.getCount() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
        }
    }

    @UiThread
    public void onFinshRefreshUI() {
        swipe_layout.setRefreshing(false);
        listview.stop();

    }

    @ItemClick
    public void listview(int position) {

    }

    public abstract BaseApi getApi();

    /**
     * 请求成功后
     *
     * @param jo
     */
    public void onSuccessCallBack(JSONObject jo) {

    }

    /**
     * 请求失败后
     */
    public void onFailCallBack() {
    }

    /**
     * 在listview setAdapter前调用
     */
    public void initListView() {
    }


    /**
     * 是否有分页
     *
     * @return
     */
    public boolean pageEnable() {
        return true;
    }

    /**
     * 是否需要定位
     *
     * @return
     */
    public boolean needLocation() {
        return false;
    }

    @Override
    public void onLocationSuccess() {
        httpPost();
    }

    @Override
    public void onLocationFail() {
        ToastUtil.toast(getActivity(), getString(R.string.location_fail));
        onFinshRefreshUI();
    }

    /**
     * 是否需要缓存
     *
     * @return
     */
    public boolean needCache() {
        return false;
    }

    public String getCacheKey() {
        return getClass().getName();
    }

    public abstract Class<?> getClazz();

    public void initParams() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (location != null) {
            location.destory();
        }
    }
}