package com.yo.libs.app;

import android.app.Activity;
import android.content.Intent;

import com.yo.libs.ui.CaptureActivity;


/**
 * 扫描类
 * 
 * @author Kaming
 *
 */
public class Capture {

	public static final int DEFAULT_REQUEST_CODE = 0x00;

	/**
	 * 开启扫描(默认扫描为二维码)
	 * 
	 * @param context
	 *            当前Activity
	 */
	public static void startScan(Activity context) {
		startScanWithFeature(context, ScanParams.QRCODE_FEATURES);
	}

	/**
	 * 根据feature开启扫描
	 * 
	 * @param context
	 *            当前Activity
	 * @param feature
	 *            值可能为ScanParams.BARCODE_FEATURES(条形码) 或者
	 *            ScanParams.QRCODE_FEATURES(二维码)
	 */
	public static void startScanWithFeature(Activity context, int feature) {
		Intent intent = new Intent(context, CaptureActivity.class);
		intent.putExtra(ScanParams.WHICH, feature);
		context.startActivityForResult(intent, DEFAULT_REQUEST_CODE);
	}

	/**
	 * 开启条形码扫描 可指定条形码扫描框大小 默认为 500*250
	 * 
	 * @param context
	 *            当前Activity
	 * @param barWidth
	 *            扫描条形码框的宽度
	 * @param barHeight
	 *            扫描条形码框的高度
	 */
	public static void starScanWithBarSize(Activity context, int barWidth,
			int barHeight) {
		Intent intent = new Intent(context, CaptureActivity.class);
		intent.putExtra(ScanParams.WHICH, ScanParams.BARCODE_FEATURES);
		intent.putExtra(ScanParams.BARCODE_WIDTH_PIX, barWidth);
		intent.putExtra(ScanParams.BARCODE_HEIGHT_PIX, barHeight);
		context.startActivityForResult(intent, DEFAULT_REQUEST_CODE);
	}

	/**
	 * 开启二维码扫描 可指定二维码扫描框大小 默认为 500*500
	 * 
	 * @param context
	 *            当前Activity
	 * @param qrWidth
	 *            扫描二维码框的宽度
	 * @param qrHeight
	 *            扫描二维码框的高度
	 */
	public static void starScanWithQRSize(Activity context, int qrWidth,
			int qrHeight) {
		Intent intent = new Intent(context, CaptureActivity.class);
		intent.putExtra(ScanParams.WHICH, ScanParams.QRCODE_FEATURES);
		intent.putExtra(ScanParams.QRCODE_WIDTH_PIX, qrWidth);
		intent.putExtra(ScanParams.QRCODE_HEIGHT_PIX, qrHeight);
		context.startActivityForResult(intent, DEFAULT_REQUEST_CODE);
	}

	/**
	 * 开启指定扫描类型，可指定二维码大小，条形码大小
	 * 
	 * @param context
	 *            当前Activity
	 * @param feature
	 *            值可能为ScanParams.BARCODE_FEATURES(条形码) 或者
	 *            ScanParams.QRCODE_FEATURES(二维码)
	 * @param barWidth
	 *            条形码宽度
	 * @param barHeight
	 *            条形码高度
	 * @param qrWidth
	 *            二维码宽度
	 * @param qrHeight
	 *            二维码高度
	 */
	public static void startScan(Activity context, int feature, int barWidth,
			int barHeight, int qrWidth, int qrHeight) {
		Intent intent = new Intent();
		intent.putExtra(ScanParams.WHICH, feature);
		intent.putExtra(ScanParams.QRCODE_WIDTH_PIX, qrWidth);
		intent.putExtra(ScanParams.QRCODE_HEIGHT_PIX, qrHeight);
		intent.putExtra(ScanParams.BARCODE_WIDTH_PIX, barWidth);
		intent.putExtra(ScanParams.BARCODE_HEIGHT_PIX, barHeight);
		context.startActivityForResult(intent, DEFAULT_REQUEST_CODE);
	}

	/**
	 * 获取解析后的值
	 * 
	 * @param data
	 *            onActivityResult回调的data变量
	 * @return
	 */
	public static String scanForResult(int requestCode, int resultCode,
			Intent data) {
		String res = null;
		if (requestCode == DEFAULT_REQUEST_CODE
				&& resultCode == ScanParams.RESULT_OK && data != null) {
			res = data.getStringExtra(ScanParams.RESULT);
		}
		return res;
	}

}
