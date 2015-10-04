package com.xl.activity.chat.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.View;

import com.xl.activity.R;
import com.xl.activity.share.CommonShared;
import com.xl.bean.MessageBean;
import com.xl.util.StaticFactory;
import com.xl.util.URLS;
import com.xl.util.Utils;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Shen on 2015/9/13.
 */
public class VideoViewHolder extends FileBaseHolder {

    @Nullable
    @Bind(R.id.radio)
    View radio;

    public VideoViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void bind(MessageBean bean) {
        super.bind(bean);

        if (bean.getLoading() == MessageBean.LOADING_NODOWNLOAD && getMstType() == ChatAdapters.LEFT_RADIO && !adapter.downloading.contains(bean)) {
            adapter.downloading.add(bean);
            File file = new File(StaticFactory.APKCardPathChat + bean.getFromId());
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(file, bean.getContent());
            ac.httpClient.get(URLS.DOWNLOADFILE + ac.deviceId + "/" + bean.getContent() + URLS.LAST, new fileDownloader(file, bean));
        } else if (bean.getLoading() == MessageBean.LOADING_DOWNLOADFAIL) {
        } else {
        }
        radio.setTag(bean);
    }

    @Nullable
    @OnClick(R.id.radio)
    public void onRadioClick(View view) {
        MessageBean mb = (MessageBean) view.getTag();
        if (mb.getToId().equals(ac.deviceId)) {
            if (ac.cs.getISVIP() == CommonShared.OFF) {
                Utils.showVipDialog(context);
                return;
            }
        }
        if (mb.getLoading() == MessageBean.LOADING_DOWNLOADED) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String type = "video/mp4";
            Uri uri = Uri.parse("file://" + mb.getContent());
            intent.setDataAndType(uri, type);
            context.startActivity(intent);
        }
    }
}
