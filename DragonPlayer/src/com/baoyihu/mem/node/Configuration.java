package com.baoyihu.mem.node;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.baoyihu.mem.NamedParameter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Root(name = "configuration", strict = false)
public class Configuration
{
    @JsonProperty("cfgType")
    @Element(name = "cfgType", required = true)
    private int cfgType;
    
    @JsonProperty("extensionInfo")
    @ElementList(name = "extensionInfo", required = false)
    private List<NamedParameter> extensionInfo;
    
    public int getCfgType()
    {
        return cfgType;
    }
    
    public void setCfgType(int cfgType)
    {
        this.cfgType = cfgType;
    }
    
    public List<NamedParameter> getExtensionInfo()
    {
        return extensionInfo;
    }
    
    public void setExtensionInfo(List<NamedParameter> extensionInfo)
    {
        this.extensionInfo = extensionInfo;
    }
    
}
