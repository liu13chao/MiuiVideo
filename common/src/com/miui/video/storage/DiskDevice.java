package com.miui.video.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.util.DKLog;

public class DiskDevice extends BaseDevice {

    private static final long serialVersionUID = 1L;

    public final static String TAG = "DiskDevice";
	
	public final static String NO_MEDIA = ".nomedia";
	
	protected final Context mContext;
	protected final String mRootPath;
	
//	protected DiskInfo mDiskInfo = null;
	
	private boolean mIsInfoLoaded = false;
	protected volatile String mCapacityText;
	protected volatile long mCapacity;
	private volatile String mLabel;
	private String fsType;
	private static final String NTFS_FILE_SYSTEM = "ntfs";
	private static final String VFAT_FILE_SYSTEM = "vfat";
	private static final String NOFS_FILE_SYSTEM = "nofs";
	private boolean mRemoveable = true;
	
	public DiskDevice(Context context, String path) {
		mContext = context;
		mRootPath = path;
		mPriority = 200; 
//		mLabel = context.getResources().getString(R.string.disk_default_label);
	}
	
	private DiskInfoTask mDiskInfoTask = new DiskInfoTask();
	
	class DiskInfoTask extends AsyncTask<Integer, Void, Void>{

		List<DiskInfoCallBack> mCallback = new LinkedList<DiskInfoCallBack>();
		
		public synchronized void addCallBack(DiskInfoCallBack dcb){
			mCallback.add(dcb);
		}
		
		public DiskInfoTask(){}
		
		@Override
		protected Void doInBackground(Integer... params) {
			initDeviceInfo();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mIsInfoLoaded = true;
			synchronized(this){
				for(DiskInfoCallBack dcb : mCallback){
					if(dcb != null){
						dcb.onDiskInfoCallBack(DiskDevice.this);
					}
				}
				mCallback.clear();
			}
		}
	}
	
	public void startDiskInfoTask(){
		mIsInfoLoaded = false;
		mDiskInfoTask.execute();
	}
	
	public void addCallBack(DiskInfoCallBack dcb){
		mDiskInfoTask.addCallBack(dcb);
	}
	
