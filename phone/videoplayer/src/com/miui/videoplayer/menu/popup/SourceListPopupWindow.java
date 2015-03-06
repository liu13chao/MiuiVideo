package com.miui.videoplayer.menu.popup;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.miui.video.R;
import com.miui.videoplayer.adapter.SourceListAdapter;
import com.miui.videoplayer.model.OnlineEpisodeSource;

import java.util.List;

public class SourceListPopupWindow extends BaseMenuPopup implements OnItemClickListener {

	private SourceListAdapter mAdapter;
	private OnSourceSelectListener mListener;

	public SourceListPopupWindow(Context context) {
		super(context);
		setTitle(context.getResources().getString(R.string.vp_select_video_source));
		mAdapter = new SourceListAdapter(context);
		mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setVerticalScrollBarEnabled(false); 
	}
	
	public void setSources(List<OnlineEpisodeSource> sources) {
		mAdapter.setGroup(sources);
	}
	
	public void setCurrentSource(OnlineEpisodeSource source) {
		mAdapter.setCurrentSource(source);
	}
	
	public OnlineEpisodeSource getCurrentSource() {
		return mAdapter.getCurrentSource();
	}
	
	public List<OnlineEpisodeSource> getSources() {
		return mAdapter.getGroup();
	}
	
    @Override
    protected int getPopupWidth() {
    	return getContext().getResources().getDimensionPixelSize(R.dimen.vp_selectsource_popup_width);
    }
	
	public void setOnSourceSelectListener(OnSourceSelectListener l) {
		mListener = l;
	}
    
	public static interface OnSourceSelectListener {
		public void onSourceSelect(int position, OnlineEpisodeSource source);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent != null && parent.getItemAtPosition(position) instanceof OnlineEpisodeSource) {
			if (mListener != null) {
				mListener.onSourceSelect(position, (OnlineEpisodeSource) parent.getItemAtPosition(position));
			}
		}		
	}
	
    @Override
    public void show(ViewGroup anchor) {
        super.show(anchor);
		if (mAdapter.getCurrentSource() != null) {
			for (int i = 0; i < mAdapter.getCount(); i++) {
				final OnlineEpisodeSource s = (OnlineEpisodeSource) mAdapter.getItem(i);
				if (mAdapter.getCurrentSource().equals(s)) {
					mListView.setSelection(i);
					break;
				}
			}
		}
    }
}
