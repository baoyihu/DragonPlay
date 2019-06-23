package com.baoyihu.mem.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "msg", strict = false)
public class PlayListResponseMessage
{
    
    @Element(name = "PlayResp", required = false)
    private PlayResponse playResponse;
    
    public PlayResponse getPlayResponse()
    {
        return playResponse;
    }
    
    public PlayListResponseMessage()
    {
        
    }
    
}
