package com.xiaomi.miui.pushads.sdk.trace;

/**
 * the cell saved in cache file.
 * @author liuwei
 *
 */
class AdsCacheCell {

    public AdsCacheCell() {

    }

    public AdsCacheCell(int showType, String base64, String md5) {
        mShowType = showType;
        mBase64 = base64;
        mMd5 = md5;
    }

    public int    mShowType;
    public String mBase64;
    public String mMd5;
}
