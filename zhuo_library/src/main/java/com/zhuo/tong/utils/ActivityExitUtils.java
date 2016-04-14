package com.zhuo.tong.utils;

import android.app.Activity;
import android.os.SystemClock;
import android.widget.Toast;

public class ActivityExitUtils {
	/**
	 * 300毫秒内双击就退出activity
	 */
	public static final int TIME = 300;
	private static long[] mHits = new long[2];
	/**
	 * 300毫秒内双击就退出activity
	 * @param activity
	 */
	public static void doubleBack(Activity activity){
		System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
		mHits[mHits.length - 1] = SystemClock.uptimeMillis();
		if (mHits[0] >= (SystemClock.uptimeMillis() - TIME)) {
			activity.finish();
		}else{
			Toast.makeText(activity, "在按一次退出",
					Toast.LENGTH_SHORT).show();
		}
	}
}
