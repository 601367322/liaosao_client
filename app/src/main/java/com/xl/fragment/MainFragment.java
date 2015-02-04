package com.xl.fragment;

import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.xl.activity.R;
import com.xl.activity.base.BaseFragment;
import com.xl.activity.chat.ChatActivity_;
import com.xl.game.GameActivity_;
import com.xl.util.BroadCastUtil;
import com.xl.util.GifDrawableCache;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.StaticUtil;
import com.xl.util.URLS;

import net.imageloader.tools.br.imakdg;
import net.imageloader.tools.br.imandg;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EFragment(R.layout.fragment_main)
public class MainFragment extends BaseFragment {

    @ViewById
    FloatingActionButton connect;

    @ViewById
    RelativeLayout mAdContainer;

    @ViewById
    View to_game;

    @Override
    public void init() {
        // 实例化LayoutParams(重要)
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        // 设置广告条的悬浮位置
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT; // 这里示例为右下角
        // 实例化广告条
        imandg adView = new imandg(getActivity(), imakdg.FIT_SCREEN);
        // 调用Activity的addContentView函数

        mAdContainer.addView(adView);
    }
    @Click
    void connect() {

        if(connect.getTag()==null) {
            connect.setTag("1");

            loadmoreTime();
            ac.httpClient.post(URLS.JOINQUEUE, ac.getRequestParams(), new JsonHttpResponseHandler() {

                @Override
                public void onFailure() {
                    cancle();
                }

                @Override
                public void onStart() {
                    connect.setIcon(GifDrawableCache.getInstance().getDrawable((long) R.drawable.loading, getActivity()));
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
        }else {
            ac.httpClient.post(URLS.EXITQUEUE, ac.getRequestParams(), new JsonHttpResponseHandler() {
            });
            cancle();
        }
    }

    @Receiver(actions = BroadCastUtil.STARTCHAT)
    void startCaht(){
        cancle();
    }

    void cancle(){
        connect.setTag(null);
        connect.setIcon(R.drawable.touch_my_face);
        hideToGame();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Click
    void to_game(){
        GameActivity_.intent(this).start();
    }

    @UiThread(delay = 1500)
    public void loadmoreTime(){
        hideToGame();
    }

    public void hideToGame(){
        if(connect.getTag()!=null){
            to_game.setVisibility(View.VISIBLE);
            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            to_game.startAnimation(shake);
        }else{
            to_game.setVisibility(View.GONE);
            to_game.clearAnimation();
        }
    }
}
