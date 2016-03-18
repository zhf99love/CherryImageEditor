package com.cherry.mr.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.xici.newapp.app.XiciApp;


import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class FileUtil
{

	public static final String PHOTOTMP = "upload.jpg";
	
	public static String getBasePath(){
		String basepath = "";
		try {
			
			File appCacheDir = XiciApp.getContext().getExternalFilesDir(null);
			if(appCacheDir == null){
				appCacheDir = XiciApp.getContext().getFilesDir();
			}
			if(appCacheDir == null){
				String cacheDirPath = "/data/data/" + XiciApp.getContext().getPackageName() + "/files/";
				appCacheDir = new File(cacheDirPath);
			}
			if(appCacheDir!=null){
				basepath = appCacheDir.getAbsolutePath();
				if (!appCacheDir.exists()) {
					appCacheDir.mkdirs();
				}
			}
			
		} catch (Exception e) {
		}
		return basepath;
	}
	
	public static String getPhotoPath(){
		String photopath = getBasePath()+File.separator+"photo";
		File file = new File(photopath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return photopath;
	}
	
	public static String getFileCachePath(){
		String cachepath = getBasePath()+File.separator+"cache";
		File file = new File(cachepath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return cachepath;
	}
	
	public static String getTempPhotoPaht(){
		String photopath = getPhotoPath();
	
		return photopath+File.separator+PHOTOTMP; 
	}
	

	public static void copy(String filesrcpath, String filedespath)
			throws FileNotFoundException, IOException {
		File srcfile = new File(filesrcpath);
		File desfile = new File(filedespath);
		if (doesExisted(srcfile) && doesExisted(desfile)) {
			copy(new FileInputStream(srcfile), new FileOutputStream(desfile));
		}

	}

	public static void copy(InputStream minput, FileOutputStream moutput)
			throws IOException {
		byte[] buffer = new byte[1024];
		int length;

		while ((length = minput.read(buffer)) > 0) {
			moutput.write(buffer, 0, length);
		}

		moutput.flush();
		moutput.close();
		minput.close();
	}

	public static void saveObject(Object object, String filepath)
			throws IOException {

		FileOutputStream fileOutputStream = new FileOutputStream(filepath);
		saveObject(object, fileOutputStream);
	}

	// 保存对象
	public static void saveObject(Object object, OutputStream outputStream)
			throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(
				makeOutputBuffered(outputStream));
		out.writeObject(object);
		out.close();
	}

	public static Object loadObject(String filepath) throws IOException,
			ClassNotFoundException {

		if (doesExisted(new File(filepath))) {
			return loadObject(new FileInputStream(filepath));
		}
		return null;
	}

	public static Object loadObject(InputStream inputStream) throws IOException, ClassNotFoundException{
		ObjectInputStream in = new ObjectInputStream(
				makeInputBuffered(inputStream));
		Object object = null;
        if(in!=null){
    		object = in.readObject();
    		in.close();
        }
		return object;
	}

	public static boolean doesExisted(File file) {
		if (file != null && file.exists()) {
			return true;
		} else {
			return false;
		}

	}

	public static InputStream makeInputBuffered(InputStream inputStream) {
		if (inputStream instanceof BufferedInputStream) {
			return inputStream;
		} else {
			return new BufferedInputStream(inputStream);
		}

	}

	public static OutputStream makeOutputBuffered(OutputStream outputStream) {
		if (outputStream instanceof BufferedOutputStream) {
			return outputStream;
		} else {
			return new BufferedOutputStream(outputStream);
		}

	}

	public static boolean deleteFile(String filepath) {
		File file = new File(filepath);
		if (doesExisted(file))
			return file.delete();
		return false;

	}

	public static String getfilename() {
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyyMMddHHmmss");
		Date localDate = new Date();
		String filename = String.valueOf(localSimpleDateFormat
				.format(localDate));
		return filename;
	}

	public static boolean checksdfilepath(String path) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return false;
		}
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return true;
	}
	
	public static String read(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }


	/**
	 * 以下是通过系统图片返回的uri来获取图片地址的方法
	 *
	 * @param context
	 * @param uri Data返回的uri
	 * @return
	 */
	public static String getPathFromUri(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
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
				final String[] selectionArgs = new String[] {
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
									   String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
}
