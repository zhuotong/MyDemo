/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhuo.tong.view.pager_indicator;



import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhuo.tong.R;

/**
 * 这个控件实现动态动作条标签的行为，在不同的配置或环境变化。
 * 这个控件自身实现了ViewPager.OnPageChangeListener接口，
 * 所以设置page监听调用该控件的setOnPageChangeListener
 * 
 * 使用注意：
 * setViewPagerHaveTitle和setTabs是互斥的；setViewPagerHaveTitle即
 * 代表使用该控件自带的tabview显示tag，如果adapter中没有设置title就用默认的title（EMPTY_TITLE）。
 * 
 * setTabs是使用自己传入的view控件来做tag，并指定要不要和viewpager绑定（如果存在viewpager）
 * 
 * 遗留问题：如果设置的控件中有隐藏的不显示的控件，那么顺序有可能错乱
 */
public class TabPagerIndicator extends HorizontalScrollView implements IPagerIndicator {
    /** 当没有设置title时默认的title */
    private static final CharSequence EMPTY_TITLE = "";
    
    private boolean viewPagerHaveTitle;
    private boolean HaveUserTag;

    /**
     * 该接口实现的设置的是对当前条目（tab）重复点击时的监听
     * Interface for a callback when the selected tab has been reselected.
     */
    public interface OnTabReselectedListener {
        /**
         * Callback when the selected tab has been reselected.
         * 当前条目重复点击时执行
         * @param position Position of the current center item.
         */
        void onTabReselected(int position);
    }
    
    private OnTabReselectedListener mTabReselectedListener;
    private final OnClickListener mTabClickListener = new OnClickListener() {
        public void onClick(View view) {
        	if(viewPagerHaveTitle && !HaveUserTag){
	            TabView tabView = (TabView)view;
	            final int oldSelected = mViewPager.getCurrentItem();
	            final int newSelected = tabView.getIndex();
	            setCurrentItem(newSelected);
	            if (oldSelected == newSelected && mTabReselectedListener != null) {
	                mTabReselectedListener.onTabReselected(newSelected);
	            }
        	}else if(HaveUserTag){
        		setCurrentItem(view);
        		if (view == mTabLayout.getChildAt(mSelectedTabIndex) && mTabReselectedListener != null) {
	                mTabReselectedListener.onTabReselected(mSelectedTabIndex);
	            }
        	}
        }
    };
    /**
	 * 设置的是对当前条目（tab）重复点击时的监听
	 * @param listener
	 */
	public void setOnTabReselectedListener(OnTabReselectedListener listener) {
        mTabReselectedListener = listener;
    }

    private final DividerLinearLayout mTabLayout;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;

    private int mMaxTabWidth;
    private int mSelectedTabIndex;

    public TabPagerIndicator(Context context) {
        this(context, null);
    }

    public TabPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);

