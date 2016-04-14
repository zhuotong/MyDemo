package com.zhuo.tong.view.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 一个继承自V4的FragmentPagerAdapter，进行了简单的封装。
 * 
 * @author dong
 *
 * @param <T>
 */
public class MyFragmentViewPagerAdapter extends FragmentPagerAdapter{

	private List<Fragment> fragments;
	
	public MyFragmentViewPagerAdapter(FragmentManager fm,List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	
	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}
	
}
