package com.miui.videoplayer.framework.history;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.net.Uri;
import android.util.Xml;

//IO operation, call in other thread
public class PlayHistoryManager {
	private static final String TAG = PlayHistoryManager.class.getSimpleName();
	
	private static final int HISTORY_ITEM_MAX_COUNT = 100;
	
	private static final String NAME_SPACE = "";
	public static final String PLAY_HISTORY_FILE = "play_history.xml";
	public static final String PLAY_INFO_FILE = "play_info.xml";
	private static final String TAG_ROOT = "playHistoryList";
	private static final String TAG_PLAY_HISTORY = "playHistory";
	private static final String ATTR_POSITION = "position";
	private static final String ATTR_DURATION = "duration";
	private static final String ATTR_URI = "uri";
	private static final String ATTR_TIMESTAMP = "timeStamp";
	private static final String ATTR_MEDIA_ID = "mediaId";
	private static final String ATTR_MEDIA_CI = "mediaCi";
	private static final String ATTR_PLAY_SOURCE = "mediaSource";
	private static final String ATTR_VIDEO_NAME = "videoName";
	private static final String ATTR_QUALITY = "quality";
	private static final String ATTR_HTML5PAGE = "html5Page";
	private static final String ATTR_PLAY_PARAMETER = "playParameter";
	private static final String ATTR_MEDIASETTYPE = "mediaSetType";
	private static final String ATTR_INBOX = "inBox";
	private static final String ATTR_ISSUEDATE = "issueDate";
	private static final String ATTR_BUCKET_NAME = "bucketName";
	private static final String ATTR_IMAGE_MD5 = "imageMd5";
	private static final String ATTR_IMAGE_URL = "imageUrl";
	
	private static final String ATTR_DATE_PLAY_INFO = "datePlayInfo";
	
	public static final String JSON_PLAY_INFO_ROOT_NAME = "content";
	public static final String JSON_PLAY_INFO_ITEM_DATE_NAME = "date";
	public static final String JSON_PLAY_INFO_ITEM_PLAYING_TIME = "playingTime";
	public static final String JSON_PLAY_INFO_ITEM_BUFFER_TIME_NAME = "bufferTime";
	
	private Context mContext;
	private List<PlayHistoryEntry> mPlayHistoryEntryList = new ArrayList<PlayHistoryEntry>();
	
	private String mFileName = PLAY_HISTORY_FILE;
	
	public PlayHistoryManager(Context context) {
		this.mContext = context;
	}

	public PlayHistoryManager(Context context, String fileName) {
		this.mContext = context;
		this.mFileName = fileName;
	}
	
	public void load() {
		mPlayHistoryEntryList = readPlayHistoryList();
	}
	
