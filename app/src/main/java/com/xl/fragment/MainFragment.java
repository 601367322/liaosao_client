package com.xl.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import com.xl.activity.MainActivity;
import com.xl.activity.R;
import com.xl.activity.base.BaseFragment;
import com.xl.activity.chat.ChatActivity_;
import com.xl.activity.pay.PayActivity_;
import com.xl.activity.share.CommonShared;
import com.xl.util.BroadCastUtil;
import com.xl.util.GifDrawableCache;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.StaticUtil;
import com.xl.util.URLS;
import com.xl.util.Utils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import a.b.c.DynamicSdkManager;

@EFragment(R.layout.fragment_main)
public class MainFragment extends BaseFragment {

    @ViewById
    FloatingActionButton connect;
    @ViewById
    FloatingActionButton connectVip;

    @ViewById
    RelativeLayout mAdContainer;


    @Override
    public void init() {
        if (MobclickAgent.getConfigParams(getActivity(), "ad_show").equals("on")) {
            View banner = DynamicSdkManager.getInstance(ac).getBanner(getActivity());
            if (banner != null) {
                mAdContainer.addView(banner);
            }
        }

        ac.httpClient.post(URLS.ISVIP, ac.getRequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jo) {
                int status = jo.optInt(ResultCode.STATUS);
                switch (status) {
                    case ResultCode.SUCCESS:
                        if (jo.has(StaticUtil.CONTENT) && jo.opt(StaticUtil.CONTENT) != null) {
                            ac.cs.setISVIP(CommonShared.ON);
                        } else {
                            ac.cs.setISVIP(CommonShared.OFF);
                        }
                        break;
                }
            }
        });
    }

    @Click({R.id.connect, R.id.connect_vip})
    void connect(final View view) {
        if (Utils.isFastDoubleClick()) {
            return;
        }
        if (ac.cs.getHadSex() == CommonShared.OFF) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.yoursex)).setPositiveButton("男", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ac.cs.setSex(2);
                    ac.cs.setHadSex(CommonShared.ON);
                    connect(view);
                }
            }).setNegativeButton("女", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ac.cs.setSex(1);
                    ac.cs.setHadSex(CommonShared.ON);
                    connect(view);
                }
            }).create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            if (connect.getTag() != null) {
                ac.httpClient.post(URLS.EXITQUEUE, ac.getRequestParams(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject jo) {
                        if (view.getId() == R.id.connect_vip) {
                            doConnectVipDialog();
                        }
                    }
                });
                cancle();
            } else if (connectVip.getTag() != null) {
                ac.httpClient.post(URLS.EXITQUEUE, ac.getRequestParams(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject jo) {
                        if (view.getId() == R.id.connect) {
                            doConnect();
                        }
                    }
                });
                cancle();
            } else {
                if (view.getId() == R.id.connect)
                    doConnect();
                else
                    doConnectVipDialog();
            }
        }
    }

    public void doConnect() {
        if (connect.getTag() == null) {
            connect.setTag("1");
            if (ac.cs.getShowDrawer() == CommonShared.OFF) {
                ac.cs.setShowDrawer(CommonShared.ON);
                loadmoreTime();
            }
            RequestParams params = ac.getRequestParams();
            params.put(StaticUtil.SEX, ac.cs.getSex());
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
                                builder_.flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                builder_.start();
                                cancle();
                                break;
                            case ResultCode.LOADING:
//                            toast("排队中,请等待");
                                break;
                            case ResultCode.FAIL:
                                ac.startService();
                                onFailure();
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

    public void doConnectVipDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setTitle("希望匹配到").setPositiveButton("男", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doConnectVip(2);
            }
        }).setNegativeButton("女", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doConnectVip(1);
            }
        }).create();
        dialog.show();
    }

    public void doConnectVip(int sex){
        connectVip.setTag("1");
        if (ac.cs.getShowDrawer() == CommonShared.OFF) {
            ac.cs.setShowDrawer(CommonShared.ON);
            loadmoreTime();
        }
        RequestParams params = ac.getRequestParams();
        params.put(StaticUtil.SEX, ac.cs.getSex());
        params.put(StaticUtil.WANTSEX, sex);
        ac.httpClient.post(URLS.JOINQUEUEVIP, params, new JsonHttpResponseHandler() {

            @Override
            public void onFailure() {
                cancle();
            }

            @Override
            public void onStart() {
                connectVip.setIcon(GifDrawableCache.getInstance().getDrawable((long) R.drawable.loading, getActivity()));
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
                            builder_.flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            builder_.start();
                            cancle();
                            break;
                        case ResultCode.LOADING:
//                            toast("排队中,请等待");
                            break;
                        case ResultCode.FAIL:
                            ac.startService();
                            onFailure();
                            break;
                        case ResultCode.NOVIP:
                            new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.kiding)).setIcon(R.drawable.dialog_icon).setMessage("你并不是会员，哈哈哈哈哈哈！！！").setPositiveButton("成为会员", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PayActivity_.intent(MainFragment.this).start();
                                }
                            }).setNegativeButton("继续做屌丝", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                            onFailure();
                            break;
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    @Receiver(actions = BroadCastUtil.STARTCHAT)
    void startCaht() {
        cancle();
    }

    void cancle() {
        if(connect!=null) {
            connect.setTag(null);
            connect.setIcon(R.drawable.touch_my_face);
        }
        if(connectVip!=null) {
            connectVip.setTag(null);
            connectVip.setIcon(R.drawable.vipface);
        }
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
        if (getActivity() != null) {
            if (((MainActivity) getActivity()).mNavigationDrawerFragment != null) {
                if (connect.getTag() != null || connectVip.getTag() != null) {
                    ((MainActivity) getActivity()).mNavigationDrawerFragment.openDrawer();
                } else {
                    ((MainActivity) getActivity()).mNavigationDrawerFragment.closeDrawer();
                }
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
