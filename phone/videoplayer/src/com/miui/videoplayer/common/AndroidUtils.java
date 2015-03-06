package com.miui.videoplayer.common;

import java.util.Arrays;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.TextView;

import com.miui.video.util.Util;
import com.miui.videoplayer.model.MediaUrlInfo.UrlInfo;
import com.miui.videoplayer.model.OnlineUri;

public class AndroidUtils {	
	
	public static float getCurrentBattery(Context context) {
		Intent batteryInfoIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//		int status = batteryInfoIntent.getIntExtra( "status" , 0 ); 
//		int health = batteryInfoIntent.getIntExtra( "health" , 1 );  
//		boolean present = batteryInfoIntent.getBooleanExtra( "present" , false );  
		int level = batteryInfoIntent.getIntExtra( "level" , 0 );  
		int scale = batteryInfoIntent.getIntExtra( "scale" , 0 );  
//		int plugged = batteryInfoIntent.getIntExtra( "plugged" , 0 );  
//		int voltage = batteryInfoIntent.getIntExtra( "voltage" , 0 );  
//		int temperature = batteryInfoIntent.getIntExtra( "temperature" , 0 );
//		String technology = batteryInfoIntent.getStringExtra( "technology" );  
//		Log.i("Battery: ", "status:  " + status);
//		Log.i("Battery: ", "health: " + health);
//		Log.i("Battery: ", "present: " + present);
//		Log.i("Battery: ", "level: " + level);
//		Log.i("Battery: ", "scale: " + scale);
//		Log.i("Battery: ", "plugged: " + plugged);
//		Log.i("Battery: ", "voltage: " + voltage);
//		Log.i("Battery: ", "temperature: " + temperature);
//		Log.i("Battery: ", "technology: " + technology);
		return level / (float)scale;
	}
	
