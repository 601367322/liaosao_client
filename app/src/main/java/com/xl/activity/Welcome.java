/*
package com.xl.activity;

import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.loopj.android.http.RequestParams;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.xl.activity.base.BaseActivity;
import com.xl.util.AccessTokenKeeper;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.LogUtil;
import com.xl.util.URLS;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

*/
/**
 * Created by Administrator on 2014/9/21.
 *//*

@EActivity(R.layout.welcome)
public class Welcome extends BaseActivity {
    @ViewById
    ImageView btn_wechat, btn_qq;

    //微信
    public static final String WEIXIN_ID = "wx488920a29404152c";

    //微博
    public static final String APP_KEY = "1770862317"; // 应用的APP_KEY
    public static final String REDIRECT_URL = "http://www.sina.com";// 应用的回调页
    public static final String SCOPE = // 应用申请的高级权限
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";
    SsoHandler mSsoHandler;
    WeiboAuth mWeiboAuth;

    //QQ
    public static final String APP_ID="1103269048";
    Tencent mTencent;

    @Override
    public void onResume() {
        super.onResume();
        getActionBar().setTitle(R.string.onekeylogin);
    }

    @Override
    protected void init() {
        btn_wechat.setOnTouchListener(VIEW_TOUCH_LIGHT);
        btn_qq.setOnTouchListener(VIEW_TOUCH_LIGHT);

    }

    @Click
    void btn_wechat() {
        Oauth2AccessToken o=AccessTokenKeeper.readAccessToken(this);
        if(o.getUid().equals("")){
            mWeiboAuth = new WeiboAuth(this, APP_KEY,
                    REDIRECT_URL, SCOPE);
            mSsoHandler = new SsoHandler(Welcome.this, mWeiboAuth);
            mSsoHandler.authorize(new AuthListener());
        }else{
            sinaLogin(o.getUid(),o.getToken(),1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Click
    void btn_qq() {
        if(mTencent!=null){
            mTencent.logout(this.getApplicationContext());
            mTencent=null;
        }
        mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
        if(!mTencent.isSessionValid()) {
            mTencent.loginWithOEM(this, "all", new IUiListener() {
                @Override
                public void onComplete(Object o) {
                    LogUtil.d(o.toString());
                    try {
                        JSONObject jo = new JSONObject(o.toString());
                        LogUtil.d(jo.toString());
                        sinaLogin(jo.get("openid").toString(), jo.get("access_token").toString(), 2);
                    }catch (Exception ex){
                       ex.printStackTrace();
                    }

                }

                @Override
                public void onError(UiError uiError) {

                }

                @Override
                public void onCancel() {

                }
            }, "", "", "");
        }
    }

    */
/**
     * 让控件点击时，颜色变暗
     *//*

    public static final View.OnTouchListener VIEW_TOUCH_LIGHT = new View.OnTouchListener() {

        public final float[] BT_SELECTED = new float[]{1, 0, 0, 0, 50, 0, 1,
                0, 0, 50, 0, 0, 1, 0, 50, 0, 0, 0, 1, 0};
        public final float[] BT_NOT_SELECTED = new float[]{1, 0, 0, 0, 0, 0,
                1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0};

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (v instanceof ImageView) {
                    ImageView iv = (ImageView) v;
                    iv.setDrawingCacheEnabled(true);

                    iv.setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
                } else {
                    v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
                    v.setBackgroundDrawable(v.getBackground());
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (v instanceof ImageView) {
                    ImageView iv = (ImageView) v;
                    iv.setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
                } else {
                    v.getBackground().setColorFilter(
                            new ColorMatrixColorFilter(BT_NOT_SELECTED));
                    v.setBackgroundDrawable(v.getBackground());
                }
            }
            return false;
        }
    };


    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            final Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(values); // 从 Bundle 中解析 Token
            if (mAccessToken.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(Welcome.this, mAccessToken); //
                sinaLogin(mAccessToken.getUid(),mAccessToken.getToken(),1);

            } else {
                String code = values.getString("code", "");
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {

        }

        @Override
        public void onCancel() {

        }
    }

    public void sinaLogin(String username,String token,int type){
        RequestParams rp = ac.getRequestParams();
        rp.put("userName", username);
        rp.put("otherToken", token);
        rp.put("userType",type);

        ac.httpClient.post(URLS.LOGIN, rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject jo) {
                super.onSuccess(jo);
                LogUtil.d(jo.toString());
            }

            @Override
            public void onFailure() {
                super.onFailure();
            }
        });
    }


}*/
