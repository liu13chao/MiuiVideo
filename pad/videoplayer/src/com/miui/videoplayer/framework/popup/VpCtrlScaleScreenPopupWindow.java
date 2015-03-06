package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.miui.video.R;
import com.miui.videoplayer.framework.DuoKanConstants;
import com.miui.videoplayer.framework.ui.LocalVideoPlaySizeAdjustable;
import com.miui.videoplayer.framework.utils.DisplayInformationFetcher;

public class VpCtrlScaleScreenPopupWindow extends MenuPopupWindow{
	
	private Context mContext;
	private LocalVideoPlaySizeAdjustable mVideoView;
	private int mLastSelectedIndex = 0;
	
	public VpCtrlScaleScreenPopupWindow(Context context, LocalVideoPlaySizeAdjustable videoView) {
		super(context);
		
		this.mContext = context;
		this.mVideoView = videoView;
		
		int width = (int) context.getResources().getDimension(R.dimen.vp_ctrl_scale_screen_pop_width);
		this.setWidth(width);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		
		if (DuoKanConstants.ENABLE_V5_UI) {
//			View contentView = getContentView();
//			contentView.setBackgroundResource(R.drawable.vp_menu_background_v5);
		}
		setupViews();
	}

	private void setupViews() {
		String[] values = new String[]{mContext.getResources().getString(R.string.vp_auto_select),
				mContext.getResources().getString(R.string.vp_full_screen),
				mContext.getResources().getString(R.string.vp_adapt_width),
				mContext.getResources().getString(R.string.vp_adapt_height),
				mContext.getResources().getString(R.string.vp_using_16_9),
				mContext.getResources().getString(R.string.vp_using_4_3)};
		if (DuoKanConstants.ENABLE_V5_UI) {
			ScaleScreenAdapter adapter = new ScaleScreenAdapter(mContext, values);
			getOptionMenuListView().setAdapter(adapter);
		} else {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.vp_menu_option_listview_item, R.id.sub_option_menu_listview_item_textview, values){

				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View result = super.getView(position, convertView, parent);
					TextView textView = (TextView) result.findViewById(R.id.sub_option_menu_listview_item_textview);
					if (position == mLastSelectedIndex) {
						textView.setTextColor(mContext.getResources().getColor(R.color.listview_item_selected_color));
					} else {
						textView.setTextColor(mContext.getResources().getColor(R.color.vp_white));
					}
					return result;
				}
				
			};
			getOptionMenuListView().setAdapter(adapter);
		}
		
		View contentView = getContentView();
        TextView topTitleName = (TextView) contentView.findViewById(R.id.vp_popup_ctrl_top_title_name);
        topTitleName.setText(R.string.vp_scale_screen);
		
