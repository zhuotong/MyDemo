package com.zhuo.tong.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 项目名称：My Application
 * 类描述：
 * 创建人：苏格
 * 创建时间：2016/4/20 18:09
 * 修改人：苏格
 * 修改时间：2016/4/20 18:09
 * 修改备注：
 */
public class MyToast {

    public static void toastShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}
