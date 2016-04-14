package com.zhuo.tong.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;



/**
 * 这是继承自懒加载的viewpager的viewpager；
 * 保留懒加载的功能并主要是控制是否可以滑动；
 * @author dong
 *
 */
public class MyNoSlideLazyViewPager extends LazyViewPager{
	private static final String TAG = MyNoSlideLazyViewPager.class.getName();
	
	private boolean Slide;
	
	public MyNoSlideLazyViewPager(Context context) {
		super(context);
	}

	public MyNoSlideLazyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(!Slide)
			return false;
		return super.onTouchEvent(ev);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(!Slide)
			return false;
		return super.onInterceptTouchEvent(ev);
	}

	public boolean isSlide() {
		return Slide;
	}

	public void setSlide(boolean slide) {
		Slide = slide;
	}
	
	
	
}