//		Log.i("video width:", videoWidth + "");
//		Log.i("video height:", videoHeight + "");
		getOptionMenuListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mLastSelectedIndex = position;
				updateVideoPlayerSize();
				dismiss();
			}
		});
	}
	
	public void updateVideoPlayerSize() {
		final int position = mLastSelectedIndex;
		int videoWidth = mVideoView.getVideoWidth();
		int videoHeight = mVideoView.getVideoHeight();
		
//		Log.e("videoView:", mVideoView + "");
//		Log.e("videoWidth:", videoWidth + "");
//		Log.e("videoHeight:", videoHeight + "");
		
		int screenWidth = DisplayInformationFetcher.getInstance(mContext).getScreenWidth();
		int screenHeight = DisplayInformationFetcher.getInstance(mContext).getScreenHeight();
//		Log.e("screenWidth:", screenWidth + "");
//		Log.e("screenHeight:", screenHeight + "");
//		Log.e("position: ", position + "");
		//auto select
		if (position == 0) {
			mVideoView.adjustVideoPlayViewSize(-1, -1, true);
		}
	
		//full screen
		if (position == 1) {
			mVideoView.adjustVideoPlayViewSize(screenWidth, screenHeight, false);
		}
		
		//adapt width
		if (position == 2) {
			mVideoView.adjustVideoPlayViewSize(screenWidth, videoHeight, false);
		}
		//adapt height
		if (position == 3) {
			mVideoView.adjustVideoPlayViewSize(videoWidth, screenHeight, false);
		}
		//force 16:9
		if (position == 4) {
			if (9 * screenWidth >= 16 * screenHeight) {
				mVideoView.adjustVideoPlayViewSize(screenHeight * 16 / 9, screenHeight, false);
			} else {
				mVideoView.adjustVideoPlayViewSize(screenWidth, screenWidth * 9 / 16, false);
			}
		}
		//force 4:3
		if (position == 5) {
			if (3 * screenWidth >= 4 * screenHeight) {
				mVideoView.adjustVideoPlayViewSize(screenHeight * 4 / 3, screenHeight, false);
			} else {
				mVideoView.adjustVideoPlayViewSize(screenWidth, screenWidth * 3 / 4, false);
			}
		}
		
	}

	@Override
	protected int getMenuHeightForPortScreen() {
//		int height = (int) mContext.getResources().getDimension(R.dimen.popup_right_menu_option_height);
		int itemHeight = (int) mContext.getResources().getDimension(R.dimen.popup_right_menu_option_listview_item_height);
		if (DuoKanConstants.ENABLE_V5_UI) {
			itemHeight = (int) mContext.getResources().getDimension(R.dimen.popup_left_menu_option_listview_item_height_v5);
		}
		int divideHeight = (int) mContext.getResources().getDimension(R.dimen.popup_left_menu_option_listview_item_divider_height);
		int space = 5;
		if (DuoKanConstants.ENABLE_V5_UI) {
			int verticalSpace = (int) mContext.getResources().getDimension(R.dimen.popup_bottom_menu_vertical_space);
			space = space + verticalSpace;
		}
		return (itemHeight + divideHeight) * 6 + space;
	}

	@Override
	protected int getBackgroundResId(int orientation) {
		if (orientation == DisplayInformationFetcher.SCREEN_LAND) {
//			return R.drawable.vp_milink_device_selection_menu_background_v5;
			return R.drawable.vp_transparent;
		} else {
//			return R.drawable.vp_milink_device_selection_menu_background_vertical_v5;
			return R.drawable.vp_transparent;
		}
	}

	@Override
	protected int getMenuWidthForLandScreen() {
		int width = (int) mContext.getResources().getDimension(R.dimen.popup_left_menu_option_width);
		if (DuoKanConstants.ENABLE_V5_UI) {
			width = (int) mContext.getResources().getDimension(R.dimen.popup_left_selection_menu_option_width_v5);
		}
		return width;
	}
	
	private class ScaleScreenAdapter extends BaseAdapter {
		
		private Context context;
		private String[] items;
		
		public ScaleScreenAdapter(Context context, String[] items) {
			this.context = context;
			this.items = items;
		}
		
		private class ViewHolder {
			private TextView  itemTv;
		}
		
		@Override
		public String getItem(int position) {
			if(items != null && position < items.length) {
				return items[position];
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public int getCount() {
			if(items != null) {
				return items.length;
			}
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder vHolder = null;
			if (convertView == null) {
				vHolder = new ViewHolder();
				convertView = View.inflate(context, R.layout.vp_popup_ctrl_scale_screen_item, null);
				vHolder.itemTv = (TextView) convertView.findViewById(R.id.vp_ctrl_scale_screen_title);
				convertView.setTag(vHolder);
			} else {
				vHolder = (ViewHolder) convertView.getTag();
			}
			String item = getItem(position);
			if(item != null) {
				vHolder.itemTv.setText(item);
			}
			if(position == mLastSelectedIndex) {
				vHolder.itemTv.setTextColor(mContext.getResources().getColor(R.color.vp_90_blue));
			} else {
				vHolder.itemTv.setTextColor(mContext.getResources().getColor(R.color.vp_90_white));
			}
			return convertView;
		}
	}
}
