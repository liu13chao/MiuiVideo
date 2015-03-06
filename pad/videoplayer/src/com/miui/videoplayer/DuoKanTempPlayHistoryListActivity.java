package com.miui.videoplayer;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.miui.videoplayer.framework.history.PlayHistoryManager;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.framework.utils.DKTimeFormatter;
import com.miui.video.R;

public class DuoKanTempPlayHistoryListActivity extends Activity {
	private LayoutInflater layoutInflater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.vp_app_activity_temp_play_history);
		layoutInflater = LayoutInflater.from(this);
	
		setupViews();
	}

	private void setupViews() {
		final ListView listView = (ListView) this.findViewById(R.id.video_listview);
		final PlayHistoryManager historyManager = new PlayHistoryManager(this);
		updateInput(listView, historyManager);
		
		Button clearButton = (Button) this.findViewById(R.id.clear_history_button);
		clearButton.setText("Clear");
		clearButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				historyManager.load();
				historyManager.clear();
				historyManager.save();
				updateInput(listView, historyManager);
			}
		});
	}

	private void updateInput(ListView listView, final PlayHistoryManager historyManager) {
		List<PlayHistoryEntry> input = historyManager.readPlayHistoryList();
		listView.setAdapter(new ListViewAdapter(input));
	}
	
	private class ListViewAdapter extends BaseAdapter {	
	    private List<PlayHistoryEntry> mList;
		
	    public ListViewAdapter(List<PlayHistoryEntry> list) {
			this.mList = list;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PlayHistoryEntry entry = mList.get(position);
			ViewHolder vh = null;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.vp_app_temp_media_listview_item, null);
				vh = new ViewHolder(convertView);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			long timeStamp = entry.getTimeStamp();
			int lastPosition = entry.getPosition();
			String dateString = DKTimeFormatter.getInstance().longToDate(timeStamp);
			String playPositonString = DKTimeFormatter.getInstance().stringForTime(lastPosition);
			vh.getNameTextView().setText(entry.getUri() + "  " + playPositonString + "  " + dateString);
		
//			vh.getDescTextView().setText(dateString + "   " + playPositonString);
			Log.i("PLAY position: ", playPositonString + "");
			Log.i("date string: ", dateString + "");
			vh.getDescTextView().setText(" MediaId: " + entry.getMediaId() + "    mediaCi: " + entry.getMediaCi()
					+ "\n  PlaySource:" + entry.getPlaySource() + "   VideoName: " + entry.getVideoName() + "\n   Html5Page: " + entry.getHtml5Page());
//			Log.i("MediId", entry.getMediaId() +"");
//			Log.i("MediaCi", entry.getMediaCi() +"");
//			Log.i("PlaySource", entry.getPlaySource()+"");
//			Log.i("VideoName", entry.getVideoName() +"");
//			Log.i("Html5Page", entry.getHtml5Page()+ "");
			return convertView;
		}
	}
	
	private class ViewHolder {
		private View root;
		private ImageView imageView;
		private TextView nameTextView;
		private TextView descTextView;
		
		public ViewHolder(View root) {
			this.root = root;
		}
		
		public ImageView getImageView() {
			if (imageView == null) {
				imageView = (ImageView) root.findViewById(R.id.listview_item_imageview);
			}
			return imageView;
		}

		public TextView getNameTextView() {
			if (nameTextView == null) {
				nameTextView = (TextView) root.findViewById(R.id.listview_item_name_textview);
			}
			return nameTextView;
		}

		public TextView getDescTextView() {
			if (descTextView == null) {
				descTextView = (TextView) root.findViewById(R.id.listview_item_desc_textview);
			}
			return descTextView;
		}
	}
}
