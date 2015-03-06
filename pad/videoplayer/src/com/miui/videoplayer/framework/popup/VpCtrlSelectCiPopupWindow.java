package com.miui.videoplayer.framework.popup;

import com.miui.video.R;
import com.miui.video.type.PlayerMediaSetInfo;
import com.miui.videoplayer.Constants;
import com.miui.videoplayer.VideoPlayerActivity;
import com.miui.videoplayer.framework.ui.LocalMediaPlayerControl;
import com.miui.videoplayer.framework.utils.DisplayInformationFetcher;
import com.miui.videoplayer.framework.views.OriginMediaController;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

public class VpCtrlSelectCiPopupWindow extends ManagedPopupWindow implements OnItemClickListener {
    private Context mContext;
    private Handler mHandler;
    private ListView mListView;
    private CiListAdapter mCiListAdapter;
    
	private LocalMediaPlayerControl mLocalMediaPlayerControl;
    private VpCtrlFullScreenPopupWindow mFullScreenPopupWindow;
    
    public VpCtrlSelectCiPopupWindow(Context context, Handler handler) {
        super(LayoutInflater.from(context).inflate(R.layout.vp_popup_ctrl_ci_selection, null));
		
        mContext = context;
        int width = 0;
        if (VideoPlayerActivity.mediaStyle == Constants.MEDIA_TYPE_TELEPLAY) {
        	width = (int) context.getResources().getDimension(R.dimen.vp_ctrl_select_ci_pop_width_ep);
        } else {
        	width = (int) context.getResources().getDimension(R.dimen.vp_ctrl_select_ci_pop_width_variety);
        }
		this.setWidth(width);
		this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setTouchable(true);
        this.setFocusable(true);
//        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable());
        
		mHandler = handler;
		View contentView = getContentView();
		
        mListView = (ListView) contentView.findViewById(R.id.ci_selection_listview);
        mListView.setSelector(R.drawable.vp_list_item_bg);
        mListView.setVerticalFadingEdgeEnabled(true);
        mListView.setFadingEdgeLength(mContext.getResources().getDimensionPixelSize(R.dimen.vp_common_fade_edge_length));
        mCiListAdapter = new CiListAdapter();
        mListView.setAdapter(mCiListAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setSelection(VideoPlayerActivity.curMediaIndex);
        
        TextView topTitleName = (TextView) contentView.findViewById(R.id.vp_popup_ctrl_top_title_name);
        topTitleName.setText(R.string.vp_select_ep);
        
		if (mFullScreenPopupWindow == null) {
			mFullScreenPopupWindow = new VpCtrlFullScreenPopupWindow(mContext);
		}
	}
    
	public void setLocalMediaPlayerControl(LocalMediaPlayerControl localMediaPlayerControl) {
		this.mLocalMediaPlayerControl = localMediaPlayerControl;
	}
    
	@Override
	public void show(View anchor) {
		// TODO Auto-generated method stub
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

	private class ViewHolder {
		private TextView  ciTitle;
	}
	
	private class CiListAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return VideoPlayerActivity.mediaCount;
		}

		@Override
		public PlayerMediaSetInfo getItem(int position) {
			// TODO Auto-generated method stub
			return VideoPlayerActivity.getMediaSetInfo(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Log.i("SelectCiPopupWindow", "getView: " + position);
			PlayerMediaSetInfo mediaSetInfo = getItem(position);
			ViewHolder vHolder = null;
			if (convertView == null) {
				vHolder = new ViewHolder();
				convertView = View.inflate(mContext, R.layout.vp_popup_ctrl_ci_selection_item, null);
				vHolder.ciTitle = (TextView) convertView.findViewById(R.id.ci_title);
				convertView.setTag(vHolder);
			} else {
				vHolder = (ViewHolder) convertView.getTag();
			}
			
			if (mediaSetInfo != null) {
				Resources res = mContext.getResources();
				int titleColor;
				if (VideoPlayerActivity.curMediaIndex == position) {
					titleColor = mContext.getResources().getColor(R.color.vp_90_blue);
				} else {
					titleColor = mContext.getResources().getColor(R.color.vp_90_white);
				}
				String str = "";
				if (VideoPlayerActivity.mediaStyle == Constants.MEDIA_TYPE_TELEPLAY) {
					str = res.getString(R.string.episode_suffix);
					str = String.format(str, mediaSetInfo.ci);
				} else {
					str = mediaSetInfo.videoname;
				}
				vHolder.ciTitle.setText(str);
				vHolder.ciTitle.setTextColor(titleColor);
			}
			return convertView;
		}
	}
	
	
	//UI callback
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Log.i("SelectCiPopupWindow", "onItemClick: " + position);
		if (view instanceof ImageView) {
			
		} else {
			if (position == VideoPlayerActivity.curMediaIndex) {
				dismiss();
			} else {
				Message msg = mHandler.obtainMessage(OriginMediaController.SELECT_CI);
				msg.arg1 = position;
				mHandler.sendMessage(msg);
			}
		}			
	}

	public void refresh() {
		mCiListAdapter.notifyDataSetChanged();
	}
}
