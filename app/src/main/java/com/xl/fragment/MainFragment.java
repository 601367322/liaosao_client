package com.xl.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.RequestParams;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.xl.activity.BuildConfig;
import com.xl.activity.R;
import com.xl.activity.base.BaseFragment;
import com.xl.activity.chat.ChatActivity_;
import com.xl.activity.job.ChaterListActivity_;
import com.xl.activity.share.CommonShared;
import com.xl.db.ChatDao;
import com.xl.db.UserTableDao;
import com.xl.location.GDLocation;
import com.xl.location.ILocationImpl;
import com.xl.util.BroadCastUtil;
import com.xl.util.GifDrawableCache;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.StaticUtil;
import com.xl.util.URLS;
import com.xl.util.Utils;

import net.google.niofile.br.AdSize;
import net.google.niofile.br.AdView;

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
    FloatingActionButton connectVip;
    @ViewById
    FloatingActionButton connectGroup;
    @ViewById
    View content_ll;

    @ViewById
    RelativeLayout mAdContainer;
    GDLocation location;

    @ViewById
    View manager;

    UserTableDao userTableDao;

    ChatDao chatDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userTableDao = UserTableDao.getInstance(getActivity());
        chatDao = ChatDao.getInstance(getActivity());
    }

    @Override
    public void init() {

        if (BuildConfig.DEBUG) {
            manager.setVisibility(View.VISIBLE);
        } else {
            manager.setVisibility(View.GONE);
        }

        location = new GDLocation(getActivity(), new ILocationImpl() {
            @Override
            public void onLocationSuccess() {
                RequestParams params = ac.getRequestParams();
                params.put("lat", ac.cs.getLat());
                params.put("lng", ac.cs.getLng());
                params.put("province", ac.cs.getPROVINCE());
                params.put("city", ac.cs.getCITY());
                ac.httpClient.post(URLS.SETUSERDETAIL, params, new JsonHttpResponseHandler() {

                });
            }

            @Override
            public void onLocationFail() {

            }
        }, true);

        ac.httpClient.post(URLS.ISVIP, ac.getRequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jo) {
                int status = jo.optInt(ResultCode.STATUS);
                switch (status) {
                    case ResultCode.SUCCESS:
                        if (jo.has(ResultCode.CONTENT) && jo.opt(ResultCode.CONTENT) != null) {
                            ac.cs.setISVIP(CommonShared.ON);
                        } else {
                            ac.cs.setISVIP(CommonShared.OFF);
                        }
                        break;
                }
            }
        });

        if (OnlineConfigAgent.getInstance().getInstance().getConfigParams(getActivity(), "ad_show").equals("on")) {
            AdView adView = new AdView(getActivity(), AdSize.FIT_SCREEN);
//            View banner = DynamicSdkManager.getInstance(ac).getBanner(getActivity());
            if (adView != null) {
                mAdContainer.addView(adView);
            }
        }
    }


    @Click({R.id.connect, R.id.connect_vip})
    void connect(final View view) {
        if (Utils.isFastDoubleClick()) {
            return;
        }
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

    public void doConnect() {
        if (connect.getTag() == null) {
            connect.setTag("1");
            if (ac.cs.getShowDrawer() == CommonShared.OFF) {
                ac.cs.setShowDrawer(CommonShared.ON);
                loadmoreTime();
            }
            RequestParams params = ac.getRequestParams();
            params.put(StaticUtil.SEX, userTableDao.getUserTableByDeviceId(ac.deviceId).getBean().getSex());
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
        if (getActivity() != null) {
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
    }

    public void doConnectVip(int sex) {
        connectVip.setTag("1");
        if (ac.cs.getShowDrawer() == CommonShared.OFF) {
            ac.cs.setShowDrawer(CommonShared.ON);
            loadmoreTime();
        }
        RequestParams params = ac.getRequestParams();
        params.put(StaticUtil.SEX, userTableDao.getUserTableByDeviceId(ac.deviceId).getBean().getSex());
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
                            Utils.showVipDialog(getActivity());
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
        if (connect != null) {
            connect.setTag(null);
            connect.setIcon(R.drawable.touch_my_face);
        }
        if (connectVip != null) {
            connectVip.setTag(null);
            connectVip.setIcon(R.drawable.vipface);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (location != null) {
            location.destory();
        }
    }


    @UiThread(delay = 1500)
    public void loadmoreTime() {
        hideToGame();
    }

    public void hideToGame() {
//        if (getActivity() != null) {
//            if (((MainActivity) getActivity()).mNavigationDrawerFragment != null) {
//                if (connect.getTag() != null || connectVip.getTag() != null) {
//                    ((MainActivity) getActivity()).mNavigationDrawerFragment.openDrawer();
//                } else {
//                    ((MainActivity) getActivity()).mNavigationDrawerFragment.closeDrawer();
//                }
//            }
//        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Click
    public void manager() {
        if (isFastDoubleClick()) {
            num++;
            if (num >= 10) {
                if (ac.cs.getISMANAGER() == CommonShared.ON) {
                    ac.cs.setISMANAGER(CommonShared.OFF);
                } else {
                    ac.cs.setISMANAGER(CommonShared.ON);
                }
                ac.initDeviceid();
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {100, 400, 100, 400};   // 停止 开启 停止 开启
                vibrator.vibrate(pattern, -1);
                num = 0;
            }
        } else {
            num = 0;
        }
    }

    private static long lastClickTime;
    private int num = 0;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            lastClickTime = time;
            return true;
        }
        lastClickTime = time;
        return false;
    }

    @Click(R.id.connect_group)
    public void connectGroup(View view) {
        ChaterListActivity_.intent(this).start();
    }

}
