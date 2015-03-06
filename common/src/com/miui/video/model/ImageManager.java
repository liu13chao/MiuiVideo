/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   ImageManager.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-13 
 */
package com.miui.video.model;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import com.miui.video.controller.BitmapFilter;
import com.miui.video.type.ImageUrlInfo;
import com.miui.video.util.DKLog;
import com.miui.video.util.Util;

/**
 * @author tianli
 * 
 */
public class ImageManager {

    private static final int MAX_THREAD = 3;
    private static final int MAX_NET_THREAD = 2;
    
    private static final int MIN_MEMORY_CACHE = 30000 * 1024; // 30m
    
    private static String TAG = ImageManager.class.getName();

    private static final int MSG_TASK_DONE = 1;

    private static ArrayList<TaskUnit> taskQueue = new ArrayList<TaskUnit>();
    private static ArrayList<TaskUnit> netTaskQueue = new ArrayList<TaskUnit>();
    private static Thread[] threadPool;
    private static byte[] localMonitor = new byte[0];
    private static byte[] netMonitor = new byte[0];
    private static final int STATE_START = 0;
    private static final int STATE_PAUSE = 1;
    private static final int MAX_RETRY = 3;
    private volatile int threadState = STATE_START;
    private static ImageManager instance = new ImageManager();

    private ImageHandler handler = new ImageHandler();
    
    private static class ImageHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MSG_TASK_DONE: {
                    final TaskUnit task = (TaskUnit) message.obj;
                    if(task == null || task.taskInfo == null || task.taskInfo.imageUrlInfo == null){
                    	return;
                    }
                    ImageManager.getInstance().cacheImage(task.taskInfo.imageUrlInfo, task.bitmap);
                    int i = 0;
                    for (final View view : task.queue) {
                        if (view != null) {
                        	Object tagObj = view.getTag();
                        	if( tagObj != null && tagObj instanceof UrlHolder)
                        	{
                                UrlHolder urlHolder = (UrlHolder) tagObj;
                                if (!task.taskInfo.imageUrlInfo.getImageUrl().equals(urlHolder.imageUrlInfo.getImageUrl())) {
                                    continue;
                                }
                                urlHolder.done = true;
                                if(view instanceof ImageView){
                                    ((ImageView) view).setImageBitmap(task.bitmap);
                                 }else{
                                     view.setBackgroundDrawable(new BitmapDrawable(task.bitmap));
                                 }
                        	}
                        }
                    }
                }
            }
        }
    }

    private ImageManager() {
        threadPool = new Thread[MAX_THREAD];
        for (int i = 0; i < MAX_THREAD; i++) {
            threadPool[i] = new Thread(new TaskRunner(i < MAX_NET_THREAD));
            threadPool[i].start();
            threadPool[i].setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            
        }
//        mMemoryCache.resize(getLruCacheSize());
    }

    public static ImageManager getInstance() {
        return instance;
    }

//    public void clearImageCache(){
//    	synchronized (mMemoryCache) {
//        	mMemoryCache.evictAll();	
//		}
//    }
    
    public void onLowMemory(){
    	Log.d(TAG, "onLowMemory");
    	synchronized (mMemoryCache) {
//    		mMemoryCache.resize(MIN_MEMORY_CACHE/2);
			System.gc();
//			mMemoryCache.resize(MIN_MEMORY_CACHE);
		}
    }
    
    private void cacheImage(ImageUrlInfo imageUrlInfo, Bitmap bitmap) {
    	synchronized (mMemoryCache) {
            CacheEntry entry = new CacheEntry();
            entry.bitmap = bitmap;
            mMemoryCache.put(imageUrlInfo.getImageUrl(), entry);
		}
    }

    private Bitmap getCacheImage(String url) {
    	synchronized (mMemoryCache) {
        	if(!TextUtils.isEmpty(url)){
        		CacheEntry entry = mMemoryCache.get(url);
        		if(entry != null){
        			return entry.bitmap;
        		}
        	}
		}
        return null;
    }

    private class UrlHolder {
    	ImageUrlInfo  imageUrlInfo;
        boolean done = false;
    }

    public static boolean isUrlDone(ImageUrlInfo imageUrlInfo, View view) {
        if ( imageUrlInfo != null && view != null && view.getTag() != null
                && view.getTag() instanceof UrlHolder) {
            UrlHolder holder = (UrlHolder) view.getTag();
            if (holder.done &&  holder.imageUrlInfo != null && 
            		  holder.imageUrlInfo.getImageUrl().equals(imageUrlInfo.getImageUrl())) {
                return true;
            }
        }
        return false;
    }

