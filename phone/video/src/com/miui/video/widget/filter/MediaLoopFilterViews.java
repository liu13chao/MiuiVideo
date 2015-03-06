package com.miui.video.widget.filter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.miui.video.R;
import com.miui.video.type.Channel;

public class MediaLoopFilterViews extends LinearLayout {

	private Context mContext;
	private Channel[] mSubChannels;
	private int[] mSelectedChannelIds;
	
	private List<MediaLoopFilterView> mLoopFilterViews = new ArrayList<MediaLoopFilterView>();
	
	public MediaLoopFilterViews(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public MediaLoopFilterViews(Context context) {
		super(context);
		this.mContext = context;
	}
	
	public void setChannel(Channel channel, int[] selectedChannelIds) {
		this.mSelectedChannelIds = selectedChannelIds;
		if(channel != null) {
			deleteSubChannelAll(channel);
			mSubChannels = channel.sub;
		}
		init();
	}
	
	public int[] getSelectedChannelIds() {
		int[] selectedChannelIds = new int[mLoopFilterViews.size()];
		for(int i = 0; i < selectedChannelIds.length; i++) {
			MediaLoopFilterView loopFilterView = mLoopFilterViews.get(i);
			if(loopFilterView != null) {
				selectedChannelIds[i] = loopFilterView.getSelectedChannelId();
			}
		}
		return selectedChannelIds;
	}

	//init
	private void init() {
		initData();
		initUI();
	}
	
	private void initData() {
		if(mSubChannels == null) {
			return;
		}
		for(int i = 0; i < mSubChannels.length; i++) {
			insertSubChannelAll(mSubChannels[i]);
		}
	}
	
	private void initUI() {
		removeAllViews();
		setOrientation(HORIZONTAL);
		mLoopFilterViews.clear();
		if(mSubChannels == null) {
			return;
		}
		
		for(int i = 0; i < mSubChannels.length; i++) {
			if(mSubChannels[i] != null && mSubChannels[i].sub != null) {
				Channel[] subChannels = mSubChannels[i].sub;
				int selectedChannelId = -1;
				if(mSelectedChannelIds != null && i < mSelectedChannelIds.length) {
					selectedChannelId = mSelectedChannelIds[i];
				}
				MediaLoopFilterView loopFilterView = new MediaLoopFilterView(mContext);
				loopFilterView.setData(subChannels, selectedChannelId);
				LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
				params.weight = 1;
				addView(loopFilterView, params);
				mLoopFilterViews.add(loopFilterView);
			}
		}
	}
	
	//packaged method
	private void deleteSubChannelAll(Channel channel) {
		if(channel == null || channel.sub == null || channel.sub.length == 0) {
			return;
		}
		
		if(isContainSubChannelAll(channel)) {
			int subChannelSize = channel.sub.length - 1;
			Channel[] subChannel = new Channel[subChannelSize];
			int i = 0;
			for(int j = 0; j < channel.sub.length; j++) {
				if(!isSubChannelAll(channel.sub[j], channel)) {
					subChannel[i++] = channel.sub[j];
				}
			}
			channel.sub = subChannel;
		}
	}
	
	private void insertSubChannelAll(Channel channel) {
		if(channel == null || channel.sub == null || channel.sub.length == 0) {
			return;
		}
		
		if(!isContainSubChannelAll(channel)) {
			Channel subChannelAll = new Channel();
			subChannelAll.id = channel.id;
			subChannelAll.name = getResources().getString(R.string.all);
			
			int subChannelSize = channel.sub.length + 1;
			Channel[] subChannel = new Channel[subChannelSize];
			subChannel[0] = subChannelAll;
			for(int j = 1; j < subChannel.length; j++) {
				subChannel[j] = channel.sub[j - 1];
			}
			channel.sub = subChannel;
		}
	}
	
	private boolean isContainSubChannelAll(Channel channel) {
		if(channel == null || channel.sub == null) {
			return false;
		}
		for(int i = 0; i < channel.sub.length; i++) {
			if(isSubChannelAll(channel.sub[i], channel)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isSubChannelAll(Channel subChannel, Channel parentChannel) {
		if(parentChannel != null && subChannel != null) {
			if(subChannel.id == parentChannel.id) {
				return true;
			}
		}
		return false;
	}
}
