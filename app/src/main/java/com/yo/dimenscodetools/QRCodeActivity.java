package com.yo.dimenscodetools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.yo.libs.dimenscode.QRCodeFactory;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 生成 二维码 条形码的界面
 * 
 * @author Kaming
 *
 */
public class QRCodeActivity extends Activity {
	private ImageView mQR;
	private static final String TAG = "YO";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivity_qr);
		mQR = (ImageView) findViewById(R.id.qr);
	}

	/**
	 * 创建存储于文件的二维码
	 * 
	 * @param view
	 */
	public void createQRCodeInFile(View view) {
		String path = getFileRoot() + File.separator + "qr_infile1" + ".jpg";
		try {
			boolean res = QRCodeFactory.createQRCodeInFile("Hello", 500, 500, path, true);
			Log.d(TAG, res + "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建存储于文件的带icon二维码
	 * 
	 * @param view
	 */
	public void createQRCodeWithIconInFile(View view) {
		String path = getFileRoot() + File.separator + "qr_with_icon_infile2"
				+ ".jpg";
		try {
			boolean res = QRCodeFactory
					.createQRCodeWithIconInFile("你好", 200, 200, BitmapFactory
							.decodeResource(getResources(), R.drawable.me),
							path,false);
			Log.d(TAG, res + "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建二维码
	 * 
	 * @param view
	 */
	public void createQRCode(View view) {
		try {
			Bitmap res = QRCodeFactory.createQRCode("Yo~ 切克闹", 200, 200,true);
			if (res != null) {
				mQR.setImageBitmap(res);
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建带icon的二维码
	 * 
	 * @param view
	 */
	public void createQRCodeWithIcon(View view) {
		try {
			Bitmap res = QRCodeFactory
					.createQRCodeWithIcon("Hey, Man!!", 550	, 550, BitmapFactory
							.decodeResource(getResources(), R.drawable.ic_launcher),false);
			if (res != null) {
				mQR.setImageBitmap(res);
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建带颜色的二维码
	 * 
	 * @param view
	 */
	public void createQRCodeWithColor(View view) {
		try {
			Bitmap res = QRCodeFactory.createQRCodeWithColor("I'm color..",
					400, 400, 0xff2196F3, 0xffE3F2FD,false);
			if (res != null) {
				mQR.setImageBitmap(res);
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建带颜色和图标的二维码
	 * @param view
	 */
	public void createQRCodeWithIconAndColor(View view) {
		try {
			Bitmap res = QRCodeFactory
					.createQRCodeWithIconAndColor("I'm icon and color...", 400,
							400, BitmapFactory.decodeResource(getResources(),
									R.drawable.me), 0xff2196F3, 0xffffffff,false);
			if (res != null) {
				mQR.setImageBitmap(res);
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取files文件路径
	 * 
	 * @return
	 */
	private String getFileRoot() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File external = this.getExternalFilesDir(null);
			if (external != null) {
				return external.getAbsolutePath();
			}
		}
		return this.getFilesDir().getAbsolutePath();
	}

}
