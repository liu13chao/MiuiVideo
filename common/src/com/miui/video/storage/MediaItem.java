package com.miui.video.storage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ImageView;

import com.miui.video.type.BaseMediaInfo;
import com.miui.video.type.ImageUrlInfo;
import com.miui.video.util.OrderUtil.NameComparable;

@SuppressLint("SimpleDateFormat")
public abstract class MediaItem extends BaseMediaInfo implements NameComparable{
	
    private static final long serialVersionUID = 1L;

    //	private static HashMap<String, Boolean> sVideoMap = new HashMap<String, Boolean>();
//	private static HashMap<String, Boolean> sAudioMap = new HashMap<String, Boolean>();
//	private static HashMap<String, Boolean> sImageMap = new HashMap<String, Boolean>();
//	private static HashMap<String, Boolean> sFileMap = new HashMap<String, Boolean>();
	private static final String EXT_VALID = "asf;avi;wm;wmp;wmv;m1v;m2p;mts;m2t;m2ts;m2v;mp2v;" +
			"mpeg;mpg;mpv2;pss;pva;tp;tpr;ts;mts;m4b;m4p;m4v;mp4;mpeg4;3g2;3gp;3gp2;3gpp;mov;qt;" +
			"f4v;flv;hlv;ifo;vob;mkv;m4v;webm;rm;ram;rmvb;";
	
	static {
		/* Windows Media */
//		sVideoMap.put("asf", true);sVideoMap.put("avi", true);sVideoMap.put("wm", true);
//		sVideoMap.put("wmp", true);sVideoMap.put("wmv", true);
		/* MPEG 2 */
//		sVideoMap.put("m1v", true);sVideoMap.put("m2p", true);sVideoMap.put("mts", true);
//		sVideoMap.put("mpv2", true);sVideoMap.put("pss", true);sVideoMap.put("pva", true);
//		sVideoMap.put("tp", true);sVideoMap.put("tpr", true);sVideoMap.put("ts", true);
//		sVideoMap.put("mts", true);
		
		/* MPEG 4 */
//		sVideoMap.put("m4b", true);sVideoMap.put("m4p", true);sVideoMap.put("m4v", true);
//		sVideoMap.put("mp4", true);sVideoMap.put("mpeg4", true);
		/* 3GPP */
//		sVideoMap.put("3g2", true);sVideoMap.put("3gp", true);sVideoMap.put("3gp2", true);
//		sVideoMap.put("3gpp", true);
		/* Apple */
//		sVideoMap.put("mov", true);sVideoMap.put("qt", true);
		/* Flash */
//		sVideoMap.put("f4v", true);sVideoMap.put("flv", true);sVideoMap.put("hlv", true);
//		sVideoMap.put("swf", true);
		/* DVD */
//		sVideoMap.put("ifo", true);sVideoMap.put("vob", true);
		/* Other*/
//		sVideoMap.put("mkv", true);sVideoMap.put("m4v", true);sVideoMap.put("webm", true);
		/* Real Media */
//		sVideoMap.put("rm", true);sVideoMap.put("ram", true);sVideoMap.put("rmvb", true);
		
		/* Audio format */
//		sAudioMap.put("mp3", true);sAudioMap.put("wav", true);sAudioMap.put("wma", true);
//		sAudioMap.put("wave", true);sAudioMap.put("au", true);sAudioMap.put("ra", true);
//		sAudioMap.put("aac", true);sAudioMap.put("adpcm", true);sAudioMap.put("alac", true);
//		sAudioMap.put("alaw", true);sAudioMap.put("amr", true);sAudioMap.put("ape", true);
//		sAudioMap.put("cook", true);sAudioMap.put("flac", true);sAudioMap.put("mp2", true);
//		sAudioMap.put("ogg", true);sAudioMap.put("ulaw", true);
//		
//		/* Image format */
//
//		sImageMap.put("jpeg", true);sImageMap.put("jpg", true);sImageMap.put("bmp", true);
//		sImageMap.put("png", true);sImageMap.put("gif", true);
	}
	
	
//	private static final String[] VIDEO_EXT_LIST = {
//			"asf", "avi", "wm", "wmp", "wmv", /* Windows Media */
//			"dat", "m1v", "m2p", "m2t", "m2ts", "m2v", "mp2v", "mpeg", "mpg", "mpv2", "pss", "pva", "tp", "tpr", "ts", /* MPEG 2 */ 
//			"m4b", "m4p", "m4v", "mp4", "mpeg4", /* MPEG 4 */
//			"3g2", "3gp", "3gp2", "3gpp", /* 3GPP */
//			"mov", "qt", /* Apple */
//			"f4v", "flv", "hlv", "swf", /* Flash */
//			"ifo", "vob", /* DVD */
//			"mkv", "m4v",/* Other*/
//			"rm", "ram", "rmvb"/* Real Media */
//			};
//
//	private static final String[] AUDIO_EXT_LIST = {
//			"mp3",
//			"wav",
//			"wma",
//			"wave",
//			"au",
//			"ra",
//			"aac",
//			"adpcm",
//			"alac",
//			"alaw",
//			"amr",
//			"ape",
//			"cook",
//			"flac",
//			"mp2",
//			"ogg",
//			"ulaw"
//		};
//
//	private static final String[] IMAGE_EXT_LIST = {
//			"jpeg", "jpg", "bmp", "png"
//		};

	protected final Context mContext;

