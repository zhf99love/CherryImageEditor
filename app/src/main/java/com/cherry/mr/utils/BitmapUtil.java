package com.cherry.mr.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;

public class BitmapUtil {

	public static final int BITMAP_SIZE_HIGHT = 960;
	public static final int BITMAP_SIZE_MID = 960;
	public static final int BITMAP_SIZE_LOW = 960;

	public static final int BITMAP_QUALITY_HIGHT = 85;
	public static final int BITMAP_QUALITY_MID = 75;
	public static final int BITMAP_QUALITY_LOW = 65;

	public static void compress(File file,int size,int quality) throws Exception {
		// 压缩大小

			FileInputStream stream = new FileInputStream(file);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			// 获取这个图片的宽和高
			Bitmap bitmap = BitmapFactory.decodeStream(stream, null, opts);// 此时返回bm为空
			stream.close();
			opts.inJustDecodeBounds = false;
			// 计算缩放比
			int be = (int) (opts.outWidth / (float) size)+1;
			if (be <= 0)
				be = 1;
			opts.inSampleSize = be;
			// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
			stream = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(stream, null, opts);
			stream.close();

			String orientation = "";
			orientation = getExifOrientation(file.getAbsolutePath(), orientation);
			// Create a matrix for the manipulation
			Matrix matrix = new Matrix();
			// Resize the bitmap
			if ((orientation != null)
					&& (orientation.equals("90") || orientation.equals("180") || orientation
							.equals("270"))) {
				matrix.postRotate(Integer.valueOf(orientation));
			}

			Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);

			// 删除文件
			file.delete();
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream outstream = new FileOutputStream(file);
			if (rotated.compress(Bitmap.CompressFormat.JPEG, quality, outstream)) {
				outstream.flush();
				outstream.close();
			}
			bitmap.recycle(); // free up memory
	}

//	/**
//	 * 发话题的时候使用
//	 * @param item
//	 * @throws Exception
//	 */
//	public static void compressHuaTi(AddMediaItem item) {
//
//		if (!TextUtils.isEmpty(item.path)) {
//			return;
//		}
//
//		InputStream myInput = null;
//		FileOutputStream myOutput = null;
//		String mfilename = "";
//		try {
//
//			myInput = new FileInputStream(item.originalUri);
//			mfilename = FileUtil.getfilename() + ".jpg";
//			FileUtil.checksdfilepath(FileUtil.getPhotoPath());
//			File file = new File(FileUtil.getPhotoPath(), mfilename);
//			myOutput = new FileOutputStream(file);
//			// copy
//			FileUtil.copy(myInput, myOutput);
//			// 压缩大小
//			FileInputStream stream = new FileInputStream(file);
//			BitmapFactory.Options opts = new BitmapFactory.Options();
//			opts.inJustDecodeBounds = true;
//			Bitmap bitmap = BitmapFactory.decodeStream(stream, null, opts);// 此时返回bm为空
//			// 获取这个图片的宽和高
//			stream.close();
//			opts.inJustDecodeBounds = false;
//			// 计算缩放比
//			int be1 = (int) (opts.outWidth / (float) 640);
//			int be2 = (int) (opts.outHeight / (float) 480);
//
//			opts.inSampleSize = be1 > be2 ? be2 : be1;
//			if (opts.inSampleSize < 1)
//				opts.inSampleSize = 1;
//			// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
//			stream = new FileInputStream(file);
//			bitmap = BitmapFactory.decodeStream(stream, null, opts);
//			stream.close();
//
//			String orientation = "";
//			orientation = getExifOrientation(file.getAbsolutePath(), orientation);
//			// Create a matrix for the manipulation
//			Matrix matrix = new Matrix();
//			// Resize the bitmap
//			if ((orientation != null)
//					&& (orientation.equals("90") || orientation.equals("180") || orientation
//					.equals("270"))) {
//				matrix.postRotate(Integer.valueOf(orientation));
//			}
//
//			int width = bitmap.getWidth();
//			int height = bitmap.getHeight();
//
//			Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, width,
//					height, matrix, true);
//
//			// 删除文件
//			file.delete();
//			if (!file.exists()) {
//				file.createNewFile();
//			}
//			FileOutputStream outstream = new FileOutputStream(file);
//			if (rotated.compress(Bitmap.CompressFormat.JPEG, BITMAP_QUALITY_HIGHT, outstream)) {
//				outstream.flush();
//				outstream.close();
//			}
//			bitmap.recycle(); // free up memory
//
//			myOutput.flush();
//			item.path = file.getAbsolutePath();
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	public  static String getExifOrientation(String path, String orientation) {
		ExifInterface exif;
		try {
			exif = new ExifInterface(path);
		} catch (IOException e) {
			return orientation;
		}
		String exifOrientation = exif
				.getAttribute(ExifInterface.TAG_ORIENTATION);
		if (exifOrientation != null) {
			if (exifOrientation.equals("1")) {
				orientation = "0";
			} else if (exifOrientation.equals("3")) {
				orientation = "180";
			} else if (exifOrientation.equals("6")) {
				orientation = "90";
			} else if (exifOrientation.equals("8")) {
				orientation = "270";
			}
		} else {
			orientation = "0";
		}
		return orientation;
	}

