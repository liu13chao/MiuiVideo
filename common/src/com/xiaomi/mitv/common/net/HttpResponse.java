/**
 *  Copyright(C) 2012 DuoKan TV Group
 * 
 *  HttpResponse.java  
 * 
 *  @author tianli (tianli@duokan.com)
 *
 *  @date 2012-6-21
 */
package com.xiaomi.mitv.common.net;

import java.io.InputStream;

/**
 * @author tianli
 *
 */
public class HttpResponse
{
    private long contentLength;
    
    private InputStream contentStream;
    
    public HttpResponse(long length, InputStream stream)
    {
        this.contentLength = length;
        this.contentStream = stream;
    }

    public long getContentLength()
    {
        return contentLength;
    }

    public void setContentLength(long contentLength)
    {
        this.contentLength = contentLength;
    }

    public InputStream getContentStream()
    {
        return contentStream;
    }

    public void setContentStream(InputStream contentStream)
    {
        this.contentStream = contentStream;
    }
    
    
}
