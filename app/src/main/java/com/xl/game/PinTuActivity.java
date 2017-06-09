package com.xl.game;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xl.activity.R;
import com.xl.activity.base.BaseBackActivity;
import com.xl.custom.MyPinTuImageView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * Created by sbb on 2015/2/12.
 */

@OptionsMenu(R.menu.pintu_menu)
@EActivity(R.layout.activity_pintu)
public class PinTuActivity extends BaseBackActivity implements MyPinTuImageView.OnTouchListener {

    @ViewById
    ImageView img, origin_img;
    @ViewById
    FrameLayout content;

    @ViewById
    View hide_restart_ll, restart_btn;

    @ViewById
    View hide_ll;
    @ViewById
    TextView times;

    Bitmap mainBitmap;
    ArrayList<MyPinTuImageView> bitmapList = new ArrayList<>();

    final int allNum = 36;

    float maxWidth = 0;

    private static final PorterDuffXfermode PORTER_DUFF_XFERMODE = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    public static final DisplayImageOptions options_no_default = new DisplayImageOptions.Builder()
            .cacheInMemory(false).cacheOnDisk(false).build();

    float[][] xy = {
            new float[]{1, 0},
            new float[]{102, 0},
            new float[]{198, 0},
            new float[]{326, 0},
            new float[]{428, 0},
            new float[]{524, 0},
            new float[]{0, 89},
            new float[]{89, 102},
            new float[]{211, 89},
            new float[]{326, 89},
            new float[]{415, 101},
            new float[]{537, 89},
            new float[]{0, 210},
            new float[]{102, 198},
            new float[]{198, 210},
            new float[]{326, 210},
            new float[]{428, 197},
            new float[]{524, 210},
            new float[]{1, 326},
            new float[]{103, 326},
            new float[]{199, 326},
            new float[]{327, 326},
            new float[]{428, 325},
            new float[]{524, 325},
            new float[]{1, 414},
            new float[]{90, 427},
            new float[]{212, 414},
            new float[]{327, 414},
            new float[]{415, 427},
            new float[]{537, 414},
            new float[]{1, 536},
            new float[]{103, 523},
            new float[]{199, 536},
            new float[]{327, 536},
            new float[]{428, 522},
            new float[]{524, 535}
    };

