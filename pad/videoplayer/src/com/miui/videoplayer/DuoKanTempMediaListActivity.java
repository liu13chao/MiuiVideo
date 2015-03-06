package com.miui.videoplayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.miui.video.R;

public class DuoKanTempMediaListActivity extends Activity {
	private LayoutInflater layoutInflater;
	private String[] locations;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vp_app_activity_temp_all_media);
		layoutInflater = LayoutInflater.from(this);
		setupViews();
	}

	private void setupViews() {
		List<String> filePaths = scanAllMediasInSdCard(); 
		
		Button button = (Button) this.findViewById(R.id.play_history_button);
//		button.setText("play history");
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DuoKanTempMediaListActivity.this, DuoKanTempPlayHistoryListActivity.class);
				startActivity(intent);
			}
		});
		
		List<String> networkAddress = scanAllMediasInDuoKanText();
		filePaths.addAll(networkAddress);
		
		locations = new String[filePaths.size()];
		for (int i=0; i<locations.length; i++) {
			String path = filePaths.get(i);
			Uri uri = null;
			if (path.startsWith("http") || path.startsWith("https") || path.startsWith("rtsp")) {
				uri = Uri.parse(path);
			} else {
				uri = Uri.fromFile(new File(path));
			}
			if (uri != null) {
				locations[i] = uri.toString();
			} else {
				locations[i] = "";
			}  
		}
		
		
		ListView listView = (ListView) this.findViewById(R.id.video_listview);
		listView.setAdapter(new ListViewAdapter());
		// List<Uri> allMediaUris = scanAllMediasInSdCard();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String path = locations[position];
//				Uri uri = Uri.parse(loc);
				Uri uri = null;
				if (path.startsWith("http") || path.startsWith("https") || path.startsWith("rtsp")) {
					uri = Uri.parse(path);
				} else {
					uri = Uri.fromFile(new File(path));
				}
				String scheme = uri.getScheme();
//				Intent intent = new Intent(DuoKanTempMediaListActivity.this, DuoKanVideoPlayerActivity.class);
				Intent intent = new Intent(DuoKanTempMediaListActivity.this, VideoPlayerActivity.class);
//				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//				intent.setType("video/mp4");
				intent.putExtra(Constants.INTENT_KEY_STRING_ARRAY_URI_LIST, locations);
				intent.putExtra(Constants.INTENT_KEY_INT_PLAY_INDEX, position);
//				intent.putExtra("Airkan", false);
				startActivity(intent);
			}
		});
	}

	 private List<String> scanAllMediasInSdCard() {
		 List<String> result = new ArrayList<String>();
		 // this.gets
		 if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			 File sdcardFile = Environment.getExternalStorageDirectory();
			 if (sdcardFile == null || !sdcardFile.exists()) {
				 return result;
			 }
			 searchVideoFiles(sdcardFile, result);
			 
		 }
		
		 return result;
	 }
	//find video in the root of sdcard
	private void searchVideoFiles(File file, List<String> result) {
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children == null) {
				return;
			}
			for (File child : children) {
				if (child.isFile()) {
					String name = child.getName().toLowerCase();
    				if (name.endsWith(".mp4")
	    					|| name.endsWith(".flv")
	    					|| name.endsWith(".mkv")
	    					|| name.endsWith(".mov")
	    					|| name.endsWith(".wmv")
	    					|| name.endsWith(".webm")
	    					|| name.endsWith(".vob")
	    					|| name.endsWith(".rm")
	    					|| name.endsWith(".rmvb")
	    					|| name.endsWith(".m4v")
	    					|| name.endsWith(".ts")
	    					|| name.endsWith(".m3u8")
	    					|| name.endsWith(".f4v")
	    					|| name.endsWith(".asf")
	    					|| name.endsWith(".mpg")
	    					|| name.endsWith(".3g2")
	    					|| name.endsWith(".3gp")
	    					|| name.endsWith(".3g2b")
	    					|| name.endsWith(".mp3")
	    					|| name.endsWith(".avi")) {

						result.add(child.getAbsolutePath());
					}
				} else if (child.isDirectory()) {
//					searchVideoFiles(child, result);
				}
				
			}
		}
	}

	private class ListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return locations.length;
		}

		@Override
		public Object getItem(int position) {
			return locations[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String loc = (String) getItem(position);
			File file = new File(loc);
			View view = layoutInflater.inflate(R.layout.vp_app_temp_media_listview_item, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.listview_item_imageview);
			TextView nameView = (TextView) view.findViewById(R.id.listview_item_name_textview);
//			TextView desView = (TextView) view.findViewById(R.id.listview_item_desc_textview);
			nameView.setText(file.getName());
			return view;
		}

	}
	
	 private List<String> scanAllMediasInDuoKanText() {
			List<String> result = new ArrayList<String>();
			 if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				 File sdcardFile = Environment.getExternalStorageDirectory();
				 if (sdcardFile == null || !sdcardFile.exists()) {
					 return result;
				 }
				 File urlsFile = new File(sdcardFile.getAbsoluteFile().toString() + File.separator + "duokan_urls.txt");
				 if (urlsFile.exists()) {
					 try {
						BufferedReader br = new BufferedReader(new FileReader(urlsFile));
						String line = null;
						try {
							while ((line = br.readLine()) != null) {
								result.add(line);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					 
				 }
			 }
			return result;
		}
}