	//public for testing, will change to private
	public List<PlayHistoryEntry> readPlayHistoryList() {
//		Log.e(TAG, "Read play history!!!!!!!!!!!!!");
		List<PlayHistoryEntry> result = new ArrayList<PlayHistoryEntry>();
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = mContext.openFileInput(mFileName);
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			//return empty list if file does not exist
			return result;
		}
//		Log.e(TAG, fileInputStream + "");
		if (fileInputStream == null) {
			return result;
		}
		XmlPullParser xmlPullParser = Xml.newPullParser();
		try {
			xmlPullParser.setInput(fileInputStream, "UTF-8");
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
//				Log.e("tag: ", eventType + "");
				if (eventType == XmlPullParser.START_DOCUMENT) {
					
				}
				if (eventType == XmlPullParser.START_TAG) {
//					Log.e("name: ", xmlPullParser.getName());
					if (TAG_PLAY_HISTORY.equals(xmlPullParser.getName())) {
						PlayHistoryEntry entry = new PlayHistoryEntry(xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_URI));
						result.add(entry);
//						Log.e("position: ", xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_POSITION));
						String posString = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_POSITION);
						int position = 0;
						if (posString != null) {
							try {
								position = Integer.parseInt(posString);
							} catch (NumberFormatException exception) {
								//ignore: position will be 0 if exception
							}
						}
						entry.setPosition(position);
						String durationString = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_DURATION);
						int duration = 0;
						if(durationString != null) {
							try {
								duration = Integer.parseInt(durationString);
							} catch (NumberFormatException exception) {
								//ignore: duration will be 0 if exception
							}
						}
						entry.setDuration(duration);
						String timeStampString = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_TIMESTAMP);
						long timeStamp = 0L;
						if (timeStampString != null) {
							try {
								timeStamp = Long.parseLong(timeStampString);
							} catch (NumberFormatException exception) {
								//ignore: timeStamp will be 0 if exception
							}
						}
						entry.setTimeStamp(timeStamp);
						
						String mediaId = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_MEDIA_ID);
						entry.setMediaId(mediaId);
						String mediaCi = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_MEDIA_CI);
						entry.setMediaCi(mediaCi);
						String playSource = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_PLAY_SOURCE);
						entry.setPlaySource(playSource);
						String videoName = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_VIDEO_NAME);
						entry.setVideoName(videoName);
						String quality = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_QUALITY);
						entry.setQuality(quality);
						String html5Page = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_HTML5PAGE);
						entry.setHtml5Page(html5Page);
						String playParameter = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_PLAY_PARAMETER);
						entry.setPlayParameter(playParameter);
						String mediaSetType = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_MEDIASETTYPE);
						entry.setMediaSetType(mediaSetType);
						String inBox = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_INBOX);
						entry.setInBox(inBox);
						String issueDate = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_ISSUEDATE);
						entry.setIssueDate(issueDate);
						String bucketName = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_BUCKET_NAME);
						entry.setBucketName(bucketName);
						String imageMd5 = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_IMAGE_MD5);
						entry.setImageMd5(imageMd5);
						String imageUrl = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_IMAGE_URL);
						entry.setImageUrl(imageUrl);
						
						String datePlayInfo = xmlPullParser.getAttributeValue(NAME_SPACE, ATTR_DATE_PLAY_INFO);
						if (datePlayInfo != null){
							JSONObject jsonObject;
							try {
								jsonObject = new JSONObject(datePlayInfo);
							} catch (JSONException e) {
//								e.printStackTrace();
								jsonObject = null;
							}
							entry.setDatePlayInfoJsonObject(jsonObject);
						}
					}
				}
				eventType = xmlPullParser.next();
