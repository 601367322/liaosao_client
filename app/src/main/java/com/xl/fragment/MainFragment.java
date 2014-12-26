package com.xl.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.widget.TextView;

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
    View connect,loading_img,loading_rl;
    @ViewById
    TextView loading_txt;


    @Override
    public void init() {
        loading_img.setEnabled(false);
        loading_txt.setEnabled(false);
    }

    @Click
    void connect() {
        ac.httpClient.post(URLS.JOINQUEUE, ac.getRequestParams(), new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                connect.animate().alpha(0f).setDuration(1500).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        connect.setEnabled(false);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        connect.setVisibility(View.GONE);
                    }
                }).start();
                loading_rl.animate().alpha(1f).setDuration(1500).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        loading_rl.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loading_img.setEnabled(true);
                        loading_txt.setEnabled(true);
                    }
                }).start();
            }

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

    @Click
    void loading_txt(){
        loading_txt.setText(R.string.finding2);
        toast(ac.getString(R.string.why_click_me));
    }

    @Click
    void loading_img(){
        ac.httpClient.post(URLS.EXITQUEUE, ac.getRequestParams(), new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                connect.animate().alpha(1f).setDuration(1500).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        connect.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        connect.setEnabled(true);
                    }
                }).start();
                loading_rl.animate().alpha(0f).setDuration(1500).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        loading_img.setEnabled(false);
                        loading_txt.setEnabled(false);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loading_rl.setVisibility(View.GONE);
                    }
                }).start();
            }

            @Override
            public void onSuccess(JSONObject jo) {
            }
        });
    }

}
