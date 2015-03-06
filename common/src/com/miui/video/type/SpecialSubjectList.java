/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   SpecialSubjectList.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-22 
 */
package com.miui.video.type;

import java.io.Serializable;

/**
 * @author tianli
 * 
 */
public class SpecialSubjectList implements Serializable {

	private static final long serialVersionUID = 2L;

	public SpecialSubject[] subjects;
	public long cacheTime;
}
