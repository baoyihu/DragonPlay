package com.baoyihu.mem.response;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.baoyihu.util.CollectionUtils;
import com.baoyihu.util.CollectionUtils.ITransform;

@Root(name = "responseList", strict = false)
public class BatchPlayListResponse
{
    @ElementList(name = "response", required = false, inline = true)
    private List<PlayListResponseItem> responses;
    
    private List<PlayResponse> result;
    
    private static class ResponseITransform<T, U> implements ITransform<PlayListResponseItem, PlayResponse>
    {
        @Override
        public PlayResponse transform(final PlayListResponseItem playListResponseItem)
        {
            return playListResponseItem.getMessage().getPlayResponse();
        }
    }
    
    public List<PlayResponse> getResult()
    {
        return result;
    }
    
    public void setResult(final List<PlayResponse> result)
    {
        this.result = result;
    }
    
    public void setResponses(final List<PlayListResponseItem> responses)
    {
        this.responses = responses;
    }
    
    public List<PlayResponse> getResponses()
    {
        if (result == null && responses != null)
        {
            result = CollectionUtils.transform(responses, new ResponseITransform<PlayListResponseItem, PlayResponse>());
        }
        return result;
    }
    
    public BatchPlayListResponse()
    {
        //Do not remove default constructor, necessary for some case.
    }
}
