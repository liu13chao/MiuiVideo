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

public class LocalDetailActivity extends BaseDelActivity {

    public static String KEY_LOCAL_MEDIA_LIST_PATH = "local_media_list_path";

    //received data
    private String mTitleName = "";
    private String mLocalMediaListPath = "";

    //manager
    private LocalMediaLoader mLocalMediaLoader;

    private GridRowInfoAdapter< LocalMedia> mLocalAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    //init
    private void init() {
        initReceivedData();
        initLocalMedias();
    }

    private void initReceivedData() {
        Intent intent = getIntent();
        mLocalMediaListPath = intent.getStringExtra(KEY_LOCAL_MEDIA_LIST_PATH);
    }

    private void initLocalMedias() {
        mLocalMediaLoader = LocalMediaLoader.getInstance();
        mLocalMediaLoader.getLocalMedias(mOnLocalMediaLoadListener, false);
    }

    //packaged method
    private void refreshTitleName() {
        setTopTitle(mTitleName);
    }

    private LocalMediaList getCurLocalMediaList(List<LocalMediaList> localMedias) {
        if(localMedias != null) {
            for(int i = 0; i < localMedias.size(); i++) {
                LocalMediaList localMediaList = localMedias.get(i);
                if(localMediaList != null) {
                    String path = localMediaList.getPath();
                    if(path != null && path.equals(mLocalMediaListPath)) {
                        return localMediaList;
                    }
                }
            }
        }
        return null;
    }

    private void setDataList(List<LocalMedia> list){
        refreshMediaList(list);
        mLocalAdapter.setDataList(list);
    }

    //data callback
    private OnLocalMediaLoadListener mOnLocalMediaLoadListener = new OnLocalMediaLoadListener() {

        @Override
        public void onLocalMediaDone(ArrayList<LocalMediaList> localMedias) {
            LocalMediaList curLocalMediaList = getCurLocalMediaList(localMedias);
            if(curLocalMediaList != null) {
                mTitleName = curLocalMediaList.getName();
                refreshTitleName();
                setDataList(curLocalMediaList.getLocalMediaList());
            }else{
                setDataList(new ArrayList<LocalMedia>());
            }
        }
    };

    @Override
    protected void onDeleteClick() {
        List<BaseMediaInfo> selectedList = getSelectedMediaList();
        if(selectedList == null || selectedList.size() == 0) {
            return;
        }
        List<LocalMedia> selectedLocalMedias = new ArrayList<LocalMedia>();
        for(int i = 0; i < selectedList.size(); i++) {
            Object object = selectedList.get(i);
            if(object instanceof LocalMedia) {
                LocalMedia localMedia = (LocalMedia) object;
                selectedLocalMedias.add(localMedia);
            }
        }
        mLocalMediaLoader.delLocalMedias(selectedLocalMedias);
        DKApp.getSingleton(PlayHistoryManager.class).delLocalMedias(selectedLocalMedias);
    }

    @Override
    protected CharSequence getPageTitle() {
        return "";
    }

    @Override
    protected void onMediaItemClick(BaseMediaInfo mediaInfo) {
        if(mediaInfo instanceof LocalMedia){
            new PlaySession(LocalDetailActivity.this).startPlayerLocal((LocalMedia)mediaInfo);
        }
    }

    @Override
    protected BaseMediaListAdapter<?> initListAdapter() {
        mLocalAdapter = new GridRowInfoAdapter<LocalMedia>(this, 2, R.layout.media_view_grid_h);
        mLocalAdapter.setMediaContentBuilder(new LocalContentBuilder(this));
        return mLocalAdapter;
    }

    @Override
    protected View getEmptyView() {
        return new EmptyView(this, R.string.local_media_empty_sub_title, 
                R.drawable.empty_icon_local);
    }
}
