package com.baoyihu.mem.request;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "request", strict = false)
public class PlayListRequestItem
{
    
    @Element(name = "name", required = true)
    private String name;
    
    @Element(name = "param", required = true)
    private PlayListRequestParam plaListParam;
    
    public PlayListRequestItem(String name, PlayListRequestParam param)
    {
        this.name = name;
        this.plaListParam = param;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public PlayListRequestParam getParam()
    {
        return plaListParam;
    }
    
    public void setParam(PlayListRequestParam param)
    {
        this.plaListParam = param;
    }
    
    public PlayListRequestItem()
    {
        
    }
}
