package com.miui.videoplayer.framework.popup;

import com.miui.video.R;
import com.miui.video.type.MediaUrlInfo;
import com.miui.video.type.MediaUrlInfoList;
import com.miui.videoplayer.VideoPlayerActivity;
import com.miui.videoplayer.adapter.SourceListAdapter;
import com.miui.videoplayer.datasupply.MediaUrlInfoListSupply;
import com.miui.videoplayer.datasupply.MediaUrlInfoListSupply.MediaUrlInfoListListener;
import com.miui.videoplayer.framework.ui.LocalMediaPlayerControl;
import com.miui.videoplayer.framework.utils.DisplayInformationFetcher;
import com.miui.videoplayer.framework.views.OriginMediaController;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

public class VpCtrlSelectSourcePopupWindow extends ManagedPopupWindow {
    private Context mContext;
    private Handler mHandler;
    private ListView mListView;
    private SourceListAdapter mSourceListAdapter;
    
	private LocalMediaPlayerControl mLocalMediaPlayerControl;
    private VpCtrlFullScreenPopupWindow mFullScreenPopupWindow;
    
    //data
    private MediaUrlInfoListSupply mMediaUrlInfoListSupply;
    private MediaUrlInfoList mMediaUrlInfoList;
    private int mCi;
    
    public VpCtrlSelectSourcePopupWindow(Context context, Handler handler) {
        super(LayoutInflater.from(context).inflate(R.layout.vp_popup_ctrl_source_selection, null));
        mContext = context;
		mHandler = handler;
        init();
	}
    
	public void setLocalMediaPlayerControl(LocalMediaPlayerControl localMediaPlayerControl) {
		this.mLocalMediaPlayerControl = localMediaPlayerControl;
	}
	
	//init
	private void init() {
		initDataSupply();
		initPopWindow();
		initUI();
	}
	
	private void initDataSupply() {
		mMediaUrlInfoListSupply = new MediaUrlInfoListSupply();
		mMediaUrlInfoListSupply.addListener(mMediaUrlInfoListListener);
	}
	
	private void initPopWindow() {
		int width = (int) mContext.getResources().getDimension(R.dimen.vp_ctrl_select_source_pop_width);
		this.setWidth(width);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setTouchable(true);
		this.setFocusable(true);
		this.setBackgroundDrawable(new ColorDrawable());
	}
	
	private void initUI() {
		View contentView = getContentView();
		
        mListView = (ListView) contentView.findViewById(R.id.source_selection_listview);
        mListView.setSelector(R.drawable.vp_list_item_bg);
        mListView.setVerticalFadingEdgeEnabled(true);
        mListView.setFadingEdgeLength(mContext.getResources().getDimensionPixelSize(R.dimen.vp_common_fade_edge_length));
        mSourceListAdapter = new SourceListAdapter(mContext);
        mListView.setAdapter(mSourceListAdapter);
        mListView.setOnItemClickListener(mOnItemClickListener);
        mListView.setVerticalScrollBarEnabled(false);
        
        TextView topTitleName = (TextView) contentView.findViewById(R.id.vp_popup_ctrl_top_title_name);
        topTitleName.setText(R.string.vp_select_video_source);
        
		if (mFullScreenPopupWindow == null) {
			mFullScreenPopupWindow = new VpCtrlFullScreenPopupWindow(mContext);
		}
	}
	
	//get data
	private void getMediaUrlInfoList() {
		if(mMediaUrlInfoList == null) {
			mMediaUrlInfoListSupply.getMediaUrlInfoList(VideoPlayerActivity.mediaId, mCi, -1);
		}
	}
	
	//packaged method
	private void refreshListView() {
		mSourceListAdapter.setData(mMediaUrlInfoList);
	}
   
	private void refreshCurCi() {
		if(mCi != VideoPlayerActivity.curCi) {
			mMediaUrlInfoList = null;
			this.mCi = VideoPlayerActivity.curCi;
		}
		getMediaUrlInfoList();
	}
	
	@Override
	public void show(View anchor) {
		refreshCurCi();
		if (mLocalMediaPlayerControl != null && mLocalMediaPlayerControl.isPlaying()) {
			mLocalMediaPlayerControl.pause();
		}
		mFullScreenPopupWindow.show(anchor);
        int orientation = DisplayInformationFetcher.getInstance(mContext).getScreenOrientation();
        if (orientation == DisplayInformationFetcher.SCREEN_LAND) {
            this.setAnimationStyle(R.style.menu_popup_anim_style);
        } else {
        	this.setAnimationStyle(R.style.menu_popup_anim_style_vertical);
        }
		this.showAtLocation(anchor, Gravity.LEFT, 0, 0);
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
		if (mLocalMediaPlayerControl != null && !mLocalMediaPlayerControl.isPlaying()) {
			mLocalMediaPlayerControl.start();
		}
		if(mFullScreenPopupWindow.isShowing()) {
			mFullScreenPopupWindow.dismiss();
		}
	}

	//UI callback
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			MediaUrlInfo mediaUrlInfo = mSourceListAdapter.getItem(position);
			if(mediaUrlInfo != null) {
				if(mediaUrlInfo.mediaSource == VideoPlayerActivity.curMediaSource
						&& mediaUrlInfo.clarity == VideoPlayerActivity.curClarity) {
					dismiss();
				} else {
					Message msg = mHandler.obtainMessage(OriginMediaController.SELECT_CLARITY, mediaUrlInfo);
					mHandler.sendMessage(msg);
				}
			}
		}
	};
	
	//data callback
	private MediaUrlInfoListListener mMediaUrlInfoListListener = new MediaUrlInfoListListener() {
		
		@Override
		public void onMediaUrlInfoListDone(MediaUrlInfoList mediaUrlInfoList,
				boolean isError) {
			mMediaUrlInfoList = mediaUrlInfoList;
			refreshListView();
		}
	};
}
