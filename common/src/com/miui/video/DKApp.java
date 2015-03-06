/**
 *  Copyright(C) 2012 DuoKan TV Group
 * 
 *  DKApp.java  
 * 
 *  @author tianli (tianli@duokan.com)
 *
 *  @date 2012-6-20
 */
package com.miui.video;

import java.util.HashMap;

import miui.external.Application;
import android.content.Context;

import com.miui.video.model.AppSingleton;
import com.miui.video.util.DKLog;


/**
 * @author tianli
 * 
 */
public class DKApp extends Application {
	
	private static final String TAG = "DKApp";
	private static DKApp sInstance;
	private static Context mAppContext;
	private HashMap<Class<?>, AppSingleton> mSingletons;
	
    public static final String APP_ID = "2882303761517147566";
    public static final String APP_KEY = "5481714735566";
    public static final String PACKAGE_NAME = "com.miui.video";
    
    public static final String USE_CELLULAR = "use_cellular";
    public static final String RECEIVE_MIPUSH = "receive_mipush";
    public static final String ALERT_NETWORK = "alert_network";
    
    @Override
    public ApplicationDelegate onCreateApplicationDelegate() {
    	return new ApplicationDelegate();
    }
    
    class ApplicationDelegate extends miui.external.ApplicationDelegate {
        @Override
        public void onCreate() {
        	super.onCreate();
    		//因为MediaPushService运行在另一个进程，这里会被初始化两次
    		DKLog.i(TAG, "onCreate()");
//    		Thread.setDefaultUncaughtExceptionHandler(mExceptionHandler);
    		sInstance = DKApp.this;
    		mAppContext = getApplicationContext();
    		mSingletons = new HashMap<Class<?>, AppSingleton>();
//	        int  returnValue1  = MvSdkJar.init(this.getApplicationContext(),1, null);
//	        DKLog.d(TAG, "returnValue1:" + returnValue1);
//	        SohuVideoPlayer.init(getApplicationContext());
        }
    }
	
	public static Context getAppContext(){
		return mAppContext;
	
	}
	
	public static String getAppPackageName() {
		return mAppContext.getPackageName();
	}
	
	@SuppressWarnings("unchecked")
	public static synchronized <T extends AppSingleton> T getSingleton(Class<T> clazz){
		HashMap<Class<?>, AppSingleton> singletons = sInstance.mSingletons;
		if(singletons.containsKey(clazz)){
			return (T)singletons.get(clazz);
		}
		AppSingleton instance = null;
		try {
          instance = clazz.newInstance();
          instance.init(mAppContext);
//			Constructor<AppSingleton> ctor;
//			ctor = (Constructor<AppSingleton>)
//					clazz.getConstructor(Context.class);
//			instance = ctor.newInstance(getAppContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		singletons.put(clazz, instance);
		return (T)instance;
	}
}
