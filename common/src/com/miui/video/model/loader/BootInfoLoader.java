package com.miui.video.model.loader;

import com.miui.video.DKApp;
import com.miui.video.api.DKApi;
import com.miui.video.model.DataStore;
import com.miui.video.model.DeviceInfo;
import com.miui.video.response.BootResponse;
import com.miui.video.type.BootResponseInfo;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;
import com.xiaomi.mitv.common.webservice.ServiceResponse;

public class BootInfoLoader extends DataLoader {

	private DataStore mDataStore;
	private BootResponseInfo mBootResponseInfo;
	private OnDownloadSoInBootListener downloadListener; 
	
	public BootInfoLoader() {
		mDataStore = DataStore.getInstance();
	}
	
	public BootResponseInfo getBootResponseInfo() {
		return mBootResponseInfo;
	}
	
	public void refreshBootResponseInfo() {
		getBootInfo();
	}
	
	@Override
	public void load() {
		(new AsyncLoadTask()).start();
	}
	
	//packaged method
	private void getBootInfo() {
		DeviceInfo deviceInfo = DKApp.getSingleton(DeviceInfo.class);
		DKApi.uploadIMEBootInfo(deviceInfo.getImeiMd5(),
				deviceInfo.getHashedAndroidId(), deviceInfo.getUserAgent(), 
				deviceInfo.getNetworkType(), deviceInfo.getDeviceType(),
				deviceInfo.getAppVersion(), mObserver);
	}
	
	//net data callback
	private Observer mObserver = new Observer() {
		
		@Override
		public void onProgressUpdate(ServiceRequest request, int progress) {
		}

		@Override
		public void onRequestCompleted(ServiceRequest request, ServiceResponse response) {
			if(response instanceof BootResponse) {
				if(response.isSuccessful()) {
					BootResponse bootResponse = (BootResponse) response;
					mBootResponseInfo = bootResponse.data;
					if(mBootResponseInfo != null) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								mDataStore.saveBootResponseInfo(mBootResponseInfo);
								if(downloadListener != null){
									downloadListener.downloadSohuSo();
								}
							}
						}).start();
					}
					notifyDataReady();
				} else {
					notifyDataFail();
				}
			}
		}
	};

	@Override
    public void doStorageLoad() {
        mBootResponseInfo = mDataStore.loadBootResponseInfo();
    }

    @Override
    public void onPostStorageLoad() {
        if(mBootResponseInfo != null) {
            notifyDataReady();
        }
    }

    @Override
    public void removeListener(LoadListener listener) {
    }

	public void setDownloadSoListener(OnDownloadSoInBootListener listener){
		downloadListener = listener;
	}
	
	public static interface OnDownloadSoInBootListener{
		public void downloadSohuSo();
	}
}
