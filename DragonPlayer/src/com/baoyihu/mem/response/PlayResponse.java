package com.baoyihu.mem.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "PlayResp", strict = false)
public class PlayResponse
{
    
    @Element(required = false)
    private String url;
    
    public String getUrl()
    {
        return url;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }
    
    public PlayResponse()
    {
        
    }
    
    public boolean isMPD()
    {
        if (url != null && url.contains(".mpd"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public boolean isHLS()
    {
        if (url != null && url.contains(".m3u8"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}