package com.baoyihu.mem.request;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "PlayReq")
public class PlayRequest
{
    
    @Element(name = "contentid", required = false)
    private String playedContentId;
    
    @Element(name = "mediaid", required = false)
    private String playedMediaId;
    
    @Element(name = "playbillid", required = false)
    private String playedPlayBillId;
    
    @Element(name = "playtype", required = false)
    private int playedPlayType;
    
    @Element(name = "begintime", required = false)
    private String playedBeginTime;
    
    @Element(name = "endtime", required = false)
    private String playedEndTime;
    
    @Element(name = "productId", required = false)
    private String playedProductId;
    
    @Element(name = "profileId", required = false)
    private String playedProfileId;
    
    @Element(name = "deviceId", required = false)
    private String playedDeviceId;
    
    public PlayRequest(String contentId, String mediaId, int playType)
    {
        this.playedContentId = contentId;
        this.playedMediaId = mediaId;
        this.playedPlayType = playType;
    }
    
    public PlayRequest(String contentId)
    {
        this.playedContentId = contentId;
    }
    
    public PlayRequest()
    {
        //To change body of created methods use File | Settings | File Templates.
    }
    
    public int getPlayType()
    {
        return playedPlayType;
    }
    
    public void setPlayType(int playType)
    {
        this.playedPlayType = playType;
    }
    
    public String getContentId()
    {
        return playedContentId;
    }
    
    public void setContentId(String contentId)
    {
        this.playedContentId = contentId;
    }
    
    public String getMediaId()
    {
        return playedMediaId;
    }
    
    public void setMediaId(String mediaId)
    {
        this.playedMediaId = mediaId;
    }
    
    public String getPlayBillId()
    {
        return playedPlayBillId;
    }
    
    public void setPlayBillId(String playbillid)
    {
        this.playedPlayBillId = playbillid;
    }
    
    public String getBeginTime()
    {
        return playedBeginTime;
    }
    
    public void setBeginTime(String beginTime)
    {
        this.playedBeginTime = beginTime;
    }
    
    public String getEndTime()
    {
        return playedEndTime;
    }
    
    public void setEndTime(String endTime)
    {
        this.playedEndTime = endTime;
    }
    
    /**
     * @return 返回 productId
     */
    public String getProductId()
    {
        return playedProductId;
    }
    
    /**
     * @param 对productId进行赋值
     */
    public void setProductId(String productId)
    {
        this.playedProductId = productId;
    }
    
    /**
     * @return 返回 profileId
     */
    public String getProfileId()
    {
        return playedProfileId;
    }
    
    /**
     * @param 对profileId进行赋值
     */
    public void setProfileId(String profileId)
    {
        this.playedProfileId = profileId;
    }
    
    /**
     * @return 返回 deviceId
     */
    public String getDeviceId()
    {
        return playedDeviceId;
    }
    
    /**
     * @param 对deviceId进行赋值
     */
    public void setDeviceId(String deviceId)
    {
        this.playedDeviceId = deviceId;
    }
    
}