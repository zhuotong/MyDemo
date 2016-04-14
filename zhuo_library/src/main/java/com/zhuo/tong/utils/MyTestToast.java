package com.zhuo.tong.utils;

import com.zhuo.tong.constant.DevelopState;

import android.content.Context;
import android.widget.Toast;

/**
 * 方便于开发的Toast，仅为了测试方便，在测试期间方便的看到错误
 * 控制开关是currentStage的值
 */
public class MyTestToast {

	/**
	 * 为了开发调试用的土司，目地在于方便控制
	 * 
	 */
	public static void toast(Context context, String msg) {
		switch (DevelopState.currentStage) {
		case DevelopState.DEVELOP:
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			break;
		case DevelopState.DEBUG:
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			break;
		case DevelopState.BATE:
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			break;
		case DevelopState.RELEASE:
			// 一般不做处理
			break;
		}
	}
}