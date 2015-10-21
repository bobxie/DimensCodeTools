package com.yo.libs.dimenscode;

import java.util.Hashtable;

import android.graphics.Bitmap;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.zxing.android.decoding.BitmapLuminanceSource;

/**
 * 扫描二维码&条形码的帮助类
 * 
 * @author Kaming
 * @update 2015/07/22 9:30
 * 
 */
public class ScanHelper {

	/**
	 * 扫描条形码
	 * 
	 * @param bitmap
	 *            条形码图片
	 * @return 返回result结果
	 * @throws NotFoundException
	 */
	public static Result scanningBarCode(Bitmap bitmap)
			throws NotFoundException {
		LuminanceSource luminanceSource = new PlanarYUVLuminanceSource(
				rgb2YUV(bitmap), bitmap.getWidth(), bitmap.getHeight(), 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), false);
		BinaryBitmap binaryBitmapBarCode = new BinaryBitmap(
				new HybridBinarizer(luminanceSource));
		MultiFormatReader multiFormatReader = new MultiFormatReader();
		Result result = multiFormatReader.decode(binaryBitmapBarCode);
		return result;
	}

	/**
	 * 扫描二维码
	 * 
	 * @param bitmap
	 *            二维码图片
	 * @return 返回result结果
	 * @throws NotFoundException
	 * @throws ChecksumException
	 * @throws FormatException
	 */
	public static Result scanningQRCode(Bitmap bitmap)
			throws NotFoundException, ChecksumException, FormatException {
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
		BitmapLuminanceSource BitmapLuminanceSource = new BitmapLuminanceSource(
				bitmap);
		BinaryBitmap binaryBitmapQR = new BinaryBitmap(new HybridBinarizer(
				BitmapLuminanceSource));
		QRCodeReader qrCodeReader = new QRCodeReader();
		Result result = qrCodeReader.decode(binaryBitmapQR, hints);
		return result;
	}

	/**
	 * 将bitmap由RGB转换为YUV
	 * 
	 * @param bitmap
	 *            转换的图形
	 * @return YUV数据
	 */
	public static byte[] rgb2YUV(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		int len = width * height;
		byte[] yuv = new byte[len * 3 / 2];
		int y, u, v;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int rgb = pixels[i * width + j] & 0x00FFFFFF;

				int r = rgb & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb >> 16) & 0xFF;

				y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
				u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
				v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;

				y = y < 16 ? 16 : (y > 255 ? 255 : y);
				u = u < 0 ? 0 : (u > 255 ? 255 : u);
				v = v < 0 ? 0 : (v > 255 ? 255 : v);

				yuv[i * width + j] = (byte) y;
				// yuv[len + (i >> 1) * width + (j & ~1) + 0] = (byte) u;
				// yuv[len + (i >> 1) * width + (j & ~1) + 1] = (byte) v;
			}
		}
		return yuv;
	}

}
