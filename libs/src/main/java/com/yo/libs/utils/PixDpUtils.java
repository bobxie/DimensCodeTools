package com.yo.libs.utils;

import android.content.Context;

/**
 * Pixel Dp 工具类
 * 
 * @author Kaming
 *
 */
public class PixDpUtils {

	/**
	 * Dip转Px 
	 * @param context 上下文
	 * @param dipValue dp值
	 * @return 返回转换后的值
	 */
	public static int dip2px(Context context, float dipValue) {
		float m = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * m + 0.5f);
	}

	/**
	 * Px转Dip
	 * @param context 上下文
	 * @param pxValue px值
	 * @return 返回转换后的值
	 */
	public static int px2dip(Context context, float pxValue) {
		float m = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / m + 0.5f);
	}

}
