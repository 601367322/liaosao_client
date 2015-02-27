package com.xl.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.custom.MyPinTuImageView;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * Created by sbb on 2015/2/12.
 */

@EActivity(R.layout.activity_pintu)
public class PinTuActivity extends BaseBackActivity {

    @ViewById
    ImageView img;
    @ViewById
    FrameLayout content;

    Bitmap mainBitmap;
    ArrayList<Bitmap> bitmapList = new ArrayList<>();

    private static final PorterDuffXfermode PORTER_DUFF_XFERMODE = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    public static final DisplayImageOptions options_no_default = new DisplayImageOptions.Builder()
            .cacheInMemory(false).cacheOnDisk(false).build();
    static final int num = 5;

    @Override
    protected void init() {

        ImageLoader.getInstance().loadImage("drawable://" + R.drawable.girl, new listener(true, 0));

    }

    class listener extends SimpleImageLoadingListener {

        boolean isMain = false;

        int index = 0;

        public listener(boolean isMain, int index) {
            this.isMain = isMain;
            this.index = index;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            float screenWidth = (float) getResources().getDisplayMetrics().widthPixels;
            int width = loadedImage.getWidth();
            int height = loadedImage.getHeight();

            if (isMain) {
                int max = width <= height ? width : height;

                int y = (int) ((float) (height - max) / 2f);
                int x = (int) ((float) (width - max) / 2f);

                float scale = screenWidth / (float) max;
                Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);

                Bitmap newBitmap = Bitmap.createBitmap(loadedImage, x, y, max, max, matrix, false);

                mainBitmap = newBitmap;
                for (int i = 1; i <= 36; i++) {
                    ImageLoader.getInstance().loadImage("drawable://" + getResources().getIdentifier("p" + i, "drawable", getPackageName()), options_no_default, new listener(false, i));
                }
            } else {
                int left = 0, top = 0;
                switch (index) {
                    case 1:
                        left = (int) (1f / 656f * screenWidth);
                        break;
                    case 2:
                        left = (int) (102f / 656f * screenWidth);
                        break;
                    case 3:
                        left = (int) (198f / 656f * screenWidth);
                        break;
                    case 4:
                        left = (int) (326f / 656f * screenWidth);
                        break;
                    case 5:
                        left = (int) (428f / 656f * screenWidth);
                        break;
                    case 6:
                        left = (int) (524f / 656f * screenWidth);
                        break;
                    case 7:
                        top = (int) (89f / 654f * screenWidth);
                        break;
                    case 8:
                        left = (int) (89f / 656f * screenWidth);
                        top = (int) (102f / 654f * screenWidth);
                        break;
                    case 9:
                        left = (int) (211f / 656f * screenWidth);
                        top = (int) (89f / 654f * screenWidth);
                        break;
                    case 10:
                        left = (int) (326f / 656f * screenWidth);
                        top = (int) (89f / 654f * screenWidth);
                        break;
                    case 11:
                        left = (int) (415f / 656f * screenWidth);
                        top = (int) (101f / 654f * screenWidth);
                        break;
                    case 12:
                        left = (int) (537f / 656f * screenWidth);
                        top = (int) (89f / 654f * screenWidth);
                        break;
                    case 13:
                        top = (int) (210f / 654f * screenWidth);
                        break;
                    case 14:
                        left = (int) (102f / 656f * screenWidth);
                        top = (int) (198f / 654f * screenWidth);
                        break;
                    case 15:
                        left = (int) (198f / 656f * screenWidth);
                        top = (int) (210f / 654f * screenWidth);
                        break;
                    case 16:
                        left = (int) (326f / 656f * screenWidth);
                        top = (int) (210f / 654f * screenWidth);
                        break;
                    case 17:
                        left = (int) (428f / 656f * screenWidth);
                        top = (int) (197f / 654f * screenWidth);
                        break;
                    case 18:
                        left = (int) (524f / 656f * screenWidth);
                        top = (int) (210f / 654f * screenWidth);
                        break;
                    case 19:
                        left = (int) (1f / 656f * screenWidth);
                        top = (int) (326f / 654f * screenWidth);
                        break;
                    case 20:
                        left = (int) (103f / 656f * screenWidth);
                        top = (int) (326f / 654f * screenWidth);
                        break;
                    case 21:
                        left = (int) (199f / 656f * screenWidth);
                        top = (int) (326f / 654f * screenWidth);
                        break;
                    case 22:
                        left = (int) (327f / 656f * screenWidth);
                        top = (int) (326f / 654f * screenWidth);
                        break;
                    case 23:
                        left = (int) (428f / 656f * screenWidth);
                        top = (int) (325f / 654f * screenWidth);
                        break;
                    case 24:
                        left = (int) (524f / 656f * screenWidth);
                        top = (int) (325f / 654f * screenWidth);
                        break;
                    case 25:
                        left = (int) (1f / 656f * screenWidth);
                        top = (int) (414f / 654f * screenWidth);
                        break;
                    case 26:
                        left = (int) (90f / 656f * screenWidth);
                        top = (int) (427f / 654f * screenWidth);
                        break;
                    case 27:
                        left = (int) (212f / 656f * screenWidth);
                        top = (int) (414f / 654f * screenWidth);
                        break;
                    case 28:
                        left = (int) (327f / 656f * screenWidth);
                        top = (int) (414f / 654f * screenWidth);
                        break;
                    case 29:
                        left = (int) (415f / 656f * screenWidth);
                        top = (int) (427f / 654f * screenWidth);
                        break;
                    case 30:
                        left = (int) (537f / 656f * screenWidth);
                        top = (int) (414f / 654f * screenWidth);
                        break;
                    case 31:
                        left = (int) (1f / 656f * screenWidth);
                        top = (int) (536f / 654f * screenWidth);
                        break;
                    case 32:
                        left = (int) (103f / 656f * screenWidth);
                        top = (int) (523f / 654f * screenWidth);
                        break;
                    case 33:
                        left = (int) (199f / 656f * screenWidth);
                        top = (int) (536f / 654f * screenWidth);
                        break;
                    case 34:
                        left = (int) (327f / 656f * screenWidth);
                        top = (int) (536f / 654f * screenWidth);
                        break;
                    case 35:
                        left = (int) (428f / 656f * screenWidth);
                        top = (int) (522f / 654f * screenWidth);
                        break;
                    case 36:
                        left = (int) (524f / 656f * screenWidth);
                        top = (int) (535f / 654f * screenWidth);
                        break;
                }

                float scaleW = screenWidth / 656f;
                float scaleH = screenWidth / 654f;

                Matrix matrix = new Matrix();
                matrix.setScale(scaleW, scaleH);

                //拼图
                Bitmap newBitmap = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), matrix, false);

                //拼图大小背景
                Bitmap background = Bitmap.createBitmap(newBitmap.getWidth(), newBitmap.getHeight(), Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(background);

                Bitmap tempMainBitmap;
                canvas.drawBitmap(tempMainBitmap = Bitmap.createBitmap(mainBitmap, left, top, newBitmap.getWidth(), newBitmap.getHeight()), 0, 0, null);
                tempMainBitmap.recycle();

                Paint paint = new Paint();
                paint.setFilterBitmap(false);
                paint.setXfermode(PORTER_DUFF_XFERMODE);
                paint.setColor(0xFF000000);
                canvas.drawBitmap(newBitmap, 0, 0, paint);

                loadedImage.recycle();
                newBitmap.recycle();

                bitmapList.add(background);
                MyPinTuImageView imageView = new MyPinTuImageView(PinTuActivity.this);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(background);
                content.addView(imageView);
                imageView.setX(left);
                imageView.setY(top);

            }

        }
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