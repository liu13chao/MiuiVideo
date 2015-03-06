/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   InitConnection.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-9 
 */
package com.miui.video.type;

/**
 * @author tianli
 * 
 */
public class InitConnection {
	public String clientVer;  // client version
	public String deviceID;
	public String m3U8Path;   // m3u8 path, for youku or qq video
	public String pythonPath; // decoder path, used for youku or qq to get meida url
	public String cookieFile; //cookie file
	public String serverURL;
	public String logServerURL;
	public int platform;
	public int videoCapability; // video capability of client
	public int audioCapability; // audio capability of client
	public int imageCapability; // image capability of client
	public int maxMediaDescLength; // max length of media description
	public TokenInfo tokenInfo;
	
	//xiaomi
	public String userID;
	public String ssec;
	public String authToken;
	
	public String proxy = null;
	public String modeInfo;      //mobile model
	
	public String userAgent;
}
