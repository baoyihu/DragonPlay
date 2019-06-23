package com.baoyihu.mem.response;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.baoyihu.mem.node.VodNode;

@Root(name = "VodListResp")
public class VodList
{
    @Element(name = "counttotal", required = false)
    private int countTotal;
    
    public int getCountTotal()
    {
        return countTotal;
    }
    
    @ElementList(name = "vodlist", type = VodNode.class, required = false)
    private List<VodNode> vodList;
    
    public List<VodNode> getList()
    {
        return vodList;
    }
}
