package com.xl.game;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class GameViewTemp extends FrameLayout {

    public GameViewTemp(Context context) {
        this(context, null);
    }

    public GameViewTemp(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameViewTemp(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}


/*
package com.xl.game;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = event.getX();
			startY = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			offsetX = event.getX() - startX;
			offsetY = event.getY() - startY;

			if (Math.abs(offsetX) > Math.abs(offsetY)) {// 水平
				if (offsetX < -5) {// ←
					// LogUtil.i(TAG, "左");
					swipeLeft();
				} else if (offsetX > 5) {// →
					// LogUtil.i(TAG, "右");
					swipeRight();
				}
			} else {
				if (offsetY < -5) {// 上
					// LogUtil.i(TAG, "上");
					swipeUp();
				} else if (offsetY > 5) {// 下
					// LogUtil.i(TAG, "下");
					swipeDown();
				}
			}

			break;
		default:
			break;
		}
		return true;
	}

	private void swipeLeft() {
		List<MoveCard> mcs = new ArrayList<MoveCard>();
		for (int j = 0; j < 4; j++) {
			
			int same=0;
			for (int i = 0; i < 4; i++) {
				Object obj = cardsMap[i][j];
				if (obj != null && obj instanceof Card) {
					int nulls = 0;
					for (int k = 0; k < i; k++) {
 						if (cardsMap[k][j] == null) {
							nulls++;
						}
					}
					
					for (int k = (i-1) ; k >=0; k--) {
						if (cardsMap[k][j] != null) {
							if(cardsMap[k][j].equals(((Card)obj))){
								same++;
								((Card) obj).setDeleted(true);
							}
							break;
						}
					}
					nulls+=same;
					if (nulls > 0)
						mcs.add(new MoveCard((Card) obj, (nulls * cardWidth)));
				}
			}
		}
		List<Animator> list = new ArrayList<Animator>();
		for (MoveCard moveCard : mcs) {
			list.add(ObjectAnimator.ofFloat(moveCard.card, "translationX",
                    moveCard.card.getX(), moveCard.card.getX() - moveCard.num));
		}
		player(list);
	}

	public void player(List<Animator> list) {
		if (list.size() > 0) {
			AnimatorSet set = new AnimatorSet();
			set.playTogether(list);
			set.addListener(adapter);
			set.setDuration(200).start();
		}
	}

	private void swipeRight() {
		List<MoveCard> mcs = new ArrayList<MoveCard>();
		for (int j = 0; j < 4; j++) {
			
			int same=0;
			for (int i = 3; i >= 0; i--) {
				Object obj = cardsMap[i][j];
				if (obj != null && obj instanceof Card) {
					int nulls = 0;
					for (int k = 3; k >i; k--) {
						if (cardsMap[k][j] == null) {
							nulls++;
						}
					}
					
					for (int k = i+1 ; k <4; k++) {
						if (cardsMap[k][j] != null) {
							if(cardsMap[k][j].equals(((Card)obj))){
								same++;
								((Card) obj).setDeleted(true);
							}
							break;
						}
					}
					nulls+=same;
					if (nulls > 0)
						mcs.add(new MoveCard((Card) obj, (nulls * cardWidth)));
				}
			}
		}
		List<Animator> list = new ArrayList<Animator>();
		for (MoveCard moveCard : mcs) {
			list.add(ObjectAnimator.ofFloat(moveCard.card, "translationX",
					moveCard.card.getX(),moveCard.card.getX() + moveCard.num));
		}
		player(list);
	}

	private void swipeUp() {
		List<MoveCard> mcs = new ArrayList<MoveCard>();
		for (int i = 0; i < 4; i++) {
			
			int same=0;
			for (int j = 0; j < 4; j++) {
				Object obj = cardsMap[i][j];
				if (obj != null && obj instanceof Card) {
					int nulls = 0;
					for (int k = 0; k < j; k++) {
						if (cardsMap[i][k] == null) {
							nulls++;
						}
					}
					for (int k = (j-1) ; k >=0; k--) {
						if (cardsMap[i][k] != null) {
							if(cardsMap[i][k].equals(((Card)obj))){
								same++;
								((Card) obj).setDeleted(true);
							}
							break;
						}
					}
					nulls+=same;
					if (nulls > 0)
						mcs.add(new MoveCard((Card) obj, (nulls * cardWidth)));
				}
			}
		}
		List<Animator> list = new ArrayList<Animator>();
		for (MoveCard moveCard : mcs) {
			list.add(ObjectAnimator.ofFloat(moveCard.card, "translationY",
					moveCard.card.getY(), moveCard.card.getY() - moveCard.num));
		}
		player(list);
	}

	private void swipeDown() {
		List<MoveCard> mcs = new ArrayList<MoveCard>();
		for (int i = 0; i < 4; i++) {
			
			int same=0;
			for (int j = 3; j >= 0; j--) {
				Object obj = cardsMap[i][j];
				if (obj != null && obj instanceof Card) {
					int nulls = 0;
					for (int k = (j+1); k < 4; k++) {
						if (cardsMap[i][k] == null) {
							nulls++;
						}
					}
					
					for (int k = j+1 ; k <4; k++) {
						if (cardsMap[i][k] != null) {
							if(cardsMap[i][k].equals(((Card)obj))){
								same++;
								((Card) obj).setDeleted(true);
							}
							break;
						}
					}
					
					nulls+=same;
					if (nulls > 0)
						mcs.add(new MoveCard((Card) obj, (nulls * cardWidth)));
				}
			}
		}
		List<Animator> list = new ArrayList<Animator>();
		for (MoveCard moveCard : mcs) {
			list.add(ObjectAnimator.ofFloat(moveCard.card, "translationY",
					moveCard.card.getY(),moveCard.card.getY() + moveCard.num));
		}
		player(list);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);

		cardWidth = (Math.min(w, h) - 10) / 4;
		LayoutParams lp = new LayoutParams(cardWidth,
				cardWidth);
		// addView(createCard(),cardWidth,cardWidth);
		addCards(cardWidth, cardWidth);

		addCard(0, 3,8);
		addCard(1, 3,4);
		addCard(2, 3,4);
		addCard(3, 3,2);
		
	}

	private void addCards(int w, int h) {
		Card c;
		LayoutParams lp;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				lp = new LayoutParams(cardWidth, cardWidth);
				// lp.topMargin = i * cardWidth;
				// lp.leftMargin = j * cardWidth;
				c = Card.createCard(getContext());
				c.setLayoutParams(lp);
				c.setX(i * cardWidth);
				c.setY(j * cardWidth);
				addView(c, lp);

				// cardsMap[i][j] = c;
			}
		}
	}

	private void addCard(int x, int y) {
		LayoutParams lp = new LayoutParams(cardWidth,
				cardWidth);
		// lp.topMargin = y * cardWidth;
		// lp.leftMargin = x * cardWidth;
		Card c = Card.createCard(getContext());
		c.setNum(Card.random());
		c.setX(x * cardWidth);
		c.setY(y * cardWidth);
		addView(c, lp);
		cardsMap[x][y] = c;
	}
	
	private void addCard(int x, int y,int num) {
		LayoutParams lp = new LayoutParams(cardWidth,
				cardWidth);
		// lp.topMargin = y * cardWidth;
		// lp.leftMargin = x * cardWidth;
		Card c = Card.createCard(getContext());
		c.setNum(num);
		c.setX(x * cardWidth);
		c.setY(y * cardWidth);
		addView(c, lp);
		cardsMap[x][y] = c;
	}

	private Card[][] cardsMap = new Card[4][4];

	AnimatorListenerAdapter adapter = new AnimatorListenerAdapter() {
		@Override
		public void onAnimationEnd(Animator animation) {
			Card[][] cardsMapCopy = new Card[4][4];
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					if (cardsMap[i][j] != null) {
						Card c = cardsMap[i][j];
						int x = (int) c.getX();
						int y = (int) c.getY();
						
						int x1=x / cardWidth;
						int y1=y / cardWidth;
						
						if(cardsMapCopy[x1][y1]!=null){
							removeView(c);
							cardsMapCopy[x1][y1].setNum(cardsMapCopy[x1][y1].getNum()*2);
							cardsMapCopy[x1][y1].setDeleted(false);
						}else{
							c.setDeleted(false);
							cardsMapCopy[x / cardWidth][y / cardWidth] = c;
						}
					}
				}
			}

			cardsMap = cardsMapCopy;

			List<NumClass> list = new ArrayList<NumClass>();
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					if (cardsMap[i][j] == null) {
						NumClass nc = new NumClass(i, j);
						list.add(nc);
					}
				}
			}
			int d = (int) (Math.random() * list.size());
			NumClass nc = list.get(d);
			addCard(nc.i, nc.j);
		}
	};

	class NumClass {
		int i;
		int j;

		public NumClass(int i, int j) {
			super();
			this.i = i;
			this.j = j;
		}

		public NumClass() {
			super();
			// TODO Auto-generated constructor stub
		}
	}

	class MoveCard {
		Card card;
		int num;

		public MoveCard() {
			super();
			// TODO Auto-generated constructor stub
		}

		public MoveCard(Card card, int num) {
			super();
			this.card = card;
			this.num = num;
		}
	}
}
*/
