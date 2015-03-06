/**
 *   Copyright(c) 2013 DuoKan TV Group
 *    
 *   DeviceIdentifierCache.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2013-4-2
 */

package com.miui.video.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.miui.video.DKApp;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;

/**
 *@author xuanmingliu
 *
 */

public class DeviceIDCache {
	
	private static final String TAG = DeviceIDCache.class.getSimpleName();

	private static String sDeviceID;
	
	private static final String DIR_NAME = "di";
	private static final String CACHE_FILE_NAME = "di.dat";
	
	private static void prepareFiles() {
		String filesPath = DKApp.getSingleton(AppEnv.class).getInternalFilesDir();
		String dirName = filesPath + File.separator + DIR_NAME;
		String fileName = dirName + File.separator + CACHE_FILE_NAME;
		try {
			File dir = new File(dirName);
			if(!dir.exists()){
				dir.mkdir();
			}
			File cacheFile = new File(fileName);
			if(!cacheFile.exists()){
				cacheFile.createNewFile();
			}
		}  catch (IOException e) {
			DKLog.e(TAG, "" + e);
		}
	}
	
	public static String getDeviceID() {
		if( Util.isEmpty(sDeviceID)) {
			readCacheID();
		}
		return sDeviceID;
	}
	
	public static void setDeviceID(String Ime) {
		sDeviceID = Ime;
		writeCacheID();
	}
	
	private static void readCacheID() {
		String filePath = getCachePath();
		File cacheFile = new File(filePath);
		if(!cacheFile.exists())
			return;
		FileInputStream fis = null;
		ObjectInputStream ips = null;
		try {
			fis = new FileInputStream(cacheFile);
			ips = new ObjectInputStream(fis);
			Object cacheObj = ips.readObject();
			if( cacheObj != null) {
				sDeviceID = cacheObj.toString();
			}
		}catch (Exception e) {
			DKLog.e(TAG, "" + e);
		}finally{
			if(ips != null){
				try {
					ips.close();
				} catch (IOException e) {
				}
			}
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	private static void writeCacheID() {
		if(Util.isEmpty(sDeviceID))
			return;
		prepareFiles();
		String filePath = getCachePath();
		File cacheFile = new File(filePath);
		FileOutputStream fos = null;
		ObjectOutputStream ops = null;
		try {
			fos = new FileOutputStream(cacheFile);
			ops = new ObjectOutputStream(fos);
			ops.writeObject(sDeviceID);
			ops.flush();
		} catch (IOException e) {
			DKLog.e(TAG, "" + e);
		}finally{
			if(ops != null){
				try {
					ops.close();
				} catch (IOException e) {
				}
			}
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	private static String getCachePath() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(DKApp.getSingleton(AppEnv.class).getInternalFilesDir());
		strBuilder.append(File.separator);
		strBuilder.append(DIR_NAME);
		strBuilder.append(File.separator);
		strBuilder.append(CACHE_FILE_NAME);
		return strBuilder.toString();
	}
}


