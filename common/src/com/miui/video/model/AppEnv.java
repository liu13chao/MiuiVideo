/**
 *  Copyright(c) 2013 XiaoMi TV Group
 *    
 *  AppEnv.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2013-10-27
 */
package com.miui.video.model;

import java.io.File;
import java.lang.reflect.Method;

import miui.os.Build;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.storage.StorageManager;

import com.miui.video.controller.Constants;
import com.xiaomi.common.PriorityStorageBroadcastReceiver;

/**
 * @author tianli
 *
 */
public class AppEnv extends AppSingleton {

	private static final String SDCARD_ZERO_ROOT = "/storage/sdcard0";
    private static final String SDCARD_ONE_ROOT =  "/storage/sdcard1"; 
	
	private String mOfflineDir = null;
	private String mAddonDir = null;
	
	  /**
     * H2 , /storage/sdcard1 is internal storage
     *                                             no sdcard              has sdcard
     * Environment.getExternalStorageDirectory() /storage/sdcard1        /storage/sdcard0
     * System.getenv("SECONDARY_STORAGE")        /storage/sdcard1        /storage/sdcard1
     *
     * Other HM devices:
     *                                             no sdcard              has sdcard
     * Environment.getExternalStorageDirectory() /storage/emulate/legacy /storage/emulate/legacy
     * System.getenv("SECONDARY_STORAGE")        /storage/sdcard1        /storage/sdcard1
     * */
    public String getInternalSdCardRoot() {
        if (Build.IS_HONGMI_TWO && !Build.IS_HONGMI_TWO_A && !Build.IS_HONGMI_TWO_S) {
            // if devices is H2, just hard-code "/storage/sdcard1"
            return SDCARD_ONE_ROOT;
        }
        // In HM devices except H2, this will return internal sdcard in phone.
        // such as "/storage/emulate/legacy" in H2A and other HM devices.
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public String getExternalSdCardRoot() {
        if(DeviceInfo.isH2()){
            // if devices is H2, just hard-code "/storage/sdcard0"
            return SDCARD_ZERO_ROOT;
        }
        // this will return "/storage/sdcard1" in all HM devices.
        return System.getenv("SECONDARY_STORAGE");
    }
	   
    public String getMainSdcardRoot() {
        if (isExternalSdcardMounted() && isPriorityStorage()) {
            return getExternalSdCardRoot();
        }
        return getInternalSdCardRoot();
    }
	
	public boolean isPriorityStorage() {
        PackageManager pm = mContext.getPackageManager();
        ComponentName name = new ComponentName(mContext, PriorityStorageBroadcastReceiver.class); 
        int state = pm.getComponentEnabledSetting(name);
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
            return true;
        } else {
            return state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        }
	}
	
    public void setPriorityStorage(boolean enabled) {
        PackageManager pm = mContext.getPackageManager();
        ComponentName name = new ComponentName(mContext, PriorityStorageBroadcastReceiver.class);
        pm.setComponentEnabledSetting(name, enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
    
	public String getInternalFilesDir() {
		File file = mContext.getFilesDir();
		if(file != null) {
			return file.getAbsolutePath();
		}
		return null;
	}
    
	public String getDcimDir() {
		File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		if(file != null) {
			return file.getAbsolutePath();
		}
		return null;
	}
	
	public String getOfflineDir() {
		if (mOfflineDir == null) {
			StringBuilder strBuilder = new StringBuilder();
			String sdcardDir = getMainSdcardRoot();
			if (sdcardDir != null) {
				strBuilder.append(sdcardDir);
				strBuilder.append(File.separator);
				strBuilder.append(Constants.OFFLINEMEDIA_DIR);
			}
			mOfflineDir = strBuilder.toString();
		}
		return mOfflineDir;
	}
	
	public String getAddonDir() {
		if (mAddonDir == null) {
			StringBuilder strBuilder = new StringBuilder();
			String sdcardDir = getMainSdcardRoot();
			if (sdcardDir != null) {
				strBuilder.append(sdcardDir);
				strBuilder.append(File.separator);
				strBuilder.append(Constants.ADDON_DIR);
				strBuilder.append(File.separator);
			}
			mAddonDir = strBuilder.toString();
		}
		return mAddonDir;
	}
	
	public String getInnerCamara() {
		return getInternalSdCardRoot() + "/DCIM/Camera";
	}
	
	public String getExternalCamara() {
		return getExternalSdCardRoot() + "/DCIM/Camera";
	}

	public  boolean isExternalSdcardMounted(){
		boolean hasRemovableStorage = false;
		try {
			StorageManager storageManager = (StorageManager)mContext.getSystemService(Context.STORAGE_SERVICE);
			Method mMethodGetState = storageManager.getClass().getMethod("getVolumeState", String.class);
			String state = (String)mMethodGetState.invoke(storageManager, getExternalSdCardRoot());
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				hasRemovableStorage = true;
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hasRemovableStorage;
	}
}