	//0~255
	public static int getSystemBrightness(Context context) {
		int result = 0;
		ContentResolver cr = context.getContentResolver();
		try {
			result = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
	//does not work now
//	public static void setSystemBrightness(Context context, int value) {
//		ContentResolver cr = context.getContentResolver();
//		Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS, value);
//		Uri uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
//		cr.notifyChange(uri, null);
//	}
	
	public static boolean isAutoAdjustBrightness(Context context) {
		ContentResolver cr = context.getContentResolver();
		try {
			return Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC == Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void setAutoAdjustBrightness(Context context, boolean auto) {
		int value = 0;
		if (auto) {
			value = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
		} else {
			value = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
		}
		ContentResolver cr = context.getContentResolver();
		Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE, value);
	}
	
	public static float getActivityBrightness(Activity activity) {
		WindowManager.LayoutParams params = activity.getWindow().getAttributes();
		return params.screenBrightness;
	}
	
	public static void setActivityBrightness(Activity activity, int value) {
		WindowManager.LayoutParams params = activity.getWindow().getAttributes();
		params.screenBrightness = value / 255f;
		activity.getWindow().setAttributes(params);
	}
	
	public static void setBoldFontForChinese(TextView textView) {
		TextPaint tp = textView.getPaint();
		tp.setFakeBoldText(true);
	}
	
	public static String getRealFilePathFromContentUri(Context context, Uri contentUri) {
		if (context == null || contentUri == null) {
			return null;
		}
		if (contentUri.getScheme() == null || !contentUri.getScheme().equals("content")) {
			return null;
		}
    	String[] columns = new String[]{MediaStore.Video.Media.DATA};
    	Cursor cursor = null;
        try {
    	    cursor = context.getContentResolver().query(contentUri, columns, null, null, null);
    	    if (cursor == null) {
    		    return null;
    	    }
	   	    int index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
	   	    cursor.moveToFirst();
	   	    String result = cursor.getString(index);
	   	    cursor.close();
	   	    return result;
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            return null;
        }
     }    	
	
	public static boolean isNetworkConncected(Context context) {
		ConnectivityManager connectiveManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectiveManager == null) {
			return false;
		}
		NetworkInfo networkInfo = connectiveManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}
	
	
	private static NetworkInfo getActiveNetworkInfo(Context context) {
		if (context == null) {
			return null;
		}
		ConnectivityManager connectiveManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectiveManager == null) {
			return null;
		}
		return connectiveManager.getActiveNetworkInfo();
	}
	
	public static boolean isMobileConnected(Context context) {
		NetworkInfo networkInfo = getActiveNetworkInfo(context);
		if (networkInfo == null) {
			return false;
		}
//		return networkInfo.isConnected() && ConnectivityManager.isNetworkTypeMobile(networkInfo.getType());
		return false;
	}
	
	public static boolean isOnlineVideo(Uri uri) {
		if (uri == null) {
			return false;
		}
		String scheme = uri.getScheme();
		if (scheme != null && (scheme.equals("http") || scheme.equals("https") || scheme.equals("rtsp"))) {
			return true;
		}
		String path = uri.toString();
		if(path != null && path.contains("app_smb")){
			return true;
		}
		return false;
	}
	
	public static boolean isSmbVideo(Uri uri) {
		if (uri == null) {
			return false;
		}

		String path = uri.toString();
		if(path != null && path.contains("app_smb")){
			return true;
		}else{
			return false;
		}
	}	
	
	public static boolean isRtspVideo(Uri uri) {
		if (uri == null) {
			return false;
		}
		String scheme = uri.getScheme();
		if(scheme != null && scheme.equalsIgnoreCase("rtsp")){
			return true;
		}else{
			return false;
		}
	}

	
	public static boolean isWifiConnected(Context context) {
		NetworkInfo networkInfo = getActiveNetworkInfo(context);
		if (networkInfo == null) {
			return false;
		}
		return networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}
	
	@SuppressLint("NewApi") 
	public static boolean isFreeNetworkConnected(Context context) {
		 ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	     if (connectivityManager == null || connectivityManager.getActiveNetworkInfo() == null
	               || !connectivityManager.getActiveNetworkInfo().isConnected()) {
	         return false;  // 没有网络链接
	     }
	     if(connectivityManager.isActiveNetworkMetered()) {
	         return false; // 收费网络，可能是收费wifi，2G，3G等
	     }
	     return true; // 免费网络
	}
	
	
	public static boolean isUseSdk(OnlineUri uri) {
		if (uri == null || TextUtils.isEmpty(uri.getSdkInfo()) || uri.getSdkdisable()) {
			return false;
		}
		String sdkinfo = uri.getSdkInfo();
		int source = uri.getSource();
		boolean sdkdisable = uri.getSdkdisable();
		int videoType = uri.getVideoType();
		return Util.playBySdk(sdkinfo, sdkdisable, source, videoType);
	}
	
	public static boolean isUseSdk(UrlInfo urlInfo) {
		if (urlInfo == null || TextUtils.isEmpty(urlInfo.sdkinfo) || urlInfo.sdkdisable) {
			return false;
		}
		String sdkinfo = urlInfo.sdkinfo;
		int source = urlInfo.mediaSource;
		boolean sdkdisable = urlInfo.sdkdisable;
		int videoType = Constants.MEDIA_TYPE_LONG;
		return Util.playBySdk(sdkinfo, sdkdisable, source, videoType);
	}
	
	public static <T> T[] concat(T[] first, T[] second) {
		if (first == null && second == null) {
			return null;
		} else if (first == null) {
			return Arrays.copyOf(second, second.length);
		} else if (second == null) {
			return Arrays.copyOf(first, first.length);
		} else {
			T[] result = Arrays.copyOf(first, first.length + second.length);
			System.arraycopy(second, 0, result, first.length, second.length);
			return result;
		}
	}
	
    public static String formatPercent(long completed, long total) {
    	if (completed <= 0 || total <= 0) {
			return "0.0%";
		}
    	final float c = completed;
    	final float t = total;
		try {
			return String.format(Locale.US, "%.1f%%", c/t*100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0.0%";
	}
}	
