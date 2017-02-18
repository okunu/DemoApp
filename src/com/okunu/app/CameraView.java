package com.okunu.app;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CameraView extends ViewGroup{

    int mWidth;
    int mHeight;
    //两个view之间的夹角
    float mAngle = 90;
    Camera mCamera;
    Matrix matrix;
    int mStartScreen = 1;
    
    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCamera = new Camera();
        matrix = new Matrix();
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

}
