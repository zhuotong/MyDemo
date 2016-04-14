package com.zhuo.tong.view.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 一个继承自V4的PagerAdapter，进行了简单的封装。
 * 提供了2种设置view的方法：
 * 1、直接设置view，常用方式
 * 2、为了封装，可以设置实现com.zhuo.tong.view.adapter.MyViewPagerAdapter.HasView接口的类
 * 
 * @author dong
 *
 * @param <T>
 */
public abstract class MyViewPagerAdapter extends PagerAdapter{
	private List<com.zhuo.tong.view.adapter.MyViewPagerAdapter.HasView> list;
	private List<View> views;
	private View view;
	
	public MyViewPagerAdapter(List<View> views) {
		super();
		this.views = views;
	}

	public MyViewPagerAdapter(List<com.zhuo.tong.view.adapter.MyViewPagerAdapter.HasView> list,int donothing) {
		super();
		this.list = list;		
	}

	@Override
	public int getCount() {
		return list==null?views.size():list.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		
		return view==object;
		
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if(list!=null&&list.size()>0){
			view = list.get(position).getView();
		}else if(views!=null&&views.size()>0){
			view = views.get(position);
		}
		bindView(container, view, position);
		container.addView(view);
		return view;
	}

	/**
	 * 在运行时动态设置数据
	 * @param container
	 * @param view2
	 * @param position
	 */
	public abstract void bindView(ViewGroup container, View view2, int position);

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if(list!=null&&list.size()>0){
			view = list.get(position).getView();
		}else if(views!=null&&views.size()>0){
			view = views.get(position);
		}
		container.removeView(view);
		view = null;
		
	}
	
	
	public interface HasView{
		View getView();
	}
	
}