//	public static ThumbnailSize getSize(String path){
//
//		ThumbnailSize size = new ThumbnailSize();
//
//		try {
//
//			BitmapFactory.Options opts = new BitmapFactory.Options();
//			opts.inJustDecodeBounds = true;
//
//			Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
//
//			size.height = opts.outHeight;
//			size.width = opts.outWidth;
//
//		} catch (Exception e) {}
//
//		return size;
//
//	}
//
//	/**
//	 * 压缩图片以便上传
//	 *
//	 * @param item
//	 */
//	public synchronized static void compressImage(AddMediaItem item) {
//		int bitmap_size;
//		int bitmap_quality;
//		switch (SettingUtil.getupdatePhotoQuality()) {
//			case 0:
//				bitmap_size = BitmapUtil.BITMAP_SIZE_LOW;
//				bitmap_quality = BitmapUtil.BITMAP_QUALITY_LOW;
//				break;
//			case 1:
//				bitmap_size = BitmapUtil.BITMAP_SIZE_MID;
//				bitmap_quality = BitmapUtil.BITMAP_QUALITY_MID;
//				break;
//			case 2:
//				bitmap_size = BitmapUtil.BITMAP_SIZE_HIGHT;
//				bitmap_quality = BitmapUtil.BITMAP_QUALITY_HIGHT;
//				break;
//			default:
//				bitmap_size = BitmapUtil.BITMAP_SIZE_MID;
//				bitmap_quality = BitmapUtil.BITMAP_QUALITY_MID;
//				break;
//		}
//
//		if (!TextUtils.isEmpty(item.path)) {
//			return;
//		}
//		InputStream myInput = null;
//		FileOutputStream myOutput = null;
//		String mfilename = "";
//		try {
//
//			myInput = new FileInputStream(item.originalUri);
//			mfilename = FileUtil.getfilename() + ".jpg";
//			FileUtil.checksdfilepath(FileUtil.getPhotoPath());
//			File file = new File(FileUtil.getPhotoPath(), mfilename);
//			myOutput = new FileOutputStream(file);
//			// copy
//			FileUtil.copy(myInput, myOutput);
//			// compress
//			BitmapUtil.compress(file, bitmap_size, bitmap_quality);
//
//			myOutput.flush();
//			item.path = file.getAbsolutePath();
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
////	public static String compressImage(Activity activity, Uri uri) {
////		try {
////			String filePath = FileUtil.PHOTO + File.separator + FileUtil.getfilename() + ".jpg";
////			InputStream myInput = activity.getContentResolver().openInputStream(uri);
////
////			File file = new File(filePath);
////			FileOutputStream myOutput = new FileOutputStream(file);
////
////			FileUtil.copy(myInput, myOutput);
////			//compress
////			BitmapUtil.compress2(activity, filePath);
////			return filePath;
////		} catch (IOException e) {
////			Log.e("BitmapUtil", "saveimage fail", e);
////		}
////		return null;
////	}
}
