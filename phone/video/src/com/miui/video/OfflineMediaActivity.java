package com.miui.video;

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.miui.video.adapter.BaseMediaListAdapter;
import com.miui.video.adapter.ClassifyMediaListAdapter;
import com.miui.video.base.BaseDelActivity;
import com.miui.video.controller.PlaySession;
import com.miui.video.controller.content.OfflineContentBuilder;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.offline.OfflineMediaList;
import com.miui.video.offline.OfflineMediaManager;
import com.miui.video.offline.OfflineMediaManager.OfflineMediasChangeListener;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.widget.EmptyView;
import com.xiaomi.common.util.Strings;

public class OfflineMediaActivity extends BaseDelActivity {

	//UI
	private View mLoadingBar;
	private TextView mLoadingCountTextView;
	private TextView mLoadingProgressTextView;
	
	private int mLoadingCount = 0;
	
	private ClassifyMediaListAdapter<OfflineMediaList> mAdapter;
	
	private EmptyView mEmptyView;
	
	//manager
	private OfflineMediaManager mOfflineMediaManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		initData();
		mOfflineMediaManager.registerUnfinishedMediasChangeListener(mUnfinishedMediasChangeListener);
		mOfflineMediaManager.registerFinishedMediasChangeListener(mFinishedMediasChangeListener);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mOfflineMediaManager.unregisterUnfinishedMediasChangeListener(mUnfinishedMediasChangeListener);
		mOfflineMediaManager.unregisterFinishedMediasChangeListener(mFinishedMediasChangeListener);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private void init() {
		initManager();
		initUI();
	}
	
	private void initManager() {
		mOfflineMediaManager = DKApp.getSingleton(OfflineMediaManager.class);
	}
	
	private void initUI() {
	    mLoadingBar = View.inflate(this, R.layout.offline_media_loading_bar, null);
	    mLoadingBar.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            if (mLoadingCount > 0) {
	                Intent intent = new Intent(OfflineMediaActivity.this, OfflineLoadingActivity.class);
	                startActivity(intent);
	            }
	        }
	    });
	    mLoadingCountTextView = (TextView) mLoadingBar.findViewById(R.id.offline_media_bar_title);
	    mLoadingProgressTextView = (TextView) mLoadingBar.findViewById(R.id.offline_media_bar_subtitle);
	    mLoadingBar.setVisibility(View.GONE);
	}
	
	private void setLoadingCount(int count) {
		mLoadingCountTextView.setText(getResources().getString(
				R.string.download_task_with, count));
	}
	
	private void setLoadingProgress(long completed, long total) {
	    if(mLoadingCountTextView != null){
	        mLoadingProgressTextView.setText(getResources().getString(
	                R.string.download_with, Strings.formatSize(completed),
	                Strings.formatSize(total)));
	    }
	}
	
	private void detachLoadingBar(){
	    if(mLoadingBar.getVisibility() == View.VISIBLE){
	        mLoadingBar.setVisibility(View.GONE);
	        mListView.removeHeaderView(mLoadingBar);
	        mLoadingListView.setEmptyView(mEmptyView);
	    }
	}
	
	private void attachLoadingBar(){
	    if(mLoadingBar.getVisibility() != View.VISIBLE){
            mLoadingBar.setVisibility(View.VISIBLE);
            mListView.addHeaderView(mLoadingBar);
            mLoadingListView.setEmptyView(null);
        }
	}
	
	private void initData(){
	    initDoneList(mOfflineMediaManager.getFinishedMedias());
	    initLoadingList(mOfflineMediaManager.getUnfinishedMedias());
	}
	
	private void initDoneList(List<OfflineMedia> medias){
	    if(medias != null){
	        List<OfflineMediaList> list = OfflineMediaList.group(medias);
	        refreshMediaList(list);
	        HashMap<String, List<OfflineMediaList>> group = new 
	                HashMap<String, List<OfflineMediaList>>();
	        group.put(getResources().getString(R.string.offline_loaded), list);
	        mAdapter.setData(group);
	    }
	}
	
	private void initLoadingList(List<OfflineMedia> medias){
        if(medias != null){
            mLoadingCount = medias.size();
            if (mLoadingCount <= 0) {
                detachLoadingBar();
            } else {
                attachLoadingBar();
                setLoadingCount(mLoadingCount);
                long completeSize = 0L;
                long totalSize = 0L;
                for (OfflineMedia media : medias) {
                    if (media != null) {
                        completeSize += media.completeSize;
                        totalSize += media.fileSize;
                    }
                }
                setLoadingProgress(completeSize, totalSize);
            }
        }
    }
	
	private OfflineMediasChangeListener mUnfinishedMediasChangeListener = new OfflineMediasChangeListener() {
		@Override
		public void onOfflineMediasChange(List<OfflineMedia> medias) {
		    initLoadingList(medias);
		}
	};
	
	private OfflineMediasChangeListener mFinishedMediasChangeListener = new OfflineMediasChangeListener() {
		@Override
		public void onOfflineMediasChange(List<OfflineMedia> medias) {
		    initDoneList(medias);
		}
	};

    @Override
    protected int getContentViewRes() {
        return R.layout.activity_offline_media;
    }

    @Override
    protected CharSequence getPageTitle() {
        return getString(R.string.offline_media);
    }

    @Override
    protected void onMediaItemClick(BaseMediaInfo mediaInfo) {
        if(mediaInfo instanceof OfflineMediaList){
            OfflineMediaList mediaList = (OfflineMediaList)mediaInfo;
            if (mediaList.size() == 1 && !mediaList.get(0).isMultiSetType()) {
                new PlaySession(this).startPlayerOffline(mediaList.get(0));
            } else{
                Intent itent = new Intent(OfflineMediaActivity.this, OfflineMediaPlayActivity.class);
                itent.putExtra(OfflineMediaPlayActivity.KEY_BUNDLE_DATA, mediaList);
                startActivity(itent);
            }
        }
    }

    @Override
    protected BaseMediaListAdapter<?> initListAdapter() {
        mAdapter = new ClassifyMediaListAdapter<OfflineMediaList>(this,
                3, R.layout.mixed_media_view);
        mAdapter.setMediaContentBuilder(new OfflineContentBuilder(this));
        return mAdapter;
    }

    @Override
    protected View getEmptyView() {
        if(mEmptyView == null){
            mEmptyView = new EmptyView(this, R.string.offline_media_empty_title, 
                    R.string.local_media_empty_sub_title, R.drawable.empty_icon_offline);
        }
        return mEmptyView;
    }

    @Override
    protected void onDeleteClick() {
        mOfflineMediaManager.deleteMedias(OfflineMediaList.ungroup(
                getSelectedMediaList(OfflineMediaList.class)));
    }
    
}
