/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   VideoThumbnailAysncLoadTaskStack.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-10-11
 */

package com.miui.video.thumbnail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Process;

import com.miui.video.util.DKLog;


/**
 *@author xuanmingliu
 *
 */

public class ThumbnailTaskStack{
    private final static String TAG = "ThumbnailTaskStack";

    private enum State {
        RUNNING, STOPPED, PAUSE,
    };

    private volatile State mState = State.STOPPED;

//    private int mTaskThreadNum = 1;
    private volatile int mThreadPriority;
    
//    public static interface CheckThumbnailTaskFinishListener {
//    	public boolean onCheckThumbnailTaskFinish(int taskType);
//    }
    
//    private CheckThumbnailTaskFinishListener checkThumbTaskFinishListener = null;
//    private byte[]  checkTaskFinishListenerMonitor;
    
    //存在本地缓存的task
//    private ConcurrentLinkedQueue<ThumbnailAsyncLoadTask> mFileCacheTaskStack;
//    private ConcurrentLinkedQueue<ThumbnailAsyncLoadTask> mFileCacheLowTaskStack;
//    private LinkedList<Thread> mFileCacheTaskThreads;
    //本地视频文件获取thumbnail的task
//    private ConcurrentLinkedQueue<ThumbnailAsyncLoadTask> mLocalVideoTaskStack;
//    private ConcurrentLinkedQueue<ThumbnailAsyncLoadTask> mLocalVideoLowTaskStack;
//    private LinkedList<Thread> mLocalVideoTaskThreads;
    //网络视频文件获取thumbnail的task
//    private ConcurrentLinkedQueue<ThumbnailAsyncLoadTask> mNetworkVideoTaskStack;
//    private ConcurrentLinkedQueue<ThumbnailAsyncLoadTask> mNetworkVideoLowTaskStack;
//    private LinkedList<Thread> mNetworkVideoThreads;
    
    private ConcurrentHashMap<Integer, ExecutorService>  mThreadPool;
    private ConcurrentHashMap<Integer, ConcurrentLinkedQueue<ThumbnailAsyncLoadTask>> mTaskContainer;
    private ConcurrentHashMap<Integer, ConcurrentLinkedQueue<ThumbnailAsyncLoadTask>> mLowTaskContainer;
    
    private byte[] mTaskLock = new byte[0];
    
    private final static int THREAD_POOL_SIZE = 3;
    private final static int CORE_POOL_SIZE = 3;

//    private int mMaxTaskNum = 25;

    //record running task
    private HashMap<String, ArrayList<ThumbnailAsyncLoadTask>> mRunningTasks = 
    		  new HashMap<String, ArrayList<ThumbnailAsyncLoadTask>>();

    public void setThreadPriority(int thread_priority) {
        this.mThreadPriority = thread_priority;
    }

