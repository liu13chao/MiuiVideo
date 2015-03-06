/**
 * 
 */
package com.miui.video.type;

import java.io.Serializable;

/**
 * @author dz
 *
 */
public class AddonInfo extends BaseMediaInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public int id;
	public int version;
	public int minappver;
	public String name;
	public String mainclassname;
	public String description;
	public String packageurl;
	public String packagemd5;
	public String packagename;
	public String imageurl;
	public int status;
	public String localPath;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getPackageName() {
		return packagename;
	}

	public void setPackageName(String packagename) {
		this.packagename = packagename;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMainClassName() {
		return mainclassname;
	}
	public void setMainClassName(String mainclassname) {
		this.mainclassname = mainclassname;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getMinAppVer() {
		return minappver;
	}
	public void setMinAppVer(int minappver) {
		this.minappver = minappver;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getPackageUrl() {
		return packageurl;
	}
	public void setPackageUrl(String packageurl) {
		this.packageurl = packageurl;
	}
	public String getPackageMd5() {
		return packagemd5;
	}
	public void setPackageMd5(String packagemd5) {
		this.packagemd5 = packagemd5;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDesc() {
		return "";
	}
	
	@Override
	public ImageUrlInfo getPosterInfo() {
		return new ImageUrlInfo(imageurl, "", null);
	}
    @Override
    public String getMediaStatus() {
        return null;
    }
    
    @Override
    public String getSubtitle() {
        return "";
    }
	
}
