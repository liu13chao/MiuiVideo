package com.miui.video;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.miui.video.adapter.BaseMediaListAdapter;
import com.miui.video.adapter.GridRowInfoAdapter;
import com.miui.video.base.BaseDelActivity;
import com.miui.video.controller.PlaySession;
import com.miui.video.controller.content.LocalContentBuilder;
import com.miui.video.local.PlayHistoryManager;
import com.miui.video.model.loader.LocalMediaLoader;
import com.miui.video.model.loader.LocalMediaLoader.OnLocalMediaLoadListener;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.LocalMedia;
import com.miui.video.type.LocalMediaList;
import com.miui.video.widget.EmptyView;

/**
 *@author tangfuling
 *
 */

public class LocalMediaActivity extends BaseDelActivity {
	
	//manager
	private LocalMediaLoader mLocalMediaLoader;
	private GridRowInfoAdapter<LocalMediaList> mLocalAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		loadData();
	}
	
	//init
	private void init() {
	    mLocalMediaLoader = LocalMediaLoader.getInstance();
	}
	
	private void loadData() {
		getLocalMedia();
	}
	
	private void getLocalMedia() {
		mLoadingListView.setShowLoading(true);
		mLocalMediaLoader.getLocalMedias(mOnLocalMediaLoadListener, true);
	}
	
	private void setData(ArrayList<LocalMediaList> localMedias){
	    refreshMediaList(localMedias);
	    mLocalAdapter.setDataList(localMedias);
	}
	
	//data callback
	private OnLocalMediaLoadListener mOnLocalMediaLoadListener = new OnLocalMediaLoadListener() {
		@Override
		public void onLocalMediaDone(ArrayList<LocalMediaList> localMedias) {
			mLoadingListView.setShowLoading(false);
			setData(localMedias);
		}
	};
	
    @Override
    protected void onDeleteClick() {
        List<BaseMediaInfo> selectedList = getSelectedMediaList();
        if(selectedList == null || selectedList.size() == 0) {
            return;
        }
        List<LocalMediaList> selectedLocalMedias = new ArrayList<LocalMediaList>();
        for(int i = 0; i < selectedList.size(); i++) {
            Object object = selectedList.get(i);
            if(object instanceof LocalMediaList) {
                LocalMediaList localMediaList = (LocalMediaList) object;
                selectedLocalMedias.add(localMediaList);
            }
        }
        mLocalMediaLoader.delLocalMediaLists(selectedLocalMedias);
        DKApp.getSingleton(PlayHistoryManager.class).delLocalMediaLists(selectedLocalMedias);
    }

    @Override
    protected CharSequence getPageTitle() {
        return getResources().getString(R.string.local_video);
    }

    @Override
    protected void onMediaItemClick(BaseMediaInfo mediaInfo) {
        if(mediaInfo instanceof LocalMediaList){
            LocalMediaList localMediaList = (LocalMediaList)mediaInfo;
            if(localMediaList.isDirType()) {
                Intent intent = new Intent(this,  LocalDetailActivity.class);
                intent.putExtra(LocalDetailActivity.KEY_LOCAL_MEDIA_LIST_PATH, localMediaList.getPath());
                LocalMediaActivity.this.startActivity(intent);
            } else {
                LocalMedia localMedia = localMediaList.get(0);
                if(localMedia != null) {
                    new PlaySession(LocalMediaActivity.this).startPlayerLocal(localMedia);
                }
            }
        }
    }

    @Override
    protected BaseMediaListAdapter<?> initListAdapter() {
        mLocalAdapter = new GridRowInfoAdapter<LocalMediaList>(this, 2, R.layout.media_view_grid_h);
        mLocalAdapter.setMediaContentBuilder(new LocalContentBuilder(this));
        return mLocalAdapter;
    }
    
    @Override
    protected View getEmptyView() {
        return new EmptyView(this, R.string.local_media_empty_title, 
                R.drawable.empty_icon_local);
    }
}
