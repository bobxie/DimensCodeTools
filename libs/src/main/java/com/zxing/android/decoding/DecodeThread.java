package com.zxing.android.decoding;/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;
import com.yo.libs.ui.CaptureActivity;

import android.os.Handler;
import android.os.Looper;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * This thread does all the heavy lifting of decoding the images.
 * 解析线程
 * @author dswitkin@google.com (Daniel Switkin)
 * 
 */
final class DecodeThread extends Thread {

	public static final String BARCODE_BITMAP = "barcode_bitmap";
	
	private final CaptureActivity activity;
	private final Hashtable<DecodeHintType, Object> hints;
	private Handler handler;
	/**
	 * 同步辅助类 允许一个或多个线程一直等待
	 */
	private final CountDownLatch handlerInitLatch;

	DecodeThread(CaptureActivity activity, Vector<BarcodeFormat> decodeFormats,
			String characterSet, ResultPointCallback resultPointCallback) {

		this.activity = activity;
		
		handlerInitLatch = new CountDownLatch(1);

		hints = new Hashtable<DecodeHintType, Object>(3);

		if (decodeFormats == null || decodeFormats.isEmpty()) {
			decodeFormats = new Vector<BarcodeFormat>();
			decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
			decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
			decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
		}

		hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

		if (characterSet != null) {
			hints.put(DecodeHintType.CHARACTER_SET, characterSet);
		}
		//结果回调
		hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK,
				resultPointCallback);
	}

	Handler getHandler() {
		try {
			// 此方法会一直阻塞当前线程，直到计时器的值为0
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		return handler;
	}

	@Override
	public void run() {
		Looper.prepare();
		handler = new DecodeHandler(activity, hints);
		//前线程调用此方法，则计数减一
		handlerInitLatch.countDown();
		Looper.loop();
	}

}
