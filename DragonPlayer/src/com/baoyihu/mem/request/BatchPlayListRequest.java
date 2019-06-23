package com.baoyihu.mem.request;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "requestList", strict = false)
public class BatchPlayListRequest
{
    @ElementList(name = "request", required = false, inline = true)
    private List<PlayListRequestItem> requestItems;
    
    public void setRequests(List<PlayRequest> requests)
    {
        requestItems = new ArrayList<PlayListRequestItem>();
        for (PlayRequest request : requests)
        {
            requestItems.add(new PlayListRequestItem("Play", new PlayListRequestParam(request)));
        }
    }
    
    public BatchPlayListRequest()
    {
        
    }
}
