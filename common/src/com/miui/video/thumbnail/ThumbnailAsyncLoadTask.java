/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   VideoThumbnailAsyncLoadTask.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-10-11
 */

package com.miui.video.thumbnail;

/**
 * @author xuanmingliu
 * 
 */

public abstract class ThumbnailAsyncLoadTask {

	private String mTaskId;
	private int mTaskType;
	
	protected Object mResult;

	final public String getId() {
		return mTaskId;
	}

	final public int getTaskType() {
		return mTaskType;
	}
	
	public Object getResult(){
		return mResult;
	}

	protected ThumbnailAsyncLoadTask(String id, int taskType) {
		this.mTaskId = id;
		this.mTaskType = taskType;
	}

	public abstract void load();

	public void postResult(Object result) {
	};
	
	@Override
	public boolean equals(Object o) {
		if(o != null && o instanceof ThumbnailAsyncLoadTask){
			String id = ((ThumbnailAsyncLoadTask)o).getId();
			if(id != null){
				return id.equals(getId());
			}
		}
		return super.equals(o);
	}

}
