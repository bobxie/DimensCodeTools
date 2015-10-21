package com.yo.libs.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.yo.libs.app.ScanParams;
import com.yo.libs.dimenscode.ScanHelper;
import com.yo.libs.utils.AssetsUtils;
import com.yo.libs.utils.FileUtils;
import com.yo.libs.utils.PixDpUtils;
import com.zxing.android.MessageIDs;
import com.zxing.android.camera.CameraManager;
import com.zxing.android.decoding.CaptureActivityHandler;
import com.zxing.android.decoding.InactivityTimer;
import com.zxing.android.view.ViewfinderView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Vector;

/**
 * Modify the original author Codes,thanks. 扫描二维码，条码的界面
 * 
 * @author Kaming
 *
 */
public class CaptureActivity extends Activity implements Callback {
	/**
	 * 文本选中的颜色
	 */
	private static final int TEXT_COLOR_PRESSED = Color.parseColor("#4BCAF7");
	/**
	 * 默认扫描
	 */
	private static final int DEFAULT_SCAN = -1;
	/**
	 * 默认二维码扫描框的宽度
	 */
	private static final int DEFAULT_QRCODE_WIDTH = 500;
	/**
	 * 默认二维码扫描框的高度
	 */
	private static final int DEFAULT_QRCODE_HEIGHT = 500;
	/**
	 * 默认条形码扫描框的宽度
	 */
	private static final int DEFAULT_BARCODE_WIDTH = 500;
	/**
	 * 默认条形码扫描框的高度
	 */
	private static final int DEFAULT_BARCODE_HEIGHT = 250;
	/**
	 * 最小二维码扫描框的宽度
	 */
	private static final int MIN_QRCODE_WIDTH = 400;
	/**
	 * 最小二维码扫描框的高度
	 */
	private static final int MIN_QRCODE_HEIGHT = 400;
	/**
	 * 最小条形码扫描框的宽度
	 */
	private static final int MIN_BARCODE_WIDTH = 400;
	/**
	 * 最小条形码扫描框的高度
	 */
	private static final int MIN_BARODE_HEIGHT = 200;
	/**
	 * 处理 捕获相机消息
	 */
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private TextView mGalleryText, mQRCodeText, mBarCodeText, mScanInfo;
	private ImageView mQRCodeIcon, mBarCodeIcon, mGalleryIcon;
	private ImageView mSwitchLight, mBackHome;
	private RelativeLayout mTitleBar;
	private SurfaceView surfaceView;
	private LinearLayout mQRCodeLinear, mBarCodeLinear, mGalleryLinear;
	private ViewGroup mContent;
	private boolean hasSurface;
	/**
	 * 条形码编码集合
	 */
	private Vector<BarcodeFormat> decodeFormats;
	/**
	 * 字符编码
	 */
	private String characterSet;
	/**
	 * 限时关闭Activity
	 */
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	/**
	 * 是否播放声音(Bi...)
	 */
	private boolean playBeep;
	// private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	/**
	 * 相机管理类
	 */
	private CameraManager cameraManager;
	/**
	 * 图片路径
	 */
	private String mPhotoPath;
	/**
	 * 功能,即二维码扫描 或者 条形码
	 */
	private int mFeatures;
	/**
	 * 二维码宽高
	 */
	private int mQRCodeWidth, mQRCodeHeight;
	/**
	 * 条形码宽高
	 */
	private int mBarCodeWidth, mBarCodeHeight;
	/**
	 * 解析的图片
	 */
	private Bitmap mScanBitmap;
	/**
	 * 闪光灯标记
	 */
	private boolean isFlash;
	/**
	 * 图片资源
	 */
	private Drawable mBackImg, mBarcodeNormalImg, mBarcodePressedImg,
			mQRCodeNormalImg, mQRCodePressedImg, mFlashNormalImg,
			mFlashPressedImg, mGalleryNormalImg, mGalleryPressedImg;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 初始化图片
		initImages();
		// 初始化布局
		initContentView();
		setContentView(mContent);
		// 设置监听
		initView();
		// 接受传递过来的值
		mFeatures = getIntent().getIntExtra(ScanParams.WHICH, DEFAULT_SCAN);
		mQRCodeWidth = getIntent().getIntExtra(ScanParams.QRCODE_WIDTH_PIX,
				DEFAULT_QRCODE_WIDTH);
		mQRCodeHeight = getIntent().getIntExtra(ScanParams.QRCODE_HEIGHT_PIX,
				DEFAULT_QRCODE_HEIGHT);
		mBarCodeWidth = getIntent().getIntExtra(ScanParams.BARCODE_WIDTH_PIX,
				DEFAULT_BARCODE_WIDTH);
		mBarCodeHeight = getIntent().getIntExtra(ScanParams.BARCODE_HEIGHT_PIX,
				DEFAULT_BARCODE_HEIGHT);
		//检查参数
		checkParams();
		// 屏幕常亮
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	/**
	 * 检查参数
	 */
	private void checkParams() {
		if (mQRCodeWidth < MIN_QRCODE_WIDTH 
				|| mQRCodeWidth > getScreenPix()[0]) {
			mQRCodeWidth = DEFAULT_QRCODE_WIDTH;
		}
		if (mQRCodeHeight < MIN_QRCODE_HEIGHT
				|| mQRCodeHeight > getScreenPix()[1] / 2) {
			mQRCodeHeight = DEFAULT_QRCODE_HEIGHT;
		}
		if (mBarCodeWidth < MIN_BARCODE_WIDTH
				|| mBarCodeWidth > getScreenPix()[0]) {
			mBarCodeWidth = DEFAULT_BARCODE_WIDTH;
		}
		if (mBarCodeHeight < MIN_BARODE_HEIGHT
				|| mBarCodeHeight > getScreenPix()[1] / 2) {
			mBarCodeHeight = DEFAULT_BARCODE_HEIGHT;
		}

	}

