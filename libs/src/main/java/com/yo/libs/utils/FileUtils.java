package com.yo.libs.utils;

import java.io.File;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * 处理文件 工具类
 * 
 * @author Kaming
 *
 */
public class FileUtils {

	/**
	 * 是否是Unicode编码
	 * 
	 * @param chineseStr
	 * @return
	 */
	public static final boolean isChineseCharacter(String chineseStr) {
		char[] charArr = chineseStr.toCharArray();
		for (int i = 0; i < charArr.length; i++) {
			if ((charArr[i] >= '\u0000' && charArr[i] < '\uFFFD')
					|| (charArr[i] > '\uFFFD' && charArr[i] < '\uFFFF')) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 根据传入的uri获取文件路径
	 * 
	 * @param context
	 *            The context
	 * @param uri
	 *            The uri to query
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getPath(final Context context, final Uri uri) {
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		String res = null;
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					res = Environment.getExternalStorageDirectory()
							+ File.separator + split[1];
				}
			} else if (isDownloadsDocument(uri)) {
				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));
				res = getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };
				res = getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		} else if ("content".equalsIgnoreCase(uri.getScheme())) {
			res = getDataColumn(context, uri, null, null);
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			res = uri.getPath();
		}
		return res;
	}

	/**
	 * 获取该uri的值，对于MediaStore和基于文件content-provider 有效
	 * 
	 * @param context
	 *            The context
	 * @param uri
	 *            The uri to query
	 * @param selection
	 *            Filter used in the query
	 * @param selectionArgs
	 *            Selection arguments used in the query
	 * @return The value of the _data column, which is typically a file path
	 */
	private static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String res = null;
		final String column = "_data";
		final String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int columnIndex = cursor.getColumnIndexOrThrow(column);
				res =  cursor.getString(columnIndex);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return res;
	}

	/**
	 * 检查uri的authority是否是DownloadsProvider
	 * 
	 * @param uri
	 *            The Uri to check
	 * @return
	 */
	private static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * 检查uri的authority是否是ExternalStorageProvider
	 * 
	 * @param uri
	 *            The Uri to check
	 * @return
	 */
	private static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * 检查uri的authority是否是MediaProvider
	 * 
	 * @param uri
	 *            The Uri to check
	 * @return
	 */
	private static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

}
