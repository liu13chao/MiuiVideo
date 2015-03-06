/**
 *  Copyright(C) 2013 XiaoMi TV Group
 * 
 *  Favorite.java  
 * 
 *  @author tianli (tianli@xiaomi.com)
 *
 *  @date 2013-12-1
 */
package com.miui.video.local;

import com.miui.video.type.BaseMediaInfo;

/**
 * @author tianli
 *
 */
public abstract class Favorite extends BaseMediaInfo implements Comparable<Favorite>{
	
    private static final long serialVersionUID = 1L;

    public static final int STATUS_ADDED = 0;
	public static final int STATUS_DELETED = 1;
	public static final int STATUS_SYNC = 2;
	
	protected int mStatus;
	protected long mCreateTime;

	public abstract String getId();
	public abstract BaseMediaInfo getFavoriteItem();

	public int getStatus() {
		return mStatus;
	}

	public void setStatus(int status) {
		this.mStatus = status;
	}

	public long getCreateTime() {
		return mCreateTime;
	}

	public void setCreateTime(long createTime) {
		this.mCreateTime = createTime;
	}
	
	public boolean isDeletedLocally() {
		return mStatus == STATUS_DELETED;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Favorite){
			String id = getId();
			if(id != null){
				return id.equals(((Favorite)o).getId());
			}
		}
		return super.equals(o);
	}

	@Override
	public int compareTo(Favorite another) {
		if(another == null){
			throw new RuntimeException("favorite list should not contain null.");
		}
		return -Long.valueOf(mCreateTime).compareTo(another.mCreateTime);
	}
    @Override
    public String getName() {
        BaseMediaInfo item = getFavoriteItem();
        if(item != null){
            return item.getName();
        }
        return "";
    }
    @Override
    public String getSubtitle() {
        return "";
    }
    
}
