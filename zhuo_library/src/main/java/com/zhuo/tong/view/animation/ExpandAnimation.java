package com.zhuo.tong.view.animation;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

/**
 * 折叠动画
 * 传入的view必须是在LinearLayout中，X即其父布局必须是LinearLayout，不然参数不对
 * 已经修改了根据传入的View获取其所属的布局，目前支持LinearLayout和RelativeLayout
 * 不过RelativeLayout好像不明显，如果需要自动关闭上一个处于打开状态的view请设置
 * setAutoCloseLast(boolean sure)并setLastViewCloseListener(LastViewCloseListener listener)
 * 如果项目中多次使用该动画且有时不需要自动关闭上一个处于打开状态的view时请最好每次使用前
 * 设置一下setAutoCloseLast(boolean sure)；
 * 
 * 如果需要对上一个处于打开状态的view做一些改变请调用setLastViewCloseListener设置LastViewCloseListener
 * 并调用doSomething(View lastview)方法；
 * 
 * （setLastViewCloseListener(LastViewCloseListener listener)设置一次即可，如果处理doSomething(View lastview)都一样的话）
 * @author dong
 *
 */
public class ExpandAnimation extends Animation {
	
    private View mAnimatedView;
    private LayoutParams mViewLayoutParams;
    private android.widget.RelativeLayout.LayoutParams mRelaViewLayoutParams;
    private boolean isLinear = true;
    private int mMarginStart, mMarginEnd;
    private boolean mIsVisibleAfter = false;
    private boolean mWasEndedAlready = false;
    public static View lastview;
    public static boolean autoCloseLast = false;
    public static LastViewCloseListener lis;
    
    public static void setAutoCloseLast(boolean sure){
    	autoCloseLast = sure;
    }
    
    public static interface LastViewCloseListener{
    	void doSomething(View lastview);
    }
    
    public static void setLastViewCloseListener(LastViewCloseListener listener){
    	lis=listener;
    }

    /**
     * Initialize the animation
     * @param view The layout we want to animate
     * @param duration The duration of the animation, in ms
     */
    public ExpandAnimation(View view, int duration) {

        setDuration(duration);
        mAnimatedView = view;
        
        if(view.getParent() instanceof LinearLayout){
        	isLinear = true;
        	 mViewLayoutParams = (LayoutParams) view.getLayoutParams();
        } else if(view.getParent() instanceof RelativeLayout){
        	isLinear = false;
        	 mRelaViewLayoutParams = (android.widget.RelativeLayout.LayoutParams) view.getLayoutParams();
        }
       
        // decide to show or hide the view
        mIsVisibleAfter = (view.getVisibility() == View.VISIBLE);

        mMarginStart = isLinear?mViewLayoutParams.bottomMargin:mRelaViewLayoutParams.bottomMargin;
        mMarginEnd = (mMarginStart == 0 ? (0- view.getHeight()) : 0);

        view.setVisibility(View.VISIBLE);
        if(autoCloseLast){
	        if(autoCloseLast&&lastview!=null&&lastview!=view&&lastview.getVisibility()==View.VISIBLE){
	        	Log.e("zhuo", "关闭上一次未关闭的");
	        	if(lis!=null){
	        		lis.doSomething(lastview);
	        	}
	        	lastview.startAnimation(new ExpandAnimation(lastview, duration));
	        }
        
	        lastview=view;
        }
        
    }
    
   
    
    /**
     * show current animation status
     * @return
     */
    public boolean toggle() {
    	return !mIsVisibleAfter;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);

        if (interpolatedTime < 1.0f) {

            // Calculating the new bottom margin, and setting it
        	
        	if(isLinear){
        		mViewLayoutParams.bottomMargin = mMarginStart
                        + (int) ((mMarginEnd - mMarginStart) * interpolatedTime);
        	}else{
        		mRelaViewLayoutParams.bottomMargin = mMarginStart
                        + (int) ((mMarginEnd - mMarginStart) * interpolatedTime);
        	}
        	
        	

            // Invalidating the layout, making us seeing the changes we made
            mAnimatedView.requestLayout();

        // Making sure we didn't run the ending before (it happens!)
        } else if (!mWasEndedAlready) {
        	
        	if(isLinear){
        		mViewLayoutParams.bottomMargin = mMarginEnd;
        	}else{
        		mRelaViewLayoutParams.bottomMargin = mMarginEnd;
        	}
        	
//            mViewLayoutParams.bottomMargin = mMarginEnd;
            mAnimatedView.requestLayout();

            if (mIsVisibleAfter) {
                mAnimatedView.setVisibility(View.GONE);
            }
            mWasEndedAlready = true;
        }
    }
}