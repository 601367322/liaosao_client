package com.xl.activity.job;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.xl.activity.R;
import com.xl.activity.pay.MoneyActivity_;
import com.xl.application.AppClass;
import com.xl.bean.ChatRoom;
import com.xl.util.JsonHttpResponseHandler;
import com.xl.util.ResultCode;
import com.xl.util.ToastUtil;
import com.xl.util.URLS;
import com.xl.util.Utils;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shen on 2016/1/17.
 */
@EFragment
public class ChatRightNowDialogFragment extends DialogFragment implements TextWatcher {

    @App
    AppClass ac;
    @FragmentArg
    ChatRoom room;
    @Bind(R.id.time)
    EditText time;
    @Bind(R.id.price)
    TextView price;
    @Bind(R.id.total)
    TextView total;

    Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null) {
            this.context = activity;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_chat_right_now_dialog, null);
        ButterKnife.bind(this, view);
        builder.setTitle("请输入购买分钟数(" + room.getMinTime() + "~" + room.getMaxTime() + ")").setView(view)
                .setPositiveButton("确定购买",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String timeStr = time.getText().toString();
                                if (TextUtils.isEmpty(timeStr)) {
                                    ToastUtil.toast(getActivity(), getString(R.string.heng));
                                    return;
                                } else {
                                    int time = Integer.valueOf(timeStr);
                                    if (time < room.getMinTime() || time > room.getMaxTime()) {
                                        ToastUtil.toast(getActivity(), getString(R.string.heng_timeout));
                                        return;
                                    }
                                }
                                sendChatRequest(timeStr);
                            }
                        }).setNegativeButton("取消", null);
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int dialogWidth = dm.widthPixels; // specify a value here
        getDialog().getWindow().setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        time.addTextChangedListener(this);
        time.setText(room.getMinTime().toString());
        time.setSelection(time.getText().toString().length());
        time.setFocusable(true);
        time.requestFocus();
        showkeyboard();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void afterTextChanged(Editable s) {
        String timeStr = s.toString();
        int time = 0;
        if (!TextUtils.isEmpty(timeStr)) {
            time = Integer.valueOf(timeStr);
        }
        if (time > CreateChatRoomActivity.MaxTime) {
            this.time.setText(String.valueOf(CreateChatRoomActivity.MaxTime));
            this.time.setSelection(this.time.getText().toString().length());
            return;
        }
        price.setText(time + "x" + room.getPrice() + "=");
        total.setText(time * room.getPrice() + "烧币");
    }

    @UiThread(delay = 100l)
    public void showkeyboard() {
        Utils.openSoftKeyboard(time);
    }

    public void sendChatRequest(String times) {
        RequestParams params = ac.getRequestParams();
        params.put("times", times);
        params.put("roomId", room.getId());
        ac.httpClient.post(getActivity(), URLS.SEND_CHAT_REQUEST, params, new JsonHttpResponseHandler(getActivity(), getString(R.string.loading)) {
            @Override
            public void onSuccessCode(JSONObject jo) throws Exception {
                super.onSuccessCode(jo);
                // TODO
            }

            @Override
            public void onFailCode(JSONObject jo) {
                super.onFailCode(jo);
                if (jo != null) {
                    int status = jo.optInt(ResultCode.STATUS);
                    switch (status) {
                        case ResultCode.NOMONEY:
                            showNoMoenyDialog();
                            break;
                    }
                }
            }
        });
    }

    public void showNoMoenyDialog() {
        new AlertDialog.Builder(context).setIcon(R.drawable.pool_gay).setTitle(R.string.poolgay)
                .setMessage(R.string.no_money_method).setNegativeButton(R.string.suanle, null).setPositiveButton(R.string.go_chongzhi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MoneyActivity_.intent(context).start();
            }
        }).create().show();
    }
}