//        mTabLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.tabpager_dividerlinearlayout, this);
        mTabLayout = new DividerLinearLayout(context, attrs);
        addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
    }
    
    /**
     * 设置屏幕可见的的tag的数目(这是个参考值，如果可以的话，因为如果某个tag宽高大于控件宽高)，默认情况：2个的话平分，更多的话显示4个
     * @param num
     */
    public void setTabNumVisible(int num){
    	final int childCount = mTabLayout.getChildCount();
    	if(num < 0 || num > childCount){
    		
    	}
    }
    
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
      //本方法是使子View可以拉伸来填满整个屏幕（当模式是指定的具体单位或者match_parent）因为布局中是0dp，所以是true
        setFillViewport(lockedExpanded);

        final int childCount = mTabLayout.getChildCount();
        if (childCount > 1 && (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
            if (childCount > 2) {//最多显示4个
                mMaxTabWidth = (int)(MeasureSpec.getSize(widthMeasureSpec) * 0.4f);
            } else {
                mMaxTabWidth = MeasureSpec.getSize(widthMeasureSpec) / 2;
            }
        } else {
            mMaxTabWidth = -1;
        }

        final int oldWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int newWidth = getMeasuredWidth();

        if (lockedExpanded && oldWidth != newWidth) {
            // Recenter the tab display if we're at a new (scrollable) size.
            setCurrentItem(mSelectedTabIndex);
        }
    }
    
    /*******************************Tab移动和添加到窗口和从窗口移除************************************/
    private Runnable mTabSelector;

	private boolean UserTagbind;
    private void animateToTab(final int position) {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                /*scrollTo 直接定位到对应的位置 而smoothScrollTo是平滑滚动过去的
                并且 scrollTo在惯性滚动时不可以打断 而smoothScrollTo在惯性滚动时则可以打断
                如果想在惯性滚动时打断 并且直接定位无动画
                scrollView.scrollTo(0, 0);  
                scrollView.smoothScrollTo(0, 0);  
                顺序不能颠倒*/
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) {
            // Re-post the selector we saved
            post(mTabSelector);//使Runnable要添加到消息队列。运行将运行在用户界面线程。
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
    }
    /*******************************Tab移动和添加到窗口和从窗口移除************************************/

    private void addTab(int index, CharSequence text, int iconResId) {
        final TabView tabView = new TabView(getContext());
        tabView.mIndex = index;
        tabView.setFocusable(true);
        tabView.setOnClickListener(mTabClickListener);
        tabView.setText(text);

        if (iconResId != 0) {
            tabView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
        }

        mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0, MATCH_PARENT, 1));
    }

    /**以下是实现ViewPager.OnPageChangeListener接口的方法**/
    @Override
    public void onPageScrollStateChanged(int state) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        if (mListener != null) {
            mListener.onPageScrolled(arg0, arg1, arg2);
        }
    }

    @Override
    public void onPageSelected(int arg0) {
        setCurrentItem(arg0);
        if (mListener != null) {
            mListener.onPageSelected(arg0);
        }
    }
    
    
    
    /**以下是实现TabPagerIndicator接口的方法**/
    @Override
    public void setViewPagerHaveTitle(ViewPager view) {
       setViewPager(view, true);
    }
    @Override
    public void setViewPager(ViewPager view) {
    	setViewPager(view, false);
    }
    
    public void setViewPager(ViewPager view, boolean haveTitle) {
    	if (mViewPager == view && !HaveUserTag) {
    		return;
    	}
    	if (mViewPager != null) {
    		mViewPager.setOnPageChangeListener(null);
    	}
    	
    	mViewPager = view;
    	viewPagerHaveTitle = haveTitle;
    	HaveUserTag = !haveTitle;
    	if(viewPagerHaveTitle){
    		UserTagbind = false;
    	}
    	
    	if(view != null)
    		view.setOnPageChangeListener(this);
    	notifyDataSetChanged();
    }
    
    @Override
    public void notifyDataSetChanged() {
        if(HaveUserTag){
        	final int count = mTabLayout.getChildCount();
        	if (mSelectedTabIndex > count) {
	            mSelectedTabIndex = count - 1;
	        }
	        setCurrentItem(mSelectedTabIndex);
        }else{
        	mTabLayout.removeAllViews();
        	if(mViewPager != null && viewPagerHaveTitle){
        		PagerAdapter adapter = mViewPager.getAdapter();
        		if(adapter != null){
        			IIconPagerAdapter iconAdapter = null;
        			if (adapter instanceof IIconPagerAdapter) {
        				iconAdapter = (IIconPagerAdapter)adapter;
        			}
        			final int count = adapter.getCount();
        			for (int i = 0; i < count; i++) {
        				CharSequence title = adapter.getPageTitle(i);
        				if (title == null) {
        					title = EMPTY_TITLE;
        				}
        				int iconResId = 0;
        				if (iconAdapter != null) {
        					iconResId = iconAdapter.getIconResId(i);
        				}
        				addTab(i, title, iconResId);
        			}
        			if (mSelectedTabIndex > count) {
        				mSelectedTabIndex = count - 1;
        			}
        			setCurrentItem(mSelectedTabIndex);
        		}
        	}
        }
        requestLayout();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }
    /** 
     * 设置Viewpager，adapter中设置了title作为tag的内容，并且移动到指定的位置
     */
    @Override
    public void setViewPagerHaveTitle(ViewPager view, int initialPosition) {
    	setViewPagerHaveTitle(view);
    	setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item) {
        final int tabCount = mTabLayout.getChildCount();
        if(item > tabCount || item < 0)
        	return;
        
        if(mViewPager != null && UserTagbind)
        	mViewPager.setCurrentItem(item);
        mSelectedTabIndex = item;
        
        if((mViewPager!=null && viewPagerHaveTitle) || (HaveUserTag && UserTagbind)){
	        for (int i = 0; i < tabCount; i++) {
	            final View child = mTabLayout.getChildAt(i);
	            final boolean isSelected = (i == item);
	            child.setSelected(isSelected);
	            if (isSelected) {
	                animateToTab(item);
	            }
	        }
        }
    }
    
    public void setCurrentItem(View currrentTagView) {
    	if(currrentTagView == null || currrentTagView.getVisibility()!=View.VISIBLE)return;
        final int tabCount = mTabLayout.getChildCount();
        
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);
            if(currrentTagView == child){
            	mSelectedTabIndex = i;
            	if(mViewPager != null && UserTagbind)
            		mViewPager.setCurrentItem(i);
	            child.setSelected(true);
                animateToTab(i);
            }else{
            	child.setSelected(false);
            }
        }
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mListener = listener;
    }
    
    /**
     * 设置tag，如果之前存在tag就移除以前的，以及是否和viewpager绑定，如果设置绑定并且viewpager不为空返回true绑定成功
     * 绑定成功的话viewpager就和tag绑定了。如果传入空view就会解除绑定。
     * @param views
     * @param bindViewPager
     * @return
     */
    public boolean setTabs(List<View> views, boolean bindViewPager){
    	UserTagbind = false;
    	if(views == null)return UserTagbind;
    	mTabLayout.removeAllViews();
    	for (View view : views) {
    		view.setOnClickListener(mTabClickListener);
			mTabLayout.addView(view, new LinearLayout.LayoutParams(0, MATCH_PARENT, 1));
		}
    	
    	HaveUserTag = true;
    	if(mViewPager != null){
	    	if(bindViewPager){
	    		mViewPager.setOnPageChangeListener(this);
	    		UserTagbind = true;
	    	}else{
	    		mViewPager.setOnPageChangeListener(null);
	    	}
    	}
    	notifyDataSetChanged();
    	return UserTagbind;
    }
    
    public void setUserTagbind(boolean userTagbind){
    	if(mViewPager != null && HaveUserTag && userTagbind){
    		UserTagbind = true;
    		mViewPager.setOnPageChangeListener(this);
    	}else {
    		UserTagbind = false;
    		mViewPager.setOnPageChangeListener(null);
    	}
    }
    
    public boolean getUserTagbind(){
    	return UserTagbind;
    }

    private class TabView extends TextView {
        private int mIndex;

        public TabView(Context context) {
            super(context, null, R.attr.PagerIndicatorStyle);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            // Re-measure if we went beyond our maximum size.
            if (mMaxTabWidth > 0 && getMeasuredWidth() > mMaxTabWidth) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(mMaxTabWidth, MeasureSpec.EXACTLY),
                        heightMeasureSpec);
            }
        }

        public int getIndex() {
            return mIndex;
        }
    }
}
