package com.xl.game;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.xl.activity.R;
import com.xl.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import me.grantland.widget.AutofitHelper;
import me.grantland.widget.AutofitTextView;

public class Card extends FrameLayout {

    private int min = 2;
    private double minOp = 0.1;
    private boolean willAdd = false;
    private boolean deleted = false;
    private boolean isMerged = false;

    private Card scaleCard = null;

    public Card getScaleCard() {
        return scaleCard;
    }

    public void setScaleCard(Card scaleCard) {
        this.scaleCard = scaleCard;
    }

    public boolean isMerged() {
        return isMerged;
    }

    public void setMerged(boolean isMerged) {
        this.isMerged = isMerged;
    }

    private List<Animator> animatorList = new ArrayList<>();

    public List<Animator> getAnimatorList() {
        return animatorList;
    }

    private int[] colors = {
            R.color.card_color1,
            R.color.card_color2,
            R.color.holo_blue_light,
            R.color.holo_blue_dark,
            R.color.holo_green_light,
            R.color.holo_green_dark,
            R.color.holo_orange_light,
            R.color.holo_orange_dark,
            R.color.holo_red_light,
            R.color.holo_red_dark,
            R.color.white,
            R.color.card_color3,
            R.color.white, R.color.white};

    public static String[] names = {
            "小学生",
            "中学生",
            "高中生",
            "大学生",
            "屌丝",
            "普通青年",
            "白领",
            "经理",
            "总监",
            "灵道",
            "高富帅",
            "超级纯屌",
            "别玩了",
            "APP将崩溃"
    };

    public static final String TRANSLATION_X = "translationX", TRANSLATION_Y = "translationY";

    public void moveToXY(int num, String xy) {
        float getXY = xy.equals(TRANSLATION_X) ? getX() : getY();
        ValueAnimator animator = ObjectAnimator.ofFloat(this, xy, getXY, getXY + num * getMeasuredWidth());
        animator.setDuration(100);
        if (deleted) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ((FrameLayout) getParent()).removeView(Card.this);
                    if (getScaleCard() != null) {
                        getScaleCard().scaleAnim();
                        setScaleCard(null);
                    }
                }
            });
        }
        animator.start();
    }

    public void scaleAnim() {

        setNum(1 + num);

        setMerged(true);

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(this, "scaleX", 1.1f);
        scaleDownX.setRepeatCount(1);
        scaleDownX.setRepeatMode(ValueAnimator.REVERSE);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(this, "scaleY", 1.1f);
        scaleDownY.setRepeatCount(1);
        scaleDownY.setRepeatMode(ValueAnimator.REVERSE);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.setDuration(100);
        scaleDown.start();

        scaleDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setMerged(false);
            }
        });
    }


    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isWillAdd() {
        return willAdd;
    }

    public void setWillAdd(boolean willAdd) {
        this.willAdd = willAdd;
    }

    public Card(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Card(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Card(Context context) {
        super(context);

        lable = new AutofitTextView(getContext());
        lable.setTextSize(24);
        lable.setSingleLine(true);
        lable.setPadding(10,0,10,0);
        AutofitHelper.create(lable);
        lable.setGravity(Gravity.CENTER);

        LayoutParams lp = new LayoutParams(-1, -1);
        lp.setMargins(GameView.COLUMSPACE, GameView.COLUMSPACE, 0, 0);
        addView(lable, lp);
    }

    private int num = 0;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        ShapeDrawable myShapeDrawable = new ShapeDrawable(new OvalShape());
        if (num == -1) {
            myShapeDrawable.getPaint().setColor(Color.parseColor("#" + getOp(0.05) + "ff6600"));
            lable.setBackgroundDrawable(myShapeDrawable);
        } else {
            this.num = num;
            String op = getOp(dpow(num, 2, 1) * minOp);
            lable.setText(names[num]);

            if (num >= 16) {
                LogUtil.d("Card", op + "");
            }
            myShapeDrawable.getPaint().setColor(getResources().getColor(colors[num]));
            lable.setBackgroundDrawable(myShapeDrawable);
        }
    }

    public int dpow(int sum, int num, int nums) {
        if (sum / num > 1) {
            nums++;
            return dpow(sum / num, num, nums);
        } else {
            return nums;
        }
    }

    public String getOp(double a) {
        double num = Math.floor((Math.floor(a * 100) / 100) * 255);
        String num_change = Integer.toHexString((int) (num));
        if (num_change.length() == 1) {
            num_change = "0" + num_change;
        }
        String o = num_change.toUpperCase();
        return o;
    }

    public static double rate0 = 0.70;
    public static double rate1 = 0.30;

    public static Card createCard(Context context) {

        Card c = new Card(context);
        c.setNum(-1);

        return c;
    }

    public static int random() {
        double num = Math.random();
        int n = 0;
        if (num >= 0 && num <= rate0) {
            n = 0;
        } else if (num >= rate0 / 100 && num <= rate0 + rate1) {
            n = 1;
        }
        return n;
    }

    private AutofitTextView lable;

    public boolean equals(Card o) {
        if (isDeleted()) {
            return false;
        }
        return o.getNum() == getNum();
    }
}
