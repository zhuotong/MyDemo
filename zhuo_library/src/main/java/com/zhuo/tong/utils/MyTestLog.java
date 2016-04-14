package com.zhuo.tong.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.TextUtils;
import android.util.Log;

import com.zhuo.tong.application.MyApplication;
import com.zhuo.tong.constant.DevelopState;

/**
 * 方便于开发的log，仅为了测试方便，在测试期间方便的看到错误 控制开关是currentStage的值
 * 为了获取应用的log目录又不想保留一个context的引用，也不想每次都传入context
 * 所以获取自定义的MyApplication中的包名，路径等，或者定义几个方法可以在使用前 设置一些信息
 * 扩展：最后的方案是从配置文件中动态获取定义的值，比如开发状态，包名，路径等
 */

public class MyTestLog {
	private static final String TAG = "MyTestLog";
	private static File file;
	private static OutputStream outputStream;
	private static final String pattern = "yyyy-MM-dd HH:mm:ss";
	private static final String Hpattern = "HH:mm:ss";
	private static final String Ypattern = "yy_MM_dd";// 日志文件名使用的时间
	private static final SimpleDateFormat DateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
	private static final SimpleDateFormat HDateFormat = new SimpleDateFormat(Hpattern, Locale.getDefault());
	private static final SimpleDateFormat YDateFormat = new SimpleDateFormat(Ypattern, Locale.getDefault());
	private static String grade;
	private static String dataLogFilePath;
	private static String SDLogFilePath;

	public static String getDataLogFilePath() {
		return dataLogFilePath;
	}

	public static void setDataLogFilePath(String dataLogFilePath) {
		MyTestLog.dataLogFilePath = dataLogFilePath;
	}

	public static String getSDLogFilePath() {
		return SDLogFilePath;
	}

	public static void setSDLogFilePath(String sDLogFilePath) {
		SDLogFilePath = sDLogFilePath;
	}

//	public static void info(String msg) {
//		info(TAG, msg);
//	}

	public static void info(String msg){
		info(getTag(), msg);
	}

