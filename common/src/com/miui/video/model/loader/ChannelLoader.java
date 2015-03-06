package com.miui.video.model.loader;

import java.util.ArrayList;
import com.miui.video.api.DKApi;
import com.miui.video.model.DataStore;
import com.miui.video.response.ChannelListResponse;
import com.miui.video.type.Channel;
import com.xiaomi.mitv.common.webservice.ServiceRequest;
import com.xiaomi.mitv.common.webservice.ServiceResponse;
import com.xiaomi.mitv.common.webservice.ServiceRequest.Observer;

public class ChannelLoader extends DataLoader {

	private Channel[] mChannels = null;
	private DataStore mDataStore;
	
	// State flag
	private boolean mAsyncStorage = true;
	
	public ChannelLoader() {
		mDataStore = DataStore.getInstance();
	}
	
	public ArrayList<Channel> getChannels(){
		ArrayList<Channel> channels = new ArrayList<Channel>();
		if(mChannels != null) {
			for(int i = 0; i < mChannels.length; i++) {
				channels.add(mChannels[i]);
			}
		}
		return channels;
	}
	
	@Override
	public void load() {
	    if(mAsyncStorage){
	        new AsyncLoadTask().start();
	    }else{
	        onPreStorageLoad();
	        doStorageLoad();
	        onPostStorageLoad();
	    }
	}
	
	@Override
    public void onPreStorageLoad() {
        super.onPreStorageLoad();
    }

    @Override
    public void doStorageLoad() {
        mChannels = mDataStore.loadChannelList();
    }

    @Override
    public void onPostStorageLoad() {
        if(mChannels != null && mChannels.length > 0){
            if(mDataStore.isChannelsExpired()) {
                getChannelList();
            } else {
                notifyDataReady();
            }
        } else{
            getChannelList();
        }
    }

    private void getChannelList(){
		DKApi.getChannelList(-1, mChannelObserver);
	}
	
	public Observer mChannelObserver = new Observer() {
		@Override
		public void onRequestCompleted(ServiceRequest request, ServiceResponse response) {
			if(response.isSuccessful()){
				ChannelListResponse myResponse = (ChannelListResponse) response;
				mChannels = myResponse.data;
				new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(mChannels != null && mChannels.length > 0){
                            mDataStore.saveChannelList(mChannels);
                        }
                    }
                });
				notifyDataReady();
			} else {
				notifyDataFail();
			}
		}
		@Override
		public void onProgressUpdate(ServiceRequest arg0, int arg1) {
		}
	};
}
