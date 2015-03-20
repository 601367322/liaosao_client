package com.xl.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.RequestParams;
import com.xl.activity.MainActivity;
import com.xl.activity.R;
import com.xl.activity.base.BaseFragment;
import com.xl.activity.chat.ChatActivity_;
import com.xl.activity.share.CommonShared;
import com.xl.util.BroadCastUtil;
import com.xl.util.GifDrawableCache;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.StaticUtil;
import com.xl.util.URLS;
import com.xl.util.Utils;

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
        if(Utils.isFastDoubleClick()){
            return;
        }
       if (ac.cs.getHadSex() == CommonShared.OFF) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.yoursex)).setPositiveButton("男", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ac.cs.setSex(2);
                    ac.cs.setHadSex(CommonShared.ON);
                    doConnect();
                }
            }).setNegativeButton("女", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ac.cs.setSex(1);
                    ac.cs.setHadSex(CommonShared.ON);
                    doConnect();
                }
            }).create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            doConnect();
        }
    }

    public void doConnect() {
        if (connect.getTag() == null) {
            connect.setTag("1");

            loadmoreTime();
            RequestParams params = ac.getRequestParams();
            params.put(StaticUtil.SEX, ac.cs.getSex());
//            if (!ac.cs.getLat().equals("") && ac.cs.getDistance() == CommonShared.ON) {
//                params.put(StaticUtil.LAT, ac.cs.getLat());
//                params.put(StaticUtil.LNG, ac.cs.getLng());
//            }
            ac.httpClient.post(URLS.JOINQUEUE, params, new JsonHttpResponseHandler() {

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
                                ChatActivity_.IntentBuilder_ builder_ = ChatActivity_.intent(getActivity()).deviceId(deviceId);
                                if (jo.has(StaticUtil.SEX)) {
                                    builder_.sex(jo.getInt(StaticUtil.SEX));
                                }
                                if (jo.has(StaticUtil.LAT)) {
                                    builder_.lat(jo.getString(StaticUtil.LAT)).lng(jo.getString(StaticUtil.LNG));
                                }
                                builder_.start();
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
        } else {
            ac.httpClient.post(URLS.EXITQUEUE, ac.getRequestParams(), new JsonHttpResponseHandler() {
            });
            cancle();
        }
    }

    @Receiver(actions = BroadCastUtil.STARTCHAT)
    void startCaht() {
        cancle();
    }

    void cancle() {
        connect.setTag(null);
        connect.setIcon(R.drawable.touch_my_face);
        hideToGame();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @UiThread(delay = 1500)
    public void loadmoreTime() {
        hideToGame();
    }

    public void hideToGame() {
        if (connect.getTag() != null) {
            ((MainActivity)getActivity()).mNavigationDrawerFragment.openDrawer();
        } else {
            ((MainActivity)getActivity()).mNavigationDrawerFragment.closeDrawer();
        }
    }
}
