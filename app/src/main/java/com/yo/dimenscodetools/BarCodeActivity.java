package com.yo.dimenscodetools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.yo.libs.dimenscode.BarCodeFactory;

public class BarCodeActivity extends Activity {

	private ImageView img;
	private Spinner spinner;
	private final String[] formats = { "CODE39", "CODE93", "CODE128",
			"CODABAR", "UPC-A", "UPC-E", "EAN-8", "EAN-13" };
	private BarcodeFormat format;
	private String content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bar);
		img = (ImageView) findViewById(R.id.imageView);
		spinner = (Spinner) findViewById(R.id.barcode_format);
		spinner.setAdapter(new ArrayAdapter<>(this,
				android.R.layout.simple_spinner_item, formats));
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					// 能表示44个字符，A-Z、0-9、SPACE、-、.、$、/、+、%、*
					format = BarcodeFormat.CODE_39;
					content = "* S 1 0 0 0 0 3 4 *";
					break;
				case 1:
					//zxing目前不支持 CODE_93
					format = BarcodeFormat.CODE_93;
					content = "12345ABCDE";
					break;
				case 2:
					format = BarcodeFormat.CODE_128;
					content = "54187841";
					break;
				case 3:
					format = BarcodeFormat.CODABAR;
					content = "A934900376047*";
					break;
				case 4:
					// UPC-A与EAN-8的编码方式相同，资料长度不同
					format = BarcodeFormat.UPC_A;
					content = "12345678901";
					break;
				case 5:
					//目前不支持UPC_E
					format = BarcodeFormat.UPC_E;
					content = "0123456789";
					break;
				case 6:
					format = BarcodeFormat.EAN_8;
					content = "12346578";
					break;
				case 7:
					format = BarcodeFormat.EAN_13;
					content = "6931987042704";
					break;
				default:
					break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				format = BarcodeFormat.CODE_128;
				content = "12345678900";
			}
		});
	}

	public void createBarcodeWithFormat(View view) {
		try {
			Bitmap bitmap = BarCodeFactory.createBarCode(this, content, 500,
					250, true, format);
			if (bitmap != null) {
				img.setImageBitmap(bitmap);
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	public void createBarcodeWithContent(View view) {
		try {
			Bitmap bitmap = BarCodeFactory.createBarCode(this, "1234567890",
					500, 250, true);
			if (bitmap != null) {
				img.setImageBitmap(bitmap);
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	public void createBarcode(View view) {
		try {
			Bitmap bitmap = BarCodeFactory.createBarCode(this, "1234567890", 500, 250,
					false);
			if (bitmap != null) {
				img.setImageBitmap(bitmap);
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

}
