package com.xl.activity.chat.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xl.activity.R;
import com.xl.activity.base.BaseActivity;
import com.xl.activity.chat.ChatPlayVideoFragment;
import com.xl.activity.chat.ChatPlayVideoFragment_;
import com.xl.activity.share.CommonShared;
import com.xl.bean.MessageBean;
import com.xl.util.StaticUtil;
import com.xl.util.URLS;
import com.xl.util.Utils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Shen on 2015/9/13.
 */
public class VideoViewHolderNew extends FileBaseHolder {

    @Nullable
    @Bind(R.id.img)
    ImageView radio;

    public VideoViewHolderNew(View itemView) {
        super(itemView);
    }

    @Override
    protected void bind(MessageBean bean) {
        super.bind(bean);

        MessageBean.RadioBean rb = bean.getRadioBean();
        if (getMstType() == ChatAdapters.RIGHT_RADIO_NEW) {
            ImageLoader.getInstance().displayImage(StaticUtil.FILE + rb.thumb, radio ,Utils.options_no_default);
        } else {
            ImageLoader.getInstance().displayImage(URLS.DOWNLOADFILE + ac.deviceId + "/" + rb.thumb + URLS.LAST, radio,Utils.options_no_default);
        }
        radio.setTag(bean);
    }

    @Nullable
    @OnClick(R.id.img)
    public void onRadioClick(View view) {
        MessageBean mb = (MessageBean) view.getTag();
        if (mb.getToId().equals(ac.deviceId)) {
            if (ac.cs.getISVIP() == CommonShared.OFF) {
                Utils.showVipDialog(context);
                return;
            }
        }
        final ChatPlayVideoFragment videoFragment = ChatPlayVideoFragment_.builder().bean(mb).build();
        videoFragment.show(((BaseActivity)context).getSupportFragmentManager(), "dialog");
    }
}
