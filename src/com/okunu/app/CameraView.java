package com.okunu.app;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class CameraView extends ViewGroup{

	private final static String TAG = "okunu";
	
    private int mWidth;
    private int mHeight;
    //两个view之间的夹角
    private float mAngle = 90;
    private Camera mCamera;
    private Matrix matrix;
    private int mStartScreen = 1;
    
    private Scroller mScroller;
    private float mDownY = 0f;
    private int mCurScreen = 1;
    private VelocityTracker mVelocityTracker;
    
    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCamera = new Camera();
        matrix = new Matrix();
        mScroller = new Scroller(context);
    }

    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(w, h);
        mWidth = w;
        mHeight = h;
        
        int childW = w - getPaddingLeft() - getPaddingRight();
        int childH = h - getPaddingTop() - getPaddingBottom();
        
        int childWSpec = MeasureSpec.makeMeasureSpec(childW, MeasureSpec.EXACTLY);
        int childHSpec = MeasureSpec.makeMeasureSpec(childH, MeasureSpec.EXACTLY);
        measureChildren(childWSpec, childHSpec);
        //默认此容器滚动到第二个view处
        scrollTo(0, mStartScreen*mHeight);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int left = (getPaddingLeft() + getPaddingRight())/2;
            int top = (getPaddingTop() + getPaddingBottom())/2;
            //子view是竖直排列的，通过camera方式，旋转才看到三D效果
            child.layout(left, top + i*mHeight, left + mWidth, top + (i+1)*mHeight);
        }
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        for (int i = 0; i < getChildCount(); i++) {
            drawScreen(canvas, i, getDrawingTime());
        }
    }
    
    private void drawScreen(Canvas canvas, int index, long time){
        int scrollHeight = mHeight * index;
        int scrollY = getScrollY();
        //view的位置明显看不到，则不需要绘制。比如滚动距离+view高度，还小于view的起始top值，则此view不绘制
        if (scrollHeight > scrollY + mHeight || scrollHeight < scrollY - mHeight) {
            return;
        }
        View child = getChildAt(index);
        //旋转中心点是旋转中的关键。view旋转的中心点都是0，0点，
        //所以需要先将中心点移到0，0点，旋转，再移动回来，看起来像view是在中心点旋转一样
        //如果是滚动距离大于view的top点，那么则y中心点则是view的bottom位置，否则则是top位置
        float centerX = mWidth/2;
        float centerY = (getScrollY() > scrollHeight) ? scrollHeight + mHeight : scrollHeight;
        //计算旋转角度
        float degree = mAngle * (getScrollY() - scrollHeight)/mHeight;
        if (degree > 90 || degree < -90) {
            return;
        }
        canvas.save();
        mCamera.save();
        matrix.reset();
        mCamera.rotateX(degree);
        mCamera.getMatrix(matrix);
        mCamera.restore();
        //移动到旋转中心点
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
        canvas.concat(matrix);
        drawChild(canvas, child, time);
        canvas.restore();
    }


	@Override
	public boolean onInterceptHoverEvent(MotionEvent event) {
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
			}
			mVelocityTracker.addMovement(event);
			mDownY = event.getY();
			if (!mScroller.isFinished()) {
				int currenty = mScroller.getCurrY();
				mScroller.setFinalY(currenty);
				mScroller.abortAnimation();
				scrollTo(0, currenty);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			mVelocityTracker.addMovement(event);
			float y = event.getY();
			float detal = y - mDownY;
			mDownY = y;
			//当scroller结束滚动时再响应move事件。
			if (mScroller.isFinished()) {
				moveScroll((int)detal);
			}
			break;
		case MotionEvent.ACTION_UP:
			mVelocityTracker.addMovement(event);
			mVelocityTracker.computeCurrentVelocity(1000);
			float vel = mVelocityTracker.getYVelocity();
//			Log.i(TAG, "vel = " + vel);
			//y速度值为正则是往下滑动，为负则是往上滑动,以500为界定
			if (vel >= 500) {
				moveToNext();
				//滑动到下一屏
			}else if (vel <= -500) {
				//滑动到上一屏
				moveToPre();
			}else {
				//依然在当前屏
				moveNormal();
			}
			mVelocityTracker.clear();
			mVelocityTracker.recycle();
			mVelocityTracker = null;
			break;
		}
		return true;
	}
	
	private void moveToPre(){
		addPreView();
		int scrolly = getScrollY();
		//以从第二个view回到第一个view为例，第二个view的滚动距离为scrolly，而第一个view的正常位置则是滚动距离为0
		//所以从第二个view滚动回第一个view的真正距离就是scrolly，因为是向上，所以为负值
		int curY = scrolly + mHeight;
		setScrollY(curY);
		int detal = -(curY - mHeight);
		mScroller.startScroll(0, curY, 0, detal,500);
	}
	
	private void moveToNext(){
		addNextView();
		int scrolly = getScrollY();
		int curY = scrolly - mHeight;
		setScrollY(curY);
		int detal = mHeight - (curY);
		mScroller.startScroll(0, curY, 0, detal,500);
	}
	
	private void moveNormal(){
		int scrolly = getScrollY();
		int curY = scrolly;
		int detal = mHeight - curY;
//		Log.i(TAG, "cury = " + curY + "  detal = " + detal + "  mheight = " + mHeight);
		mScroller.startScroll(0, curY, 0, detal,500);
		//此处必须刷新，否则computeScroll不会执行
		invalidate();
	}
	
	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
//			Log.i(TAG, "cy = " + mScroller.getCurrY());
			invalidate();
		}
	}

	private void moveScroll(int detal){
//		Log.i(TAG, "scrolly = " + getScrollY());
		scrollBy(0, detal);
		//滚动y轴非常有意思，它的值是动态变化的，当第0个view显示完全时scrolly值为0，此时会添加新的一页且index为0
		//此时view自动调节此时的滚动距离为height高度，因为当前显示的子view，实质上已经是viewgroup的第二个view了
		if (getScrollY() < 8) {
			addPreView();
		}else if (getScrollY() > (getChildCount() - 1)*mHeight - 10) {
			addNextView();
		}
//		Log.i(TAG, "mcurrenty = " + mCurScreen);
	}
	
	//容器内第一个view显示完后需要将最后一个view添加进来，循环显示
	private void addPreView(){
		mCurScreen = (mCurScreen - 1 + getChildCount())%getChildCount();
		int last = getChildCount() - 1;
		View view = getChildAt(last);
		removeViewAt(last);
		addView(view, 0);
	}
	
	//容器的最后一个view显示完后，需要添加新的view进来，循环显示
	private void addNextView(){
		mCurScreen = (mCurScreen + 1)%getChildCount();
		int childCount = getChildCount();
		View view = getChildAt(0);
		removeViewAt(0);
		addView(view, childCount - 1);
	}
    
}