	protected String mPath;
	protected String mParentPath = null;
	protected String mRootPath = null; //only for historyMediaItem
	protected boolean mIsDirectory = false;
	protected MediaType mMediaType = MediaType.Unknown;
	protected String mName;
	protected String mHeadName = "";
	private long mPosition;
	private String mPlayTimeText = "";
	private long mLastPlayTime = 0;
//	public boolean isDone = false;
//	public boolean isDirty = false;
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private int mOrientation = 0;
	public ImageView imageView;
	
	public int getOrientation() {
		return mOrientation;
	}
	
	public void setOrientation(int orientation) {
		this.mOrientation = orientation;
	}
	
	public enum MediaType {
		Unknown, Video, Audio, Image, Apk
	};

	public MediaItem(Context context) {
		mContext = context;
	}

	public MediaItem(Context context, String name, boolean isDirectory) {
		mContext = context;
		mName = name;
		mIsDirectory = isDirectory;
	}
	
	@Override
	public String getName() {
		return mName;
	}
	
	@Override
	public String getDesc() {
		return "";
	}
	
	@Override
	public ImageUrlInfo getPosterInfo() {
//		if(mThumbnailTaskInfo == null) {
//			if(!Util.isEmpty(mPath)) {
//				mThumbnailTaskInfo = new ThumbnailTaskInfo(mPath, 3);
//			}
//		}
//		return mThumbnailTaskInfo;
	    return null;
	}
	
	public String getHeadName(){
		return mHeadName;
	}
	
	public void setHeadName(char headName){
		String str = headName +"";
		str = str.toUpperCase(Locale.getDefault());
		this.mHeadName = str;
	}
	
	public String getPath() {
		return mPath;
	}

	public boolean isDirectory() {
		return mIsDirectory;
	}

	public String getMediaUrl() {
		return mPath;
	}

	public MediaType getMediaType() {
		return mMediaType;
	}
	
	protected String getFormatDate(long date){
		return dateFormat.format(new Date(date));
	}

	protected String sizeToString(Long size) {
		String sizeStr = new String();
		if (size == 0) {
			sizeStr = "0Byte";
		} else if (size < 1024) {
			// <1KB
			sizeStr = String.format(Locale.getDefault(), "%dBytes", size);
		} else if (size < 1024 * 1024) {
			// <1MB
			float f = size / 1024;
			sizeStr = String.format(Locale.getDefault(), "%.1fKB", f);
		} else if (size < 1024 * 1024 * 1024) {
			// <1GB
			float f = size / (1024 * 1024);
			sizeStr = String.format(Locale.getDefault(), "%.1fMB", f);
		} else {
			// >1GB
			float f = size / (1024 * 1024 * 1024);
			sizeStr = String.format(Locale.getDefault(), "%.1fGB", f);
		}
		return sizeStr;
	}
	
	public boolean isVideo() {
		return mMediaType == MediaType.Video;
	}

	public boolean isAudio() {
		return mMediaType == MediaType.Audio;
	}
	
	public boolean isImage(){
		return mMediaType == MediaType.Image;
	}

	public boolean isApk(){
		return mMediaType == MediaType.Apk;
	}
	
	@Override
	public boolean equals(Object other){
		if(other == null)
			return false;
		
		if(!(other instanceof MediaItem)){
			return false;
		}
		
		MediaItem otherItem = (MediaItem)other;
		
		return (mPath + mName).equalsIgnoreCase(otherItem.getPath() + otherItem.getName());
	}
	
	@Override
	public int hashCode(){
		return (mPath + mName).hashCode();
	}

	public long getPosition() {
		return mPosition;
	}

	public void setPosition(long position) {
		this.mPosition = position;
	}

	public long getLastPlayTime() {
		return mLastPlayTime;
	}
	
	public String getPlayTimeText() {
		return mPlayTimeText;
	}

	public void setLastPlayTime(long lastPlayTime) {
		this.mPlayTimeText = dateFormat.format(new Date(lastPlayTime));
		this.mLastPlayTime = lastPlayTime;
	}
	
	public String getRootPath(){
		return mRootPath;
	}
	
	public boolean isApply(){
		if(getName().startsWith("."))
			return false;
		
		if(mIsDirectory)
			return true;
		
		return mMediaType == MediaType.Audio 
				|| mMediaType == MediaType.Video
				|| mMediaType == MediaType.Image
				|| mMediaType == MediaType.Apk;
	}

    protected static MediaType getTypeByFilenameExt(String name) {
		try {
			int i = name.lastIndexOf(".");
			if (i>=0) {
				String extension = name.substring(i+1).toLowerCase(Locale.getDefault());
				if(EXT_VALID.contains(extension + ";")){
					if(StorageUtils.isVideoFromFilePath(name)){
						return MediaType.Video;
					}
				}
//				if(sAudioMap.containsKey(extension)){
//					return MediaType.Audio;
//				}
//				if(sImageMap.containsKey(extension)){
//					return MediaType.Image;
//				}
//
//				if(sFileMap.containsKey(extension)){
//					return MediaType.Apk;
//				}
//				String extension = name.substring(i+1);
//				for(String ext : VIDEO_EXT_LIST) {
//					if (extension.equalsIgnoreCase(ext)) {
//						return MediaType.Video;
//					}
//				}
//				for(String ext : AUDIO_EXT_LIST) {
//					if (extension.equalsIgnoreCase(ext)) {
//						return MediaType.Audio;
//					}
//				}
//				for(String ext : IMAGE_EXT_LIST) {
//					if (extension.equalsIgnoreCase(ext)) {
//						return MediaType.Image;
//					}
//				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return MediaType.Unknown;
	}

    @Override
    public String getMediaStatus() {
        return "";
    }

    @Override
    public String getSubtitle() {
         return "";
    }
    
    
	
    
}
