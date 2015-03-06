package com.miui.video.type;
/**
 * @author: shangmin@xiaomi.com
 * @since: 2012-11-12
 */
import org.apache.http.NameValuePair;

/**
 * @author: shangmin@xiaomi.com
 * @since: 2012-11-6
 */
public class StringToStringPair implements NameValuePair {
	private String name;
	private String value;

	public StringToStringPair(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}
}