	/**
	 * 获取屏幕的像素
	 * 
	 * @return 宽和高的数组
	 */
	private int[] getScreenPix() {
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		int[] screenPix = new int[2];
		screenPix[0] = outMetrics.widthPixels;
		screenPix[1] = outMetrics.heightPixels;
		return screenPix;
	}

	/**
	 * 初始化图片
	 */
	private void initImages() {
		mBackImg = AssetsUtils.assets2Drawable(this, "back.png");
		mBarcodeNormalImg = AssetsUtils.assets2Drawable(this,
				"barcode_normal.png");
		mBarcodePressedImg = AssetsUtils.assets2Drawable(this,
				"barcode_pressed.png");
		mQRCodeNormalImg = AssetsUtils.assets2Drawable(this,
				"qrcode_normal.png");
		mQRCodePressedImg = AssetsUtils.assets2Drawable(this,
				"qrcode_pressed.png");
		mGalleryNormalImg = AssetsUtils.assets2Drawable(this,
				"gallery_normal.png");
		mGalleryPressedImg = AssetsUtils.assets2Drawable(this,
				"gallery_pressed.png");
		mFlashNormalImg = AssetsUtils.assets2Drawable(this,
				"flash_light_normal.png");
		mFlashPressedImg = AssetsUtils.assets2Drawable(this,
				"falsh_ligh_pressed.png");
	}

	/**
	 * 根据传入的features，选择 进行二维码or条码 进行扫描
	 * 
	 * @param features
	 *            值可能为 Constant.QRCODE_FEATURES(二维码) or
	 *            Constant.BARCODE_FEATURES(条码)
	 */
	private void chooseWhichFeatures(int features) {
		switch (features) {
		case ScanParams.QRCODE_FEATURES:
			chooseQRCodeScan();
			break;
		case ScanParams.BARCODE_FEATURES:
			chooseBarCodeScan();
			break;
		case DEFAULT_SCAN:
			chooseQRCodeScan();
			break;
		default:
			break;
		}
	}

