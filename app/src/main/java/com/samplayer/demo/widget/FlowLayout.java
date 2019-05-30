package com.samplayer.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * 流布局
 *
 * @url http://www.27house.cn/archives/329
 */
public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public FlowLayout(Context context) {
        super(context, null);
    }

    /**
     * 测量视图
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // 最终测量的大小
        int width = 0;
        int height = 0;

        // 每一行的宽高
        int lineWidth = 0;

        // 第N-1行的高度
        int tempHeight = 0;

        // 获取有多少个子布局
        int childCount = getChildCount();

        for (int index = 0; index < childCount; index++) {
            // 获取子View
            View child = getChildAt(index);

            // 如果子View不可见
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            // 测量子View
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 获取子View的布局参数
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            // 获取子布局的宽高
            int childWidth = child.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin;

            // 如果一行放不下了，那么这个子View应该放到下一行
            if (lineWidth + childWidth + getPaddingLeft() + getPaddingRight() > sizeWidth) {
                tempHeight = height;// 记录N-1行的高度
                width = Math.max(width, lineWidth);
                lineWidth = childWidth;
                height = height + childHeight;
            } else {
                lineWidth = lineWidth + childWidth;
                height = Math.max(height, tempHeight + childHeight);
            }
        }
        // 加上边距
        height = height + getPaddingTop() + getPaddingBottom();
        width = width + getPaddingLeft() + getPaddingRight();
        Log.v("x", height + "/" + width);
        setMeasuredDimension(
                //
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width,
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height);
    }

    /**
     * 视图布局
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 获取视图的宽度
        int width = getWidth();

        // 布局坐标,考虑到布局有可能设置了内边距
        int left = getPaddingLeft();
        int top = getPaddingTop();

        // 一行中最高的一个view的高度
        int maxHeight = 0;
        int viewCount = getChildCount();
        for (int index = 0; index < viewCount; index++) {
            View child = getChildAt(index);
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();
            int childHeight = child.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin;
            int childWidth = child.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin;

            // 换行
            if (left + childWidth + getPaddingRight() > width) {
                left = getPaddingLeft();
                top = top + maxHeight;
                maxHeight = 0;
            } else {
                // 得到最高高度
                maxHeight = Math.max(childHeight, maxHeight);
            }
            int lc = left + lp.leftMargin;
            int tc = top + lp.topMargin;
            int rc = lc + child.getMeasuredWidth();
            int bc = tc + child.getMeasuredHeight();

            child.layout(lc, tc, rc, bc);
            left = left + childWidth;
        }
    }

    /**
     * 返回默认的LayoutParams，如果一个视图使用addView(View)添加而没有指定LayoutParams的时候
     * 使用MarginLayoutParams当做LayoutParams
     * 这个函数返回的类型就是View.getLayoutParams的返回值类型
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}