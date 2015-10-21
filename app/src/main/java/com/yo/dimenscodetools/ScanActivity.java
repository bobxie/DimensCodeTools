package com.yo.dimenscodetools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.yo.libs.app.Capture;


/**
 * Sample Demo
 * 
 * @author Kaming
 *
 */
public class ScanActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
	}

	/**
	 * 启动扫描
	 * 
	 * @param view
	 */
	public void start(View view) {
		Capture.startScan(this);
		// 开启扫描条形码
		// Capture.startScanWithFeature(this, ScanParams.BARCODE_FEATURES);
		// 开启扫描条形码 指定所有参数
		// Capture.startScan(this, ScanParams.BARCODE_FEATURES, 500, 250, 500,
		// 500);
		// 指定条行码扫描大小
		// Capture.starScanWithBarSize(this, 500, 250);
		// 指定二维码扫描大小
		// Capture.starScanWithQRSize(this, 500, 500);
	}

	/**
	 * 生成条形码
	 * 
	 * @param view
	 */
	public void startCreateBarCode(View view) {
		Intent intent = new Intent(this, BarCodeActivity.class);
		startActivity(intent);
	}

	/**
	 * 生成二维码
	 * 
	 * @param view
	 */
	public void startCreateQRCode(View view) {
		Intent intent = new Intent(this, QRCodeActivity.class);
		startActivity(intent);
	}

	/**
	 * 解析回调
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String res = Capture.scanForResult(requestCode, resultCode, data);
		if (res !=null) {
			Pattern p = Pattern.compile("[0-9]*");
			Matcher m = p.matcher(res);
			if (m.matches()) {
				super.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://www.upcdatabase.com/item/" + res))); // 7
			}else{
				Toast.makeText(this, res, Toast.LENGTH_LONG).show();
			}
		}
	}

}
