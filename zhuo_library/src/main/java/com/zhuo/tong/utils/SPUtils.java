package com.zhuo.tong.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * @author dong 对SharedPreference的使用做了简单的封装，对外公布出put，get，remove，clear等等方法；
 *         注意一点，里面所有的commit操作使用了SharedPreferencesCompat.apply进行了替代，
 *         目的是尽可能的使用apply代替commit
 *         首先说下为什么，因为commit方法是同步的，并且我们很多时候的commit操作都是UI线程中，毕竟是IO操作，尽可能异步；
 *         所以我们使用apply进行替代，apply异步的进行写入；
 *         但是apply相当于commit来说是new API呢，为了更好的兼容，我们做了适配；
 *         SharedPreferencesCompat也可以给大家创建兼容类提供了一定的参考~~
 *         
 *         ----------------
 *         补充：增加了自定义名称的SharedPreferences，调用getSp获取SharedPreferences；
 *         其中的SharedPreferences.Editor是为了应对put多条数据时的应对，就不再写其他方法了，记得自己手动提交事务。
 *         另外没有保留context的引用，因为目前的我的需求很少改变SharedPreferences文件的名称并且这个类有静态对象，目的就是
 *         不用多次调用getsp而且为了方便再你确认sp已经赋值过的话直接使用SPUtils.sp来调用。也就是这个原因
 *         其他一些属性没有定义成私有。关于java封装和易用性的抉择，大家自己取舍
 */
public class SPUtils {
	/**
	 * 保存在手机里面的文件名
	 */
	public static String FILE_NAME = "config";//默認的SharedPreferences文件的名稱
	public static SPUtils spu;
	public SharedPreferences sp;
	public SharedPreferences.Editor editor;
	private SharedPreferencesCompat spc;
	
	/**
	 * 返回一个默认config名称的SharedPreferences
	 * @param context
	 * @return SPUtils对象
	 */
	public static SPUtils getSp(Context context) {
		if (spu == null || !FILE_NAME.equals("config")) {
			synchronized (SPUtils.class) {
				if (spu == null || !FILE_NAME.equals("config")) {
					FILE_NAME = "config";
					spu = new SPUtils(context,null);
				}
			}
		}

		return spu;
	}
	/**
	 * 返回一个自定义名称的SharedPreferences
	 * @param context
	 * @return SPUtils对象
	 */
	public static SPUtils getSp(Context context,String name) {
		if (spu == null || (!TextUtils.isEmpty(name)&&!FILE_NAME.equals(name))) {
			synchronized (SPUtils.class) {
				if (spu == null || (!TextUtils.isEmpty(name)&&!FILE_NAME.equals(name))) {
					FILE_NAME = name;
					spu = new SPUtils(context,name);
				}
			}
		}
		
		return spu;
	}

	private SPUtils(Context context,String name) {
		super();
		/*if(!TextUtils.isEmpty(name))
			FILE_NAME = name;*/
		sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		editor = sp.edit();
		spc = new SharedPreferencesCompat();
	}

	/**
	 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 * 
	 * @param context
	 * @param key
	 * @param object
	 */
	public void put(String key, Object object) {

		if (object instanceof String) {
			editor.putString(key, (String) object);
		} else if (object instanceof Integer) {
			editor.putInt(key, (Integer) object);
		} else if (object instanceof Boolean) {
			editor.putBoolean(key, (Boolean) object);
		} else if (object instanceof Float) {
			editor.putFloat(key, (Float) object);
		} else if (object instanceof Long) {
			editor.putLong(key, (Long) object);
		} else if (object != null) {
			editor.putString(key, object.toString());
		} else {
			editor.putString(key, null);
		}

		spc.apply(editor);
	}

	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 * 
	 * @param context
	 * @param key
	 * @param defaultObject
	 * @return
	 */
	public Object get(String key, Object defaultObject) {

		if (defaultObject instanceof String) {
			return sp.getString(key, (String) defaultObject);
		} else if (defaultObject instanceof Integer) {
			return sp.getInt(key, (Integer) defaultObject);
		} else if (defaultObject instanceof Boolean) {
			return sp.getBoolean(key, (Boolean) defaultObject);
		} else if (defaultObject instanceof Float) {
			return sp.getFloat(key, (Float) defaultObject);
		} else if (defaultObject instanceof Long) {
			return sp.getLong(key, (Long) defaultObject);
		} else if (defaultObject == null) {
			return sp.getString(key, null);
		}

		return null;
	}

	/**
	 * 移除某个key值已经对应的值
	 * 
	 * @param context
	 * @param key
	 */
	public void remove(String key) {

		editor.remove(key);
		spc.apply(editor);
	}

	/**
	 * 清除所有数据
	 * 
	 * @param context
	 */
	public void clear() {

		editor.clear();
		spc.apply(editor);
	}

	/**
	 * 查询某个key是否已经存在
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public boolean contains(String key) {

		return sp.contains(key);
	}

	/**
	 * 返回所有的键值对
	 * 
	 * @param context
	 * @return
	 */
	public Map<String, ?> getAll() {

		return sp.getAll();
	}

	/**
	 * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
	 * 
	 * @author zhy
	 * 
	 */
	private class SharedPreferencesCompat {
		private final Method sApplyMethod = findApplyMethod();

		/**
		 * 反射查找apply的方法
		 * 
		 * @return
		 */

		private Method findApplyMethod() {
			try {
				Class<SharedPreferences.Editor> clz = SharedPreferences.Editor.class;
				return clz.getMethod("apply");
			} catch (NoSuchMethodException e) {
			}

			return null;
		}

		/**
		 * 如果找到则使用apply执行，否则使用commit
		 * 
		 * @return 0是apply方法；1是commit执行成功；2是commit执行失败
		 * @param editor
		 */
		public int apply(SharedPreferences.Editor editor) {
			try {
				if (sApplyMethod != null) {
					sApplyMethod.invoke(editor);
					return 0;
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			return editor.commit() ? 1 : 2;
		}
	}

}