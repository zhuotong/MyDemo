package com.zhuo.tong.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;



/**
 * 这是继承自懒加载的viewpager的viewpager；
 * 保留懒加载的功能并主要是在分发触摸事件的事件分析是否是移动事件并请求父控件不接手事件；
 * 所以此viewpager可以实现在父viewpager中智能滑动，即在第一页时，向右滑时交给父控件处理，向左滑进入第二页；
 * 在最后一页时，向左滑交给父控件处理，向右滑进入上一页；
 * @author dong
 *
 */
public class MyChildLazyViewPager extends LazyViewPager{
	private static final String TAG = MyChildLazyViewPager.class.getName();
	private float startX;
	private float startY;
	private boolean DEBUG;
	
	public MyChildLazyViewPager(Context context) {
		super(context);
	}

	public MyChildLazyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = event.getAction();
	   	switch (action)
	   	{
	   	case MotionEvent.ACTION_DOWN:
	   		startX = event.getX();
	   		startY = event.getY();
	   		if(DEBUG)Log.e(TAG, "当前处于第"+getCurrentItem()+"页--按下，startX="+startX+";startY="+startY);
	   		getParent().requestDisallowInterceptTouchEvent(true);
	   		break;
	   		//滑动，在此对里层viewpager的第一页和最后一页滑动做处理
	   	case MotionEvent.ACTION_MOVE:
	   		if(DEBUG)Log.e(TAG, "当前处于第"+getCurrentItem()+"页--移动，getX="+event.getX()+";getY="+event.getY());
	   		if (startX == event.getX())
	   		{
	   			if (0 == this.getCurrentItem()
	   					|| this.getCurrentItem() == this
	   					.getAdapter().getCount() - 1)
	   			{
	   				getParent().requestDisallowInterceptTouchEvent(false);
	   			}
	   		}
	   		//里层viewpager已经是最后一页，此时继续向左滑（手指从右往左滑）
	   		else if ((startX - event.getX())>20)//(startX > event.getX())
	   		{
	   			if (this.getCurrentItem() == this
	   					.getAdapter().getCount() - 1)    				
	   			{
	   				if(DEBUG)Log.e(TAG, "当前处于第"+getCurrentItem()+"页（最后一页）--向左移动，getX="+event.getX()+";getY="+event.getY());
	   				getParent().requestDisallowInterceptTouchEvent(false);
	   			}
	   		}
	   		//里层viewpager已经是第一页，此时继续向右滑（手指从左往右滑）
	   		else if ((event.getX()-startX)>20)//(startX < event.getX())
	   		{
	   			if (this.getCurrentItem() == 0)
	   			{
	   				if(DEBUG)Log.e(TAG, "当前处于第"+getCurrentItem()+"页（第一页）--向右移动，getX="+event.getX()+";getY="+event.getY());
	   				getParent().requestDisallowInterceptTouchEvent(false);
	   			}
	   		} else
	   		{
	   			if(DEBUG)Log.e(TAG, "当前处于第"+getCurrentItem()+"页--移动，getX="+event.getX()+";getY="+event.getY());
	   			getParent().requestDisallowInterceptTouchEvent(true);
	   		}
	   		break;
	   	case MotionEvent.ACTION_UP://抬起
	   		if(DEBUG)Log.e(TAG, "当前处于第"+getCurrentItem()+"页--抬起，getX="+event.getX()+";getY="+event.getY());
	   		getParent().requestDisallowInterceptTouchEvent(false);
	   		break;
	   	case MotionEvent.ACTION_CANCEL:
	   		if(DEBUG)Log.e(TAG, "当前处于第"+getCurrentItem()+"页--取消，getX="+event.getX()+";getY="+event.getY());
	   		getParent().requestDisallowInterceptTouchEvent(false);
	   		break;
	   	}
		return super.dispatchTouchEvent(event);
	}
	
	
	
}
