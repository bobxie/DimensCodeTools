package com.yo.libs.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * 处理Assets文件 工具类 
 * @author Kaming
 *
 */
public class AssetsUtils {
	
	/**
	 * 将assets文件转成drawable
	 * @param fileName 文件名
	 * @return 
	 */
	public static Drawable assets2Drawable(Context context,String fileName) {
		InputStream open = null;
		Drawable drawable = null;
		try {
			open = context.getAssets().open(fileName);
			drawable = Drawable.createFromStream(open, null);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (open != null) {
					open.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return drawable;
	}
	
}
