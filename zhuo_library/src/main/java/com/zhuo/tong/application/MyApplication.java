package com.zhuo.tong.application;

import java.io.File;

import com.zhuo.tong.constant.DevelopState;
import com.zhuo.tong.utils.SDCardUtils;

import android.app.Application;
import android.os.Build;

/**
 * 自定义的MyApplication，实现一些基本功能，比如存储一个生命周期长的context
 * @author dong
 *
 */
public class MyApplication extends Application {
	public static String packageName;
	public static File myDataFilesDir;
	public static File mySDCacheFilesDir;
	public static int SDK_NUM = -1;
	
	@Override
	public void onCreate() {
		super.onCreate();
		SDK_NUM = Build.VERSION.SDK_INT;
		packageName = getPackageName();
		switch (DevelopState.currentStage) {
		case DevelopState.DEBUG:
			myDataFilesDir = getFilesDir();
			break;
		case DevelopState.BATE:
			mySDCacheFilesDir = SDCardUtils.getExternalCacheDir(this);
			break;
		}
		
	}
	
}
