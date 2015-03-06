package com.miui.videoplayer.framework.popup;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.miui.video.R;
import com.miui.videoplayer.adapter.SourceListAdapter;
import com.miui.videoplayer.model.OnlineEpisodeSource;

public class SourceListPopupWindow extends AutoDismissPopupWindow implements OnItemClickListener {
	
	private ListView mListView;
	private SourceListAdapter mAdapter;
	
	private OnSourceSelectListener mListener;
	
	public SourceListPopupWindow(Context context) {
		super(View.inflate(context, R.layout.vp_popup_source_list, null));
		setWidth(context.getResources().getDimensionPixelSize(R.dimen.vp_popup_sources_width));
		setHeight(LayoutParams.MATCH_PARENT);
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.vp_pop_source_list_bg));
		mListView = (ListView) getContentView().findViewById(R.id.source_list_items);
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent != null && parent.getItemAtPosition(position) instanceof OnlineEpisodeSource) {
			if (mListener != null) {
				mListener.onSourceSelect(position, (OnlineEpisodeSource) parent.getItemAtPosition(position));
			}
		}
	}
	
	public void setOnSourceSelectListener(OnSourceSelectListener l) {
		mListener = l;
	}
	
	public static interface OnSourceSelectListener {
		public void onSourceSelect(int position, OnlineEpisodeSource source);
	}

	@Override
	public void show(View anchor) {
//		Controller.sendMessage(UIConfig.MSG_WHAT_HIDE_CORE_FRAGMENT);
		showAtLocation(anchor, Gravity.RIGHT | Gravity.TOP, 0, 0);
		
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
