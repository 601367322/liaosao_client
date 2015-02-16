/*
package com.xl.game;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.custom.MyPinTuImageView;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

*/
/**
 * Created by sbb on 2015/2/12.
 *//*

@EActivity(R.layout.activity_pintu)
public class PinTuActivity extends BaseBackActivity {

    @ViewById
    RelativeLayout content;

    static final int num = 5;

    @Override
    protected void init() {

        final List<MyPinTuImageView> list = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            list.add((MyPinTuImageView) findViewById(getResources().getIdentifier("img_p" + i, "id",
                    getPackageName())));
        }

        ImageLoader.getInstance().loadImage("drawable://" + R.drawable.girl, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
               */
/* <com.xl.custom.MyPinTuImageView
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:src="@drawable/girl"
                app:siShape="@drawable/p1"
                app:siSquare="true" />*//*


                for (int i = 0; i < num; i++) {
                    list.get(i).setImageBitmap(loadedImage);

                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

//        dochange();
    }

    @UiThread(delay = 2000)
    public void dochange() {
//        img_p8.setDrawingCacheEnabled(true);
//        Bitmap obmp = Bitmap.createBitmap(img_p8.getDrawingCache());
//        img_p8.setDrawingCacheEnabled(false);
//        Bitmap obmp = Bitmap.createBitmap(img_p8.getDrawingCache());
//        img_temp.setImageBitmap(cropBitmapToBoundingBox(obmp, Color.TRANSPARENT));

    }

    public Bitmap cropBitmapToBoundingBox(Bitmap picToCrop, int unusedSpaceColor) {
        int[] pixels = new int[picToCrop.getHeight() * picToCrop.getWidth()];
        int marginTop = 0, marginBottom = 0, marginLeft = 0, marginRight = 0, i;
        picToCrop.getPixels(pixels, 0, picToCrop.getWidth(), 0, 0,
                picToCrop.getWidth(), picToCrop.getHeight());

        for (i = 0; i < pixels.length; i++) {
            if (pixels[i] != unusedSpaceColor) {
                marginTop = i / picToCrop.getWidth();
                break;
            }
        }

        outerLoop1:
        for (i = 0; i < picToCrop.getWidth(); i++) {
            for (int j = i; j < pixels.length; j += picToCrop.getWidth()) {
                if (pixels[j] != unusedSpaceColor) {
                    marginLeft = j % picToCrop.getWidth();
                    break outerLoop1;
                }
            }
        }

        for (i = pixels.length - 1; i >= 0; i--) {
            if (pixels[i] != unusedSpaceColor) {
                marginBottom = (pixels.length - i) / picToCrop.getWidth();
                break;
            }
        }

        outerLoop2:
        for (i = pixels.length - 1; i >= 0; i--) {
            for (int j = i; j >= 0; j -= picToCrop.getWidth()) {
                if (pixels[j] != unusedSpaceColor) {
                    marginRight = picToCrop.getWidth()
                            - (j % picToCrop.getWidth());
                    break outerLoop2;
                }
            }
        }

        return Bitmap.createBitmap(picToCrop, marginLeft, marginTop,
                picToCrop.getWidth() - marginLeft - marginRight,
                picToCrop.getHeight() - marginTop - marginBottom);
    }
}
*/
