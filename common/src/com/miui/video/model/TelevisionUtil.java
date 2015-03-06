package com.miui.video.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import com.miui.video.type.TelevisionInfo;
import com.miui.video.type.TelevisionShow;

public class TelevisionUtil {
	
	public static TelevisionInfo[] addNewTelevisionInfos(TelevisionInfo[] oldTelevisionInfos, TelevisionInfo[] newTelevisionInfos){
		int oldTelevisionInfoLength = 0;
		int newTelevisionInfoLength = 0;
		int totalTelevisionInfoLength = 0;
		if(oldTelevisionInfos != null){
			oldTelevisionInfoLength = oldTelevisionInfos.length;
		}
		if(newTelevisionInfos != null){
			newTelevisionInfoLength = newTelevisionInfos.length;
		}
		totalTelevisionInfoLength = oldTelevisionInfoLength + newTelevisionInfoLength;
		if(totalTelevisionInfoLength == 0){
			return null;
		}
		
		TelevisionInfo[] resTelevisionInfos = new TelevisionInfo[totalTelevisionInfoLength];
		int resTelevisionInfoIndex = 0;
		for(int i = 0; i < oldTelevisionInfoLength; i++){
			resTelevisionInfos[resTelevisionInfoIndex++] = oldTelevisionInfos[i];
		}
		for(int i = 0; i < newTelevisionInfoLength; i++){
			resTelevisionInfos[resTelevisionInfoIndex++] = newTelevisionInfos[i];
		}
		return resTelevisionInfos;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String timeMillisToDate(long timeMillis){
		String resDate = null;
		Date date = new Date(timeMillis);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		resDate = simpleDateFormat.format(date);
		return resDate;
	}
	
	public static String timeMillisToHourAndMinute(long timeMillis){
		String resHourAndMinute = null;
		String timeMillisDate = timeMillisToDate(timeMillis);
		resHourAndMinute = timeMillisDate.substring(11, 16);
		return resHourAndMinute;
	}
	
	public static boolean isTelevisionShowExpired(TelevisionInfo[] televisionInfos){
		if(televisionInfos != null){
			for(int i = 0; i < televisionInfos.length; i++){
				TelevisionInfo televisionInfo = televisionInfos[i];
				if(isTelevisionShowExpired(televisionInfo)){
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isTelevisionShowExpired(TelevisionInfo televisionInfo){
		if( televisionInfo == null)
			return false;
		
		long curSystemTime = System.currentTimeMillis();
		TelevisionShow currentShow = televisionInfo.getCurrentShow();
		if( currentShow == null)
			return true;
		else  if( currentShow != null && 
				currentShow.videoendtime <= curSystemTime) {
			return true;
		}		
		return false;
	}
}