//				Log.e("next : ", xmlPullParser.next() + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return result;
	}
	
	public void save() {
//		Log.e(TAG, "write count: " + playHistoryList.size());
//		Log.e(TAG, "Save play history!!!!!!!!!!!!!");
		XmlSerializer xmlSerializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			xmlSerializer.setOutput(writer);
			xmlSerializer.startDocument("UTF-8", true);
			xmlSerializer.startTag(NAME_SPACE, TAG_ROOT);
			for (PlayHistoryEntry entry : mPlayHistoryEntryList) {
				xmlSerializer.startTag(NAME_SPACE, TAG_PLAY_HISTORY);
				xmlSerializer.attribute(NAME_SPACE, ATTR_POSITION, entry.getPosition() + "");
				xmlSerializer.attribute(NAME_SPACE, ATTR_DURATION, entry.getDuration() + "");
				xmlSerializer.attribute(NAME_SPACE, ATTR_URI, entry.getUri() + "");
				xmlSerializer.attribute(NAME_SPACE, ATTR_TIMESTAMP, entry.getTimeStamp() + "");
				if (entry.getMediaId() != null) {
					xmlSerializer.attribute(NAME_SPACE, ATTR_MEDIA_ID, entry.getMediaId());
				}
				if (entry.getMediaCi() != null) {
					xmlSerializer.attribute(NAME_SPACE, ATTR_MEDIA_CI, entry.getMediaCi());
				}
				if (entry.getPlaySource() != null) {
					xmlSerializer.attribute(NAME_SPACE, ATTR_PLAY_SOURCE, entry.getPlaySource());
				}
				if (entry.getVideoName() != null) {
					xmlSerializer.attribute(NAME_SPACE, ATTR_VIDEO_NAME, entry.getVideoName());
				}
				if (entry.getQuality() != null) {
					xmlSerializer.attribute(NAME_SPACE, ATTR_QUALITY, entry.getQuality());
				}
				if (entry.getHtml5Page() != null) {
					xmlSerializer.attribute(NAME_SPACE, ATTR_HTML5PAGE, entry.getHtml5Page());
				}
//				if (entry.getVideoRepoRequest() != null) {
//					xmlSerializer.attribute(NAME_SPACE, ATTR_VIDEO_REPO_REQUEST, entry.getVideoRepoRequest());
//				}
				if (entry.getPlayParameter() != null) {
					xmlSerializer.attribute(NAME_SPACE, ATTR_PLAY_PARAMETER, entry.getPlayParameter());
				}
				if (entry.getMediaSetType() != null) {
					xmlSerializer.attribute(NAME_SPACE, ATTR_MEDIASETTYPE, entry.getMediaSetType());
				}
				if (entry.getInBox() != null) {
					xmlSerializer.attribute(NAME_SPACE, ATTR_INBOX, entry.getInBox());
				}
				if (entry.getIssueDate() != null) {
					xmlSerializer.attribute(NAME_SPACE, ATTR_ISSUEDATE, entry.getIssueDate());
				}
				if (entry.getBucketName() != null) {
					xmlSerializer.attribute(NAME_SPACE, ATTR_BUCKET_NAME, entry.getBucketName());
				}
				if (entry.getImageMd5() != null) {
					xmlSerializer.attribute(NAME_SPACE, ATTR_IMAGE_MD5, entry.getImageMd5());
				}
				if (entry.getImageUrl() != null) {
					xmlSerializer.attribute(NAME_SPACE, ATTR_IMAGE_URL, entry.getImageUrl());
				}
				
				if (entry.getDatePlayInfoJsonObject() != null) {
					xmlSerializer.attribute(NAME_SPACE, ATTR_DATE_PLAY_INFO, entry.getDatePlayInfoJsonObject().toString());
				}
				xmlSerializer.endTag(NAME_SPACE, TAG_PLAY_HISTORY);
			}
			xmlSerializer.endTag(NAME_SPACE, TAG_ROOT);
			xmlSerializer.endDocument();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		Log.e("String: ", writer.toString());
		FileOutputStream fileOutputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		try {
			fileOutputStream = mContext.openFileOutput(mFileName, Context.MODE_WORLD_WRITEABLE + Context.MODE_WORLD_READABLE);
			outputStreamWriter = new OutputStreamWriter(fileOutputStream);
			outputStreamWriter.write(writer.toString());
			outputStreamWriter.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeIOStream(fileOutputStream);
			closeIOStream(outputStreamWriter);
		}

	}
	
	private void closeIOStream(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public PlayHistoryEntry findPlayHistory(Uri uri) {
		if (uri == null) {
			return null;
		}
		for (PlayHistoryEntry entry : mPlayHistoryEntryList) {
			String entryUri = entry.getUri();
			if (entryUri != null && entryUri.equals(uri.toString())) {
				return entry;
			}
		}
		return null;
	}

	public PlayHistoryEntry findPlayHistoryById(String mediaId) {
		if (mediaId == null) {
			return null;
		}
		for (PlayHistoryEntry entry : mPlayHistoryEntryList) {
			String histMediaId = entry.getMediaId();
			if (histMediaId != null && histMediaId.equals(mediaId)) {
				return entry;
			}
		}
		return null;
	}

	public PlayHistoryEntry findPlayHistoryByHtml5Page(String html5Page) {
		if (html5Page == null) {
			return null;
		}
		for (PlayHistoryEntry entry : mPlayHistoryEntryList) {
			String histHtml5Page = entry.getHtml5Page();
			if (histHtml5Page != null && histHtml5Page.equals(html5Page)) {
				return entry;
			}
		}
		return null;
	}

	public void moveToFirst(PlayHistoryEntry oldEntry) {
		if (mPlayHistoryEntryList == null) {
			return;
		}
		mPlayHistoryEntryList.remove(oldEntry);
		oldEntry.setTimeStamp(System.currentTimeMillis());
		mPlayHistoryEntryList.add(0, oldEntry);
	}

	public void addToHistory(PlayHistoryEntry entry) {
		if (mPlayHistoryEntryList == null) {
			return;
		}
		mPlayHistoryEntryList.add(0, entry);
		if (mPlayHistoryEntryList.size() > HISTORY_ITEM_MAX_COUNT) {
			mPlayHistoryEntryList.remove(mPlayHistoryEntryList.size() - 1);
		}
	}

	public void recordLastPlayPosition(String uri, int position) {
		PlayHistoryEntry entry = findPlayHistory(Uri.parse(uri));
		if (entry == null) {
			entry = new PlayHistoryEntry(uri);
			entry.setTimeStamp(System.currentTimeMillis());
			addToHistory(entry);
		}
		entry.setPosition(position);
	}

	public void clear() {
		if (mPlayHistoryEntryList != null) {
			mPlayHistoryEntryList.clear();
		}
	}
	
	public static class PlayHistoryEntry {
		private String mUri;
		private int mPosition = 0;
		private int mDuration;
		private long mTimeStamp;
		
		//corresponding the media 
		private String mMediaId;
		
		//current playing number 
		private String mMediaCi;
		
		//source, example : sohu or qiyi
		private String mPlaySource;
		
		private String mVideoName;
		private String mQuality;
		private String mHtml5Page;
		private String mVideoRepoRequest;
		
		private String mPlayParameter;
		private String mMediaSetType;
		private String mInBox;
		private String mIssueDate;
		
		private String mBucketName;
		private String mImageMd5;
		private String mImageUrl;
		
//		private String mPlayTime;
//		private String mBufferingTimes;
		
		private JSONObject mDatePlayInfoJsonObject;
		
		public String getMediaSetType() {
			return mMediaSetType;
		}

		public void setMediaSetType(String mediaSetType) {
			this.mMediaSetType = mediaSetType;
		}

		public String getInBox() {
			return mInBox;
		}

		public void setInBox(String inBox) {
			this.mInBox = inBox;
		}

		public String getIssueDate() {
			return mIssueDate;
		}

		public void setIssueDate(String issueDate) {
			this.mIssueDate = issueDate;
		}
		
		public void setBucketName(String bucketName) {
			this.mBucketName = bucketName;
		}
		
		public String getBucketName() {
			return mBucketName;
		}
		
		public void setImageMd5(String imageMd5) {
			this.mImageMd5 = imageMd5;
		}
		
		public String getImageMd5() {
			return mImageMd5;
		}
		
		public void setImageUrl(String imageUrl) {
			this.mImageUrl = imageUrl;
		}
		
		public String getImageUrl() {
			return mImageUrl;
		}

		public String getPlayParameter() {
			return mPlayParameter;
		}

		public void setPlayParameter(String playParameter) {
			this.mPlayParameter = playParameter;
		}

		public long getTimeStamp() {
			return mTimeStamp;
		}

		public void setTimeStamp(long timeStamp) {
//			Log.e("SET :" + this.mUri, timeStamp + "");
			this.mTimeStamp = timeStamp;
		}

		public PlayHistoryEntry(String uri) {
			this.mUri = uri;
		}
		
		public void setPosition(int position) {
//			Log.e("SET :" + this.mUri, position + "");
			this.mPosition = position;
		}
		
		public void setDuration(int duration) {
			this.mDuration = duration;
		}
		
		public String getUri() {
			return mUri;
		}

		public void setUri(String uri) {
			this.mUri = uri;
		}

		public int getPosition() {
			return mPosition;
		}
		
		public int getDuration() {
			return mDuration;
		}

		public String getMediaId() {
			return mMediaId;
		}

		public void setMediaId(String mediaId) {
			this.mMediaId = mediaId;
		}

		public String getMediaCi() {
			return mMediaCi;
		}

		public void setMediaCi(String mediaCi) {
			this.mMediaCi = mediaCi;
		}

		public String getPlaySource() {
			return mPlaySource;
		}

		public void setPlaySource(String playSource) {
			this.mPlaySource = playSource;
		}

		public String getVideoName() {
			return mVideoName;
		}

		public void setVideoName(String videoName) {
			this.mVideoName = videoName;
		}

		public String getQuality() {
			return mQuality;
		}

		public void setQuality(String quality) {
			this.mQuality = quality;
		}

		public String getHtml5Page() {
			return mHtml5Page;
		}

		public void setHtml5Page(String html5Page) {
			this.mHtml5Page = html5Page;
		}
		
		public String getVideoRepoRequest() {
			return mVideoRepoRequest;
		}

		public void setVideoRepoRequest(String videoRepoRequest) {
			this.mVideoRepoRequest = videoRepoRequest;
		}
		
		public JSONObject getDatePlayInfoJsonObject() {
			return mDatePlayInfoJsonObject;
		}

		public void setDatePlayInfoJsonObject(JSONObject datePlayInfoJsonObject) {
			this.mDatePlayInfoJsonObject = datePlayInfoJsonObject;
		}
		
	}
}
