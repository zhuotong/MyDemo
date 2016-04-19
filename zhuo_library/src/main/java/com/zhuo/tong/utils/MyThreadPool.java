package com.zhuo.tong.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 项目名称：My Application
 * 类描述：
 * 创建人：苏格
 * 创建时间：2016/4/16 11:13
 * 修改人：苏格
 * 修改时间：2016/4/16 11:13
 * 修改备注：
 */
public class MyThreadPool {
    private static ExecutorService newCachedThreadPool;

    public static ExecutorService getThreadPool(){
        if (newCachedThreadPool == null) {
            synchronized (MyThreadPool.class) {
                if (newCachedThreadPool == null) {
                    newCachedThreadPool = Executors.newCachedThreadPool();
                }
            }
        }
        return newCachedThreadPool;
    }
}
