package com.zhuo.tong.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;


/**
 * 包的管理类
 * @author dong
 *
 */
public class MyPackageManager {
	
	/**
	 * 获取并返回应用的版本信息
	 * @param context
	 * @return 应用的版本信息
	 */
	public static String getVersionName(Context context){
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
}