    @OptionsItem(R.id.menu_item_show_origin)
    void showOrigin() {
        if (origin_img.getVisibility() == View.VISIBLE) {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(origin_img, "scaleX", 0f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(origin_img, "scaleY", 0f);
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.playTogether(scaleDownX, scaleDownY);
            scaleDown.setDuration(200);
            scaleDown.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    origin_img.setVisibility(View.GONE);
                }
            });
            scaleDown.start();
        } else {
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(origin_img, "scaleX", 1f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(origin_img, "scaleY", 1f);
            AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.playTogether(scaleDownX, scaleDownY);
            scaleDown.setDuration(200);
            scaleDown.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    origin_img.setVisibility(View.VISIBLE);
                }
            });
            scaleDown.start();
        }
    }


    float[][] centerXY = new float[allNum][2];

    @Override
    protected void init() {
        maxWidth = (float) getResources().getDisplayMetrics().widthPixels;
        ImageLoader.getInstance().loadImage("file://" + new PhotoAlbumActivity(this).getPhotoAlbum(), new listener(true, 0));
        origin_img.setPivotX(getResources().getDisplayMetrics().widthPixels);
        origin_img.setPivotY(0);
        origin_img.setScaleX(0f);
        origin_img.setScaleY(0f);

    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setTitle(getString(R.string.boring_pintu));
        getSupportActionBar().setSubtitle("点击拼块可旋转");
    }

    class listener extends SimpleImageLoadingListener {

        boolean isMain = false;

        int index = 0;

        public listener(boolean isMain, int index) {
            this.isMain = isMain;
            this.index = index;
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            toast("相册中并没有图片！！");
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            try {
                int width = loadedImage.getWidth();
                int height = loadedImage.getHeight();

                if (isMain) {
                    int max = width <= height ? width : height;

                    int y = (int) ((float) (height - max) / 2f);
                    int x = (int) ((float) (width - max) / 2f);

                    float scale = maxWidth / (float) max;
                    Matrix matrix = new Matrix();
                    matrix.setScale(scale, scale);

                    Bitmap newBitmap = Bitmap.createBitmap(loadedImage, x, y, max, max, matrix, false);

                    mainBitmap = newBitmap;

                    origin_img.setImageBitmap(mainBitmap);

                    for (int i = 1; i <= allNum; i++) {
                        ImageLoader.getInstance().loadImage("drawable://" + getResources().getIdentifier("p" + i, "drawable", getPackageName()), options_no_default, new listener(false, i));
                    }
                } else {
                    int left = (int) (xy[index - 1][0] / 656f * maxWidth), top = (int) (xy[index - 1][1] / 654f * maxWidth);

                    float scaleW = maxWidth / 656f;
                    float scaleH = maxWidth / 654f;

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


                    MyPinTuImageView imageView = new MyPinTuImageView(PinTuActivity.this);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setImageBitmap(background);
                    imageView.setTouchListener(PinTuActivity.this);
                    imageView.setTag(new float[]{left, top, index});
                    content.addView(imageView);
                    bitmapList.add(imageView);
                    imageView.setX(left);
                    imageView.setY(top);

                    centerXY[index - 1] = new float[]{left + background.getWidth() / 2, top + background.getHeight() / 2};

                    if (bitmapList.size() == allNum) {
                        hide_ll.setVisibility(View.VISIBLE);
                        handler.postDelayed(runnable, 1000);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
                toast("什么鬼？");
                finish();
            }
        }
    }

    int[] rota = {0, 90, 180, -90, -180};

    @UiThread
    public void startAnimation() {
        for (int i = 0; i < bitmapList.size(); i++) {
            final MyPinTuImageView imageView = bitmapList.get(i);
            float y = imageView.getY();
            float x = imageView.getX();
            float toY = content.getMeasuredHeight() - imageView.getMeasuredHeight();

            float toX = (float) (Math.random() * content.getMeasuredWidth() - imageView.getWidth());
            if (toX < 0) {
                toX = 0;
            } else if (toX + imageView.getWidth() > content.getMeasuredWidth()) {
                toX = content.getMeasuredWidth() - imageView.getWidth();
            }
            ValueAnimator animator = ObjectAnimator.ofFloat(imageView, "y", y, toY).setDuration(1000);
            animator.setInterpolator(new BounceInterpolator());

            final int radom = (int) (Math.random() * rota.length);

            ValueAnimator animatorX = ObjectAnimator.ofFloat(imageView, "x", x, toX).setDuration(500);
            animatorX.setStartDelay(300);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animator, animatorX);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    Matrix matrix = new Matrix();
                    matrix.setRotate(rota[radom]);

                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false));
                }
            });
            float[] floats = (float[]) imageView.getTag();
            animatorSet.setStartDelay((long) (allNum - floats[2]) * 100);
            animatorSet.start();
        }
    }

    @Override
    public void onClick(MyPinTuImageView imageView) {
        Matrix matrix = new Matrix();
        matrix.setRotate(90);

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false));
    }

    @Override
    public void onActionUp(MyPinTuImageView imageView) {

        float x = imageView.getX();
        float y = imageView.getY();
        if (x > maxWidth || y > maxWidth) {
            return;
        }

        float centerX = x + imageView.getWidth() / 2;
        float centerY = y + imageView.getHeight() / 2;

        int index = -1;

        float temp = -1;

        for (int i = 0; i < centerXY.length; i++) {

            float n = -1;
            if (Math.abs((centerX - centerXY[i][0])) == 0 && Math.abs((centerY - centerXY[i][1])) != 0) {
                n = Math.abs((centerY - centerXY[i][1]));
            } else if (Math.abs((centerX - centerXY[i][0])) != 0 && Math.abs((centerY - centerXY[i][1])) == 0) {
                n = Math.abs((centerX - centerXY[i][0]));
            } else {
                n = Math.abs((centerX - centerXY[i][0])) * Math.abs((centerY - centerXY[i][1]));
            }

            float dis = getResources().getDisplayMetrics().widthPixels / 12;

            if (Math.abs((centerX - centerXY[i][0])) < dis && Math.abs((centerY - centerXY[i][1])) < dis) {
                if (temp == -1) {
                    temp = n;
                    index = i;
                } else {
                    if (n < temp) {
                        temp = n;
                        index = i;
                    }
                }
            }
        }

        if (index > -1) {
            float nx = centerXY[index][0];
            float ny = centerXY[index][1];

            imageView.setX(nx - imageView.getWidth() / 2);
            imageView.setY(ny - imageView.getHeight() / 2);
        }

        boolean allSuccess = true;

        for (int i = 0; i < bitmapList.size(); i++) {
            ImageView imageView1 = bitmapList.get(i);
            float[] floats = (float[]) imageView1.getTag();
            float oldLeft = floats[0];
            float oldTop = floats[1];
            float newLeft = imageView1.getX();
            float newTop = imageView1.getY();

            if (oldLeft == newLeft && oldTop == newTop) {
            } else {
                allSuccess = false;
            }
        }
        if (allSuccess) {
            hide_restart_ll.setVisibility(View.VISIBLE);
        }
    }

    @Click
    public void origin_img() {
        showOrigin();
    }

    @Click
    public void restart_btn() {
        bitmapList.clear();
        content.removeAllViews();
        hide_restart_ll.setVisibility(View.GONE);
        ImageLoader.getInstance().loadImage("file://" + new PhotoAlbumActivity(this).getPhotoAlbum(), new listener(true, 0));
        origin_img.setPivotX(getResources().getDisplayMetrics().widthPixels);
        origin_img.setPivotY(0);
        origin_img.setScaleX(0f);
        origin_img.setScaleY(0f);
    }

    Handler handler = new Handler();
    int num = 3;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (num == 1) {
                hide_ll.setVisibility(View.GONE);
                num = 3;
                times.setText(num + "");
                startAnimation();
                return;
            }
            times.setText(--num + "");
            handler.postDelayed(this, 1000);
        }
    };

}