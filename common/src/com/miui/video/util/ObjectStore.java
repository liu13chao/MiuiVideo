/**
 *  Copyright(c) 2013 XiaoMi TV Group
 *    
 *  ObjectSerializer.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2013-10-29
 */
package com.miui.video.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.util.Log;

/**
 * @author tianli
 *
 */
public class ObjectStore {
	
	public static final String TAG = "ObjectSerializer";
	
	public static Object readObject(String path) {
		ObjectInputStream stream = null;
		try {
			stream = new ObjectInputStream(
					new FileInputStream(path));
			return stream.readObject();
		} catch (Exception e) {
			Log.e(TAG, "failed to read object from " + path);
			return null;
		} finally {
			if(stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					DKLog.e(TAG, e.getLocalizedMessage());
				}
			}
		}
	}

	public static boolean writeObject(String path, Serializable object) {
		ObjectOutputStream stream = null;
		try {
			stream = new ObjectOutputStream(
					new FileOutputStream(path));
			stream.writeObject(object);
			return true;
		} catch (Exception e) {
			Log.e(TAG, "failed to write object to " + path);
		} finally {
			if(stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					DKLog.e(TAG, e.getLocalizedMessage());
				}
			}
		}
		return false;
	}
}
