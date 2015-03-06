/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   BannerRecommendation.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-9-17 
 */
package com.miui.video.type;

import com.miui.video.api.ApiConfig;

/**
 * @author tianli
 *
 */
public class Banner extends BaseMediaInfo {

	private static final long serialVersionUID = 3L;

	public int midtype;    //0影片, 1人物, 100专题, 200直播, 300插件
	
	// media info
	public int mediaid;
	public String medianame;
	public String desc;
	public int rank;
	public String posterurl;
	public String md5;
	public int resolution;
	public float score;
	public String director;
	public int setnow;
	public int playcount;
	public int scorecount;
	public int flag;
	public int setcount;
	public String actors;
	public int ismultset;
	public int playlength;
	public String category;

	// addon info
	public String mainclassname;
	public int minappver;
	public int version;
	public String packageurl;
	public String packagemd5;
	public String packagename;
	public String bannerurl;
	
	// tv info
    public int source;
    public char headletter = '#';  //电视台首字母, char
    public String videoidentifying;  //电台播放id
    public int epgid;                //佳视互动电台播放id
    public String cmccid;
    public int backgroundcolor;  //海报背景颜色
    public int hotindex;  
    public int channelid;        //和mediaId是同一个东西，和后台约定字段的时候重复了
    public String channelname;   //和mediaName是同一个东西，和后台约定字段的时候重复了
    public TelevisionShow currentprogramme;  //电台当前节目
	
	public MediaInfo mediaInfo;
	public SpecialSubject specialSubjectInfo;
	public AddonInfo addonInfo;
	
	//TODO: support tv info
	public void completeData() {
		switch (midtype) {
		case ApiConfig.ID_TYPE_MEDIA:
		    mediaInfo = new MediaInfo();
		    mediaInfo.mediaid = mediaid;
		    mediaInfo.medianame = medianame;
		    mediaInfo.posterurl = posterurl;
		    mediaInfo.md5 = md5;
		    mediaInfo.flag = flag;
		    mediaInfo.resolution = resolution;
		    mediaInfo.category = category;
		    mediaInfo.director = director;
		    mediaInfo.actors = actors;
		    mediaInfo.score = score;
		    mediaInfo.setnow = setnow;
		    mediaInfo.playlength = playlength;
		    mediaInfo.setcount = setcount;
		    mediaInfo.playcount = playcount;
		    mediaInfo.scorecount = scorecount;
		    mediaInfo.ismultset = ismultset;
			break;
		case ApiConfig.ID_TYPE_SPECIALSUBJECT:
		    specialSubjectInfo = new SpecialSubject();
		    specialSubjectInfo.id = mediaid;
		    specialSubjectInfo.name = medianame;
		    specialSubjectInfo.desc = desc;
		    specialSubjectInfo.posterurl = posterurl;
		    specialSubjectInfo.md5 = md5;
			break;
		case ApiConfig.ID_TYPE_ADDON:
		    addonInfo = new AddonInfo();
		    addonInfo.setId(mediaid);
		    addonInfo.setVersion(version);
		    addonInfo.setPackageName(packagename);
		    addonInfo.setMainClassName(mainclassname);
		    addonInfo.setStatus(1);
		    addonInfo.setName(medianame);
		    addonInfo.setDescription(desc);
		    addonInfo.setMinAppVer(minappver);
		    addonInfo.setPackageUrl(packageurl);
		    addonInfo.setPackageMd5(packagemd5);
		    addonInfo.imageurl = bannerurl;
			break;
		default:
			break;
		}		
	}

    @Override
    public String getName() {
        switch (midtype) {
        case ApiConfig.ID_TYPE_MEDIA:
            if(mediaInfo != null){
                return mediaInfo.getName();
            }
        case ApiConfig.ID_TYPE_SPECIALSUBJECT:
            if(specialSubjectInfo != null){
                return specialSubjectInfo.getName();
            }
        case ApiConfig.ID_TYPE_ADDON:
            if(addonInfo != null){
                return addonInfo.getName();
            }
        default:
            break;
        }       
        return "";
    }

    @Override
    public String getDesc() {
        switch (midtype) {
        case ApiConfig.ID_TYPE_MEDIA:
            if(mediaInfo != null){
                return mediaInfo.getDesc();
            }
        case ApiConfig.ID_TYPE_SPECIALSUBJECT:
            if(specialSubjectInfo != null){
                return specialSubjectInfo.getDesc();
            }
        case ApiConfig.ID_TYPE_ADDON:
            if(addonInfo != null){
                return addonInfo.getDesc();
            }
        default:
            break;
        }       
        return "";
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