	/**
	 * 用于输出info级别的日志
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void info(String tag, String msg) {
		grade = "info";
		switch (DevelopState.currentStage) {
		case DevelopState.DEVELOP:
			// 控制台输出
//			Log.i("zhuotong", tag + ":" + msg);//方便过滤
			Log.i(tag ,msg);
			break;
		case DevelopState.DEBUG:
			// 在应用下面创建目录存放日志
		case DevelopState.BATE:
			// 写日志到sdcard
			saveLog(tag, msg);
			break;
		case DevelopState.RELEASE:
			// 一般不做日志记录
			break;
		}
	}

	public static void debug(String msg){
		debug(getTag(), msg);
	}

	/**
	 * 用于输出debug级别的日志
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void debug(String tag, String msg) {
		grade = "debug";
		switch (DevelopState.currentStage) {
		case DevelopState.DEVELOP:
			// 控制台输出
			Log.d(tag ,msg);
			break;
		case DevelopState.DEBUG:
			// 在应用下面创建目录存放日志
		case DevelopState.BATE:
			// 写日志到sdcard
			saveLog(tag, msg);
			break;
		case DevelopState.RELEASE:
			// 一般不做日志记录
			break;
		}
	}

	public static void warn(String msg){
		warn(getTag(), msg);
	}

	/**
	 * 用于输出warn级别的日志
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void warn(String tag, String msg) {
		grade = "warn";
		switch (DevelopState.currentStage) {
		case DevelopState.DEVELOP:
			// 控制台输出
			Log.w(tag ,msg);
			break;
		case DevelopState.DEBUG:
			// 在应用下面创建目录存放日志
		case DevelopState.BATE:
			// 写日志到sdcard
			saveLog(tag, msg);
			break;
		case DevelopState.RELEASE:
			// 一般不做日志记录
			break;
		}
	}

	public static void error(String msg){
		error(getTag(), msg);
	}

	/**
	 * 用于输出error级别的日志
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void error(String tag, String msg) {
		grade = "error";
		switch (DevelopState.currentStage) {
		case DevelopState.DEVELOP:
			// 控制台输出
			Log.e(tag ,msg);
			break;
		case DevelopState.DEBUG:
			// 在应用下面创建目录存放日志
		case DevelopState.BATE:
			// 写日志到sdcard
			saveLog(tag, msg);
			break;
		case DevelopState.RELEASE:
			// 一般不做日志记录
			break;
		}
	}

	private static void saveLog(String tag, String msg) {
		String time;
		File tempFile = null;
		switch (DevelopState.currentStage) {
		case DevelopState.DEBUG:
			if(TextUtils.isEmpty(dataLogFilePath)){
				try {
					Class<?> clazz = Class.forName("com.zhuo.tong.application.MyApplication");
					Field field = clazz.getField("myDataFilesDir");
					tempFile = (File) field.get(clazz);
					dataLogFilePath = tempFile.getAbsolutePath();
				} catch (Exception e) {
					Log.e(tag, "无法保存log--"+msg);
					e.printStackTrace();
				}
			}
//			tempFile = MyApplication.myDataFilesDir;
			break;
		case DevelopState.BATE:
			if (!SDCardUtils.isSDCardEnable()) {
				return;
			}
			if(TextUtils.isEmpty(dataLogFilePath)){
				try {
					Class<?> clazz = Class.forName("com.zhuo.tong.application.MyApplication");
					Field field = clazz.getField("mySDCacheFilesDir");
					tempFile = (File) field.get(clazz);
					SDLogFilePath = tempFile.getAbsolutePath();
				} catch (Exception e) {
					e.printStackTrace();
					Log.e(tag, "无法保存log--"+msg);
				}
			}
//			tempFile = MyApplication.mySDCacheFilesDir;
			break;
		default:
			return;
		}
		
		if (file == null) {
			time = "======================="
					+ DateFormat.format(new Date())
					+ "：启动了应用=====================\n"
					+ HDateFormat.format(new Date());
			file = new File(tempFile,YDateFormat.format(new Date() + ".log"));
		} else {
			time = HDateFormat.format(new Date());
		}
		try {
			
			outputStream = new BufferedOutputStream(new FileOutputStream(
					file, true));
			
			outputStream.write(time.getBytes());
			outputStream.write((":--" + tag + ":--"+grade+":--").getBytes());
			outputStream.write(msg.getBytes());
			outputStream.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取调用该方法的方法名和所属类等信息->本类方法调用不计
	 * @return
	 */
	public static String getTag(){
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();

		if (sts == null) {
			return TAG;
		}

		String className;

		for (StackTraceElement st : sts) {
			if (st.isNativeMethod()) {
				continue;
			}

			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}

			className = st.getClassName();
			if(className.equals(MyTestLog.class.getName()))
				continue;

			if (className.startsWith(DevelopState.PACKAGE_NAME_LIBRARY) || className.startsWith(MyApplication.packageName)) {
//				return st.toString();
				return StackTraceElementToString(st);
			}
		}
		return TAG;
	}

	/**
	 * 因为StackTraceElement的StringBuilder只有80个字符，所以自己重写了方法
	 * @param st
	 * @return
	 */
	public static String StackTraceElementToString(StackTraceElement st) {
		StringBuilder buf = new StringBuilder(120);

		buf.append(st.getClassName());
		buf.append('.');
		buf.append(st.getMethodName());

		if (st.isNativeMethod()) {
			buf.append("(Native Method)");
		} else {
			String fName = st.getFileName();

			if (fName == null) {
				buf.append("(Unknown Source)");
			} else {
				int lineNum = st.getLineNumber();

				buf.append('(');
				buf.append(fName);
				if (lineNum >= 0) {
					buf.append(':');
					buf.append(lineNum);
				}
				buf.append(')');
			}
		}
		return buf.toString();
	}
	
}