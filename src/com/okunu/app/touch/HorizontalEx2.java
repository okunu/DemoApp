package com.okunu.app.touch;

import com.okunu.app.util.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

public class HorizontalEx2 extends ViewGroup {

    private int mContentWidth;
    private int mContentHeight;
    private Scroller mScroller;
    private VelocityTracker mTracker;
    
    public HorizontalEx2(Context context) {
        super(context);
        init();
    }

    public HorizontalEx2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        mContentWidth = wm.getDefaultDisplay().getWidth() - Util.getStatusBarHeight(getContext());
        mContentHeight = wm.getDefaultDisplay().getHeight();
        mScroller = new Scroller(getContext());
        mTracker = VelocityTracker.obtain();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            Util.log("left = " + (l + left));
            child.layout(l + left, t, r + left, b);
            left += mContentWidth;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int w = 0;
        int h = 0;

        if (getChildCount() == 0) {
            w = width;
            h = height;
        } else {
            w = mContentWidth * getChildCount();
            h = mContentHeight;
        }
        int childW = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, mContentWidth);
        int childH = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, mContentHeight);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(childW, childH);
        }
        Util.log("w = " + w + "  h = " + h + "  getChildCount = " + getChildCount() + "  screenw = " + mContentWidth);
        setMeasuredDimension(w, h);
    }

    private int mLastX;
    private int mLastY;
    private int mInterceptX;
    private int mInterceptY;
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Util.log("group2 onInterceptTouchEvent action = " + ev.getAction());
        //父view在down事件时，不拦截，子view处理down事件时将父view设置标志位，禁止父view拦截touch事件
        //父view其它事件均返回为true，这是为了时刻准备着，如果子view不需要处理此事件，则父view获得机会，将拦截此事件
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
                return true;
            }
            return false;
        }else {
            return true;
        }
    }

    private boolean mFirstTouch = true;
    private int mChildIndex = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        mTracker.addMovement(event);
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mFirstTouch) {
                    mLastX = x;
                    mLastY = y;
                    mFirstTouch = false;
                }
                int deltax = x - mLastX;
                scrollBy(-deltax, 0);
                break;
            case MotionEvent.ACTION_UP:
                int scrollx = getScrollX();
                mTracker.computeCurrentVelocity(1000, configuration.getScaledMaximumFlingVelocity());
                float xV = mTracker.getXVelocity();
                if (Math.abs(xV) > configuration.getScaledMinimumFlingVelocity()) {
                    mChildIndex = xV < 0 ? mChildIndex + 1 : mChildIndex - 1;
                }else {
                    mChildIndex = (scrollx + mContentWidth/2)/mContentWidth;
                }
                mChildIndex = Math.min(getChildCount() - 1, Math.max(mChildIndex, 0));
                smoothScrolly(mContentWidth * mChildIndex - scrollx);
                mTracker.clear();
                mFirstTouch = true;
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }
    
    private void smoothScrolly(int dx){
        mScroller.startScroll(getScrollX(), getScrollY(), dx, 0, 500);
        invalidate();
    }
    
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

}
