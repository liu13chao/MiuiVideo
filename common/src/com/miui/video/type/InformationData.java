package com.miui.video.type;


public class InformationData extends OnlineMediaInfo {
    
	private static final long serialVersionUID = 2L;
	
	public int playcount;
	public String playurl;
	public int source;
	public boolean sdkdisable;
	public String sdkinfo2;
	public int resolution;
	public int channelid;
    public int playType;
	public int playlength;  //播放时长  second
	
	private String formatMediaDuration() {
		int mediaDurationSecond = playlength;
		int hour = (int) (mediaDurationSecond / 60 / 60);
		int minute = (int) ((mediaDurationSecond - hour * 3600) / 60);
		int second = (int) ((mediaDurationSecond - hour * 3600) % 60);
		StringBuilder strBuilder = new StringBuilder();
		if(hour > 0){
			if( hour < 10)
				strBuilder.append("0");
			strBuilder.append(hour);
			strBuilder.append(":");
		}
		if( minute < 10)
			strBuilder.append("0");
		strBuilder.append(minute);
		strBuilder.append(":");
		if( second < 10)
			strBuilder.append("0");
		strBuilder.append(second);
		return strBuilder.toString();
	}
	
	@Override
    public String getMediaStatus() {
        return formatMediaDuration();
    }

    @Override
	public String getDesc() {
		return formatMediaDuration();
	}
	
	@Override
	public String getDescSouth() {
		return formatMediaDuration();
	}
}
