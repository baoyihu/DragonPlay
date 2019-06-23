package com.baoyihu.mem.response;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "response", strict = false)
public class PlayListResponseItem
{
    @Element(name = "name")
    private String name;
    
    @Element(name = "msg")
    private PlayListResponseMessage message;
    
    public String getName()
    {
        return name;
    }
    
    public PlayListResponseMessage getMessage()
    {
        return message;
    }
    
    public PlayListResponseItem()
    {
        
    }
}
