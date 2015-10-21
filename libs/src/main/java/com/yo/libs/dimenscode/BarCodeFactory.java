package com.yo.libs.dimenscode;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.Gravity;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * 创建条形码的工厂类
 * 
 * @author Kaming
 * @update 2015/07/22 9:47
 */
public class BarCodeFactory {

	/**
	 * 默认条形码编码方式
	 */
	public static final BarcodeFormat DEFAULT_FORMAT = BarcodeFormat.CODE_128;

	/**
	 * 构造方法私有化
	 */
	private BarCodeFactory(){
	}
	
	/**
	 * 创建存储于文件中的条形码
	 * 
	 * @param context
	 *            上下文
	 * @param content
	 *            内容
	 * @param widthPix
	 *            宽，像素为单位
	 * @param heightPix
	 *            高，像素为单位
	 * @param filePath
	 *            文件路径
	 * @param displayCode
	 *            是否在条形码下方显示内容
	 * @return true表示创建成功，false表示创建失败
	 * @throws FileNotFoundException
	 * @throws WriterException
	 */
	public static boolean createBarCodeInFile(Context context, String content,
			int widthPix, int heightPix, String filePath, boolean displayCode)
			throws FileNotFoundException, WriterException {
		Bitmap resultBitmap = createBarCode(context, content, widthPix,
				heightPix, displayCode);
		return resultBitmap != null
				&& resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						new FileOutputStream(filePath));
	}

	/**
	 * 创建指定格式的条形码并存储于文件中
	 * 
	 * @param context
	 *            上下文
	 * @param content
	 *            内容
	 * @param widthPix
	 *            宽度
	 * @param heightPix
	 *            高度
	 * @param filePath
	 *            文件路径
	 * @param displayCode
	 *            是否在条形码下方显示内容
	 * @param format
	 *            值必须是BarcodeFormat泛型类下的所有常量,可能为BarCodeFactory.DEFAULT_FORMAT(
	 *            code128)
	 * @return
	 * @throws WriterException
	 */
	public static boolean createBarCodeInFile(Context context, String content,
			int widthPix, int heightPix, String filePath, boolean displayCode,
			BarcodeFormat format) throws WriterException, FileNotFoundException {
		Bitmap resultBitmap = createBarCode(context, content, widthPix,
				heightPix, displayCode, format);
		return resultBitmap != null
				&& resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						new FileOutputStream(filePath));
	}

	/**
	 * 创建条形码 默认格式为CODE_128
	 * 
	 * @param context
	 *            上下文
	 * @param content
	 *            内容
	 * @param widtPix
	 *            宽度
	 * @param heightPix
	 *            高度
	 * @param display
	 *            是否在条形码下方显示内容
	 * @return 返回条形码
	 * @throws WriterException
	 */
	public static Bitmap createBarCode(Context context, String content,
			int widtPix, int heightPix, boolean display) throws WriterException {
		return createBarCode(context, content, widtPix, heightPix, display,
				DEFAULT_FORMAT);
	}

	/**
	 * 创建指定格式的条形码
	 * 
	 * @param context
	 *            上下文
	 * @param content
	 *            内容
	 * @param widthPix
	 *            宽度
	 * @param heightPix
	 *            高度
	 * @param displayCode
	 *            是否在条形码下方显示内容
	 * @param format
	 *            值必须是BarcodeFormat泛型类下的所有常量,可能为BarCodeFactory.DEFAULT_FORMAT(
	 *            code128)
	 * @return
	 * @throws WriterException
	 */
	public static Bitmap createBarCode(Context context, String content,
			int widthPix, int heightPix, boolean displayCode,
			BarcodeFormat format) throws WriterException {
		Bitmap resultBitmap = null;
		// 条形码 编码格式
		BarcodeFormat barcodeFormat = format;
		if (displayCode) {
			// 生成条形码的Bitmap
			Bitmap barCodeBitmap = encodeAsBitmap(content, barcodeFormat,
					widthPix, heightPix);
			// 生成显示编码的Bitmap
			Bitmap codeBitmap = createCodeBitmap(content, widthPix, heightPix,
					context);
			// 合成 bitmap
			if (barCodeBitmap != null && codeBitmap != null) {
				resultBitmap = mixtureBitmap(barCodeBitmap, codeBitmap,
						new PointF(0, heightPix));
			}
		} else {
			resultBitmap = encodeAsBitmap(content, barcodeFormat, widthPix,
					heightPix);
		}
		return resultBitmap;
	}

	/**
	 * 合并两张bitmap图片
	 * 
	 * @param first
	 *            第一张
	 * @param second
	 *            第二章
	 * @param pointF
	 *            图片的左上点
	 * @return
	 */
	private static Bitmap mixtureBitmap(Bitmap first, Bitmap second,
			PointF pointF) {
		Bitmap newBitmap = null;
		if (first != null && second != null && pointF != null) {
			newBitmap = Bitmap.createBitmap(first.getWidth(), first.getHeight()
					+ second.getHeight() / 2, Config.ARGB_8888);
			Canvas canvas = new Canvas(newBitmap);
			canvas.drawBitmap(first, 0, 0, null);
			canvas.drawBitmap(second, pointF.x, pointF.y, null);
			canvas.save(Canvas.ALL_SAVE_FLAG);
			canvas.restore();
		}
		return newBitmap;
	}

	/**
	 * 生成显示编码的bitmap
	 * 
	 * @param content
	 *            内容
	 * @param width
	 *            宽度，像素为单位
	 * @param height
	 *            高度，像素为单位
	 * @param context
	 *            上下文
	 * @return 返回 生成显示的bitmap
	 */
	private static Bitmap createCodeBitmap(String content, int width,
			int height, Context context) {
		TextView tv = new TextView(context);
		LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tv.setLayoutParams(params);
		tv.setText(content);
		tv.setHeight(height);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setWidth(width);
		tv.setDrawingCacheEnabled(true);
		tv.setTextColor(Color.BLACK);
		tv.setBackgroundColor(Color.WHITE);
		tv.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
		tv.buildDrawingCache();
		Bitmap bitmapCache = tv.getDrawingCache();
		return bitmapCache;
	}

	/**
	 * 生成条形码的bitmap
	 * 
	 * @param content
	 *            内容
	 * @param format
	 *            条形码编码 格式
	 * @param widthPix
	 *            宽，像素为单位
	 * @param heightPix
	 *            高，像素为单位
	 * @return 返回为 生成条形码的bitmap
	 * @throws WriterException
	 */
	private static Bitmap encodeAsBitmap(String content, BarcodeFormat format,
			int widthPix, int heightPix) throws WriterException {
		final int WHITE = 0xFFFFFFFF;
		final int BLACK = 0xFF000000;
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result = writer.encode(content, format, widthPix, heightPix);
		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		// 生成条码算法
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Config.ARGB_8888);
		//位图替换像素
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

}
