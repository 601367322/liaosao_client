package com.xl.game;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class GameView extends FrameLayout implements OnTouchListener {

    private static final String TAG = "GameView";

    private static final int MAX_COLUM = 4;//最大行数和列数

    private static final int MAX_MOVE = 5;//手指移动的最大距离

    private Card[][] cardsMap = new Card[MAX_COLUM][MAX_COLUM];

    private boolean isChange = false;

    public static final int COLUMSPACE=26;

    private int cardWidth;

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameView();
    }

    public GameView(Context context) {
        super(context);
        initGameView();
    }

    private void initGameView() {
        setOnTouchListener(this);
        setBackgroundColor(0xffbbaba0);
    }

    private float startX, startY, offsetX, offsetY;

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 250) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(isFastDoubleClick()){
                    return false;
                }
                isChange = false;
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                offsetX = event.getX() - startX;
                offsetY = event.getY() - startY;

                if (Math.abs(offsetX) > Math.abs(offsetY)) {// 水平
                    if (offsetX < -MAX_MOVE) {// ←
                        // LogUtil.i(TAG, "左");
                        swipeLeft();
                    } else if (offsetX > MAX_MOVE) {// →
                        // LogUtil.i(TAG, "右");
                        swipeRight();
                    }
                } else {
                    if (offsetY < -MAX_MOVE) {// 上
                        // LogUtil.i(TAG, "上");
                        swipeUp();
                    } else if (offsetY > MAX_MOVE) {// 下
                        // LogUtil.i(TAG, "下");
                        swipeDown();
                    }
                }
                randomAddCard(isChange);
                break;
            default:
                break;
        }
        return true;
    }

    public void randomAddCard(boolean isChange) {
        List<NumClass> list = new ArrayList<>();
        for (int i = 0; i < MAX_COLUM; i++) {
            for (int j = 0; j < MAX_COLUM; j++) {
                if (cardsMap[i][j] == null) {
                    NumClass nc = new NumClass(i, j);
                    list.add(nc);
                }
            }
        }
        if (isChange) {
            int d = (int) (Math.random() * list.size());
            NumClass nc = list.get(d);
            addCard(nc.i, nc.j);
        } else if (list.size() == 0) {//失败
            boolean nul = false;
            for (int x = 0; x < MAX_COLUM; x++) {
                for (int y = 0; y < MAX_COLUM; y++) {
                    Card card = cardsMap[x][y];
                    Card left = null, right = null, top = null, bottom = null;
                    if (x > 0) {
                        left = cardsMap[x - 1][y];
                    }
                    if (x < MAX_COLUM - 1 - 1) {
                        right = cardsMap[x + 1][y];
                    }
                    if (y > 0) {
                        top = cardsMap[x][y - 1];
                    }
                    if (y < MAX_COLUM - 1 - 1) {
                        bottom = cardsMap[x][y + 1];
                    }
                    if ((left != null && left.getNum() == card.getNum()) || (right != null && right.getNum() == card.getNum()) || (top != null && top.getNum() == card.getNum()) || (bottom != null && bottom.getNum() == card.getNum())) {
                        nul = true;
                    }
                }
            }
            if (listener != null && !nul) {
                int max = 0;
                for (int x = 0; x < MAX_COLUM; x++) {
                    for (int y = 0; y < MAX_COLUM; y++) {
                        Card card = cardsMap[x][y];
                        if(card.getNum()>max){
                            max=card.getNum();
                        }
                    }
                }

                listener.onFinishListener(Card.names[max]);
            }
        }
    }

    class NumClass {
        int i;
        int j;

        public NumClass(int i, int j) {
            super();
            this.i = i;
            this.j = j;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        cardWidth = (Math.min(w, h) - COLUMSPACE) / MAX_COLUM;

        addCards();

        randomAddCard(true);
        randomAddCard(true);
    }
    @SuppressWarnings("unused")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //添加背景
    private void addCards() {
        Card c;
        LayoutParams lp;
        for (int i = 0; i < MAX_COLUM; i++) {
            for (int j = 0; j < MAX_COLUM; j++) {
                lp = new LayoutParams(cardWidth, cardWidth);
                c = Card.createCard(getContext());
                c.setLayoutParams(lp);
                c.setX(i * cardWidth);
                c.setY(j * cardWidth);
                addView(c, lp);
            }
        }
    }

    //添加随机card
    private void addCard(int x, int y) {
        LayoutParams lp = new LayoutParams(cardWidth,
                cardWidth);
        Card c = Card.createCard(getContext());
        c.setNum(Card.random());
        c.setX(x * cardWidth);
        c.setY(y * cardWidth);
        addView(c, lp);
        cardsMap[x][y] = c;
        scaleAnim(c);
    }

    public void scaleAnim(View view) {

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.2f, 1f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.2f, 1f);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleDownX).with(scaleDownY);
        scaleDown.setDuration(200);
        scaleDown.start();

    }

    private void swipeLeft() {
        //从左至右，从上至下迭代
        for (int y = 0; y < MAX_COLUM; y++) {//行
            for (int x = 1; x < MAX_COLUM; x++) {//列
                boolean canMerge = true;//可以合并
                Card cards = cardsMap[x][y];
                if (cards != null) {
                    int num = 0;
                    for (int n = x - 1; n >= 0; n--) {//倒循环
                        if (cardsMap[n][y] == null) {
                            num--;
                        } else {
                            Card temp = cardsMap[n][y];
                            if (temp.getNum() == cards.getNum() && canMerge && !temp.isMerged()) {//如果数字一样，并且中间没有间隔,并且上一个未曾合并过
                                cardsMap[x][y] = null;//移除
                                if (num == 0) {//如果不需要移动直接删除
                                    temp.scaleAnim();//合并变大
                                    removeView(cards);
                                } else {
                                    cards.setDeleted(true);
                                    cards.setScaleCard(temp);
                                }
                                isChange = true;
                            } else {
                                canMerge = false;//中间有不一样的相隔，将不允许合并
                            }
                        }
                    }
                    if (num != 0) {
                        cardsMap[x][y] = null;//移动
                        if (!cards.isDeleted()) {//如果没有将要删除
                            cardsMap[x + num][y] = cards;
                        }
                        cards.moveToXY(num, Card.TRANSLATION_X);
                        isChange = true;
                    }
                }
            }
        }
    }

    private void swipeRight() {
        //从右至左，从上至下迭代
        for (int y = 0; y < MAX_COLUM; y++) {//行
            for (int x = MAX_COLUM - 1 - 1; x >= 0; x--) {//列
                boolean canMerge = true;//可以合并
                Card cards = cardsMap[x][y];
                if (cards != null) {
                    int num = 0;
                    for (int n = x + 1; n < MAX_COLUM; n++) {//倒循环
                        if (cardsMap[n][y] == null) {
                            num++;
                        } else {
                            Card temp = cardsMap[n][y];
                            if (temp.getNum() == cards.getNum() && canMerge && !temp.isMerged()) {//如果数字一样，并且中间没有间隔
                                cardsMap[x][y] = null;//移除
                                if (num == 0) {//如果不需要移动直接删除
                                    temp.scaleAnim();//合并变大
                                    removeView(cards);
                                } else {
                                    cards.setDeleted(true);
                                    cards.setScaleCard(temp);
                                }
                                isChange = true;
                            } else {
                                canMerge = false;//中间有不一样的相隔，将不允许合并
                            }
                        }
                    }
                    if (num != 0) {
                        cardsMap[x][y] = null;//移动
                        if (!cards.isDeleted()) {//如果将要删除就没有必要再赋值
                            cardsMap[x + num][y] = cards;
                        }
                        cards.moveToXY(num, Card.TRANSLATION_X);
                        isChange = true;
                    }
                }
            }
        }
    }

    private void swipeUp() {
        //从上至下，从左至右迭代
        for (int x = 0; x < MAX_COLUM; x++) {//行
            for (int y = 1; y < MAX_COLUM; y++) {//列
                boolean canMerge = true;//可以合并
                Card cards = cardsMap[x][y];
                if (cards != null) {
                    int num = 0;
                    for (int n = y - 1; n >= 0; n--) {//倒循环
                        if (cardsMap[x][n] == null) {
                            num--;
                        } else {
                            Card temp = cardsMap[x][n];
                            if (temp.getNum() == cards.getNum() && canMerge && !temp.isMerged()) {//如果数字一样，并且中间没有间隔,并且上一个未曾合并过
                                cardsMap[x][y] = null;//移除
                                if (num == 0) {//如果不需要移动直接删除
                                    temp.scaleAnim();//合并变大
                                    removeView(cards);
                                } else {
                                    cards.setDeleted(true);
                                    cards.setScaleCard(temp);
                                }
                                isChange = true;
                            } else {
                                canMerge = false;//中间有不一样的相隔，将不允许合并
                            }
                        }
                    }
                    if (num != 0) {
                        cardsMap[x][y] = null;//移动
                        if (!cards.isDeleted()) {//如果没有将要删除
                            cardsMap[x][y + num] = cards;
                        }
                        cards.moveToXY(num, Card.TRANSLATION_Y);
                        isChange = true;
                    }
                }
            }
        }
    }

    private void swipeDown() {
        //从下至上，从左至右迭代
        for (int x = 0; x < MAX_COLUM; x++) {//行
            for (int y = MAX_COLUM - 1 - 1; y >= 0; y--) {//列
                boolean canMerge = true;//可以合并
                Card cards = cardsMap[x][y];
                if (cards != null) {
                    int num = 0;
                    for (int n = y + 1; n < MAX_COLUM; n++) {//倒循环
                        if (cardsMap[x][n] == null) {
                            num++;
                        } else {
                            Card temp = cardsMap[x][n];
                            if (temp.getNum() == cards.getNum() && canMerge && !temp.isMerged()) {//如果数字一样，并且中间没有间隔
                                cardsMap[x][y] = null;//移除
                                if (num == 0) {//如果不需要移动直接删除
                                    temp.scaleAnim();//合并变大
                                    removeView(cards);
                                } else {
                                    cards.setDeleted(true);
                                    cards.setScaleCard(temp);
                                }
                                isChange = true;
                            } else {
                                canMerge = false;//中间有不一样的相隔，将不允许合并
                            }
                        }
                    }
                    if (num != 0) {
                        cardsMap[x][y] = null;//移动
                        if (!cards.isDeleted()) {//如果将要删除就没有必要再赋值
                            cardsMap[x][y + num] = cards;
                        }
                        cards.moveToXY(num, Card.TRANSLATION_Y);
                        isChange = true;
                    }
                }
            }
        }
    }

    public void setListener(GameViewListener listener) {
        this.listener = listener;
    }

    private GameViewListener listener;

    public interface GameViewListener {

        public void onFinishListener(String names);

    }

    public void reseat(){
        cardsMap = new Card[MAX_COLUM][MAX_COLUM];
        removeAllViews();

        addCards();

        randomAddCard(true);
        randomAddCard(true);
    }
}
