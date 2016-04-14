package com.zhuo.tong.view.pager_indicator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 简单的扩展线性布局，为了支持低版本（3.0以下）设置分隔线，添加扩展方法
 * 很重要的：线性布局（3.0以下）只能使用一个参数的构造方法，所以自定义的
 * 要自己处理传进来的参数。虽然不清楚低版本是否没有2个及3个参数的构造方法
 * 但是就算是低版本在布局文件中定义该控件，还是调用2个参数的构造方法，所以
 * 该控件要自己解析布局文件中的参数，不然像朝向这样的值默认都是水平的，即使
 * 布局文件指定垂直也没用，因为要自己在代码中实现。
 * 自己猜测很可能是低版本的布局填充器有其他的设置属性的方式
 * 注意：获取布局中的属性好像不是同一个类型的属性要new不同的数组重新获取，
 * 像orientation，gravity放在一个数组中只能获取到第一个。
 * 另外好像layout开头的属性好像是父布局获取的，不然这个控件中也没有获取
 * 布局中的layout宽高，但是却是按照布局文件中的设置走的
 */
public class DividerLinearLayout extends LinearLayout {
	@SuppressLint("InlinedApi")
	private static final int[] LL = new int[] {
    	/* 0 */ android.R.attr.divider,
    	/* 1 */ android.R.attr.showDividers,
    	/* 2 */ android.R.attr.dividerPadding,
    };
    
    private static final int LL_DIVIDER = 0;
    private static final int LL_SHOW_DIVIDER = 1;
    private static final int LL_DIVIDER_PADDING = 2;
    
    /*线性布局中的参数，都是关于分割线的*/
    private Drawable mDivider;
    private int mDividerWidth;
    private int mDividerHeight;
    private int mShowDividers;
    private int mDividerPadding;

