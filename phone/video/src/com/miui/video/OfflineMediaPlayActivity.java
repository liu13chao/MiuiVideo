package com.miui.video;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.miui.video.adapter.BaseMediaListAdapter;
import com.miui.video.adapter.OfflinePlayAdapter;
import com.miui.video.base.BaseDelActivity;
import com.miui.video.controller.PlaySession;
import com.miui.video.offline.OfflineMedia;
import com.miui.video.offline.OfflineMediaList;
import com.miui.video.offline.OfflineMediaManager;
import com.miui.video.offline.OfflineMediaManager.OfflineMediasChangeListener;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.widget.EmptyView;

public class OfflineMediaPlayActivity extends BaseDelActivity {

	public static final String KEY_BUNDLE_DATA = "bundle.data";
	
	private OfflineMediaManager mOfflineMediaManager;
//	private OfflineplayAdapter mAdapter;
	private OfflineMediaList mMediaList;
	OfflineSelectEpView mOfflineSelectView;
	
	private OfflinePlayAdapter mAdapter = new OfflinePlayAdapter(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
		
	@Override
    protected void onStart() {
        super.onStart();
        mOfflineMediaManager.registerFinishedMediasChangeListener(
                mFinishedMediasChangeListener);
    }
	
	@Override
	protected void onStop() {
	    super.onStop();
	    mOfflineMediaManager.unregisterFinishedMediasChangeListener(mFinishedMediasChangeListener);
	}

	private void initIntentData(){
	    if(getIntent().getSerializableExtra(KEY_BUNDLE_DATA) instanceof OfflineMediaList){
	            mMediaList = (OfflineMediaList) getIntent().getSerializableExtra(KEY_BUNDLE_DATA);
	    }
	}
	
	private void init() {
	    initIntentData();
		initManager();
		initUI();
	}
	
	private void initUI() {
	    mAdapter.setOnMoreClickListener(mMoreClickListener);
	    if(mMediaList != null){
	        setTopTitle(mMediaList.getName());
	        initDataList(mMediaList.getAll());
	    }
	}
	
	private void initDataList(List<OfflineMedia> list){
        mAdapter.setDataList(list);
        refreshMediaList(list);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event){
		if(event.getAction() == KeyEvent.ACTION_DOWN && 
				event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			if(mOfflineSelectView != null && mOfflineSelectView.isShowing()){
				mOfflineSelectView.dismiss();
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}
	
	private void initManager() {
		mOfflineMediaManager = DKApp.getSingleton(OfflineMediaManager.class);
	}
	
    @Override
    protected CharSequence getPageTitle() {
        return "";
    }

    @Override
    protected void onMediaItemClick(BaseMediaInfo mediaInfo) {
        if(mediaInfo instanceof OfflineMedia){
            new PlaySession(this).startPlayerOffline((OfflineMedia)mediaInfo);
        }
    }

    @Override
    protected BaseMediaListAdapter<?> initListAdapter() {
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
    
    private OnClickListener mMoreClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mMediaList != null){
                Intent intent = new Intent(OfflineMediaPlayActivity.this, OfflineSelectEpView.class);
                intent.putExtra(OfflineSelectEpView.KEY_MEDIA_ID, mMediaList.getMediaId());
                mOfflineSelectView = new OfflineSelectEpView(OfflineMediaPlayActivity.this, intent);
                mOfflineSelectView.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            }
        }
    };
    
    private OfflineMediasChangeListener mFinishedMediasChangeListener = new OfflineMediasChangeListener() {
        @Override
        public void onOfflineMediasChange(List<OfflineMedia> medias) {
            if(mMediaList != null){
                mMediaList.setAll(medias);
                initDataList(mMediaList.getAll());
            }
        }
    };

}
