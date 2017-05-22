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

public class HorizontalEx extends ViewGroup {

    private int mContentWidth;
    private int mContentHeight;
    private Scroller mScroller;
    private VelocityTracker mTracker;
    
    public HorizontalEx(Context context) {
        super(context);
        init();
    }

    public HorizontalEx(Context context, AttributeSet attrs) {
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
        boolean intercepted = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInterceptX = x;
                mInterceptY = y;
                intercepted = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    intercepted = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int deltax = Math.abs(mInterceptX - x);
                int deltay = Math.abs(mInterceptY - y);
                if (deltax > deltay) {
                    intercepted = true;
                }else {
                    intercepted = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercepted = false;
                break;
        }
        Util.log("intercepted = " + intercepted + "  action = " + ev.getAction());
        return intercepted;
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
