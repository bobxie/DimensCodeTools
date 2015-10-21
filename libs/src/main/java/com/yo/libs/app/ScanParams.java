package com.yo.libs.app;

/**
 * 开启扫描界面时传递参数的常量类
 * 
 * @author Kaming
 *
 */
public class ScanParams {
	/**
	 * 跳转CaptureActivity 默认 扫描二维码
	 */
	public static final int QRCODE_FEATURES = 0x00;
	/**
	 * 跳转CaptureActivity 默认 扫描条形码
	 */
	public static final int BARCODE_FEATURES = 0x01;
	/**
	 * 返回结果码
	 */
	public static final int RESULT_OK = 0x02;
	/**
	 * 跳转CaptureActivity时的 Tag key
	 */
	public static final String WHICH = "WHICH";
	/**
	 * 返回给调用界面的Tag key
	 */
	public static final String RESULT = "RESULT";
	/**
	 * 二维码扫描框 宽度
	 */
	public static final String QRCODE_WIDTH_PIX = "QRCODE_WIDTH_PIX";
	/**
	 * 二维码扫描框 高度
	 */
	public static final String QRCODE_HEIGHT_PIX = "QRCODE_HEIGHT_PIX";
	/**
	 * 条形码扫描框 宽度
	 */
	public static final String BARCODE_WIDTH_PIX = "BARCODE_WIDTH_PIX";
	/**
	 * 条形码扫描框 高度
	 */
	public static final String BARCODE_HEIGHT_PIX = "BARCODE_HEIGHT_PIX";
	

	
}
