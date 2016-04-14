package com.zhuo.tong;

import java.util.ArrayList;
import java.util.List;

import com.zhuo.tong.view.pager_indicator.TabPagerIndicator;



import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class ZhuoTongLibraryMainActivity extends Activity {
	String str[] = {"niid","jndfh","jhdfur","jijri","jhrfu","ihrf"}; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		com.zhuo.tong.view.pager_indicator.TabPagerIndicator tab = (com.zhuo.tong.view.pager_indicator.TabPagerIndicator) findViewById(R.id.indicator);
		
		List<View> views = new ArrayList<View>();
		TextView tv;
		for(int i=0;i<10;i++){
			tv = new TextView(this,null,R.attr.PagerIndicatorStyle);
			tv.setText(i+"hhdj");
			views.add(tv);
		}
//		tab.setTabs(views , false);
		ViewPager vp = (ViewPager) findViewById(R.id.pager); 
		vp.setAdapter(new PagerAdapter() {
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return str.length;
			}
			@Override
			public CharSequence getPageTitle(int position) {
				// TODO Auto-generated method stub
				return str[position];
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				TextView textView = new TextView(getApplicationContext());
				textView.setText(str[position]);
				textView.setLayoutParams(new LayoutParams(200, 100));
				container.addView(textView);
				return textView;
			}
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}
			
			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				// TODO Auto-generated method stub
//				super.destroyItem(container, position, object);
				container.removeView((View) object);
			}
		});
//		vp.setCurrentItem(0);
		tab.setViewPagerHaveTitle(vp);
		
		tab.setTabs(views , false);
		tab.setUserTagbind(true);
//		tab.setViewPagerHaveTitle(vp);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
