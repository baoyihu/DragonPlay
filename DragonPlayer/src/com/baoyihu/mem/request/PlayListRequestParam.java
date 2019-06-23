package com.baoyihu.mem.request;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "param", strict = false)
public class PlayListRequestParam
{
    @Element(name = "PlayReq", required = true)
    private PlayRequest request;
    
    public PlayListRequestParam(PlayRequest request)
    {
        this.request = request;
    }
    
    public PlayRequest getRequest()
    {
        return request;
    }
    
    public void setRequest(PlayRequest request)
    {
        this.request = request;
    }
    
    public PlayListRequestParam()
    {
        
    }
}
