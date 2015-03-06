/**
 * 
 */
package com.miui.video.util;

/**
 * @author tianli
 *
 */
public class TypeTraits<T> {
	
	Class<T> mType;
	
	Class<?> getType(){
		return mType.getClass();
	}
}
