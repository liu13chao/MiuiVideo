package com.miui.video.widget.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.video.R;
import com.miui.video.type.Channel;

/**
 *@author tangfuling
 *
 */

public class FilterViewFilter extends LinearLayout {
	
	private Context context;
	
	//listener
	private OnFilterViewSelectedListener listener;
	
	//received data, for example film
	private Channel channel;
	
	//data, for example area type time
	private ArrayList<Channel> subChannel = new ArrayList<Channel>();
	
	//selected channel id map, key means channel id, value means sub channel id 
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, Integer> selectedChannelIdMap = new HashMap<Integer, Integer>();
	
	//button bg view map, key means channel id, value means button bg view
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, View> buttonBgViewMap = new HashMap<Integer, View>();
	
	//filter btns
	private ArrayList<Button> filterBtns = new ArrayList<Button>();
	private ArrayList<HorizontalScrollView> horizontalScrollViews = new ArrayList<HorizontalScrollView>();
	
	//animation
	private int animationDuration = 300;

	private int scrollViewHeight;
	private int scrollViewWidth;
	private int buttonWidth;
	private int buttonHeight;
	private int buttonPaddingH;
	private int textSize;
	private int nameViewTextColor;
	
	public FilterViewFilter(Context context) {
		super(context);
		this.context = context;
	}
	
	public FilterViewFilter(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	public void setChannel(Channel channel) {
		this.channel = channel;
		init();
	}
	
	public void setOnFilterViewSelectedListener(OnFilterViewSelectedListener listener) {
		this.listener = listener;
	}

	//init
	private void init() {
		initData();
		initUI();
	}
	
	private void initData() {
		if(channel == null || channel.sub == null) {
			return;
		}
		//filter channel all
		subChannel.clear();
		for(int i = 0; i < channel.sub.length; i++) {
			if(channel.sub[i] != null && channel.id != channel.sub[i].id) {
				subChannel.add(channel.sub[i]);
			}
		}
		
		//insert channel all to subChannel's sub
		for(int i = 0; i < subChannel.size(); i++) {
			Channel channel = subChannel.get(i);
			if(isContainSubChannelAll(channel)) {
				break;
			}
			
			//insert sub channel
			Channel subChannelAll = new Channel();
			subChannelAll.id = channel.id;
			subChannelAll.name = getResources().getString(R.string.all);
			
			int subChannelSize = 1;
			if(channel.sub != null) {
				subChannelSize = channel.sub.length + 1;
			}
			Channel[] subChannel = new Channel[subChannelSize];
			subChannel[0] = subChannelAll;
			for(int j = 1; j < subChannel.length; j++) {
				subChannel[j] = channel.sub[j - 1];
			}
			channel.sub = subChannel;
		}
	}
	
	private void initUI() {
		setOrientation(VERTICAL);
		initDimen();
		initFilterViewRows();
	}
	
	private void initDimen() {
		Resources res = context.getResources();
		scrollViewWidth = res.getDimensionPixelSize(R.dimen.filter_view_scroll_view_width);
		scrollViewHeight = res.getDimensionPixelSize(R.dimen.filter_view_scroll_view_height);
		buttonWidth = res.getDimensionPixelSize(R.dimen.filter_view_filter_btn_width);
		buttonHeight = res.getDimensionPixelSize(R.dimen.filter_view_filter_btn_height);
		buttonPaddingH = res.getDimensionPixelSize(R.dimen.filter_view_filter_btn_paddingH);
		textSize = context.getResources().getDimensionPixelSize(R.dimen.font_size_15);
		nameViewTextColor = context.getResources().getColor(R.color.p_80_white);
	}
	
	private void initFilterViewRows() {
		if(subChannel.size() == 0) {
			return;
		}
		removeAllViews();
		for(int i = 0; i < subChannel.size(); i++) {
			Channel channel = subChannel.get(i);
			if(channel != null) {
				LinearLayout filterViewRow = createFilterViewRow(channel, getChannelNameMaxCount());
				addView(filterViewRow);
			}
		}
	}
	
	private int getChannelNameMaxCount() {
		int channelNameMaxCount = 0;
		for(int i = 0; i < subChannel.size(); i++) {
			Channel channel = subChannel.get(i);
			if(channel != null && channel.name != null) {
				if(channel.name.length() > channelNameMaxCount) {
					channelNameMaxCount = channel.name.length();
				}
			}
		}
		return channelNameMaxCount;
	}
	
	//channel.sub do not contains sub channel any more
	private LinearLayout createFilterViewRow(Channel channel, int channelNameMaxCount) {
		if(channel == null || channel.sub == null) {
			return null;
		}
		LinearLayout linearLayout = new LinearLayout(context);
		//add name view
		TextView nameView = new TextView(context);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, scrollViewHeight);
		nameView.setGravity(Gravity.CENTER);
		nameView.setLayoutParams(params);
		nameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		nameView.setTextColor(nameViewTextColor);
		StringBuilder name = new StringBuilder();
		name.append(channel.name);
		if(channel.name != null) {
			int blankCount = channelNameMaxCount - channel.name.length();
			for(int i = 0; i < blankCount; i++) {
				name.append("    ");
			}
		}
		name.append("：");
		nameView.setText(name.toString());
		linearLayout.addView(nameView);
		//add btn scrollview
		HorizontalScrollView btnScrollView = createBtnScrollView(channel);
		linearLayout.addView(btnScrollView);
		return linearLayout;
	}
	
