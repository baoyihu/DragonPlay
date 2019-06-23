package com.baoyihu.mem.response;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import com.baoyihu.mem.node.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticateResponse
{
    @JsonProperty("upgradedomain") //    @Element(name = "upgradedomain", required = false)
    private String upgradeDomain;
    
    @JsonProperty("mgmtdomain")
    //@Element(name = "mgmtdomain", required = false)
    private String managementDomain;
    
    @JsonProperty("ntpdomain")
    @Element(name = "ntpdomain", required = false)
    private String ntpDomain;
    
    @JsonProperty("ntpdomainbackup")
    @Element(name = "ntpdomainbackup", required = false)
    private String ntpdomainbackup;
    
    @JsonProperty("areaid")
    @Element(name = "areaid", required = false)
    private String areaId;
    
    @JsonProperty("templatename")
    @Element(name = "templatename", required = false)
    private String templateName;
    
    @JsonProperty("usergroup")
    @Element(name = "usergroup", required = false)
    private String userGroup;
    
    @JsonProperty("epgurl")
    @Element(name = "epgurl", required = false)
    private String epgUrl;
    
    @JsonProperty("transportprotocol")
    @Element(name = "transportprotocol", required = false)
    private int transportProtocol;
    
    @JsonProperty("sessionid")
    @Element(name = "sessionid", required = false)
    private String sessionId;
    
    @JsonProperty("currenttime")
    @Element(name = "currenttime", required = false)
    private String currentTime;
    
    @JsonProperty("isTriplePlay")
    @Element(name = "isTriplePlay", required = false)
    private String isTriplePlay;
    
    @JsonProperty("STBRCUsubscribed")
    @Element(name = "STBRCUsubscribed", required = false)
    private String stbRCUsubscribed;
    
    @JsonProperty("bandwidth")
    @Element(name = "bandwidth", required = false)
    private int bandwidth;
    
    @JsonProperty("isFirstLogin")
    private String isFirstLogin;
    
    @JsonProperty("dsmdomain")
    @Element(name = "dsmdomain", required = false)
    private String dsmDomain;
    
    @JsonProperty("timezone")
    @Element(name = "timezone", required = false)
    private String timezone;
    
    @JsonProperty("dstTime")
    @Element(name = "dstTime", required = false)
    private String dstTime;
    
    @JsonProperty("subnetId")
    public String subnetId;
    
    @JsonProperty("lockedNum")
    @Element(name = "lockedNum", required = false)
    private int lockedNum;
    
    @JsonProperty("waitUnLockTime")
    @Element(name = "waitUnLockTime", required = false)
    private long waitUnlockTime;
    
    @JsonProperty("remainLockedNum")
    @Element(name = "remainLockedNum", required = false)
    private String remainLockedNum;
    
    @JsonProperty("userID")
    @Element(name = "userID", required = false)
    private String userId;
    
    @JsonProperty("loginOccasion")
    @Element(name = "loginOccasion", required = false)
    private String loginOccasion;
    
    @JsonProperty("paymentType")
    @Element(name = "paymentType", required = false)
    private String paymentType;
    
    @JsonProperty("deviceId")
    @Element(name = "deviceId", required = false)
    private String deviceId;
    
    @JsonProperty("profileId")
    @Element(name = "profileId", required = false)
    private String profileId;
    
    @JsonProperty("userToken")
    @Element(name = "userToken", required = false)
    private String userToken;
    
    @JsonProperty("needSignEULA")
    @Element(name = "needSignEULA", required = false)
    private int needSignEula;
    
    @JsonProperty("packageid")
    @Element(name = "packageid", required = false)
    private String packageid;
    
    @JsonProperty("loginName")
    @Element(name = "loginName", required = false)
    private String loginName;
    
    @JsonProperty("pwdResetTime")
    @Element(name = "pwdResetTime", required = false)
    private String pwdResetTime;
    
    @JsonProperty("location")
    @Element(name = "location", required = false)
    private String location;
    
    @JsonProperty("deviceName")
    @Element(name = "deviceName", required = false)
    private String deviceName;
    
    @JsonProperty("configurations")
    @ElementList(name = "configurations", required = false)
    private List<Configuration> configurations;
    
    @JsonProperty("retmsg")
    private String retmsg;
    
    public String getRetmsg()
    {
        return retmsg;
    }
    
    public List<Configuration> getConfigurations()
    {
        return configurations;
    }
    
    public String getUpgradeDomain()
    {
        return upgradeDomain;
    }
    
    public void setUpgradeDomain(String upgradeDomain)
    {
        this.upgradeDomain = upgradeDomain;
    }
    
    public String getManagementDomain()
    {
        return managementDomain;
    }
    
    public void setManagementDomain(String managementDomain)
    {
        this.managementDomain = managementDomain;
    }
    
    public String getNtpDomain()
    {
        return ntpDomain;
    }
    
    public void setNtpDomain(String ntpDomain)
    {
        this.ntpDomain = ntpDomain;
    }
    
    public String getNtpdomainbackup()
    {
        return ntpdomainbackup;
    }
    
    public void setNtpdomainbackup(String ntpdomainbackup)
    {
        this.ntpdomainbackup = ntpdomainbackup;
    }
    
    public String getAreaId()
    {
        return areaId;
    }
    
    public void setAreaId(String areaId)
    {
        this.areaId = areaId;
    }
    
    public String getTemplateName()
    {
        return templateName;
    }
    
    public void setTemplateName(String templateName)
    {
        this.templateName = templateName;
    }
    
    public String getUserGroup()
    {
        return userGroup;
    }
    
    public void setUserGroup(String userGroup)
    {
        this.userGroup = userGroup;
    }
    
    public String getEpgUrl()
    {
        return epgUrl;
    }
    
    public void setEpgUrl(String epgUrl)
    {
        this.epgUrl = epgUrl;
    }
    
    public int getTransportProtocol()
    {
        return transportProtocol;
    }
    
    public void setTransportProtocol(int transportProtocol)
    {
        this.transportProtocol = transportProtocol;
    }
    
    public String getSessionId()
    {
        return sessionId;
    }
    
    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }
    
    public String getCurrentTime()
    {
        return currentTime;
    }
    
    public void setCurrentTime(String currentTime)
    {
        this.currentTime = currentTime;
    }
    
    public String getIsTriplePlay()
    {
        return isTriplePlay;
    }
    
    public void setIsTriplePlay(String isTriplePlay)
    {
        this.isTriplePlay = isTriplePlay;
    }
    
    public boolean isStbRCUsubscribed()
    {
        return stbRCUsubscribed.equalsIgnoreCase("1");
    }
    
    public int getBandwidth()
    {
        return bandwidth;
    }
    
    public void setBandwidth(int bandwidth)
    {
        this.bandwidth = bandwidth;
    }
    
    public boolean isFirstLogin()
    {
        return isFirstLogin.equalsIgnoreCase("1");
    }
    
    public String getDsmDomain()
    {
        return dsmDomain;
    }
    
    public void setDsmDomain(String dsmDomain)
    {
        this.dsmDomain = dsmDomain;
    }
    
    public String getTimezone()
    {
        return timezone;
    }
    
    public void setTimezone(String timezone)
    {
        this.timezone = timezone;
    }
    
    public String getDstTime()
    {
        return dstTime;
    }
    
    public void setDstTime(String dstTime)
    {
        this.dstTime = dstTime;
    }
    
    public String getSubnetId()
    {
        return subnetId;
    }
    
    public void setSubnetId(String subnetId)
    {
        this.subnetId = subnetId;
    }
    
    public int getLockedNum()
    {
        return lockedNum;
    }
    
    public void setLockedNum(int lockedNum)
    {
        this.lockedNum = lockedNum;
    }
    
    public long getWaitUnlockTime()
    {
        return waitUnlockTime;
    }
    
    public void setWaitUnlockTime(long waitUnlockTime)
    {
        this.waitUnlockTime = waitUnlockTime;
    }
    
    public String getRemainLockedNum()
    {
        return remainLockedNum;
    }
    
    public void setRemainLockedNum(String remainLockedNum)
    {
        this.remainLockedNum = remainLockedNum;
    }
    
    public String getUserId()
    {
        return userId;
    }
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    public String getLoginOccasion()
    {
        return loginOccasion;
    }
    
    public void setLoginOccasion(String loginOccasion)
    {
        this.loginOccasion = loginOccasion;
    }
    
    public String getPaymentType()
    {
        return paymentType;
    }
    
    public void setPaymentType(String paymentType)
    {
        this.paymentType = paymentType;
    }
    
    public String getDeviceId()
    {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }
    
    public String getProfileId()
    {
        return profileId;
    }
    
    public void setProfileId(String profileId)
    {
        this.profileId = profileId;
    }
    
    public String getUserToken()
    {
        return userToken;
    }
    
    public void setUserToken(String userToken)
    {
        this.userToken = userToken;
    }
    
    public int getNeedSignEula()
    {
        return needSignEula;
    }
    
    public void setNeedSignEula(int needSignEula)
    {
        this.needSignEula = needSignEula;
    }
    
    public String getPackageid()
    {
        return packageid;
    }
    
    public void setPackageid(String packageid)
    {
        this.packageid = packageid;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public String getPwdResetTime()
    {
        return pwdResetTime;
    }
    
    public void setPwdResetTime(String pwdResetTime)
    {
        this.pwdResetTime = pwdResetTime;
    }
    
    public String getLocation()
    {
        return location;
    }
    
    public void setLocation(String location)
    {
        this.location = location;
    }
    
    //    public String getDeviceName()
    //    {
    //        return deviceName;
    //    }
    //    
    //    public void setDeviceName(String deviceName)
    //    {
    //        this.deviceName = deviceName;
    //    }
    
    public void setConfigurations(List<Configuration> configurations)
    {
        this.configurations = configurations;
    }
    
}
