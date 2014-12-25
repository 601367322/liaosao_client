package com.xl.fragment;

import android.widget.Button;

import com.xl.activity.R;
import com.xl.activity.base.BaseFragment;
import com.xl.activity.chat.ChatActivity_;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.StaticUtil;
import com.xl.util.URLS;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EFragment(R.layout.fragment_main)
public class MainFragment extends BaseFragment {

    @ViewById
    Button startBtn;
    @ViewById
    Button stopBtn;
    @ViewById
    Button isOnline;

    @Override
    public void init() {

    }

    @Click
    void startBtn() {
        ac.startService();
    }

    @Click
    void connect() {
        ac.httpClient.post(URLS.JOINQUEUE, ac.getRequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jo) {
                try {
                    int status = jo.getInt(ResultCode.STATUS);
                    switch (status) {
                        case ResultCode.SUCCESS:
                            String deviceId = jo.getString(StaticUtil.OTHERDEVICEID);
                            ChatActivity_.intent(getActivity()).deviceId(deviceId).start();
                            break;
                        case ResultCode.LOADING:
                            toast("排队中,请等待");
                            break;
                    }
                } catch (Exception e) {
                }
            }
        });
    }


    int i = 1;

    @Click
    void isOnline() {
        isOnline.setText(ac.isOnline() + "");
        ac.getBean().setNumber(i++).commit(getActivity());
    }

    @Click
    void stopBtn() {
        ac.stopService();
    }
}
