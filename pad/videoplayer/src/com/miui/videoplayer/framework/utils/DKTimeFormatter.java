package com.miui.videoplayer.framework.utils;

import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.Locale;

public class DKTimeFormatter {
	private static DKTimeFormatter instance = new DKTimeFormatter();

	private StringBuilder mFormatBuilder;
	private Formatter mFormatter;

	private DKTimeFormatter() {
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
	}

	public static DKTimeFormatter getInstance() {
		return instance;
	}

	public String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}
	
	public int getHoursForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;
		int hours = totalSeconds / 3600;
		return hours;
	}
	
	public int getMinutesForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;
		int minutes = (totalSeconds / 60) % 60;
		return minutes;
	}
	
	public int getSecondsForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;
		int seconds = totalSeconds % 60;
		return seconds;
	}
	
	public String longToDate(long timeMillis) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		return sdf.format(timeMillis);
	}
	
	public String longToDayDate(long timeMillis) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(timeMillis);
	}
}
