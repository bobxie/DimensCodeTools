**DimensCodeTools**
====================
一个可以支持生成二维码，条形码和扫描的库
SAMPLE:
-------
调用示例可以直接看app包下的SAMPLE.

图片录制的不是很清楚，将就下看哈
![enter image description here](https://github.com/ng2Kaming/DimensCodeTools/blob/master/art/dimens_sample.gif)

USAGE:
-------


 - DimensCodeTools 调用扫描的工具

开启扫描界面
 

    DimensCodeTools.startScan(this);
    // 开启扫描条形码
    // DimensCodeTools.startScanWithFeature(this, ScanParams.BARCODE_FEATURES);
    // 开启扫描条形码 指定所有参数
    // DimensCodeTools.startScan(this, ScanParams.BARCODE_FEATURES, 500, 250, 500,
	// 500);
    // 指定条行码扫描大小
    // DimensCodeTools.starScanWithBarSize(this, 500, 250);
    // 指定二维码扫描大小
    // DimensCodeTools.starScanWithQRSize(this, 500, 500);

解析返回

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String res = DimensCodeTools.scanForResult(requestCode, resultCode, data);
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

 - BarCodeFactory 产生条形码的工厂

    Bitmap bitmap = BarCodeFactory.createBarCode(this, content, 500,
					250, true, format);

 - QRCodeFactory 产生二维码的工厂

    Bitmap bmp = QRCodeFactory.createQRCode("Yo~ 切克闹", 200, 200, true);

 - ScanHelper 扫描（可以直接通过图片返回结果）
	Fun: Result scanningBarCode(Bitmap bitmap) 
	Fun: Result scanningQRCode(Bitmap bitmap)

 - CaptureActivity 扫描的Activity

More and more 看demo

THANKS:
-------
 - zxing

