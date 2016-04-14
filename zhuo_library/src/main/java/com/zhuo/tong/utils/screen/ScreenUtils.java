package com.zhuo.tong.utils.screen;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * 获得屏幕相关的辅助类
 * 
 * 
 * 
 */

public class ScreenUtils {
	private ScreenUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 获取屏幕宽高
	 * 
	 * @param context
	 * @return 数组，第一是宽，第二是高
	 */
	public static int[] getScreenWidthAndHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		// int w_screen = dm.widthPixels;
		// int h_screen = dm.heightPixels;
		// Log.i(tag, "屏幕尺寸2：宽度 = " + w_screen + "高度 = " + h_screen + "密度 = " +
		// dm.densityDpi);
		return new int[] { dm.widthPixels, dm.heightPixels };
	}

	/**
	 * 获得屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
		/*
		 * float density = outMetrics.density; // 屏幕密度（0.75 / 1.0 / 1.5） int
		 * densityDpi = outMetrics.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
		 * 在一个低密度的小屏手机上
		 * ，仅靠上面的代码是不能获取正确的尺寸的。比如说，一部240x320像素的低密度手机，如果运行上述代码，获取到的屏幕尺寸是320x427
		 * 。因此
		 * ，研究之后发现，若没有设定多分辨率支持的话，Android系统会将240x320的低密度（120）尺寸转换为中等密度（160）对应的尺寸
		 * ，这样的话就大大影响了程序的编码
		 * 。所以，需要在工程的AndroidManifest.xml文件中，加入supports-screens节点，具体的内容如下：
		 * <supports-screens android:smallScreens="true"
		 * android:normalScreens="true" android:largeScreens="true"
		 * android:resizeable="true" android:anyDensity="true" />
		 * 
		 * 这样的话，当前的Android程序就支持了多种分辨率，那么就可以得到正确的物理尺寸了。
		 */
		/*
		 * WindowManager wm =
		 * (WindowManager)getSystemService(Context.WINDOW_SERVICE); Display
		 * display = wm.getDefaultDisplay(); Log.i(tag,
		 * "屏幕尺寸1: 宽度 = "+display.getWidth()+"高度 = :"+display.getHeight() );
		 */
	}

	/**
	 * 获得屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	/**
	 * 获得状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context) {

		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}

	/**
	 * 获得状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Activity activity)  
    {  
    	Rect outRect = new Rect();
    	activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
    	return outRect.top;
    }
	
	/**
	 * 获取标题栏高度
	 * @param activity
	 * @return
	 */
	public static int getTitleBarHeight(Activity activity){
		View view = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
		//这个方法获取到的view就是程序不包括标题栏的部分，然后就可以知道标题栏的高度了。
		return view.getTop()-getStatusHeight(activity);
	}
	
	/**
	 * 获取当前屏幕截图，包含状态栏
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int[] temp = getScreenWidthAndHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, temp[0], temp[1]);
		view.destroyDrawingCache();
		return bp;

	}
	
	/**
	 * 获取当前屏幕截图，并保存到指定路径
	 * 是百分百的png图片
	 * @param activity
	 * @param file 要保存的截图路径
	 * @param hasStatusBar true：包含状态栏；false：不包含状态栏
	 * @return 返回已经保存的截图路径,为null则失败
	 */
	public static File saveSnapShot(Activity activity,File file,boolean hasStatusBar) {
		
		Bitmap bp = null;
		if(hasStatusBar){
			bp = snapShotWithStatusBar(activity);
		}else{
			bp = snapShotWithoutStatusBar(activity);
		}
		if(bp != null){
			OutputStream out = null;
			try {
				out = new BufferedOutputStream(new FileOutputStream(file));
				return bp.compress(Bitmap.CompressFormat.PNG, 100, out )?file:null;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}finally{
				bp.recycle();
				if(out != null){
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		
		return null;
	}

	/**
	 * 获取当前屏幕截图，不包含状态栏
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithoutStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		view.getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		int[] temp = getScreenWidthAndHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, temp[0], temp[1]
				- statusBarHeight);
		view.destroyDrawingCache();
		return bp;

	}

}