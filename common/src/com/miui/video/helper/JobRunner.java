/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   AsyncTaskRunner.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-4-6
 */

package com.miui.video.helper;

import java.util.Hashtable;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * @author tianli
 *
 */
public class JobRunner {
	public final static Handler sHandler;
	public final static HandlerThread sThread;
	public final static Hashtable<Runnable, Runnable> sJobMap = new 
			Hashtable<Runnable, Runnable>();
	
	static {
		sThread = new HandlerThread("JobRunner");
		sThread.start();
		sHandler = new Handler(sThread.getLooper());
	}
	
	public static void postJob(final Runnable job){
		if(job != null){
			Runnable runner = new Runnable() {
				@Override
				public void run() {
					try{
						job.run();
						sJobMap.remove(job);
					}catch (Throwable e) {
					}
				}
			};
			sJobMap.put(job, runner);
			sHandler.post(runner);
		}
	}
	
	public static void removeJob(Runnable job){
		if(job != null){
			Runnable runner = sJobMap.remove(job);
			if(runner != null){
				sHandler.removeCallbacks(runner);
			}
		}
	}
}
