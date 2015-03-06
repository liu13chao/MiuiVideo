/**
 *   Copyright(c) 2013 DuoKan TV Group
 *    
 *   LocaMediaHideInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2013-5-9
 */

package com.miui.video.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

import com.miui.video.DKApp;
import com.miui.video.type.LocalMediaHideItemInfo;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;

/**
 *@author xuanmingliu
 *
 */

public class LocalMediaHideInfo {
	public static final String TAG = LocalMediaHideInfo.class.getSimpleName();
	
	private static LocalMediaHideInfo sLocaMediaHideInfo = null;
	private static final String HIDEINFO_DIRNAME = ".hide";
	private static final String HIDEINFO_FILENAME = "info.dat";
	private Hashtable<String, LocalMediaHideItemInfo> localMediaHideItemInfoHashtable;
	
	private LocalMediaHideInfo() {
		localMediaHideItemInfoHashtable = new Hashtable<String, LocalMediaHideItemInfo>();
		prepareFiles();
		readHideInfo();
	}
	
	public static synchronized LocalMediaHideInfo getInstance() {
		if( sLocaMediaHideInfo == null)
			sLocaMediaHideInfo = new LocalMediaHideInfo();
		return sLocaMediaHideInfo;
	}
	
	public void addLocalMediaHideInfo(LocalMediaHideItemInfo localMediaHideItemInfo) {
		if( localMediaHideItemInfo == null)
			return;
		
		localMediaHideItemInfoHashtable.put(localMediaHideItemInfo.bucketName, localMediaHideItemInfo);
	}
	
	public void removeLocalMediaHideInfo(String bucketName) {
		if( Util.isEmpty(bucketName))
			return;
		
		localMediaHideItemInfoHashtable.remove(bucketName);
	}
	
	public int getLocalMediaHideInfoCount() {
		return localMediaHideItemInfoHashtable.size();
	}
	
	public void saveLocalMediaHideInfo() {
		writeHideInfo();
	}
	
	public boolean isLocalMediaHide(String bucketName) {
		if(bucketName != null && localMediaHideItemInfoHashtable.containsKey(bucketName))
			return true;
		
		return false;
	}
	
	private void prepareFiles() {
		String filesPath = DKApp.getSingleton(AppEnv.class).getInternalFilesDir();
		String hideDirPath = filesPath + File.separator + HIDEINFO_DIRNAME;
		String hideInfoCachePath = hideDirPath + File.separator + HIDEINFO_FILENAME;
		File hideDir = new File(hideDirPath);
		
		try {
			if(!hideDir.exists()) {
				hideDir.mkdir();
				File cacheFile = new File(hideInfoCachePath);
				cacheFile.createNewFile();
			} else {
				File cacheFile = new File(hideInfoCachePath);
				if( !cacheFile.exists()) {
					cacheFile.createNewFile();
				}
			}
		}  catch (IOException e) {
			DKLog.e(TAG, "" + e);
		}	
	}
	
	private String getHideDirPath() {
		String filesPath = DKApp.getSingleton(AppEnv.class).getInternalFilesDir();
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(filesPath);
		strBuilder.append(File.separator);
		strBuilder.append(HIDEINFO_DIRNAME);
		return strBuilder.toString();
	}
	
	private String getHideInfoFilePath() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(getHideDirPath());
		strBuilder.append(File.separator);
		strBuilder.append(HIDEINFO_FILENAME);
		return strBuilder.toString();
	}
	
	@SuppressWarnings("unchecked")
	private void readHideInfo() {
		File file = new File(getHideInfoFilePath());
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream  ois = new ObjectInputStream(fis);
			try {
				Object hideObj = ois.readObject();
				localMediaHideItemInfoHashtable = (Hashtable<String, LocalMediaHideItemInfo>) hideObj;
			} catch (ClassNotFoundException e) {
				DKLog.e(TAG, "" + e);
			}			
			ois.close();
			fis.close();
		}catch(IOException e) {
			DKLog.e(TAG, "" + e);
		}
	}
	
	private void writeHideInfo() {
		File file = new File(getHideInfoFilePath());
		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(localMediaHideItemInfoHashtable);
			oos.flush();
			oos.close();
			fos.close();
		} catch (IOException e) {
			DKLog.e(TAG, "" + e);
		}
	}
}


