package com.miui.video.storage;



public abstract class BaseItem{

//	protected onIconUpdateListener mIconUpdateListener = null;
	protected String mName = null;
	protected String mDate = null;
	protected int mPriority = 100;
	
//	abstract public Bitmap getIcon();
//	abstract public boolean hasIcon();

	public String getName() {
		return mName;
	}
	
	public void setString(String name) {
		mName = name;
	}
	
	public String getDate() {
		return mDate;
	}
	
	public void setDate(String date) {
		mDate = date;
	}
	
//	public void setIconUpdateListener(onIconUpdateListener listener) {
//		mIconUpdateListener = listener;
//	}
//	
//	public interface onIconUpdateListener {
//		public void onIconReady(BaseItem item);
//	}
	
	public int getPriority(){
		return mPriority;
	}

}
