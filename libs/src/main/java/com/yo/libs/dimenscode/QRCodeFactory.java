package com.yo.libs.dimenscode;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 二维码生成工厂类
 * 
 * @author Kaming
 * @date 2015/07/19 15:13
 * 
 */
public class QRCodeFactory {

	/**
	 * 二维码的颜色
	 */
	public static final int DEFAULT_COLOR_FRONT = 0xff000000;

	/**
	 * 背景颜色
	 */
	public static final int DEFAULT_COLOR_BACKGROUND = 0xffffffff;

	/**
	 * 构造私有化
	 */
	private QRCodeFactory() {
	}

	/**
	 * 创建普通二维码
	 * 
	 * @param content
	 *            内容
	 * @param widthPix
	 *            宽度，像素为单位
	 * @param heightPix
	 *            高度，像素为单位
	 * @param hasMargin
	 *            是否需要边框 true表示有边框 false表示无边框
	 * @return 返回普通二维码
	 * @throws WriterException
	 */
	public static Bitmap createQRCode(String content, int widthPix,
			int heightPix, boolean hasMargin) throws WriterException {
		Bitmap res = createQRCodeWithColor(content, widthPix, heightPix,
				DEFAULT_COLOR_FRONT, DEFAULT_COLOR_BACKGROUND, hasMargin);
		return res;
	}

	/**
	 * 创建指定颜色的二维码 并返回
	 * 
	 * @param content
	 *            内容
	 * @param widthPix
	 *            二维码宽度
	 * @param heightPix
	 *            二维码高度
	 * @param colorFront
	 *            二维码颜色 十六进制
	 * @param colorBg
	 *            二维码背景颜色 十六进制
	 * @param hasMargin
	 *            是否需要边框 true表示有边框 false表示无边框
	 * @return 二维码 Bitmap类型
	 * @throws WriterException
	 */
	public static Bitmap createQRCodeWithColor(String content, int widthPix,
			int heightPix, int colorFront, int colorBg, boolean hasMargin)
			throws WriterException {
		Bitmap res = createQRCode(content, widthPix, heightPix, null,
				colorFront, colorBg, hasMargin);
		return res;
	}

	/**
	 * 创建带有icon的二维码
	 * 
	 * @param content
	 *            内容
	 * @param widthPix
	 *            宽度，像素为单位
	 * @param heightPix
	 *            高度，像素为单位
	 * @param logoBm
	 *            icon图片
	 * @param hasMargin
	 *            是否需要边框 true表示有边框 false表示无边框
	 * @return 返回带icon的二维码
	 * @throws WriterException
	 */
	public static Bitmap createQRCodeWithIcon(String content, int widthPix,
			int heightPix, Bitmap logoBm, boolean hasMargin)
			throws WriterException {
		Bitmap res = createQRCode(content, widthPix, heightPix, logoBm,
				DEFAULT_COLOR_FRONT, DEFAULT_COLOR_BACKGROUND, hasMargin);
		return res;
	}

	/**
	 * 创建指定颜色带有icon的二维码
	 * 
	 * @param content
	 *            内容
	 * @param widthPix
	 *            二维码宽度
	 * @param heightPix
	 *            二维码高度
	 * @param logoBm
	 *            icon图片
	 * @param colorFront
	 *            二维码颜色 十六进制
	 * @param colorBg
	 *            二维码背景颜色 十六进制
	 * @param hasMargin
	 *            是否需要边框 true表示有边框 false表示无边框
	 * @return
	 * @throws WriterException
	 */
	public static Bitmap createQRCodeWithIconAndColor(String content,
			int widthPix, int heightPix, Bitmap logoBm, int colorFront,
			int colorBg, boolean hasMargin) throws WriterException {
		Bitmap res = createQRCode(content, widthPix, heightPix, logoBm,
				colorFront, colorBg, hasMargin);
		return res;
	}

