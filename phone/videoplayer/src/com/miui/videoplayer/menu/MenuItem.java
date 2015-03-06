/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MenuItem.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-11-29
 */
package com.miui.videoplayer.menu;

/**
 * @author tianli
 *
 */
public class MenuItem {
    
    private int mId;

    private int mText;
    
    private int mIcon;
    
    public MenuItem(){
    }
    
    public MenuItem(int id, int text, int icon){
        mId = id;
        mText = text;
        mIcon = icon;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public int getText() {
        return mText;
    }

    public void setText(int mText) {
        this.mText = mText;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int mIcon) {
        this.mIcon = mIcon;
    }
    
}
