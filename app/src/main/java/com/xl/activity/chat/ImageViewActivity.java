package com.xl.activity.chat;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xl.activity.base.BaseActivity;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.Fullscreen;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by sbb on 2015/1/8.
 */
@Fullscreen
@EActivity
public class ImageViewActivity extends BaseActivity {

    @Extra
    String imageUrl;
    ImageView imageView;
    PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
        mAttacher = new PhotoViewAttacher(imageView);
        ImageLoader.getInstance().displayImage(imageUrl,imageView);
        setContentView(imageView);
    }

    @Override
    protected void init() {
        try{
            getSupportActionBar().hide();
        }catch (Exception ex){

        }
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                finish();
            }
        });
    }

}
