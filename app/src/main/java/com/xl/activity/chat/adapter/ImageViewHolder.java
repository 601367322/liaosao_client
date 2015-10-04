package com.xl.activity.chat.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xl.activity.R;
import com.xl.activity.chat.ImageViewActivity_;
import com.xl.bean.MessageBean;
import com.xl.util.StaticFactory;
import com.xl.util.StaticUtil;
import com.xl.util.URLS;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Shen on 2015/9/13.
 */
public class ImageViewHolder extends FileBaseHolder {

    @Nullable
    @Bind(R.id.img)
    PorterShapeImageView img;

    private DisplayImageOptions options_default = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.aio_image_default)
            .showImageOnFail(R.drawable.aio_image_default).showImageOnLoading(R.drawable.aio_image_default)
            .cacheInMemory(true).cacheOnDisk(true).build();

    public ImageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void bind(MessageBean bean) {
        super.bind(bean);
        switch (getMstType()) {
            case ChatAdapters.LEFT_IMG:
                if (bean.getLoading() == MessageBean.LOADING_NODOWNLOAD && !adapter.downloading.contains(bean)) {
                    adapter.downloading.add(bean);
                    File file = new File(StaticFactory.APKCardPathChat + bean.getFromId());
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    file = new File(file, bean.getContent());
                    ac.httpClient.get(URLS.DOWNLOADFILE + ac.deviceId + "/" + bean.getContent() + URLS.LAST, new fileDownloader(file, bean));
                } else if (bean.getLoading() == MessageBean.LOADING_DOWNLOADFAIL) {
                    img.setImageResource(R.drawable.aio_image_default);
                } else {
                    ImageLoader.getInstance().displayImage(StaticUtil.FILE + bean.getContent(), img, options_default);
                }
                break;
            case ChatAdapters.RIGHT_IMG:
                ImageLoader.getInstance().displayImage(StaticUtil.FILE + bean.getContent(), img, options_default);
                break;
        }

        img.setTag(StaticUtil.FILE + bean.getContent());
    }

    @Nullable
    @OnClick(R.id.img)
    public void imgClick(View v){
        ImageViewActivity_.intent(context).imageUrl(v.getTag().toString()).start();
    }
}
