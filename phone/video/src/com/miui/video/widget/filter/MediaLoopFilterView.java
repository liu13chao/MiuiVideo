package com.miui.video.widget.filter;

import miui.widget.NumberPicker;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.miui.video.type.Channel;

public class MediaLoopFilterView extends FrameLayout {

	private Context mContext;
	private NumberPicker mNumberPicker;
	
	private Channel[] mChannels;
	private String[] mChannelNames;
	private int[] mChannelIds;
	private int mSelectedChannelId;
	
	public MediaLoopFilterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public MediaLoopFilterView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public void setData(Channel[] channels, int selectedChannelId) {
		this.mChannels = channels;
		this.mSelectedChannelId = selectedChannelId;
		if(mChannels != null && mChannels.length > 0) {
			mChannelNames = new String[mChannels.length];
			mChannelIds = new int[mChannels.length];
			for(int i = 0; i < mChannels.length; i++) {
				if(mChannels[i] != null) {
					mChannelNames[i] = mChannels[i].name;
					mChannelIds[i] = mChannels[i].id;
				}
			}
		}
		
		refreshNumberPicker();
	}
	
	public int getSelectedChannelId() {
		int value = mNumberPicker.getValue();
		return nPickerValue2ChannelId(value);
	}

	//init
	private void init() {
		mNumberPicker = new NumberPicker(mContext);
		addView(mNumberPicker);
	}
	
	//packaged method
	private void refreshNumberPicker() {
		if(mChannels != null && mChannels.length > 0) {
			mNumberPicker.setMaxValue(mChannels.length - 1);
			mNumberPicker.setMinValue(0);
			mNumberPicker.setDisplayedValues(mChannelNames);
			
			int value = channelId2NPickerValue(mSelectedChannelId);
			if(value != -1) {
				mNumberPicker.setValue(value);
			} else {
				mNumberPicker.setValue(0);
			}
		}
	}
	
	private int nPickerValue2ChannelId(int nPickerValue) {
		if(mChannelIds != null && nPickerValue < mChannelIds.length) {
			return mChannelIds[nPickerValue];
		}
		return -1;
	}
	
	private int channelId2NPickerValue(int channelId) {
		if(mChannelIds != null) {
			for(int i = 0; i < mChannelIds.length; i++) {
				if(mChannelIds[i] == channelId) {
					return i;
				}
			}
		}
		return -1;
	}
}