    public DividerLinearLayout(Context context, AttributeSet attrs){
    	super(context);
		//最后一个参数是样式文件，这个可以是布局文件中指定的样式（如果布局文件中指定就出现在attrs中，该参数可以为0），如果
    	//布局中未指定则默认使用这个参数对应的样式文件，和第三个参数对应，第三个参数是这个样式文件中的值赋值给该参数对应的attr文件中的定义
    	//虽然可以实现效果但是为了布局文件中不提示找不到样式文件，还是布局文件中指定好了
		TypedArray a = context.obtainStyledAttributes(attrs, LL);
		// 作用 : 设置垂直布局时两个按钮之间的分隔条;
        setDividerDrawable(a.getDrawable(DividerLinearLayout.LL_DIVIDER)); 
        mDividerPadding = a.getDimensionPixelSize(LL_DIVIDER_PADDING, 0);
         /*设置LinearLayout标签的 android:showDividers 属性, 该属性有四个值 :
    	none :**不显示分隔线**;
    	beginning : 在LinearLayout**开始处显示分隔线**;
    	middle : 在LinearLayout中**每两个组件之间显示分隔线**;
    	end : 在LinearLayout**结尾处显示分隔线**;
    	设置**android:divider**属性, 这个**属性的值是一个Drawable的id**;*/
        mShowDividers = a.getInteger(LL_SHOW_DIVIDER, SHOW_DIVIDER_NONE);
        a.recycle();
        
        a= context.obtainStyledAttributes(attrs, new int[]{android.R.attr.orientation});
        int index = a.getInteger(0, -1);
        if (index >= 0) {
            setOrientation(index);
        }
        a.recycle();
            
        a= context.obtainStyledAttributes(attrs, new int[]{android.R.attr.gravity});    
        index = a.getInt(0, -1);
        if (index >= 0) {
            setGravity(index);
        }
        a.recycle();
        
        a= context.obtainStyledAttributes(attrs, new int[]{android.R.attr.baselineAligned});
        boolean baselineAligned = a.getBoolean(0, true);
        if (!baselineAligned) {
            setBaselineAligned(baselineAligned);
        }
        
        a.recycle();
    	
    }
    
    
    /**
     * 和父类方法一样
     */
    public void setDividerDrawable(Drawable divider) {
        if (divider == mDivider) {
            return;
        }
        mDivider = divider;
        if (divider != null) {
            mDividerWidth = divider.getIntrinsicWidth();
            mDividerHeight = divider.getIntrinsicHeight();
        } else {
            mDividerWidth = 0;
            mDividerHeight = 0;
        }
        setWillNotDraw(divider == null);
        requestLayout();
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        final int index = indexOfChild(child);
        final int orientation = getOrientation();
        final LayoutParams params = (LayoutParams) child.getLayoutParams();
        if (hasDividerBeforeChildAt(index)) {
            if (orientation == VERTICAL) {
                //Account for the divider by pushing everything up
                params.topMargin = mDividerHeight;
            } else {
                //Account for the divider by pushing everything left
                params.leftMargin = mDividerWidth;
            }
        }

        final int count = getChildCount();
        if (index == count - 1) {
            if (hasDividerBeforeChildAt(count)) {
                if (orientation == VERTICAL) {
                    params.bottomMargin = mDividerHeight;
                } else {
                    params.rightMargin = mDividerWidth;
                }
            }
        }
        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDivider != null) {
            if (getOrientation() == VERTICAL) {
                drawDividersVertical(canvas);
            } else {
                drawDividersHorizontal(canvas);
            }
        }
//        super.onDraw(canvas);
    }

    private void drawDividersVertical(Canvas canvas) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child != null && child.getVisibility() != GONE) {
                if (hasDividerBeforeChildAt(i)) {
                    final android.widget.LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) child.getLayoutParams();
                    final int top = child.getTop() - lp.topMargin/* - mDividerHeight*/;
                    drawHorizontalDivider(canvas, top);
                }
            }
        }

        if (hasDividerBeforeChildAt(count)) {
            final View child = getChildAt(count - 1);
            int bottom = 0;
            if (child == null) {
                bottom = getHeight() - getPaddingBottom() - mDividerHeight;
            } else {
                //final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                bottom = child.getBottom()/* + lp.bottomMargin*/;
            }
            drawHorizontalDivider(canvas, bottom);
        }
    }

    private void drawDividersHorizontal(Canvas canvas) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child != null && child.getVisibility() != GONE) {
                if (hasDividerBeforeChildAt(i)) {
                    final android.widget.LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) child.getLayoutParams();
                    final int left = child.getLeft() - lp.leftMargin/* - mDividerWidth*/;
                    drawVerticalDivider(canvas, left);
                }
            }
        }

        if (hasDividerBeforeChildAt(count)) {
            final View child = getChildAt(count - 1);
            int right = 0;
            if (child == null) {
                right = getWidth() - getPaddingRight() - mDividerWidth;
            } else {
                //final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                right = child.getRight()/* + lp.rightMargin*/;
            }
            drawVerticalDivider(canvas, right);
        }
    }

    private void drawHorizontalDivider(Canvas canvas, int top) {
        mDivider.setBounds(getPaddingLeft() + mDividerPadding, top,
                getWidth() - getPaddingRight() - mDividerPadding, top + mDividerHeight);
        mDivider.draw(canvas);
    }

    private void drawVerticalDivider(Canvas canvas, int left) {
        mDivider.setBounds(left, getPaddingTop() + mDividerPadding,
                left + mDividerWidth, getHeight() - getPaddingBottom() - mDividerPadding);
        mDivider.draw(canvas);
    }

    /**
     * 判断该子view之前是否有分割线
     * @param childIndex
     * @return
     */
    private boolean hasDividerBeforeChildAt(int childIndex) {
        if (childIndex == 0 || childIndex == getChildCount()) {
          return false;
        }
        if ((mShowDividers & SHOW_DIVIDER_MIDDLE) != 0) {
            boolean hasVisibleViewBefore = false;
            for (int i = childIndex - 1; i >= 0; i--) {
                if (getChildAt(i).getVisibility() != GONE) {
                    hasVisibleViewBefore = true;
                    break;
                }
            }
            return hasVisibleViewBefore;
        }
        return false;
    }
}
