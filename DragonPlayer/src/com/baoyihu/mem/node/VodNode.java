package com.baoyihu.mem.node;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "vod", strict = false)
public class VodNode
{
    @Element(name = "id", required = true)
    private String vodId;
    
    @Element(name = "name", required = true)
    private String vodName;
    
    public String getVodId()
    {
        return vodId;
    }
    
    public String getVodName()
    {
        return vodName;
    }
    
}