	/**
	 * 设置监听
	 */
	private void initView() {
		/**
		 * 二维码扫描
		 */
		mQRCodeLinear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				chooseQRCodeScan();
			}
		});
		/**
		 * 条形码扫描
		 */
		mBarCodeLinear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				chooseBarCodeScan();
			}
		});
		/**
		 * 相册
		 */
		mGalleryLinear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				chooseGallery();
			}
		});
		/**
		 * 回退
		 */
		mBackHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				backHome();
			}
		});
		/**
		 * 闪光灯开关
		 */
		mSwitchLight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switchLight();
			}
		});
	}

	/**
	 * 初始化布局
	 */
	@SuppressWarnings("deprecation")
	private void initContentView() {
		// root
		mContent = new RelativeLayout(this);
		mContent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		// 添加Surface控件
		surfaceView = new SurfaceView(this);
		surfaceView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		mContent.addView(surfaceView);
		// 添加扫描框
		viewfinderView = new ViewfinderView(this);
		viewfinderView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mContent.addView(viewfinderView);
		// 添加标题栏
		mTitleBar = new RelativeLayout(this);
		RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, PixDpUtils.dip2px(this, 70.0f));
		titleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		int titleBtnWidth = PixDpUtils.dip2px(this, 48.0f);
		int titleBtnHeight = PixDpUtils.dip2px(this, 48.0f);
		// 添加返回键
		mBackHome = new ImageView(this);
		mBackHome.setBackgroundDrawable(mBackImg);
		RelativeLayout.LayoutParams backParams = new RelativeLayout.LayoutParams(
				titleBtnWidth, titleBtnHeight);
		backParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
				RelativeLayout.TRUE);
		backParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		backParams.leftMargin = PixDpUtils.dip2px(this, 8.0f);
		mTitleBar.addView(mBackHome, backParams);
		// 添加闪光灯按钮
		mSwitchLight = new ImageView(this);
		mSwitchLight.setBackgroundDrawable(mFlashNormalImg);
		RelativeLayout.LayoutParams lightParams = new RelativeLayout.LayoutParams(
				titleBtnWidth, titleBtnHeight);
		lightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
				RelativeLayout.TRUE);
		lightParams
				.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		lightParams.rightMargin = PixDpUtils.dip2px(this, 8.0f);
		mTitleBar.addView(mSwitchLight, lightParams);

		mContent.addView(mTitleBar, titleParams);
		// 添加扫描提示信息
		mScanInfo = new TextView(this);
		mScanInfo.setText("请将二维码置于取景框内扫描");
		mScanInfo.setTextSize(15.0f);
		RelativeLayout.LayoutParams infoParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		infoParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
				RelativeLayout.TRUE);
		infoParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
				RelativeLayout.TRUE);
		infoParams.bottomMargin = PixDpUtils.dip2px(this, 100.0f);
		mContent.addView(mScanInfo, infoParams);
		// 底部三个按钮
		LinearLayout bottomLinear = new LinearLayout(this);
		bottomLinear.setBackgroundColor(0xAA0A0A08);
		bottomLinear.setOrientation(LinearLayout.HORIZONTAL);
		bottomLinear.setGravity(Gravity.CENTER_VERTICAL);
		bottomLinear.setBaselineAligned(false);
		RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, PixDpUtils.dip2px(this, 90.0f));
		bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
				RelativeLayout.TRUE);

		int iconWidth = PixDpUtils.dip2px(this, 36.0f);
		int iconHeight = PixDpUtils.dip2px(this, 36.0f);

		// 二维码
		mQRCodeLinear = createLinear();
		LinearLayout.LayoutParams qrParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
		// 二维码图标
		mQRCodeIcon = new ImageView(this);
		mQRCodeIcon.setBackgroundDrawable(mQRCodeNormalImg);
		mQRCodeLinear.addView(mQRCodeIcon, new LayoutParams(iconWidth,
				iconHeight));
		// 二维码文字
		mQRCodeText = createTextView("二维码");
		LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textParams.topMargin = PixDpUtils.dip2px(this, 4.0f);
		mQRCodeLinear.addView(mQRCodeText, textParams);
		bottomLinear.addView(mQRCodeLinear, qrParams);

		// 条形码
		mBarCodeLinear = createLinear();
		LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
		// 条形码图标
		mBarCodeIcon = new ImageView(this);
		mBarCodeIcon.setBackgroundDrawable(mBarcodeNormalImg);
		mBarCodeLinear.addView(mBarCodeIcon, new LayoutParams(iconWidth,
				iconHeight));
		// 条形码文字
		mBarCodeText = createTextView("条形码");
		mBarCodeLinear.addView(mBarCodeText, textParams);
		bottomLinear.addView(mBarCodeLinear, barParams);

		// 相册
		mGalleryLinear = createLinear();
		LinearLayout.LayoutParams galleryParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
		// 条形码图标
		mGalleryIcon = new ImageView(this);
		mGalleryIcon.setBackgroundDrawable(mGalleryNormalImg);
		mGalleryLinear.addView(mGalleryIcon,
				new LayoutParams(PixDpUtils.dip2px(this, 40.0f), iconHeight));
		// 条形码文字
		mGalleryText = createTextView("相册");
		mGalleryLinear.addView(mGalleryText, textParams);
		bottomLinear.addView(mGalleryLinear, galleryParams);

		// 添加底部
		mContent.addView(bottomLinear, bottomParams);

	}

	/**
	 * 创建 TextView
	 * 
	 * @param content
	 *            内容
	 * @return
	 */
	private TextView createTextView(String content) {
		TextView tv = new TextView(this);
		tv.setText(content);
		tv.setTextSize(14.0f);
		tv.setTextColor(Color.WHITE);
		return tv;
	}

	/**
	 * 创建Linear
	 * 
	 * @return
	 */
	private LinearLayout createLinear() {
		LinearLayout linear = new LinearLayout(this);
		linear.setOrientation(LinearLayout.VERTICAL);
		linear.setGravity(Gravity.CENTER_HORIZONTAL);
		return linear;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		cameraManager = new CameraManager(getApplication());
		// 初始化扫描框
		chooseWhichFeatures(mFeatures);

		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// 初始化相机
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		// 初始化BiBi..声音
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			// 关闭相机预览
			handler.quitSynchronously();
			handler = null;
		}
		// 关闭闪光灯
		cameraManager.offFlashLight();
		// 释放相机
		cameraManager.closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * 初始化 打开相机 启动线程解析
	 * 
	 * @param surfaceHolder
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			cameraManager.openDriver(surfaceHolder);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		// 初始化 Handler 并开启线程解析
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	/**
	 * 当SurFace 创建完成后回调此函数
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	/**
	 * 获取 相机管理
	 * 
	 * @return CameraManger
	 */
	public CameraManager getCameraManager() {
		return cameraManager;
	}

	/**
	 * 获取 扫描框
	 * 
	 * @return
	 */
	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	/**
	 * 获取 消息处理
	 * 
	 * @return
	 */
	public Handler getHandler() {
		return handler;
	}

	/**
	 * 重绘
	 */
	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	/**
	 * 处理解析
	 * 
	 * @param obj
	 * @param barcode
	 */
	public void handleDecode(Result obj, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		showResult(obj, barcode);
	}

	/**
	 * 处理中文乱码问题
	 * 
	 * @param text
	 * @return
	 */
	private String recode(String text) {
		String format = "";
		final boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
				.canEncode(text);
		try {
			if (ISO) {
				// 编码转成 GB2312
				format = new String(text.getBytes("ISO-8859-1"), "GB2312");
			} else {
				format = text;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return format;
	}

	/**
	 * 回调 onActivityResult
	 * 
	 * @param rawResult
	 *            扫描结果
	 * @param barcode
	 */
	private void showResult(final Result rawResult, Bitmap barcode) {
		String result = recode(rawResult.getText());
		Intent intent = new Intent();
		intent.putExtra(ScanParams.RESULT, result);
		setResult(ScanParams.RESULT_OK, intent);
		finish();
	}

	/**
	 * 重置相机预览
	 * 
	 * @param delayMS
	 *            延迟重置的时间，mm为单位
	 */
	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(MessageIDs.restart_preview, delayMS);
		}
	}

	/**
	 * 初始化 声音
	 */
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			try {
				AssetFileDescriptor fileDescriptor = getAssets().openFd(
						"qrbeep.ogg");
				this.mediaPlayer.setDataSource(
						fileDescriptor.getFileDescriptor(),
						fileDescriptor.getStartOffset(),
						fileDescriptor.getLength());
				this.mediaPlayer.setVolume(0.1F, 0.1F);
				this.mediaPlayer.prepare();
			} catch (IOException e) {
				this.mediaPlayer = null;
			}
		}
	}

	/**
	 * 震动时长
	 */
	private static final long VIBRATE_DURATION = 200L;

	/**
	 * 启动相册 Tag
	 */
	private static final int SELECT_PICTURE = 0;

	/**
	 * 播放声音并震动
	 */
	private void playBeepSoundAndVibrate() {
		// 播放声音
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		// 震动
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean res = super.onKeyDown(keyCode, event);
		try {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				setResult(RESULT_CANCELED);
				finish();
				res = true;
			} else if (keyCode == KeyEvent.KEYCODE_FOCUS
					|| keyCode == KeyEvent.KEYCODE_CAMERA) {
				res = true;
			}
		} catch (Exception e) {
			Toast.makeText(this, "异常错误", Toast.LENGTH_SHORT).show();
			Log.e("Key Down ", "Key Down Error ");
		}
		return res;
	}

	/**
	 * 退出
	 * 
	 */
	private void backHome() {
		finish();
	}

	/**
	 * 闪光灯开关
	 * 
	 */
	@SuppressWarnings("deprecation")
	private void switchLight() {
		cameraManager.switchFlashLight();
		if (!isFlash) {
			mSwitchLight.setBackgroundDrawable(mFlashPressedImg);
			isFlash = true;
		} else {
			mSwitchLight.setBackgroundDrawable(mFlashNormalImg);
			isFlash = false;
		}
	}

	/**
	 * 选择相册
	 */
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void chooseGallery() {
		// 重置 底部所有的view
		resetAllView();
		mGalleryText.setTextColor(TEXT_COLOR_PRESSED);
		mGalleryIcon.setBackgroundDrawable(mGalleryPressedImg);
		Intent intent = new Intent();
		if (Build.VERSION.SDK_INT < 19) {
			intent.setAction(Intent.ACTION_GET_CONTENT);
		} else {
			intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
		}
		intent.setType("image/*");
		startActivityForResult(Intent.createChooser(intent, "选择图片"),
				SELECT_PICTURE);
	}

	/**
	 * 选择扫描 条形码
	 */
	@SuppressWarnings("deprecation")
	private void chooseBarCodeScan() {
		resetAllView();
		mScanInfo.setText("请将条码置于取景框内扫描");
		mBarCodeText.setTextColor(TEXT_COLOR_PRESSED);
		mBarCodeIcon.setBackgroundDrawable(mBarcodePressedImg);
		// 设置扫描框的大小
		cameraManager.setManualFramingRect(mBarCodeWidth, mBarCodeHeight);
		// 重新绘制 扫描线
		viewfinderView.reDraw();
		viewfinderView.setCameraManager(cameraManager);
		drawViewfinder();
	}

	/**
	 * 选择扫描
	 */
	@SuppressWarnings("deprecation")
	private void chooseQRCodeScan() {
		resetAllView();
		mScanInfo.setText("请将二维码置于取景框内扫描");
		mQRCodeText.setTextColor(TEXT_COLOR_PRESSED);
		mQRCodeIcon.setBackgroundDrawable(mQRCodePressedImg);
		;
		// 设置 扫描框的大小
		cameraManager.setManualFramingRect(mQRCodeWidth, mQRCodeHeight);
		// 重新绘制 扫描线
		viewfinderView.reDraw();
		viewfinderView.setCameraManager(cameraManager);
		drawViewfinder();
	}

	/**
	 * 还原化底部三个view
	 */
	@SuppressWarnings("deprecation")
	private void resetAllView() {
		mBarCodeText.setTextColor(Color.WHITE);
		mQRCodeText.setTextColor(Color.WHITE);
		mGalleryText.setTextColor(Color.WHITE);

		mBarCodeIcon.setBackgroundDrawable(mBarcodeNormalImg);
		mQRCodeIcon.setBackgroundDrawable(mQRCodeNormalImg);
		mGalleryIcon.setBackgroundDrawable(mGalleryNormalImg);
	}

	/**
	 * 得到选中图片 并解析
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(data.getData(), proj,
					null, null, null);
			if (cursor.moveToFirst()) {
				int columnIndex = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				mPhotoPath = cursor.getString(columnIndex);
				if (TextUtils.isEmpty(mPhotoPath)) {
					mPhotoPath = FileUtils.getPath(this, data.getData());
					Log.i("Photo path", "the path : " + mPhotoPath);
				}
				Log.i("Photo path", "the path : " + mPhotoPath);
			}
			cursor.close();
			// 解析
			startScanning();
		}
	}

	/**
	 * 开启线程 解析图片
	 */
	private void startScanning() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Result result = scanningImage(mPhotoPath);
				if (result == null) {
					// 子线程 需要Looper才能更新主视图
					Looper.prepare();
					Toast.makeText(getApplicationContext(), "图片格式有误",
							Toast.LENGTH_LONG).show();
					Looper.loop();
				} else {
					Log.i("Result", "result : " + result.toString());
					// 显示结果
					showResult(result, null);
				}
			}
		}).start();
	}

	/**
	 * 解析二维码图片 or 条形码图片
	 * 
	 * @param path
	 * @return
	 */
	protected Result scanningImage(String path) {
		Result result;
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		BitmapFactory.Options options = getSampleOptions(path);
		mScanBitmap = BitmapFactory.decodeFile(path, options);
		Result resultBarCode = null;
		try {
			// 扫描条形码
			resultBarCode = ScanHelper.scanningBarCode(mScanBitmap);
			Log.i("Content", "content : " + resultBarCode.getText());
		} catch (NotFoundException e1) {
			e1.printStackTrace();
			Log.e("NotFoundException", "The error : " + e1);
		}
		Result resultQRCode = null;
		try {
			// 扫描二维码
			resultQRCode = ScanHelper.scanningQRCode(mScanBitmap);
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		// 返回不为空的结果
		if (resultBarCode != null) {
			result = resultBarCode;
		} else if (resultQRCode != null) {
			result = resultQRCode;
		} else {
			result = null;
		}
		return result;
	}

	/**
	 * 获得缩小比例的 options
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 */
	private BitmapFactory.Options getSampleOptions(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		mScanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false;
		int sampleSize = (int) (options.outHeight / (float) 200);
		if (sampleSize <= 0) {
			sampleSize = 1;
		}
		options.inSampleSize = sampleSize;
		return options;
	}

}