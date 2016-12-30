package com.zskj.helmetclient.util;

import android.util.Log;

import com.zskj.helmetclient.app.Constant;


/**
 * 
 * @ClassName: LogUtil
 * @Description: 打印日志工具类
 * @author：LiZhimin
 * @version V1.0
 */
public class LogUtil {

	public static void defLog(String content) {
			String log = getTraceInfo() + "  :  " + content;
			Log.i(Constant.APP_TAG, log);
	}

	public static void printI(String tag, String content) {
			String log = getTraceInfo() + "  :  " + content;
			Log.i(tag, log);
	}

	public static void printE(String tag, String content) {
			String log = getTraceInfo() + "  :  " + content;
			Log.e(tag, log);
	}

	public static void printD(String tag, String content) {
			String log = getTraceInfo() + "  :  " + content;
			Log.d(tag, log);
	}

	public static void printV(String tag, String content) {
			String log = getTraceInfo() + "  :  " + content;
			Log.v(tag, log);
	}

	public static void syso(String content) {
			String log = getTraceInfo() + "  :  " + content;
			System.out.println(log);
	}

	/**
	 * 获取堆栈信息
	 */
	private static String getTraceInfo() {
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] stacks = new Throwable().getStackTrace();
		String className = stacks[2].getClassName();
		int index = className.lastIndexOf('.');
		if (index >= 0) {
			className = className.substring(index + 1, className.length());
		}
		String methodName = stacks[2].getMethodName();
		int lineNumber = stacks[2].getLineNumber();
		sb.append(className).append("->").append(methodName).append("()->").append(lineNumber);
		return sb.toString();
	}

}
