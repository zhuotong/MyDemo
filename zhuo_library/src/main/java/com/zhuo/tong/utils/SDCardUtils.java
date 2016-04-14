package com.zhuo.tong.utils;

import java.io.File;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

/**
 * SD卡相关的辅助类
 * 
 * 
 * 
 */


public class SDCardUtils {
	private static final String TAG = "SDCardUtils";
	
	private SDCardUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}
	
	/**
	 * 用于把字节转换成其他单位，具体是哪个单位是系统自动判断的
	 * @param context
	 * @param size
	 * @return
	 */
	public static String formatSize(Context context,long size){
		return Formatter.formatFileSize(context, size);
	}
	
	
	/**
	 * 在sd卡创建目录和文件并返回,是创建在sd卡目录下的，不要把sd卡路径加上
	 * @param dirs 多级目录，请按照先后顺序填写，比如sdcard/zhuo/tong->new String[]{"zhuo","tong"}
	 * @param name 要创建的文件的名字，注意是否需要文件名后缀，比如：1.txt,1.exe
	 * @return
	 */
	public static File mkFile(String[] dirs,String name){
		String dir="";
		for (String string : dirs) {
			dir+=string+File.separator;
		}
		File file = new File(sdpath+dir);
		if(!file.exists()){
			file.mkdirs();
		}
		
		return new File(file,name);
	}
	/**
	 * 在sd卡创建目录并返回，是创建在sd卡目录下的，不要把sd卡路径加上
	 * @param dirs 多级目录，请按照先后顺序填写，比如sdcard/zhuo/tong->new String[]{"zhuo","tong"}
	 * 
	 * @return
	 */
	public static File mkDir(String[] dirs){
		String dir="";
		for (String string : dirs) {
			dir+=string+File.separator;
		}
		File file = new File(sdpath+dir);
		if(!file.exists()){
			file.mkdirs();
		}
		
		return file;
	}

	/**
	 * 判断SDCard是否可用
	 * 
	 * @return
	 */
	public static boolean isSDCardEnable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);

	}

	/**
	 * SD卡路径,包含/	
	 * 如果sd卡不存在没有。所以谨慎起见再加一个get方法。
	 */
	public static String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator;
	
	/**
	 * 获取sd卡路径
	 * @return
	 */
	public static String getSDPATH(){
		sdpath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator;
		return sdpath;
	}
	
	/**
	 * data路径,包含/
	 * 这个应该都能获取到
	 */
	public static String datapath = Environment.getDataDirectory().getAbsolutePath()
			+ File.separator;
	

	/**
	 * 获取SD卡的剩余可用容量 单位byte
	 * 
	 * @return -1时没有sd卡
	 */
	public static long getSDCardAllFreeSize() {
		if (isSDCardEnable()) {
			StatFs stat = new StatFs(sdpath);
			// 获取空闲的数据块的数量
			// long availableBlocks = stat.getAvailableBlocks();
			// 获取单个数据块的大小（byte）

			// long blockSize = stat.getBlockSize();

			return stat.getBlockSize() * stat.getAvailableBlocks();
		}

		return -1;
	}
	/**
	 * 获取SD卡的总容量 单位byte
	 * 
	 * @return -1时没有sd卡
	 */
	public static long getSDCardAllSize() {
		if (isSDCardEnable()) {
			StatFs stat = new StatFs(sdpath);
			return stat.getBlockSize() * stat.getBlockCount();
		}		
		return -1;
	}
	/**
	 * 获取Data分区的总容量 单位byte，即可以安装应用的分区的大小
	 * 
	 * @return 
	 */
	public static long getDataAllSize() {
		StatFs stat = new StatFs(datapath);
		return stat.getBlockSize() * stat.getBlockCount();
	}
	/**
	 * 获取Data分区的总的可用容量 单位byte，即可以安装应用的分区的剩余可用大小，安装应用时最好检测一下
	 * 
	 * @return 
	 */
	public static long getDataAllFreeSize() {
		StatFs stat = new StatFs(datapath);
		return stat.getBlockSize() * stat.getAvailableBlocks();
	}

	/**
	 * 获取指定路径所在空间的剩余可用容量字节数，单位byte
	 * 
	 * @param filePath
	 * @return 容量字节;-1时路径有错误
	 */
	public static long getFreeBytes(String filePath) {
		if(TextUtils.isEmpty(filePath) || !new File(filePath).exists() || new File(filePath).isFile()){
			Log.i(TAG, "can't get the FreeBytes,bad path");
			return -1;
		}
		// 如果是sd卡的下的路径，则获取sd卡可用容量
		if (filePath.startsWith(sdpath)) {
			filePath = sdpath;
		} else if(filePath.startsWith(datapath)){// 如果是内部存储的路径，则获取内存存储的可用容量
			filePath = datapath;
		}
		StatFs stat = new StatFs(filePath);
		
		return stat.getBlockSize() * stat.getAvailableBlocks();
	}

	/**
	 * 获取系统存储路径
	 * 
	 * @return
	 */
	public static String getRootDirectoryPath() {
		return Environment.getRootDirectory().getAbsolutePath();
	}
	
	/**
	 * 获取系统的为应用设置的sd卡的缓存目录，如果系统版本支持直接调用系统api，不支持自己创建目录
	 * 自定义的为：cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
	 * @param context
	 * @return
	 */
	public static File getExternalCacheDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {//安卓2.2起就可以获取到
            return context.getExternalCacheDir();
        }
        // Before Froyo we need to construct the external cache dir ourselves
        return mkDir(new String[]{"Android","data",context.getPackageName(),"cache"});
    }

}
