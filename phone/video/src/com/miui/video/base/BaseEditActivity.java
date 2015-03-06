package com.miui.video.base;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.miui.video.R;
import com.miui.video.type.BaseMediaInfo;
import com.miui.video.widget.actionmode.ActionModeView;
import com.miui.video.widget.media.MediaView;

/**
 *@author tangfuling
 *
 */

public abstract class BaseEditActivity extends BaseTitleActivity {

    private ActionModeView mActionModeView;

    private List<BaseMediaInfo> mMediaInfos = new ArrayList<BaseMediaInfo>();
    private List<BaseMediaInfo> mSelectedMedias = new ArrayList<BaseMediaInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionMode();
    }

    protected void startActionMode() {
        if(mActionModeView != null && !mActionModeView.isVisible()) {
            mActionModeView.startActionMode();
        }
        setInEditMode(true);
        mBtnAction.setText(R.string.cancel);
    }

    protected void exitActionMode() {
        if(mActionModeView != null && mActionModeView.isVisible()) {
            mActionModeView.exitActionMode();
        }
        setNullMediaSelected();
        setInEditMode(false);
        mBtnAction.setText(R.string.edit);
    }

    protected boolean isActionModeViewDisplaying() {
        if(mActionModeView != null && mActionModeView.isVisible()) {
            return true;
        }
        return false;
    }

    protected void refresh() {
        refreshActionModeViewTitle(mSelectedMedias.size());
        refreshListView();
    }

    protected void setAllMediaSelected() {
        mSelectedMedias.clear();
        for(int i = 0; i < mMediaInfos.size(); i++) {
            BaseMediaInfo baseMediaInfo = mMediaInfos.get(i);
            baseMediaInfo.mIsSelected = true;
            mSelectedMedias.add(baseMediaInfo);
        }
        setUiSelectNull();
        refresh();
    }

    protected void setNullMediaSelected() {
        for(int i = 0; i < mMediaInfos.size(); i++) {
            BaseMediaInfo baseMediaInfo = mMediaInfos.get(i);
            baseMediaInfo.mIsSelected = false;
        }
        mSelectedMedias.clear();
        setUiSelectAll();
        refresh();
    }

    protected void reverseMediaViewStatus(MediaView mediaView,
            BaseMediaInfo baseMediaInfo) {
        if(mediaView == null || baseMediaInfo == null) {
            return;
        }
        boolean isSelected = mediaView.isSelected();
        baseMediaInfo.mIsSelected = !isSelected;
        mediaView.setContentInfo(baseMediaInfo);

        if(baseMediaInfo.mIsSelected) {
            mSelectedMedias.add(baseMediaInfo);
        } else {
            mSelectedMedias.remove(baseMediaInfo);
        }

        if(mSelectedMedias.size() == 0) {
            setUiSelectAll();
        } else if(mSelectedMedias.size() == mMediaInfos.size()) {
            setUiSelectNull();
        } else {
            setUiSelectParty();
        }

        refreshActionModeViewTitle(mSelectedMedias.size());
    }

    //init	
    private void initActionMode() {
        mActionModeView = new ActionModeView(this, getActionModeViewCallback());
        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
        viewGroup.addView(mActionModeView);
        mBtnAction.setText(R.string.edit);
        mBtnAction.setVisibility(View.VISIBLE);
    }

    //packaged method
    private void setUiSelectAll() {
        mActionModeView.setUISelectAll();
    }

    private void setUiSelectNull() {
        mActionModeView.setUISelectNone();
    }

    private void setUiSelectParty() {
        mActionModeView.setUiSelectPart();
    }

    private void refreshActionModeViewTitle(int selectedInfoSize) {
        String str = getResources().getString(R.string.select_count_xiang);
        str = String.format(str, selectedInfoSize);
        mActionModeView.setTitle(str);
    }

    //UI callback	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && mActionModeView != null && mActionModeView.isVisible()) {
            exitActionMode();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    };

    @Override
    protected void onActionClick() {
        if(mActionModeView != null && mActionModeView.isVisible()) {
            exitActionMode();
        }else{
            startActionMode();
        }
    }

    protected abstract void setInEditMode(boolean isInEditMode);
    protected abstract void refreshListView();
    protected abstract ActionModeView.Callback getActionModeViewCallback();
}
