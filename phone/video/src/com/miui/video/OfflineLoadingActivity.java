package com.miui.video;

import java.util.List;

import android.os.Bundle;
import android.view.View;

import com.miui.video.adapter.BaseMediaListAdapter;
import com.miui.video.adapter.SimpleListRowInfoAdapter;
import com.miui.video.base.BaseDelActivity;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.offline.OfflineMediaManager;
import com.miui.video.offline.OfflineMediaManager.OfflineMediasChangeListener;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.widget.EmptyView;

public class OfflineLoadingActivity extends BaseDelActivity {

	//manager
	private OfflineMediaManager mOfflineMediaManager;
	
	private SimpleListRowInfoAdapter<OfflineMedia>  mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mOfflineMediaManager.registerUnfinishedMediasChangeListener(mMediasChangeListener);
		refreshData();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mOfflineMediaManager.unregisterUnfinishedMediasChangeListener(mMediasChangeListener);
	}
	
	//init
	private void init() {
		initManager();
	}
	
	private void initManager() {
		mOfflineMediaManager = DKApp.getSingleton(OfflineMediaManager.class);
	}
	
	private void refreshData(){
	    initLoadingList(mOfflineMediaManager.getUnfinishedMedias());
	}
	
	private void initLoadingList(List<OfflineMedia> list){
	    refreshMediaList(list);
	    mAdapter.setDataList(list);
	}
	
	private OfflineMediasChangeListener mMediasChangeListener = new OfflineMediasChangeListener() {
		
		@Override
		public void onOfflineMediasChange(List<OfflineMedia> medias) {
		    initLoadingList(medias);
		}
	};

	@Override
	protected int getContentViewRes() {
		return R.layout.activity_offline_media;
	}

    @Override
    protected CharSequence getPageTitle() {
        return getString(R.string.offline_loading);
    }

    @Override
    protected void onMediaItemClick(BaseMediaInfo mediaInfo) {
        if(mediaInfo instanceof OfflineMedia){
            OfflineMedia media = (OfflineMedia)mediaInfo;
            if ( media.isUnrecovrableError()) {
                mOfflineMediaManager.deleteMedia(media);
            } else if (media.isLoading() || media.isWaiting()) {
                mOfflineMediaManager.pauseDownloader(media);
            } else {
                mOfflineMediaManager.startDownloader(media);
            }
        }
    }

    @Override
    protected BaseMediaListAdapter<?> initListAdapter() {
        mAdapter = new SimpleListRowInfoAdapter<OfflineMedia>(this, R.layout.offline_loading_media_view);
        return mAdapter;
    }

    @Override
    protected View getEmptyView() {
        return new EmptyView(this, R.string.offline_media_empty_title, 
                R.string.local_media_empty_sub_title, R.drawable.empty_icon_offline);
    }

    @Override
    protected void onDeleteClick() {
        mOfflineMediaManager.deleteMedias(getSelectedMediaList(OfflineMedia.class));
    }

}