//    public void fetchImage(TaskInfo taskInfo, ImageView view) {
//        DKLog.i(TAG, "fetchImage. ");
//        if ( taskInfo == null || taskInfo.imageUrlInfo == null || 
//        		Util.isEmpty(taskInfo.imageUrlInfo.url) || view == null) {
//            return;
//        }
//        if (isUrlDone(taskInfo.imageUrlInfo, view)) {
//            DKLog.i(TAG, "isUrlDone. ");
//            return;
//        }
//        UrlHolder holder = new UrlHolder();
//         DKLog.i(TAG, "url changed, request new url");
//        holder.imageUrlInfo = taskInfo.imageUrlInfo;
//        holder.done = false;
//        view.setTag(holder);
//        Bitmap bitmap = getCacheImage(taskInfo.imageUrlInfo.url);
//        if (bitmap != null && view != null) {
//            view.setImageBitmap(bitmap);
//            holder.done = true;
//            return;
//        }
//        pushTask(taskInfo, view, false);
//    }    
    
    public boolean fetchImage(TaskInfo taskInfo, View view) {
        DKLog.i(TAG, "fetchImage. ");
        if ( taskInfo == null || taskInfo.imageUrlInfo == null || 
                Util.isEmpty(taskInfo.imageUrlInfo.getImageUrl()) || view == null) {
            return false;
        }
        if (isUrlDone(taskInfo.imageUrlInfo, view)) {
            DKLog.i(TAG, "isUrlDone. ");
            return true;
        }
        UrlHolder holder = new UrlHolder();
         DKLog.i(TAG, "url changed, request new url");
        holder.imageUrlInfo = taskInfo.imageUrlInfo;
        holder.done = false;
        view.setTag(holder);
        Bitmap bitmap = getCacheImage(taskInfo.imageUrlInfo.getImageUrl());
        if (bitmap != null && view != null) {
            if(view instanceof ImageView){
               ((ImageView) view).setImageBitmap(bitmap);
            }else{
                view.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }
            holder.done = true;
            return true;
        }
        pushTask(taskInfo, view, false);
        return false;
    }    

    
    public static TaskInfo createTask(ImageUrlInfo imageUrlInfo, BitmapFilter filter){
    	return new TaskInfo(imageUrlInfo, filter);
    }
    
    public static class TaskInfo{
    	public TaskInfo(ImageUrlInfo imageUrlInfo, BitmapFilter filter){
    		this.imageUrlInfo = imageUrlInfo; 
    		this.bitmapFilter = filter;
    	}
    	public ImageUrlInfo imageUrlInfo;
    	public BitmapFilter bitmapFilter;
    }
    

    public synchronized void pushTask(TaskInfo taskInfo, View view, boolean isNetTask) {
        TaskUnit task = new TaskUnit();
        task.taskInfo = taskInfo;
        ArrayList<TaskUnit> queue;
        Object monitor;
        if(isNetTask){
            queue = netTaskQueue;
            monitor = netMonitor;
        }else{
            queue = taskQueue;
            monitor = localMonitor;
        }
        int pos = queue.indexOf(task);
        if (pos >= 0) {
            TaskUnit preTask = queue.remove(pos);
            task.queue = preTask.queue;
            if (!task.queue.contains(view)) {
                task.queue.add(view);
            }
        } else {
            task.queue.add(view);
        }
        queue.add(0, task);
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }

    public synchronized void moveToNetTaskQueue(TaskUnit task, boolean addToFront){
        ArrayList<TaskUnit> queue;
        Object monitor;
        queue = netTaskQueue;
        monitor = netMonitor;
        int pos = queue.indexOf(task);
        if (pos >= 0) {
            TaskUnit preTask = queue.remove(pos);
            for(int i = 0; i < preTask.queue.size(); i++){
                if(!task.queue.contains(preTask.queue.get(i)))
                    task.queue.add(preTask.queue.get(i));
            }
        }
        if(addToFront){
            queue.add(0, task);
        }else{
            queue.add(task);
        }
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }

    public synchronized TaskUnit popTask(boolean isNetTask) {
        ArrayList<TaskUnit> queue;
        if(isNetTask){
            queue = netTaskQueue;
        }else{
            queue = taskQueue;
        }
        if (queue.size() > 0) {
            return queue.remove(0);
        }
        return null;
    }

    public void pause() {
        this.threadState = STATE_PAUSE;
    }

    public void resume() {
        this.threadState = STATE_START;
        synchronized (localMonitor) {
            localMonitor.notifyAll();
        }
        synchronized (netMonitor) {
            netMonitor.notifyAll();
        }
    }

    // public void clear() {
    // this.state = START;
    // workingSet.clear();
    // }

    private static class CacheEntry {
        Bitmap bitmap;
    }

    private static class TaskUnit {
        TaskInfo taskInfo;
        ArrayList<View> queue = new ArrayList<View>();
        Bitmap bitmap;
        int retry = 0;
        @Override
        public boolean equals(Object o) {
	        if (o != null && o instanceof TaskUnit && taskInfo != null && taskInfo.imageUrlInfo != null 
	       		     && taskInfo.imageUrlInfo.getImageUrl() != null) {
	        	TaskUnit taskUnitObj = (TaskUnit)o;
	        	if(taskUnitObj.taskInfo == null || taskUnitObj.taskInfo.imageUrlInfo == null ){
	        		return false;
	        	}
		       	if(taskInfo.imageUrlInfo.md5 != null){
		            return taskInfo.imageUrlInfo.getImageUrl().equals(taskUnitObj.taskInfo.imageUrlInfo.getImageUrl()) &&
		            		   taskInfo.imageUrlInfo.md5.equals(taskUnitObj.taskInfo.imageUrlInfo.md5);
		       	}else{
		       		return taskInfo.imageUrlInfo.getImageUrl().equals(taskUnitObj.taskInfo.imageUrlInfo.getImageUrl());
		       	}
	       }
            return super.equals(o);
        }
    }

    private class TaskRunner implements Runnable {

        public boolean isNetTask = false;

        public TaskRunner(boolean isNetTask){
            this.isNetTask = isNetTask;
        }
        private void postTaskDone(TaskUnit task) {
            Message msg = new Message();
            msg.what = MSG_TASK_DONE;
            msg.obj = task;
            handler.sendMessage(msg);
        }
        
        public void run() {

            while (true) {
                try {
                    if (Thread.interrupted()) {
                        // shutdown has been called.
                        return;
                    }
                    Object monitor = isNetTask ? netMonitor : localMonitor;
                    if (threadState == STATE_PAUSE) {
                        synchronized (monitor) {
                            DKLog.i(TAG, "In pause state, thread "
                                    + Thread.currentThread().getId()
                                    + "enter wait.");
                            monitor.wait();
                            continue;
                        }
                    }
                    TaskUnit task = popTask(isNetTask);
                    if (task == null) {
                        synchronized (monitor) {
                            DKLog.i(TAG, "Task queue is empty, thread "
                                    + Thread.currentThread().getId()
                                    + "enter wait.");
                            monitor.wait();
                            continue;
                        }
                    }
                    
                    ImageUrlInfo  imageUrlInfo = task.taskInfo.imageUrlInfo;
                    Bitmap bitmap = getCacheImage(imageUrlInfo.getImageUrl());
                    if (bitmap != null) {
                        task.bitmap = bitmap;
                        postTaskDone(task);
                    } else {
                        bitmap = DataStore.getInstance().getImage(imageUrlInfo, isNetTask);
                        if(task.taskInfo.bitmapFilter != null){
                        	Bitmap newBitmap = task.taskInfo.bitmapFilter.filter(bitmap);
//                        	if(newBitmap != bitmap){
//                        		bitmap.recycle();
//                        	}
                        	bitmap = newBitmap;
                        }
                        if (bitmap != null) {
                            task.bitmap = bitmap;
                            postTaskDone(task);
                        }else if(!isNetTask){
                            task.retry = 0;
                            moveToNetTaskQueue(task, true);
                        }else{
                            if(task.retry++ < MAX_RETRY){
                                moveToNetTaskQueue(task, false);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }
    
    private LruCache<String, CacheEntry> mMemoryCache = new LruCache<String, CacheEntry>(MIN_MEMORY_CACHE){
		@Override
		protected int sizeOf(String key, CacheEntry value) {
			if(value != null && value.bitmap != null){
				return value.bitmap.getByteCount();
			}
			return 0;
		}

		@Override
		protected void entryRemoved(boolean evicted, String key,
				CacheEntry oldValue, CacheEntry newValue) {
			Log.d(TAG, "entryRemoved ");
			super.entryRemoved(evicted, key, oldValue, newValue);
		}
    };
}