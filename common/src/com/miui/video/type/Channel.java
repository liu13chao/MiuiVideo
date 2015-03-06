/**
 *  Copyright(C) 2012 DuoKan TV Group
 * 
 *  ChannelInfo.java  
 * 
 *  @author tianli (tianli@duokan.com)
 *
 *  @date 2012-8-9
 */
package com.miui.video.type;

import java.io.Serializable;

/**
 * @author tianli
 * 
 */
public class Channel implements Serializable {
    private static final long serialVersionUID = 2L;

    public static final int CHANNEL_ID_SERIES = 33554432; 
    public static final int CHANNEL_ID_MOVIE = 16777216; 
    public static final int CHANNEL_ID_VARIETY = 67108864;
    public static final int CHANNEL_ID_YY = 170000000;
    public static final int CHANNEL_ID_TV = 150994944; 

    public final static int CHANNEL_TYPE_UNKOWN = 0;
    public final static int CHANNEL_TYPE_MOVIE = 1;
    public final static int CHANNEL_TYPE_VARIETY = 2;
    public final static int CHANNEL_TYPE_YY = 3;
    public final static int CHANNEL_TYPE_TV = 4;

    public int id;
    public String name;
    public int listtype;  //海报类型：0V, 1H, 2Tv
    public int type; // 子分类信息数量，没有的话为0,有的话为1
    public int channeltype;  //频道分类
    public Channel[] sub;    //进入更多的子分类
    //	public Channel[] recsub; //首页的子分类
    public Channel[] subfilter;
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Channel) {
            return ((Channel) o).id == id;
        }
        return super.equals(o);
    }

    public static boolean isTvChannel(int channelId){
        if(channelId == CHANNEL_ID_TV){
            return true;
        }
        return false;
    }

    public static boolean isInformationType(int channelType){
        if(channelType == 1){
            return true;
        }
        return false;
    }
    
    public static boolean isMovieChannel(int channelId){
        if(channelId == CHANNEL_ID_MOVIE){
            return true;
        }
        return false;
    }

    public boolean isTvChannel(){
        if(id == CHANNEL_ID_TV){
            return true;
        }
        return false;
    }

    public boolean isInformationType(){
        if(channeltype == 1){
            return true;
        }
        return false;
    }
    
    public int getChannelType(){
        switch(id){
        case CHANNEL_ID_MOVIE:
            return CHANNEL_TYPE_MOVIE;
        case CHANNEL_ID_VARIETY:
            return CHANNEL_TYPE_VARIETY;
        case CHANNEL_ID_YY:
            return CHANNEL_TYPE_YY;
        }
        return CHANNEL_TYPE_UNKOWN;
    }

    //	public final static String CHANNEL_NAME_DIANYING = "电影";
    //	public final static String CHANNEL_NAME_DIANSHIJU = "电视剧";
    //	public final static String CHANNEL_NAME_ZONGYI = "综艺";
    //	public final static String CHANNEL_NAME_DONGMAN = "动漫";
    //	public final static String CHANNEL_NAME_JILUPIAN = "纪录片";
    //	public final static String CHANNEL_NAME_ZIXUN = "资讯";
    //	public final static String CHANNEL_NAME_YOUXI = "游戏竞技场";

    //	public int getVarietyChannelType(){
    //		if(name != null){
    //			if(name.equals(CHANNEL_NAME_DIANYING)){
    //				return CHANNEL_TYPE_DIANYING;
    //			}else if(name.equals(CHANNEL_NAME_DIANSHIJU)){
    //				return CHANNEL_TYPE_DIANSHIJU;
    //			}else if(name.equals(CHANNEL_NAME_ZONGYI)){
    //				return CHANNEL_TYPE_ZONGYI;
    //			}else if(name.equals(CHANNEL_NAME_DONGMAN)){
    //				return CHANNEL_TYPE_DONGMAN;
    //			}else if(name.equals(CHANNEL_NAME_JILUPIAN)){
    //				return CHANNEL_TYPE_JILUPIAN;
    //			}else if(name.equals(CHANNEL_NAME_ZIXUN)){
    //				return CHANNEL_TYPE_ZIXUN;
    //			}else if(name.equals(CHANNEL_NAME_YOUXI)){
    //				return CHANNEL_TYPE_YOUXI;
    //			}
    //		}
    //		return CHANNEL_TYPE_UNKOWN;
    //	}
}
