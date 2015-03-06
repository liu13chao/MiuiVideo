package com.miui.video.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;

import com.miui.video.R;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.util.ViewUtils;
import com.miui.video.widget.actionmode.ActionDeleteView;

/**
 *@author tangfuling
 *
 */

public abstract class BaseDelActivity extends BaseMediaListActivity{

    private ActionDeleteView mDeleteActionMode;
    
    protected List<BaseMediaInfo> mMediaInfos = new ArrayList<BaseMediaInfo>();
    protected List<BaseMediaInfo> mSelectedMedias = new ArrayList<BaseMediaInfo>();

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionMode();
    }
    
    public boolean isInEditMode(){
        return mDeleteActionMode.isInEditMode();
    }
    
    private void initActionMode() {
        mDeleteActionMode = new ActionDeleteView(this, mDeleteCallback);
        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.MATCH_PARENT);
        mDeleteActionMode.setLayoutParams(params);
        viewGroup.addView(mDeleteActionMode);
        mBtnAction.setText(R.string.edit);
        hideEditButton();
    }
    
    protected void startActionMode() {
        if(!mDeleteActionMode.isInEditMode()) {
            mDeleteActionMode.startActionMode();
        }
        mBtnAction.setText(R.string.cancel);
        if(mAdapter != null){
            mAdapter.setInEditMode(true);
            mAdapter.refresh();
        }
        onEnterEditMode();
    }
    
    protected void exitActionMode() {
        if(mDeleteActionMode.isInEditMode()) {
            mDeleteActionMode.exitActionMode();
        }
        mBtnAction.setText(R.string.edit);
        clearAllSelected();
        refreshPage();
        onExitEditMode();
        if(mAdapter != null){
            mAdapter.setInEditMode(false);
            mAdapter.refresh();
        }
    }
    
    private void selectAll(){
        mSelectedMedias.clear();
        for(int i = 0; i < mMediaInfos.size(); i++) {
            BaseMediaInfo baseMediaInfo = mMediaInfos.get(i);
            baseMediaInfo.mIsSelected = true;
            mSelectedMedias.add(baseMediaInfo);
        }
        onSelectAll();
        refreshPage();
    }
    
    private void unSelectAll(){
        clearAllSelected();
        onUnSelectAll();
        refreshPage();
    }
    
    private void clearAllSelected(){
        for(int i = 0; i < mMediaInfos.size(); i++) {
            BaseMediaInfo baseMediaInfo = mMediaInfos.get(i);
            baseMediaInfo.mIsSelected = false;
        }
        mSelectedMedias.clear();
    }
    
    protected void toggleMediaInfo(BaseMediaInfo baseMediaInfo){
        if(baseMediaInfo == null) {
            return;
        }
        baseMediaInfo.mIsSelected = !baseMediaInfo.mIsSelected;
        if(baseMediaInfo.mIsSelected) {
            mSelectedMedias.add(baseMediaInfo);
        } else {
            mSelectedMedias.remove(baseMediaInfo);
        }
        refreshPage();
    }
    
    public void refreshMediaList(List<? extends BaseMediaInfo> list){
        if(list == null){
            return;
        }
        HashMap<BaseMediaInfo, Boolean> status = new HashMap<BaseMediaInfo, Boolean>();
        for(BaseMediaInfo mediaInfo : mSelectedMedias){
            if(mediaInfo != null){
                status.put(mediaInfo, mediaInfo.mIsSelected);
            }
        }
        mMediaInfos.clear();
        mSelectedMedias.clear();
        for(BaseMediaInfo mediaInfo : list){
            if(mediaInfo != null){
                if(status.containsKey(mediaInfo) && status.get(mediaInfo)){
                    mediaInfo.mIsSelected = true;
                    mSelectedMedias.add(mediaInfo);
                }
            }
        }
        mMediaInfos.addAll(list);
        if(mMediaInfos.size() > 0){
            showEditButton();
        }else{
            hideEditButton();
        }
        refreshPage();
    }
    
    public List<BaseMediaInfo> getSelectedMediaList(){
        return mSelectedMedias;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends BaseMediaInfo> List<T> getSelectedMediaList(Class<T > clazz){
        List<T> list = new ArrayList<T>();
        for(BaseMediaInfo media : mSelectedMedias){
            try{
                if(media != null){
                    list.add((T)media);
                }
            }catch(Exception e){
            }
        }
        return list;
    }
    
    protected void selectMediaInfo(BaseMediaInfo baseMediaInfo){
        if(baseMediaInfo == null) {
            return;
        }
        baseMediaInfo.mIsSelected = true;
        if(baseMediaInfo.mIsSelected) {
            mSelectedMedias.add(baseMediaInfo);
        }
        refreshPage();
    }
    
    private void refreshPage(){
        if(mSelectedMedias.size() == mMediaInfos.size()) {
            mDeleteActionMode.setUIUnSelectAll();
        } else {
            mDeleteActionMode.setUISelectAll();
        }
        mDeleteActionMode.setSelectCount(mSelectedMedias.size());
        if(mAdapter != null){
            mAdapter.refresh();
        }
//        onPageInvalidate();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(isInEditMode()){
                exitActionMode();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    protected  void onEnterEditMode(){
    }
    
    protected  void onExitEditMode(){
    }
    
    protected void onSelectAll(){
    }
    
    protected void onUnSelectAll(){
    }
    
    protected abstract void onDeleteClick();
    protected abstract void onMediaItemClick(BaseMediaInfo mediaInfo);

	
	private ActionDeleteView.Callback mDeleteCallback = new ActionDeleteView.Callback(){
        @Override
        public void onActionDeleteClick() {
            onDeleteClick();
            exitActionMode();
            mSelectedMedias.clear();
            if(mAdapter != null){
                mAdapter.refresh();
            }
        }
        @Override
        public void onActionSelectAll() {
            selectAll();
        }

        @Override
        public void onActionUnSelectAll() {
            unSelectAll();
        }
	};


    @Override
    protected void onActionClick() {
        if(isInEditMode()){
            exitActionMode();
        }else{
            startActionMode();
        }
    }

    protected void showEditButton(){
        ViewUtils.showView(mBtnAction);
    }
    
    protected void hideEditButton(){
        ViewUtils.hideView(mBtnAction);
    }

    @Override
    final protected void onItemClick(BaseMediaInfo mediaInfo) {
        if(isInEditMode()){
            toggleMediaInfo(mediaInfo);
        }else{
            onMediaItemClick(mediaInfo);
        }
    }

    @Override
    final protected void onItemLongClick(BaseMediaInfo mediaInfo) {
        if(!isInEditMode()) {
            selectMediaInfo(mediaInfo);
            startActionMode();
        }else{
            toggleMediaInfo(mediaInfo);
        }
    }
    
    
}
