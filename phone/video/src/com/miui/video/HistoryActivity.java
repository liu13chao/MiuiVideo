package com.miui.video;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.miui.video.adapter.BaseMediaListAdapter;
import com.miui.video.adapter.PlayHistoryListAdapter;
import com.miui.video.base.BaseDelActivity;
import com.miui.video.controller.PlaySession;
import com.miui.video.controller.content.HistoryContentBuilder;
import com.miui.video.info.InfoPlayUtil;
import com.miui.video.local.LocalPlayHistory;
import com.miui.video.local.OnlinePlayHistory;
import com.miui.video.local.PlayHistory;
import com.miui.video.local.PlayHistoryManager;
import com.miui.video.local.PlayHistoryManager.OnHistoryChangedListener;
import com.miui.video.statistic.SourceTagValueDef;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.InformationData;
import com.miui.video.type.LocalMedia;
import com.miui.video.type.LocalMediaList;
import com.miui.video.type.MediaInfo;
import com.miui.video.util.DKLog;
import com.miui.video.widget.EmptyView;

/**
 *@author tangfuling
 *
 */

public class HistoryActivity extends BaseDelActivity {
	
	private final String TAG = HistoryActivity.class.getName();
	
	private PlayHistoryListAdapter mAdapter;
	
	private PlayHistoryManager mPlayHisManager;
	
	public static final String KEY_TITLE = "title";
	private String mTitle;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mPlayHisManager.loadPlayHistory();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mPlayHisManager.removeListener(mOnHistoryChangedListener);
	}
	
	//init
	private void init() {
		initManagers();
		initData();
	}
	
	private void initManagers() {
	    mPlayHisManager = DKApp.getSingleton(PlayHistoryManager.class);
	    mPlayHisManager.addListener(mOnHistoryChangedListener);
	}
	
	private void initData() {
	    preparePlayHistoryList();
	    mTitle = getIntent().getStringExtra(KEY_TITLE);
	    if(!TextUtils.isEmpty(mTitle)){
	        setTopTitle(mTitle);
	    }
	}
	
	//get data
	private void preparePlayHistoryList() {
		DKLog.d(TAG, "get play his medias");
		refreshMediaList(mPlayHisManager.getPlayHisList());
		mAdapter.setPlayHistoryMap(mPlayHisManager.getHistoryListMap());
	}
	
	//packaged method
	private void startPlayOnlineHistory(OnlinePlayHistory playHistory) {
		if(playHistory == null) {
			return;
		}
		Object obj = playHistory.getPlayItem();
		if(obj instanceof MediaInfo) {
			MediaInfo mediaInfo = (MediaInfo) obj;
			Intent intent = new Intent();
			intent.setClass(this, MediaDetailActivity.class);
			intent.putExtra(MediaDetailActivity.KEY_MEDIA_INFO, mediaInfo);
			intent.putExtra(MediaDetailActivity.KEY_IS_BANNER, false);
			intent.putExtra(MediaDetailActivity.KEY_SOURCE_PATH, SourceTagValueDef.PHONE_V6_PLAY_HIS_VALUE);
			this.startActivity(intent);
		} else if(obj instanceof InformationData) {
			InformationData informationData = (InformationData) obj;
			InfoPlayUtil.playInformation(HistoryActivity.this, informationData, 
					SourceTagValueDef.PHONE_V6_BANNER_VALUE);
		}
	}

	//data callback
	private OnHistoryChangedListener mOnHistoryChangedListener = new OnHistoryChangedListener() {
		
		@Override
		public void onHistoryChanged(List<PlayHistory> historyList) {
			DKLog.d(TAG, "on play his changed");
			preparePlayHistoryList();
		}
	};

    @Override
    protected void onDeleteClick() {
        List<BaseMediaInfo> selectedList = getSelectedMediaList();
        if(selectedList.size() == 0) {
            return;
        }
        List<PlayHistory> list = new ArrayList<PlayHistory>();
        for(int i = 0; i < selectedList.size(); i++) {
            BaseMediaInfo baseMediaInfo = selectedList.get(i);
            if(baseMediaInfo instanceof PlayHistory) {
                list.add((PlayHistory)baseMediaInfo);
            }
        }
        mPlayHisManager.delPlayHistoryList(list);
    }

    @Override
    protected CharSequence getPageTitle() {
        return getResources().getString(R.string.play_history);
    }

    @Override
    protected BaseMediaListAdapter<?> initListAdapter() {
        mAdapter = new PlayHistoryListAdapter(this);
        mAdapter.setMediaContentBuilder(new HistoryContentBuilder(this));
        return mAdapter;
    }

    @Override
    protected void onMediaItemClick(BaseMediaInfo mediaInfo) {
        if(mediaInfo instanceof OnlinePlayHistory) {
            OnlinePlayHistory onlinePlayHis = (OnlinePlayHistory) mediaInfo;
            startPlayOnlineHistory(onlinePlayHis);
        } else if(mediaInfo instanceof LocalPlayHistory) {
            LocalPlayHistory localPlayHistory = (LocalPlayHistory) mediaInfo;
            Object content = localPlayHistory.getPlayItem();
            if(content instanceof LocalMediaList) {
                LocalMediaList localMediaList = (LocalMediaList) content;
                if(localMediaList.size() > 1) {
                    Intent intent = new Intent();
                    intent.setClass(HistoryActivity.this, LocalDetailActivity.class);
                    intent.putExtra(LocalDetailActivity.KEY_LOCAL_MEDIA_LIST_PATH, localMediaList);
                    HistoryActivity.this.startActivity(intent);
                } else {
                    LocalMedia localMedia = localMediaList.get(0);
                    if(localMedia != null) {
                        new PlaySession(HistoryActivity.this).startPlayerLocal(localMedia);
                    }
                }
            } else if(content instanceof LocalMedia) {
                LocalMedia localMedia = (LocalMedia) content;
                new PlaySession(HistoryActivity.this).startPlayerLocal(localMedia);
            }
        }
    }

    @Override
    protected View getEmptyView() {
        return new EmptyView(this, R.string.play_his_empty_title, 
                R.drawable.empty_icon_play_his);
    }
}
