package com.example.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class F_GetBitmap {

	public static Bitmap bitmap = null;
	public static String filePath;
	public static String picFilePath;
	public static File file;
	public static boolean isEmpty(String picName) {
		// 判断SD卡是否存在
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			filePath = Environment.getExternalStorageDirectory()
					+ "/download_test";
			file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			picFilePath = Environment.getExternalStorageDirectory().toString()
					+ "/download_test" + "/" + picName;
			file = new File(picFilePath);
			if (file.exists()) {
				return false;
			} else {
				return true;
			}
		}
		return true;
	}

	public static Bitmap getSDBitmap(String picName) {
		picFilePath = Environment.getExternalStorageDirectory()+ "/download_test" + "/" + picName;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = false; /* 不进行图片抖动处理 */
		options.inPreferredConfig = null; /* 设置让解码器以最佳方式解码 */
		options.inSampleSize = 1; /* 图片长宽方向缩小倍数 */
		Bitmap bit = BitmapFactory.decodeFile(picFilePath, options);
		return bit;
	}

	public static void setInSDBitmap(byte[] bb, String picName) {
		filePath = Environment.getExternalStorageDirectory()+ "/download_test/";
		InputStream input = null;
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		input = new ByteArrayInputStream(bb);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(
				input, null, options));
		bitmap = (Bitmap) softRef.get();
		if (bb != null) {
			bb = null;
		}try {
			if (input != null) {
				input.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		file = new File(filePath);
		if (!file.exists()) {
			file.mkdir();
		}
		FileOutputStream fos = null;
		file = new File(filePath + "/" + picName);
		try {
			fos = new FileOutputStream(file); // 读到SD卡中
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			bitmap.recycle();
			System.gc();
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
