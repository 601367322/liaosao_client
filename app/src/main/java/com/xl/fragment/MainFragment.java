package com.xl.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xl.activity.R;
import com.xl.activity.base.BaseFragment;
import com.xl.activity.chat.ChatActivity_;
import com.xl.util.BroadCastUtil;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.StaticUtil;
import com.xl.util.URLS;
import com.xl.util.Utils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

@EFragment(R.layout.fragment_main)
public class MainFragment extends BaseFragment {

    @ViewById
    View connect,loading_rl;
    @ViewById
    ImageView loading_img;
    @ViewById
    TextView loading_txt;


    @Override
    public void init() {
        loading_img.setEnabled(false);
        loading_txt.setEnabled(false);

        GifDrawable drawable = null;
        try {
            drawable = new GifDrawable(getResources(), R.drawable.loading);
            Utils.setFullScreenImage(loading_img,drawable,getResources().getDisplayMetrics().widthPixels);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                            cancle();
                            break;
                        case ResultCode.LOADING:
//                            toast("排队中,请等待");
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
                cancle();
            }

            @Override
            public void onSuccess(JSONObject jo) {
            }
        });
    }

    @Receiver(actions = BroadCastUtil.STARTCHAT)
    void startCaht(){
        cancle();
    }

    void cancle(){
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
                loading_txt.setText(R.string.finding1);
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loading_img();
    }
}