	public void initDeviceInfo(){
//		if(DeviceManager.isMounted(mRootPath)){
//			String lable = Environment.getStorageLabel(mRootPath);
//			long capacity = 0;
//			fsType = Environment.getStorageFS(mRootPath);
//			if(fsType != null && lable != null && fsType.equals(VFAT_FILE_SYSTEM)){
//				try {
//					byte[] bytes = new byte[lable.toCharArray().length/2];
//				    char[] hexData = lable.toCharArray();
//				    for (int count = 0; count < hexData.length - 1; count += 2) {
//				        int firstDigit = Character.digit(hexData[count], 16);
//				        int lastDigit = Character.digit(hexData[count + 1], 16);
//				        int decimal = firstDigit * 16 + lastDigit;
//				        bytes[count/2] = (byte) decimal;
//				    }
//				    lable = new String(bytes, "gbk");
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			try {
//				StatFs statFs = new StatFs(mRootPath + "/");
//				capacity = (long)statFs.getBlockSize() * statFs.getBlockCount();
//			} catch (Exception e) {
//				// bug BH-4130. bad remove cause IllegalArgumentException
//				e.printStackTrace();
//			}
//			mLabel = lable;
//			mCapacity = capacity;
//			mCapacityText = getCapacityText(mCapacity);
//		}
	}
	
	@Override
	public String getRootPath() {
		return mRootPath;
	}
	
	public static class FileScanner{
		
		private DeviceScanTask mScanTask;
		private OnBrowseCompleteListener mListener;
		private DiskDevice mDevice;
		private ArrayList<MediaItem> mFiles = new ArrayList<MediaItem>();
		
		private List<NativeFile> mDirs = new ArrayList<NativeFile>();
//		private List<String> mDirHeadTexts = new ArrayList<String>();
		private List<File> mDirStack = new ArrayList<File>();
		
		private boolean mStopped = false;
		
		private Timer mDirScanTimer = null;
		private boolean mTimeout = false;

		public FileScanner(DiskDevice device, DeviceScanTask scanTask, 
				OnBrowseCompleteListener listener){
			this.mDevice = device;
			this.mListener = listener;
			this.mScanTask = scanTask;
		}
		
		@SuppressWarnings("unchecked")
		public void scanDir(String path){
				DKLog.i(TAG, "scanDir");
				mStopped = false;
				mDirs.clear();
				mFiles.clear();
				DKLog.i(TAG, "listFiles start.");
				List<NativeFile> list = (List<NativeFile>)listFiles(path);
				DKLog.i(TAG, "listFiles end.");
				if(list != null){
					for(NativeFile file : list){
						if(mScanTask.isStopped()){
//							DKLog.i(TAG, "stopTask : " + mBrowserable.getPath());
							mStopped = true;
							return;
						}
						if(mScanTask.isPaused()){
//							DKLog.i(TAG, "pauseTask : " + mBrowserable.getPath());
							mScanTask.pauseTask();
//							DKLog.i(TAG, "resumeTask : " + mBrowserable.getPath());
						}
						if(file.isDir){
							mDirs.add(file);
						}else{
							MediaItem mediaItem = mDevice.prepareMediaItemFile(file.path, file.name);
							if(mediaItem != null){
								mediaItem.setHeadName(file.headName);
								mFiles.add(mediaItem);
							}
						}
					}
				}
				DKLog.i(TAG, "prepare files end. dirs = " + mDirs.size());
				if(mFiles.size() > 0){
//					DKLog.i(TAG, "size = " + mFiles.size());
					DKLog.i(TAG, "onBrowseFileReady: " + mFiles.size());
//					mListener.onBrowseFileReady(mFiles);
//					OrderUtil.orderItems(mFiles, mBrowserable);
					DKLog.i(TAG, "onBrowseFileComplete: " + mFiles.size());
					mListener.onBrowseFileComplete(mFiles);
				}
//				OrderUtil.orderItems(mDirs, mBrowserable);
				for(NativeFile dir : mDirs){
					mDirStack.clear();
					File file = new File(dir.path);
					startTimer();
					if(applyDir(file)){
						MediaItem dirItem = mDevice.prepareMediaItemDir(file);
						if( dirItem != null){
							dirItem.setHeadName(dir.headName);
							mListener.onBrowseDir(dirItem);
						}
					}
					if(mStopped){
						return;
					}
				}
				clearTimer();
		}
		
		public boolean applyDir(File file){
			MediaItem mediaItem = mDevice.prepareMediaItemDir(file);
			if(mediaItem == null){
				return false;
			}
			mDirStack.add(file);
			while(!mDirStack.isEmpty()){
				File dir = mDirStack.remove(0);
				if(nativeScanDir(this, dir.getPath(), false) && !mStopped){
					return true;
				}
				if(mStopped){
					return false;
				}
				if(mTimeout){
					return true;
				}
			}
			return false;
		}
		
		private void startTimer(){
			clearTimer();
			mDirScanTimer = new Timer();
			mDirScanTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					mTimeout = true;
				}
			}, 10000);
		}
		
		private void clearTimer(){
			if(mDirScanTimer != null){
				mDirScanTimer.cancel();
				mDirScanTimer.purge();
			}
			mTimeout = false;
		}
		
		/**
		 *  determine whether the file is applied.
		 *    
		 *  @return true means stop this dir scan.
		 */
		public boolean handleFile(String path, boolean isDir){
			DKLog.i(TAG, "handleFile = " + path + ", isDir = " + isDir);
			if(mScanTask.isStopped()){
//				DKLog.i(TAG, "stopTask : " + mBrowserable.getPath());
				mStopped = true;
				return true;
			}
			if(mScanTask.isPaused()){
//				DKLog.i(TAG, "pauseTask : " + mBrowserable.getPath());
				mScanTask.pauseTask();
//				DKLog.i(TAG, "resumeTask : " + mBrowserable.getPath());
			}
			if(mTimeout){
				return true;
			}
			File file = new File(path);
			if(isDir){
				File nomedia = new File(file.getPath() + "/" + NO_MEDIA);
				if(nomedia.exists()){
					return false;
				}
				mDirStack.add(0, file);
			}else{
				MediaItem mediaItem = mDevice.prepareMediaItemFile(file.getPath(), file.getName());
				if(mediaItem != null){
					return true;
				}
			}
//			DKLog.i(TAG, "return false : " + path);
			return false;
		}
	};
	
	private static native boolean nativeScanDir(FileScanner fileScanner, String path, boolean fullScan);
	private static native List<?> listFiles(String path);
	private native void listFilesEx(String path, List<?> files, List<?> dirs);

	@Override
	public void startBrowsing(final DeviceScanTask scanTask, final OnBrowseCompleteListener listener) {
//		DKLog.i(TAG, "startBrowsing, path: " + browserable.getPath());
        File dir = new File(scanTask.getPath());
        if (!dir.exists() || dir.isFile()) {
        	listener.onBrowseFail(FAIL_REASON_OTHER);
        	return;
		}
        FileScanner scanner = new FileScanner(this, scanTask, listener);
        scanner.scanDir(scanTask.getPath());
		listener.onBrowseCompelete();
	}
	
	protected MediaItem prepareMediaItemDir(File file) {
		DiskDirMediaItem  dirMediaItem = new DiskDirMediaItem(mContext, file);
		if(!dirMediaItem.isApply()){
			return null;
		}
		return dirMediaItem;
	}
	private MediaItem prepareMediaItemFile(String path, String name) {
		MediaItem mediaItem = new FileMediaItem(mContext, path, name);
		if (mediaItem.isApply())
			return mediaItem;
		else
			return null;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DiskDevice that = (DiskDevice) o;
		return mRootPath.equals(that.mRootPath);
	}

	public boolean isHardDisk(){
//		boolean removable = Environment.getStorageRemovable(mRootPath);
//		DKLog.d(TAG, "path = " + mRootPath + ", getStorageRemovable = " + removable);
//		return !removable; 
		return false;
	}
	
	@Override
	public boolean isRemoveable() {
		return mRemoveable;
	}

	public boolean isInfoInited(){
		return mIsInfoLoaded;
	}
	
	public String getCapacityText(){
		return mCapacityText;
	}
	
	private String getCapacityText(long capacity){
		double TB, GB, MB;
		TB = capacity / (double)((long)1024 * 1024 * 1024 * 1024);
		GB = capacity / (double)((long)1024 * 1024 * 1024);
		MB = capacity / (double)((long)1024 * 1024);
		if(TB < 1){
			if(GB < 0){
				return formatSize(MB) + "MB";
			}else{
				return formatSize(GB) + "GB";
			}
		}else{
			return formatSize(TB) + "TB";
		}
	}
	
	private String formatSize(double size){
		long result = (long)Math.ceil(size * 10);
		StringBuilder builder = new StringBuilder();
		builder.append(result / 10);
		if(result % 10 != 0){
			builder.append(".");
			builder.append(result % 10);
		}
		return builder.toString();
	}
	
	public void unmount(){
		//notify xunlei to unmount
		try{
			//TODO tfling
			/*
			IMountService mountService = IMountService.Stub.asInterface(
					ServiceManager.getService("mount"));
			mountService.unmountVolume(mRootPath, true, true);
			*/
		}catch (Exception e) {
//			DKLog.e(TAG, e.getMessage(), e);
		}
		
//		StatisticsUtil.logEvent(StatisticsUtil.EVENT_UNMOUNT_USB_DEVICE, 0, 0);
//		try{
////			Class<?> serviceManager = Class.forName("android.os.ServiceManager");
////			Method method = serviceManager.getDeclaredMethod("getService", String.class);
////			Class<?> mountService = Class.forName("android.os.storage.IMountService");
////			Class<?> stub = null;
////			Class<?>[] clazz = mountService.getDeclaredClasses();
////			for(int i = 0; i < clazz.length; i++){
////				if(clazz[i].getName().contains("Stub")){
////					stub = clazz[i];
////					break;
////				}
////			}
////			 ServiceManager.getService("mount");
////			Method asInterface = stub.getDeclaredMethod("asInterface", IBinder.class);
////			Method unmountMethod = mountService.getDeclaredMethod("unmountVolume", String.class,
////					Boolean.TYPE, Boolean.TYPE);
////			unmountMethod.invoke(asInterface.invoke(stub, method.invoke(serviceManager, "mount")), 
////					mRootPath, true, true);
//		}catch (Exception e) {
//			DKLog.e(TAG, e.getMessage(), e);
//		}
	}
	
	@Override
	public String getName() {
		if(TextUtils.isEmpty(mLabel)){
			return "u";
		}else{
			return mLabel;
		}
	}

	@Override
	public String getDesc() {
		String str = DKApp.getAppContext().getResources().getString(R.string.count_ge_media);
		str = String.format(str, mVideoSize);
		return str;
	}

    @Override
    public String getSubtitle() {
        // TODO Auto-generated method stub
        return null;
    }

//	@Override
//	public DeviceType getDeviceType() {
//		return DeviceType.DISK;
//	}
}
