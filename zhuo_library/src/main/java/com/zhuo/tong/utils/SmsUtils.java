package com.zhuo.tong.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

/**
 * 短信的备份和还原 工具类
 * bug很多，慎用，有时间再修改，毕竟感觉用的不多
 * 
 */
public class SmsUtils {
	public interface BackUpCallBack{
		/**
		 * 短信调用前调用的方法
		 */
		public void beforeSmsBackup(int total);
		/**
		 * 短信备份中调用的方法 
		 * @param progress 当前备份的进度。
		 */
		public void onSmsBackup(int progress);
	}
	
	
	
	/**
	 * 短信的备份
	 * @param context 上下文
	 * @param od 进度条对话框
	 * @throws Exception 
	 */
	public static void backupSms(Context context,BackUpCallBack backupCallback) throws Exception{
		ContentResolver resolver = context.getContentResolver();
		File file = new File(Environment.getExternalStorageDirectory(),"backuo.xml");
		FileOutputStream fos = new FileOutputStream(file);
		//把用户的短信一条一条读出来,按照一定的格式写到文件里
		XmlSerializer serializer = Xml.newSerializer();//获取xml文件的生存期(系列器)
		//初始化生成器
		serializer.setOutput(fos,"utf-8");
		serializer.startDocument("utf-8",true);//true 是否独立
		serializer.startTag(null,"smss");
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri,new String[]{"body","address","type","date"}, null, null, null);
		//开始备份的时候,设置进度条的最大值
		backupCallback.beforeSmsBackup(cursor.getCount());
		int progress = 0;
		while (cursor.moveToNext()) {
//			Thread.sleep(500);
			String body = cursor.getString(0);
			String address = cursor.getString(1);
			String type = cursor.getString(2);
			String date = cursor.getString(3);
			
			serializer.startTag(null,"sms");
			
			serializer.startTag(null,"body");
			serializer.text(body==null?"":body);
			serializer.endTag(null,"body");
			
			serializer.startTag(null,"address");
			serializer.text(address==null?"":address);
			serializer.endTag(null,"address");
			
			serializer.startTag(null,"type");
			serializer.text(type==null?"":type);
			serializer.endTag(null,"type");
			
			serializer.startTag(null,"date");
			serializer.text(date==null?"":date);
			serializer.endTag(null,"date");
			
			serializer.endTag(null,"sms");
			//备份过程中,增加进度
			progress++;
			backupCallback.onSmsBackup(progress);
		}
		serializer.endTag(null,"smss");
		serializer.endDocument();
		fos.close();
	}
	/**
	 * 短信的还原
	 * @throws Exception 
	 */
	public static void restoreSms(Context context,boolean flag) throws Exception{
		Uri uri = Uri.parse("content://sms/");
		if(flag){ //true则清除本身的短信
			context.getContentResolver().delete(uri,null,null);
		}
		XmlPullParser pull = Xml.newPullParser();
		File file = new File(Environment.getExternalStorageDirectory(),"backuo.xml");
		FileInputStream fis = new FileInputStream(file);
		pull.setInput(fis,"utf-8");
		int eventType = pull.getEventType();
		String body = null;
		String date = null;
		String type = null;
		String address = null;
		ContentValues values = null;
		while(eventType != XmlPullParser.END_DOCUMENT){
			String tagName = pull.getName();
			switch (eventType) {
				case XmlPullParser.START_TAG: //如果是开始标签
					if("body".equals(tagName)){
						body = pull.nextText();
					}else if("date".equals(tagName)){
						date = pull.nextText();
					}else if("type".equals(tagName)){
						type = pull.nextText();
					}else if("address".equals(tagName)){
						address = pull.nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					if("sms".equals(tagName)){
						values = new ContentValues();
						values.put("body",body);
						values.put("date",date);
						values.put("type",type);
						values.put("address",address);
						context.getContentResolver().insert(uri, values);
					}
					break;
			}
			eventType = pull.next();
		}
		fis.close();
	}
}
