package com.xiaomi.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.miui.video.util.DKLog;

public final class Strings {
	/**
	 * 分割字符串成为List<String>
	 * 
	 * @param strValues
	 *            : 被分割的字符串
	 * @param seperator
	 *            : 分割符, 用于String.split(...)
	 * @return: 字符数组
	 */
	public static final List<String> seperateValues(String strValues,
			String seperator) {
		if (strValues != null && strValues.length() > 0) {
			return Arrays.asList(strValues.split(seperator));
		}
		return new ArrayList<String>();
	}

	/**
	 * 将args通过connector连接
	 * 
	 * @param connector
	 * @param args
	 * @return
	 */
	public static String concat(String connector, String... args) {
		final StringBuilder sb = new StringBuilder();
		if (args != null) {
			for (String v : args) {
				if (v != null) {
					sb.append(v);
				}

				if (connector != null) {
					sb.append(connector);
				}
			}
		}
		if (connector != null) {
			final int len = sb.length();
			final int connectorLen = connector.length();
			sb.delete(len - connectorLen, len);
		}
		return sb.toString();
	}

	private static final long KILO_BYTES = 1024;
	private static final long MEGA_BYTES = KILO_BYTES * 1024;
	private static final long GIGA_BYTES = MEGA_BYTES * 1024;

	public static String formatSize(long size) {
		try {
			if (size <= 0) {
				return "0 B";
			} else if (size < MEGA_BYTES) {
				return String.format(Locale.US, "%.1f K", (size + 0F)
						/ KILO_BYTES);
			} else if (size < GIGA_BYTES) {
				return String.format(Locale.US, "%.1f M", (size + 0F)
						/ MEGA_BYTES);
			} else {
				return String.format(Locale.US, "%.1f G", (size + 0F)
						/ GIGA_BYTES);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			DKLog.e(LOGTAG, "size: " + size);
		}

		return "0 B";
	}

	public static String formatPercent(long completed, long total) {
		if (completed <= 0 || total <= 0) {
			return "0.0%";
		}
		try {
			return String.format(Locale.US, "%.1f%%", completed / total * 100F);
		} catch (Exception e) {
			e.printStackTrace();
			DKLog.e(LOGTAG, "completed: " + completed + ", total: " + total);
		}
		return "0.0%";
	}

	private final static String LOGTAG = "common/Strings";
}