    public ThumbnailTaskStack() {
        mThreadPriority = Process.THREAD_PRIORITY_BACKGROUND;
        
        mThreadPool = new ConcurrentHashMap<Integer, ExecutorService>();
        mTaskContainer = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue<ThumbnailAsyncLoadTask>>();
        mLowTaskContainer = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue<ThumbnailAsyncLoadTask>>();
        
        int type = TaskType.TASK_FILECACHE_TYPE;
//        mFileCacheTaskStack = new ConcurrentLinkedQueue<ThumbnailAsyncLoadTask>();
//        mFileCacheLowTaskStack = new ConcurrentLinkedQueue<ThumbnailAsyncLoadTask>();
        mThreadPool.put(type, new ThreadPoolExecutor(CORE_POOL_SIZE, THREAD_POOL_SIZE, 30, TimeUnit.SECONDS, 
        		new LinkedBlockingDeque<Runnable>()));
        mTaskContainer.put(type, new ConcurrentLinkedQueue<ThumbnailAsyncLoadTask>());
        mLowTaskContainer.put(type, new ConcurrentLinkedQueue<ThumbnailAsyncLoadTask>());
        
        type = TaskType.TASK_LOCALVIDEO_TYPE;
//        mLocalVideoTaskStack = new ConcurrentLinkedQueue<ThumbnailAsyncLoadTask>();
//        mLocalVideoLowTaskStack = new ConcurrentLinkedQueue<ThumbnailAsyncLoadTask>();
        mThreadPool.put(type, new ThreadPoolExecutor(CORE_POOL_SIZE, THREAD_POOL_SIZE, 30, TimeUnit.SECONDS, 
        		new LinkedBlockingDeque<Runnable>()));
        mTaskContainer.put(type, new ConcurrentLinkedQueue<ThumbnailAsyncLoadTask>());
        mLowTaskContainer.put(type, new ConcurrentLinkedQueue<ThumbnailAsyncLoadTask>());
        
        type = TaskType.TASK_NETWORKVIDEO_TYPE;
//        mNetworkVideoTaskStack = new ConcurrentLinkedQueue<ThumbnailAsyncLoadTask>();
//        mNetworkVideoLowTaskStack = new ConcurrentLinkedQueue<ThumbnailAsyncLoadTask>();
        mThreadPool.put(type, new ThreadPoolExecutor(CORE_POOL_SIZE, THREAD_POOL_SIZE, 30, TimeUnit.SECONDS, 
        		new LinkedBlockingDeque<Runnable>()));
        mTaskContainer.put(type, new ConcurrentLinkedQueue<ThumbnailAsyncLoadTask>());
        mLowTaskContainer.put(type, new ConcurrentLinkedQueue<ThumbnailAsyncLoadTask>());
    }
    
//    public void setMaxNum(int max) {
//        setMaxTaskNum(max);
//    }
//
//    public void setMaxTaskNum(int maxNum) {
//        mMaxTaskNum = Math.max(0, maxNum);
//    }
    
//    public void setCheckThumbTaskFinishListener(CheckThumbnailTaskFinishListener checkThumbTaskFinishListener)
//    {
//    	synchronized(checkTaskFinishListenerMonitor)
//    	{
//    		this.checkThumbTaskFinishListener = checkThumbTaskFinishListener;
//    	}
//    }

    private void clearPendingTask(int taskType) {
    	mTaskContainer.get(taskType).clear();
    	mLowTaskContainer.get(taskType).clear();
    }
    
    public void clearFileCachePendingTask(){
    	clearPendingTask(TaskType.TASK_FILECACHE_TYPE);
    }
    
    public void clearLocalVideoPendingTask(){
    	clearPendingTask(TaskType.TASK_LOCALVIDEO_TYPE);
    }
    
    public void clearNetworkVideoPendingTask(){
    	clearPendingTask(TaskType.TASK_NETWORKVIDEO_TYPE);
    }
    
    public void pushBack(ThumbnailAsyncLoadTask task, boolean lowPriority) {
    	if( task == null) {
    		return;
    	}
    	DKLog.i(TAG, "pushBack task: " + task + ", task id: " + task.getId() + ", taskType: " + task.getTaskType());
    	synchronized (mRunningTasks) {
    		String id = task.getId();
    		ArrayList<ThumbnailAsyncLoadTask> list = mRunningTasks.get(id);
    		if (list != null) {
    			DKLog.i(TAG, "pushBack list: " + list);
    			/*   there is a task is loading same data as this task, so directly add this task to
                   responding queue for later postResult. */
    			list.add(task);
    			return;
    		}
    	}
    	int taskType = task.getTaskType();
    	ConcurrentLinkedQueue<ThumbnailAsyncLoadTask> queue = null;
    	if(lowPriority){
    		queue = mLowTaskContainer.get(taskType);
    	}else{
    		queue = mTaskContainer.get(taskType);
    	}
    	if( queue == null) {
    		DKLog.i(TAG, "pushBack queue: " + queue);
    		return;
    	}
    	queue.offer(task);
    	DKLog.i(TAG, "pushBack queue size: " + queue.size());
    	ExecutorService executorService = mThreadPool.get(taskType);
    	if(executorService != null && !executorService.isShutdown()) {
    		executorService.execute(new TaskRunner(taskType));
    		DKLog.i(TAG, "pushBack execute");
    	}
    }

//    private void pushBack(ThumbnailAsyncLoadTask task, ConcurrentLinkedQueue<ThumbnailAsyncLoadTask> stack){
//    	int taskType = task.getTaskType();
//    	stack.offer(task);
//    	LinkedList<Thread>  threadList = mThreadPool.get(taskType);
//    	ExecutorService es;
//    	synchronized(threadList){
//    		// remove dead threads
//    		for (int i = threadList.size() - 1; i >= 0; i--) {
//    			if (!threadList.get(i).isAlive()) {
//    				threadList.remove(i);
//    			}
//    		}
//    		// start new threads
//    		while (threadList.size() < mTaskThreadNum) {
//    			Thread t = new Thread(this, String.valueOf(taskType));
//    			threadList.add(t);
//    			t.start();
//    		}
//    	}
//    }

