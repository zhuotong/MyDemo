package com.zhuo.tong.constant;

/**
 * 定义了开发状态，针对不同的开发状态实行不同的方案
 * 控制开关是currentStage的值
 * @author dong
 *
 */
public interface DevelopState {
	/**
	 * 开发阶段
	 */
	int DEVELOP = 0;
	/**
	 * 内部测试阶段
	 */
	int DEBUG = 1;
	/**
	 * 公开测试
	 */
	int BATE = 2;
	/**
	 * 正式版
	 */
	int RELEASE = 3;

	/**
	 * 当前阶段标示
	 */
	int currentStage = DEVELOP;

	String PACKAGE_NAME_LIBRARY = "com.zhuo.tong";
}
