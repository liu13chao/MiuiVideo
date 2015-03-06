/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   MediaSetInfo.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-11-30
 */

package com.miui.video.type;

import java.io.Serializable;

/**
 *@author xuanmingliu
 *
 */

public class MediaSetInfo implements Serializable{
	private static final long serialVersionUID = 2L;

	public int ci;
	public String date;
	public String videoname;
	public int ciidx;
	public int playlength;
	
	public int[] ci_available_download_source;
	
	public static  int indexOfCi(MediaSetInfo[] setList, int ci){
        if(setList != null){
            int start = 0; 
            int end = setList.length  - 1;
            while(start <= end){ 
                int mid = start +  (end -start)/2;
                MediaSetInfo setInfo = setList[mid];
                if(setInfo == null){
                    start++;
                }else{
                    if(setInfo.ci == ci){
                        return mid;
                    }else if(setInfo.ci < ci){
                        start = mid + 1;
                    }else{
                        end = mid -1;
                    }
                }
            }
        }
        return -1;
    }
}