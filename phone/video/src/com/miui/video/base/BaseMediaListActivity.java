/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   BaseMediaListActivity.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-17
 */
package com.miui.video.base;

import android.os.Bundle;
import android.view.View;

import com.miui.video.R;
import com.miui.video.adapter.BaseMediaListAdapter;
import com.miui.video.controller.MediaViewClickListener;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.widget.ListViewEx;
import com.miui.video.widget.LoadingListView;

/**
 * @author tianli
 *
 */
public abstract class BaseMediaListActivity extends BaseTitleActivity {

    //UI
    protected LoadingListView mLoadingListView;
    protected ListViewEx mListView;
    protected View mLoadingView;
    protected View mEmptyView;

    // Data
    protected BaseMediaListAdapter<?> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        initUI();
    }

    private void initUI() {
        initTitleTop();
        initListView();
    }

    private void initTitleTop() {
        setTopTitle(getPageTitle());
    }

    private void initListView() {
        mLoadingListView = (LoadingListView) findViewById(R.id.media_list);
        mListView = mLoadingListView.getListView();
        mListView.setClipToPadding(false);
        int paddingLeft = getResources().getDimensionPixelSize(R.dimen.page_margin);
        int paddingTop = getResources().getDimensionPixelSize(R.dimen.page_margin_top);
        mListView.setPadding(paddingLeft, paddingTop,  paddingLeft, paddingTop);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setSelector(R.drawable.transparent);
        mAdapter = initListAdapter();
        mAdapter.setMediaViewClickListener(mClickListener);
        mListView.setAdapter(mAdapter);
        mLoadingView = View.inflate(this, R.layout.load_view, null);
        mLoadingListView.setLoadingView(mLoadingView);

        mLoadingListView.setEmptyView(getEmptyView());
    }

    @Override
    protected int getContentViewRes() {
        return R.layout.activity_media_info_list;
    }

    protected abstract CharSequence getPageTitle();
    
    protected abstract void onItemClick(BaseMediaInfo mediaInfo);
    
    protected abstract void onItemLongClick(BaseMediaInfo mediaInfo);

    protected abstract BaseMediaListAdapter<?> initListAdapter();
    
    protected abstract View getEmptyView();
    
    public MediaViewClickListener mClickListener = new MediaViewClickListener() {
        @Override
        public void onMediaLongClick(View view, BaseMediaInfo media) {
            onItemLongClick(media);
        }

        @Override
        public void onMediaClick(View view, BaseMediaInfo media) {
            onItemClick(media);
        }
    };
}