package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.videoplayer.framework.utils.AndroidUtils;
import com.miui.videoplayer.framework.utils.DisplayInformationFetcher;
import com.miui.videoplayer.framework.views.OriginMediaController;

public class ControlListPopupWindow extends PauseMediaPlayerPopupWindow {

	private Context mContext;
	private String[] mUris;
	private int mPlayingIndex;
	
	private ControlListAdapter mControlListAdapter;
	private OriginMediaController mMediaController;
	private ListView mListView;
	
	public ControlListPopupWindow(Context context, String[] uris, OriginMediaController mediaController) {
		super(context, LayoutInflater.from(context).inflate(R.layout.vp_popup_left_control_list, null));

		mContext = context;
		mUris = uris;
		mMediaController = mediaController;
		
		setupViews();
	}

	private void setupViews() {
		this.setFocusable(true);
		this.setTouchable(true);
		this.setOutsideTouchable(true);
//		this.setBackgroundDrawable(new ColorDrawable());
		ColorDrawable backgroudColorDrawable = new ColorDrawable(mContext.getResources().getColor(R.color.vp_black));
		backgroudColorDrawable.setAlpha(0);
		this.setBackgroundDrawable(backgroudColorDrawable);
		
		View rootView = getContentView();
		mListView = (ListView) rootView.findViewById(R.id.control_list_listview); 
		mControlListAdapter = new ControlListAdapter(mContext, mUris);
		mListView.setAdapter(mControlListAdapter);
		mListView.setFocusableInTouchMode(true);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == mPlayingIndex) {
					return;
				}
				mMediaController.switchMedia(position);
				dismiss();
			}
		});
		mListView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					mMediaController.onKeyDown(keyCode, event);
					return true;
				}
				return false;
			}
		});
	}
	
	public void setPlayingIndex(int playingIndex) {
		this.mPlayingIndex = playingIndex;
	}
	
	private class ControlListAdapter extends BaseAdapter {
		private String[] mUris;
		private LayoutInflater mLayoutInflater;
		
		public ControlListAdapter(Context context, String[] uris) {
			mLayoutInflater = LayoutInflater.from(context);
			this.mUris = uris;
		}

		@Override
		public int getCount() {
			return mUris.length;
		}

		@Override
		public Object getItem(int position) {
			return mUris[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View result = null;
			ViewHolder viewHolder = null;
			if (convertView == null) {
				result = mLayoutInflater.inflate(R.layout.vp_control_list_listview_item, null);
				viewHolder = new ViewHolder(result);
				result.setTag(viewHolder);
			} else {
				result = convertView;
				viewHolder = (ViewHolder) result.getTag();
			}
			TextView nameTextView = viewHolder.getNameTextView();
			ImageView playingImageView = viewHolder.getPlayingImageView();
			AndroidUtils.setBoldFontForChinese(nameTextView);
			Uri uri = Uri.parse(mUris[position]);
			nameTextView.setText(uri.getLastPathSegment());
			nameTextView.setTag(uri);
			nameTextView.setTextColor(mContext.getResources().getColor(android.R.color.white));
			playingImageView.setImageBitmap(null);
//			Log.e("position: ", position + "");
//			Log.e("mPlayingIndex: ", mPlayingIndex + " ");
			if (position == mPlayingIndex) {
				playingImageView.setImageResource(R.drawable.vp_list_playing);
				nameTextView.setTextColor(mContext.getResources().getColor(R.color.listview_item_selected_color));
			}
			return result;
		}
		
	}
	
	private static class ViewHolder {
		private View mRoot;
		private TextView mNameTextView;
		private ImageView mPlayingImageView;
		
		public ViewHolder(View root) {
			this.mRoot = root;
		}

		public TextView getNameTextView() {
			if (mNameTextView == null) {
				mNameTextView = (TextView) mRoot.findViewById(R.id.control_listview_item_name_textview);
			}
			return mNameTextView;
		}

		public ImageView getPlayingImageView() {
			if (mPlayingImageView == null) {
				mPlayingImageView = (ImageView) mRoot.findViewById(R.id.control_listview_item_name_imageview);
			}
			return mPlayingImageView;
		}
	}
	
	@Override
	public void show(View anchor) {
		int orientation = DisplayInformationFetcher.getInstance(mContext).getScreenOrientation();
//		Log.e("orientation: ", orientation + "");
		if (orientation == DisplayInformationFetcher.SCREEN_LAND) {
			int width = (int) mContext.getResources().getDimension(R.dimen.popup_left_control_listview_width);
			this.setWidth(width);
			this.setHeight(LayoutParams.MATCH_PARENT);
			showAtLocation(anchor, Gravity.LEFT, 0, 0);
		} else {
			int height = (int) mContext.getResources().getDimension(R.dimen.popup_left_control_listview_height);
			this.setWidth(LayoutParams.MATCH_PARENT);
			this.setHeight(height);
			showAtLocation(anchor, Gravity.BOTTOM, 0, 0);
		}
		
		int selectionIndex = computeSelectionIndex();
		mListView.setSelection(selectionIndex);
	}

	private int computeSelectionIndex() {
		int length = mUris.length;
		if (length > 4 && mPlayingIndex > 4) {
			return mPlayingIndex - 3;
		}
		return 0;
	}
}
