package com.zhuo.tong.utils.memory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Build;
import android.os.Debug;

/**
 * 手机内存相关的类
 * @author dong
 *
 */
public class MyMemoryManager {

	/**
	 * 最好是app得到的context，生命周期长
	 */
//	private Context context;
	private ActivityManager activityManager;
	private ActivityManager.MemoryInfo info;
	private static MyMemoryManager mem;
	public final static long size = 20*1024*1024;

	private MyMemoryManager(Context context) {
		super();
//		this.context = context;
		activityManager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
		info = new ActivityManager.MemoryInfo();
	}
	
	/**
	 * 获取实例,如果第一次传入context为null时返回null；
	 * 第一次设置后，如果mem存在直接返回
	 * @param context
	 * @return
	 */
	public static MyMemoryManager getInstance(Context context) {
		if (mem == null) {
			if (context == null)
				return null;
			else {
				synchronized (MyMemoryManager.class) {
					if (mem == null && context != null)
						mem = new MyMemoryManager(context);
				}

			}
		}
		return mem;
	}
	
	/**
	 * 获取正在运行的进程的数量
	 * @param context 上下文
	 * @return
	 */
	public int getRunningProcessCount(){
		//PackageManager //包管理器 相当于程序管理器。静态的内容。
		//ActivityManager  进程管理器。管理的手机的活动信息。动态的内容。
		List<RunningAppProcessInfo> infos = activityManager.getRunningAppProcesses();
		return infos.size();
	}
	
	/**
	 * 获取当前系统分配jvm虚拟机的堆内存和最大的堆内存（第二个为-1时说明是低版本不支持，而且这个大内存需要在清单文件中申请）；
	 * @return 返回长度为2的数组
	 */
	@SuppressLint("NewApi")
	public long[] getClassDefaultMemoryAndLargeMemory(){
		long large = -1;
		if(android.os.Build.VERSION.SDK_INT>10){
			large = activityManager.getLargeMemoryClass();
		}
		
		return new long[]{activityManager.getMemoryClass(),large};
	}
	
	/**
	 * 获取当前系统总的剩余可用内存;
	 */
	public long getAvailMem(){
		activityManager.getMemoryInfo(info); 
	    return info.availMem;
	}
	
	/**
	 * 获取手机可用的总内存
	 * @param context 上下文
	 * @return long byte 返回-1代表异常了
	 */
	@SuppressLint("NewApi")
	public long getTotalMem(){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
			activityManager.getMemoryInfo(info);
			return info.totalMem;
		}
		
		BufferedReader br=null;
		try {
			File file = new File("/proc/meminfo");
			FileInputStream fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis));
			String line = br.readLine();
			//MemTotal:         513000 kB
			StringBuilder sb = new StringBuilder();
			for(char c: line.toCharArray()){
				if(c>='0'&&c<='9'){
					sb.append(c);
				}
			}
			return Long.parseLong(sb.toString())*1024;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}finally{
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 判断当前是否处于低内存（如果自己没有定义低内存的阀值则使用系统默认的阀值）
	 * 注意，这个仅是判断整个系统是否低内存而不是判断该应用是否内存不足
	 * 
	 * @return false：内存足够；true：内存不足；
	 */
	public boolean isLowMem(){
		 if(getAvailMem()<info.threshold||getAvailMem()<size){
			 return true;
		 }
		return info.lowMemory;
	}
	
	/**
	 * 获取本地堆内存
	 * @return	1:该应用现有本地堆内存（应该理解成总的）；2:该应用本地分配的堆内存（应该理解成正在使用的）；3:该应用空闲的堆内存
	 */
	public long[] get(){
        return new long[]{Debug.getNativeHeapSize(),Debug.getNativeHeapAllocatedSize(),Debug.getNativeHeapFreeSize()};
	}
	

}