	/**
	 * 创建二维码核心方法
	 * 
	 * @param content
	 *            内容
	 * @param widthPix
	 *            宽度，像素为单位
	 * @param heightPix
	 *            高度，像素为单位
	 * @param logoBm
	 *            icon图片
	 * @param colorFront
	 *            二维码颜色 十六进制
	 * @param colorBg
	 *            二维码背景颜色 十六进制
	 * @param hasMargin
	 *            是否需要边框 true表示有边框 false表示无边框
	 * @return 返回指定边框的二维码
	 * @throws WriterException
	 */
	private static Bitmap createQRCode(String content, int widthPix,
			int heightPix, Bitmap logoBm, int colorFront, int colorBg,
			boolean hasMargin) throws WriterException {
		Bitmap res = null;
		if (content != null || !"".equals(content)) {
			// 配置参数
			Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			if (!hasMargin) {
				hints.put(EncodeHintType.MARGIN, 0);
			}
			// 容错级别 H为最高
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			// 图像数据转换（矩阵转换）
			BitMatrix bitMatrix = new QRCodeWriter().encode(content,
					BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
			int[] pixels = new int[widthPix * heightPix];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < heightPix; y++) {
				for (int x = 0; x < widthPix; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * widthPix + x] = colorFront;
					} else {
						pixels[y * widthPix + x] = colorBg;
					}
				}
			}
			// 生成二维码图片的格式 ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
			if (logoBm != null) {
				bitmap = addLogon(bitmap, logoBm);
			}
			res = bitmap;
		}
		return res;
	}

	/**
	 * 创建存储于文件中的普通二维码
	 * 
	 * @param content
	 *            内容
	 * @param widthPix
	 *            宽，像素为单位
	 * @param heightPix
	 *            高，像素为单位
	 * @param filePath
	 *            二维码存储路径
	 * @param hasMargin
	 *            是否需要边框 true表示有边框 false表示无边框
	 * @return true 表示存储成功 false 表示存储失败
	 * @throws WriterException
	 *             编码时可能抛出的异常
	 * @throws FileNotFoundException
	 */
	public static boolean createQRCodeInFile(String content, int widthPix,
			int heightPix, String filePath, boolean hasMargin)
			throws WriterException, FileNotFoundException {
		boolean res = createQRCodeWithIconInFile(content, widthPix, heightPix,
				null, filePath, hasMargin);
		return res;
	}

	/**
	 * 创建存储于文件中的指定颜色二维码
	 * 
	 * @param content
	 *            内容
	 * @param widthPix
	 *            二维码宽度
	 * @param heightPix
	 *            二维码高度
	 * @param filePath
	 *            文件路径
	 * @param colorFront
	 *            二维码颜色 十六进制
	 * @param colorBg
	 *            二维码背景颜色 十六进制
	 * @param hasMargin
	 *            是否需要边框 true表示有边框 false表示无边框
	 * @return true 表示存储成功 false 表示存储失败
	 * @throws WriterException
	 * @throws FileNotFoundException
	 */
	public static boolean createQRCodeWithColorInFile(String content,
			int widthPix, int heightPix, String filePath, int colorFront,
			int colorBg, boolean hasMargin) throws WriterException,
			FileNotFoundException {
		Bitmap bitmap = createQRCodeWithColor(content, widthPix, heightPix,
				colorFront, colorBg, hasMargin);
		boolean res = bitmap != null
				&& bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						new FileOutputStream(filePath));
		return res;
	}

	/**
	 * 创建存储于文件中的带icon二维码
	 * 
	 * @param content
	 *            内容
	 * @param widthPix
	 *            宽，像素为单位
	 * @param heightPix
	 *            高，像素为单位
	 * @param logoBm
	 *            icon图片
	 * @param filePath
	 *            二维码存储路径
	 * @param hasMargin
	 *            是否需要边框 true表示有边框 false表示无边框
	 * @return true 表示存储成功 false 表示存储失败
	 * @throws WriterException
	 *             编码时可能抛出的异常
	 * @throws FileNotFoundException
	 */
	public static boolean createQRCodeWithIconInFile(String content,
			int widthPix, int heightPix, Bitmap logoBm, String filePath,
			boolean hasMargin) throws WriterException, FileNotFoundException {
		Bitmap bitmap = createQRCodeWithIconAndColor(content, widthPix,
				heightPix, logoBm, DEFAULT_COLOR_FRONT,
				DEFAULT_COLOR_BACKGROUND, hasMargin);
		boolean res = bitmap != null
				&& bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						new FileOutputStream(filePath));
		return res;
	}

	/**
	 * 创建存储于文件中带颜色和图标的二维码
	 * 
	 * @param content
	 *            内容
	 * @param widthPix
	 *            二维码的宽度
	 * @param heightPix
	 *            二维码的高度
	 * @param logoBm
	 *            icon图片
	 * @param filePath
	 *            文件路径
	 * @param colorFront
	 *            二维码颜色 十六进制
	 * @param colorBg
	 *            二维码背景颜色 十六进制
	 * @param hasMargin
	 *            是否需要边框 true表示有边框 false表示无边框
	 * @return true 表示存储成功 false 表示存储失败
	 * @throws WriterException
	 * @throws FileNotFoundException
	 */
	public static boolean createQRCodeWithIconAndColorInFile(String content,
			int widthPix, int heightPix, Bitmap logoBm, String filePath,
			int colorFront, int colorBg, boolean hasMargin)
			throws WriterException, FileNotFoundException {
		Bitmap bitmap = createQRCodeWithIconAndColor(content, widthPix,
				heightPix, logoBm, colorFront, colorBg, hasMargin);
		boolean res = bitmap != null
				&& bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						new FileOutputStream(filePath));
		return res;

	}

	/**
	 * 添加 logo
	 * 
	 * @param src
	 *            二维码图片
	 * @param logo
	 *            logo图片
	 * @return 返回带logo的二维码
	 */
	private static Bitmap addLogon(Bitmap src, Bitmap logo) {
		Bitmap res = null;
		if (src != null && logo != null) {
			// 获取图片的宽高
			int srcWidth = src.getWidth();
			int srcHeight = src.getHeight();
			int logoWidth = logo.getWidth();
			int logoHeight = logo.getHeight();
			if (srcWidth != 0 && srcHeight != 0) {
				// logo大小为二维码整体大小的1/5
				float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
				Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight,
						Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				canvas.drawBitmap(src, 0, 0, null);
				canvas.scale(scaleFactor, scaleFactor, srcWidth / 2,
						srcHeight / 2);
				canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2,
						(srcHeight - logoHeight) / 2, null);
				canvas.save(Canvas.ALL_SAVE_FLAG);
				canvas.restore();
				res = bitmap;
			} else {
				res = src;
			}
		}
		return res;
	}

}
