package com.xl.fragment;

import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.xl.activity.R;
import com.xl.activity.base.BaseFragment;
import com.xl.activity.chat.ChatActivity_;
import com.xl.util.BroadCastUtil;
import com.xl.util.GifDrawableCache;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.StaticUtil;
import com.xl.util.URLS;

import net.imageloader.tools.br.imakdg;
import net.imageloader.tools.br.imandg;
import net.imageloader.tools.br.imandgListener;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

@EFragment(R.layout.fragment_main)
public class MainFragment extends BaseFragment {

    @ViewById
    FloatingActionButton connect;

    @ViewById
    RelativeLayout mAdContainer;

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

        // 监听广告条接口
        adView.isbypl(new imandgListener() {

            @Override
            public void isbtpl(imandg arg0) {
            }

            @Override
            public void isbopl(imandg arg0) {
            }
            @Override
            public void isbnpl(imandg arg0) {
            }
        });

        mAdContainer.addView(adView);
    }
    @Click
    void connect() {

        if(connect.getTag()==null) {
            connect.setTag("1");
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