    private ThumbnailAsyncLoadTask popNextTask(int taskType) throws InterruptedException {
    	ConcurrentLinkedQueue<ThumbnailAsyncLoadTask> taskQueue = mTaskContainer.get(taskType);
    	ThumbnailAsyncLoadTask task = taskQueue.poll();
//    	DKLog.i(TAG, "popNextTask high task: " + task);
    	if( task == null){
    		taskQueue = mLowTaskContainer.get(taskType);
        	task = taskQueue.poll();
//        	DKLog.i(TAG, "popNextTask low task: " + task);
    	}
        // check whether there is a task is running and loading same data as this task
        if ( task != null) {
			synchronized (mRunningTasks) {
				String id = task.getId();
				ArrayList<ThumbnailAsyncLoadTask> list = mRunningTasks.get(id);
				if (list == null) {
					list = new ArrayList<ThumbnailAsyncLoadTask>();
					list.add(task);
					mRunningTasks.put(id, list);
				} else {
					list.add(task);
					task = null;
				}
			}
        }
//        DKLog.i(TAG, "popNextTask final task: " + task);
        return task;
    }

    private void postResult(String id) {
        ArrayList<ThumbnailAsyncLoadTask> list = null;
        synchronized (mRunningTasks) {
            list = mRunningTasks.get(id);
            mRunningTasks.remove(id);
        }
        if (list == null || list.size() <= 0) {
			return;
		}
		Object result = list.get(0).mResult;
        for (int i = 0; i < list.size(); i++) {
        	ThumbnailAsyncLoadTask task = list.get(i);
            task.postResult(result);
        }
    }

    public boolean stop() {
        mState = State.STOPPED;
        return true;
    }
    
    public boolean resume(){
    	 mState = State.RUNNING;
    	 synchronized(mTaskLock){
    		 mTaskLock.notifyAll();
    	 }
    	 return true;
    }
    
    public boolean pause(){
    	mState = State.PAUSE;
    	return true;
    }
    
    private class TaskRunner implements Runnable{
    	
    	private int mTaskType = TaskType.TASK_INVALID_TYPE;
    	
    	public TaskRunner(int taskType){
    		mTaskType = taskType;
    	}
		@Override
		public void run() {
	        Process.setThreadPriority(mThreadPriority);
	        mState = State.RUNNING;
	        Thread thread = Thread.currentThread();
	        long id = thread.getId();
	        DKLog.i(TAG, "thread id : " + id + " taskType = " + mTaskType);
	        while (mState != State.STOPPED) {	 
	            try {
	                if( thread.isInterrupted()){
	                	return;
	                }
			    	if( mState == State.PAUSE){
			    		synchronized(mTaskLock){
			    			mTaskLock.wait();
							continue;
			    		}
			    	}
	        		ThumbnailAsyncLoadTask task = popNextTask(mTaskType);
	                if (task != null) {
	                	DKLog.i(TAG, "runner task: " + task + ", task id: " + task.getId() + ", thread id: " + thread.getId());
	                    task.load();
	                    postResult(task.getId());
	                    DKLog.i(TAG, "runner done task: " + task + ", task id: " + task.getId() + ", thread id: " + thread.getId());
	                } else {
	                	return;
	                }
	            } catch (Exception e) {
	            	e.printStackTrace();
	            }
	        }
		}
    }
    
	public static class TaskType {
		public static final int TASK_INVALID_TYPE = -1;
		public static final int TASK_FILECACHE_TYPE = 0;
		public static final int TASK_LOCALVIDEO_TYPE = 1;
		public static final int TASK_NETWORKVIDEO_TYPE = 2;
	}
}