	//channel.sub do not contains sub channel any more
	private HorizontalScrollView createBtnScrollView(Channel channel) {
		if(channel == null || channel.sub == null) {
			return null;
		}
		
		//add buttons
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(HORIZONTAL);
		LayoutParams lParams = new LayoutParams(LayoutParams.WRAP_CONTENT, scrollViewHeight);
		linearLayout.setLayoutParams(lParams);
		for(int i = 0; i < channel.sub.length; i++) {
			if(channel.sub[i] != null) {
				Button button = (Button) View.inflate(context, R.layout.button, null);
				ButtonTag tag = new ButtonTag();
				tag.parentChannelId = channel.id;
				tag.channelId = channel.sub[i].id;
				tag.parentChannelName = channel.name;
				tag.channelName = channel.sub[i].name;
				button.setTag(tag);
				lParams = new LayoutParams(buttonWidth, scrollViewHeight);
				lParams.leftMargin = buttonPaddingH;
				button.setSingleLine(true);
				button.setLayoutParams(lParams);
				button.setPadding(0, 0, 0, 0);
				button.setGravity(Gravity.CENTER);
				button.setBackgroundColor(getResources().getColor(R.color.transparent));
				button.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
				button.setText(channel.sub[i].name);
				if(i == 0) {
					button.setSelected(true);
					selectedChannelIdMap.put(tag.parentChannelId, tag.channelId);
				}
				button.setOnClickListener(mOnClickListener);
				filterBtns.add(button);
				linearLayout.addView(button);
			}
		}
		
		//add button bg view
		View buttonBgView = new View(context);
//		buttonBgView.setBackgroundResource(R.drawable.channel_all_btn_bg);
		android.widget.FrameLayout.LayoutParams fParams = new 
				android.widget.FrameLayout.LayoutParams(buttonWidth, buttonHeight);
		fParams.leftMargin = buttonPaddingH;
		fParams.gravity = Gravity.CENTER_VERTICAL;
		buttonBgView.setLayoutParams(fParams);
		buttonBgView.setBackgroundResource(R.drawable.channel_btn_bg);
		buttonBgViewMap.put(channel.id, buttonBgView);
		
		FrameLayout frameLayout = new FrameLayout(context);
		frameLayout.addView(linearLayout);
		frameLayout.addView(buttonBgView);
		
		HorizontalScrollView scrollView = new HorizontalScrollView(context);
		scrollView.setHorizontalScrollBarEnabled(false);
		lParams = new LayoutParams(scrollViewWidth, scrollViewHeight);
		scrollView.setLayoutParams(lParams);
		scrollView.addView(frameLayout);
		horizontalScrollViews.add(scrollView);
		return scrollView;
	}
	
	//packaged method
	private boolean isContainSubChannelAll(Channel channel) {
		if(channel == null || channel.sub == null) {
			return false;
		}
		for(int i = 0; i < channel.sub.length; i++) {
			if(channel.sub[i].id == channel.id) {
				return true;
			}
		}
		return false;
	}
	
	private int[] getSelectedChannelIds() {
		int[] selectedChannelIds = new int[selectedChannelIdMap.size()];
		Set<Integer> keys = selectedChannelIdMap.keySet();
		int i = 0;
		for(Integer id : keys) {
			selectedChannelIds[i++] = selectedChannelIdMap.get(id);
		}
		return selectedChannelIds;
	}
	
	private void moveButtonBg(View targetView) {
		if(targetView == null) {
			return;
		}
		
		Object tag = targetView.getTag();
		if(tag instanceof ButtonTag) {
			ButtonTag buttonTag = (ButtonTag) tag;
			int channelId = buttonTag.parentChannelId;
			View buttonBgView = buttonBgViewMap.get(channelId);
			int targetX = (int) (targetView.getX() - buttonPaddingH);
			startAnimationX(buttonBgView, targetX);
		}
	}
	
	private void refreshBtnHighLight() {
		for(int i = 0; i < filterBtns.size(); i++) {
			Button button = filterBtns.get(i);
			button.setSelected(false);
			
			Object obj = button.getTag();
			if(obj instanceof ButtonTag) {
				ButtonTag tag = (ButtonTag) obj;
				if(selectedChannelIdMap.get(tag.parentChannelId) != null) {
					int selectedChannelId = selectedChannelIdMap.get(tag.parentChannelId);
					if(tag.channelId == selectedChannelId) {
						button.setSelected(true);
					}
				}
			}
		}
	}
	
	private void refreshBtnScrollViewParam() {
		for(int i = 0; i < horizontalScrollViews.size(); i++) {
			HorizontalScrollView horizontalScrollView = horizontalScrollViews.get(i);
			LayoutParams params = (LayoutParams) horizontalScrollView.getLayoutParams();
			params.width = scrollViewWidth;
			horizontalScrollView.setLayoutParams(params);
		}
	}
	
	private void startAnimationX(View view, int targetX) {
		view.clearAnimation();
		ObjectAnimator animator = ObjectAnimator.ofFloat(
				view, "translationX", view.getTranslationX(), targetX);
		animator.setDuration(animationDuration);
		animator.start();
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Object tag = v.getTag();
			if(tag instanceof ButtonTag) {
				ButtonTag buttonTag = (ButtonTag) tag;
				selectedChannelIdMap.put(buttonTag.parentChannelId, buttonTag.channelId);
				moveButtonBg(v);
				refreshBtnHighLight();
				if(listener != null) {
					listener.onFilterViewSelected(getSelectedChannelIds(), buttonTag);
				}
			}
		}
	};

	//self def class
	public class ButtonTag {
		public int parentChannelId;
		public int channelId;
		public String parentChannelName;
		public String channelName;
	}
	
	public interface OnFilterViewSelectedListener {
		public void onFilterViewSelected(int[] selectedChannelIds, ButtonTag tag);
	}
	
	//screen change
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		scrollViewWidth = getResources().getDimensionPixelSize(R.dimen.filter_view_scroll_view_width);
		refreshBtnScrollViewParam();
	}
}
