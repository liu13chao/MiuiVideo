package com.miui.video.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.RemoteService;
import org.teleal.cling.model.types.UDAServiceType;
import org.teleal.cling.support.contentdirectory.callback.Browse;
import org.teleal.cling.support.model.BrowseFlag;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;

import android.content.Context;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.util.DKLog;


public class DLNADevice extends BaseDevice {
    private static final long serialVersionUID = 1L;

    private static String TAG = "MediaExplorer.DLNADeviceItem";	
	private RemoteDevice mDlnaRemoteDevice;
	private final ControlPoint mControlPoint;
	private Context mContext = null;
	//for keep all the parent id for history goto folder feature
	private HashMap<String, DLNADirMediaItem> mDLNADirMediaItemMap = new HashMap<String, DLNADirMediaItem>();
	
	public DLNADevice(Context context, RemoteDevice device, ControlPoint controlPoint) {
		mContext = context;
		mDlnaRemoteDevice = device;
		mControlPoint = controlPoint;
		mPriority = 300;
	}
	
	public void setDevice(RemoteDevice device) {
		mDlnaRemoteDevice = device;
	}

	@Override
	public boolean isRemoveable() {
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DLNADevice that = (DLNADevice) o;
		return mDlnaRemoteDevice.equals(that.mDlnaRemoteDevice);
	}
	
	@Override
	public void startBrowsing(final DeviceScanTask scanTask, final OnBrowseCompleteListener listener) {
//		MILog.i(TAG, "startBrowsing, path: " + browerable.getPath());
		
		final UDAServiceType udaType = new UDAServiceType("ContentDirectory");
		final RemoteService contentDirectoryService = mDlnaRemoteDevice.findService(udaType);
        if (contentDirectoryService != null) {
        	
        	Browse browseAction = new Browse(contentDirectoryService, scanTask.getPath(), 
        			BrowseFlag.DIRECT_CHILDREN) {
				@SuppressWarnings("rawtypes")
				@Override
				public void received(ActionInvocation ai, DIDLContent didl) {
					DKLog.i(TAG, "DLNADevice received.");
					List<Item> didlItems = didl.getItems();
					
					List<String> parentList = new ArrayList<String>();
					if(didlItems.size() > 0){
						parentList = getParentList(didlItems.get(0));
						parentList.add(0, "0");
					}
					
					ArrayList<MediaItem> mediaList = new ArrayList<MediaItem>();
					ArrayList<MediaItem> dirMediaList = new ArrayList<MediaItem>();
					DLNAMediaItem mediaItem = null;
					for (Item didlItem : didlItems) {
//						MILog.i(TAG, "DLNADevice didlitem parent id: " + didlItem.getParentID());
//						MILog.i(TAG, "DLNADevice didlitem id: " + didlItem.getId());
						if(scanTask.isStopped()){
//							MILog.i(TAG, "stopTask : " + browerable.getPath());
							return;
						}
						if(scanTask.isPaused()){
//							MILog.i(TAG, "pauseTask : " + browerable.getPath());
							scanTask.pauseTask();
//							MILog.i(TAG, "resumeTask : " + browerable.getPath());
						}
						mediaItem = new DLNAMediaItem(mContext, didlItem);
//						MILog.i(TAG, "DLNA mediaItem: " + mediaItem.getName());
						if(mediaItem.isApply()) {
							mediaItem.addParentList(parentList);
							mediaList.add(mediaItem);
						}
					}
//					MILog.i(TAG, "onBrowseFileReady: " + mediaList.size());
					listener.onBrowseFileReady(mediaList);
//					OrderUtil.orderItems(mediaList, browerable);
//					MILog.i(TAG, "onBrowseFileComplete: " + mediaList.size());
					listener.onBrowseFileComplete(mediaList);
					
					List<Container> containers = didl.getContainers();
					DLNADirMediaItem dlnaDirMediaItem = null;
					for (Container container : containers) {
						
//						MILog.i(TAG, "DLNADevice container parent id: " + container.getParentID());
//						MILog.i(TAG, "DLNADevice container id: " + container.getId());
						
						if(scanTask.isPaused()){
//							MILog.i(TAG, "pauseTask : " + browerable.getPath());
							scanTask.pauseTask();
//							MILog.i(TAG, "resumeTask : " + browerable.getPath());
						}
						if(scanTask.isStopped()){
//							MILog.i(TAG, "stopTask : " + browerable.getPath());
							return;
						}
						dlnaDirMediaItem = new DLNADirMediaItem(mContext, container);
						mDLNADirMediaItemMap.put(dlnaDirMediaItem.getPath(), dlnaDirMediaItem);
						dirMediaList.add(dlnaDirMediaItem);
					}
//					OrderUtil.orderItems(dirMediaList, browerable);
					listener.onBrowseDirComplete(dirMediaList);
//					MILog.i(TAG, "onBrowseCompelete: ");
					listener.onBrowseCompelete();
				}

				private List<String> getParentList(Item item) {
					List<String> parentIds = new ArrayList<String>();
					
					boolean findRoot = false;
					String pId = item.getParentID();
					
					while(!findRoot){
						DLNADirMediaItem dirItem = mDLNADirMediaItemMap.get(pId);
						if(dirItem == null){
							findRoot = true;
						}else{
							parentIds.add(0, dirItem.getPath());
							pId = dirItem.getParentId();
						}
					}
					return parentIds;
				}

				@SuppressWarnings("rawtypes")
				@Override
				public void failure(ActionInvocation arg0, UpnpResponse arg1,String arg2) {
//					MILog.d(TAG, "browse failure, arg0=" + arg0 + ", arg1=" + arg1 + ", arg2" + arg2);
					listener.onBrowseFail(FAIL_REASON_OTHER);
				}
				
				@Override
				public void updateStatus(Status arg0) {
				}
        	};
        	browseAction.setControlPoint(mControlPoint);
        	browseAction.run();
		}
		
	}

	@Override
	public String getRootPath() {
		return new String("0");
	}
	
	@Override
	public String getName() {
	    if(mDlnaRemoteDevice != null && mDlnaRemoteDevice.getDetails() != null){
	        return mDlnaRemoteDevice.getDetails().getFriendlyName();
	    }
	    return "";
	}

	@Override
	public String getDesc() {
		String str = DKApp.getAppContext().getResources().getString(R.string.count_ge_media);
		str = String.format(str, mVideoSize);
		return str;
	}
}
