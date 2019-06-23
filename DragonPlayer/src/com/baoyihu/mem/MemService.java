package com.baoyihu.mem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.xml.core.PersistenceException;

import com.baoyihu.common.util.DebugLog;
import com.baoyihu.dragonfly.url.Media;
import com.baoyihu.dragonfly.xml.SerializerService;
import com.baoyihu.dragonplayer.DragonApplication;
import com.baoyihu.mem.node.Category;
import com.baoyihu.mem.node.Configuration;
import com.baoyihu.mem.node.VodNode;
import com.baoyihu.mem.request.PlayRequest;
import com.baoyihu.mem.response.AuthenticateResponse;
import com.baoyihu.mem.response.CategoryListResponse;
import com.baoyihu.mem.response.PlayResponse;
import com.baoyihu.mem.response.VodList;

import android.text.TextUtils;

public class MemService
{
    private static final String TAG = "MemService";
    
    private static final String EPGHead = "https://140.207.250.4:33207/EPG/";
    
    private static final int BATCH_SIZE = 40;// 不能超过100个
    
    private boolean authenticate = false;
    
    private List<Configuration> configList = null;
    
    public interface ProgressInterface
    {
        void onPercentChange(int percent);
    }
    
    private ExecutorService service = null;
    
    public MemService()
    {
        BlockingQueue<Runnable> runnableQueue = new LinkedBlockingQueue<Runnable>();
        service = new ThreadPoolExecutor(50, 50, 2000L, TimeUnit.MILLISECONDS, runnableQueue);
    }
    
    public void release()
    {
        service.shutdown();
        service = null;
    }
    
    public boolean logIn()
    {
        if (!authenticate)
        {
            authenticate = autenticateCBG();
            cetificate();
        }
        
        return authenticate;
    }
    
    public List<Category> getAllCategoryList(String videType)
    {
        final ArrayList<Category> totalList = new ArrayList<Category>();
        TreeMap<String, Category> map = new TreeMap<String, Category>();
        List<Category> recommendList = getRecommendList();
        List<Category> channelList = getChannelList();
        List<Category> categoryList = getCategoryList("2000000008");
        for (Category category : recommendList)
        {
            map.put(category.getName(), category);
        }
        for (Category category : channelList)
        {
            map.put(category.getName(), category);
        }
        for (Category category : categoryList)
        {
            DebugLog.info(TAG, "getAllCategoryList name:" + category.getName() + " id:" + category.getCategoryId());
            map.put(category.getName(), category);
        }
        totalList.addAll(map.values());
        return totalList;
    }
    
    public List<Media> downloadCategroyUrl(String categoryId, ProgressInterface progressHandler, String videoType)
    {
        DebugLog.debug(TAG, "downloadCategroyUrl begin:");
        List<Media> retMedia = new ArrayList<Media>();
        if (!authenticate)
        {
            DebugLog.info(TAG, "downloadCategroyUrl return authenticate failed:");
            return retMedia;
        }
        String getVodListStr = EPGHead + "XML/VodList";
        int count = BATCH_SIZE;
        int from = 0;
        int totalCount = Integer.MAX_VALUE;
        int receivedCount = 0;
        
        while (receivedCount < totalCount)
        {
            DebugLog.info(TAG, "queryVodList begin 1:");
            from = receivedCount;
            String vodListBuffer =
                "<VodListReq><action>0</action><count>" + count + "</count><categoryid>%s</categoryid><offset>" + from
                    + "</offset><orderType>3</orderType></VodListReq>";//
            Map<String, VodNode> ret = new HashMap<String, VodNode>();
            
            String request = String.format(vodListBuffer, categoryId);
            String vodListStr = HttpHelper.sendForEPG(getVodListStr, request, null);
            
            VodList vodList = null;
            if (!TextUtils.isEmpty(vodListStr))
            {
                try
                {
                    vodList = SerializerService.fromXml(VodList.class, vodListStr);
                    totalCount = vodList.getCountTotal();
                    DebugLog.info(TAG, "queryVodList totalCount:" + totalCount);
                }
                catch (PersistenceException e)
                {
                    DebugLog.info(TAG, "parse string:" + vodListStr);
                    DebugLog.trace(TAG, e);
                }
            }
            receivedCount += count;
            if (vodList != null)
            {
                DebugLog.debug(TAG, "getAllVodList begin 2:" + vodList.getList().size());
                List<VodNode> vods = vodList.getList();
                
                if (vods != null)
                {
                    for (VodNode node : vods)
                    {
                        DebugLog.debug(TAG, "getAllVodList name:" + node.getVodName() + " id:" + node.getVodId());
                        ret.put(node.getVodId(), node);
                    }
                }
            }
            if (!ret.isEmpty())
            {
                List<Media> batchMedia = getBatchPlayUrl(ret, videoType);
                retMedia.addAll(batchMedia);
            }
            int percent = 100;
            if (totalCount > 0)
            {
                percent = (receivedCount * 100) / totalCount;
            }
            progressHandler.onPercentChange(percent);
        }
        DebugLog.debug(TAG, "downloadCategroyUrl end:" + retMedia.size());
        return retMedia;
    }
    
    private List<Category> getChannelList()
    {
        List<Category> ret = new ArrayList<Category>();
        String hotChannelStr = null;
        for (Configuration config : configList)
        {
            int type = config.getCfgType();
            if (type == 0)
            {
                for (NamedParameter namedParam : config.getExtensionInfo())
                {
                    if (namedParam.getKey().equalsIgnoreCase("hot_channel"))
                    {
                        hotChannelStr = namedParam.getValue();
                        break;
                    }
                }
            }
        }
        JSONArray cateogoryArray;
        try
        {
            JSONArray channelArray = new JSONArray(hotChannelStr);
            for (int iLoop = 0; iLoop < channelArray.length(); iLoop++)
            {
                JSONObject channelObj = channelArray.getJSONObject(iLoop);
                cateogoryArray = channelObj.getJSONArray("data");
                for (int jLoop = 0; jLoop < cateogoryArray.length(); jLoop++)
                {
                    JSONObject objectItem = cateogoryArray.getJSONObject(jLoop);
                    if (objectItem.has("categoryId"))
                    {
                        long categoryId = objectItem.getLong("categoryId");
                        String name = objectItem.getString("name");
                        Category category = new Category();
                        category.setCategoryId(String.valueOf(categoryId));
                        category.setName(name);
                        ret.add(category);
                    }
                }
            }
            
        }
        catch (JSONException e)
        {
            DebugLog.trace(TAG, e);
        }
        return ret;
    }
    
    private List<Category> getRecommendList()
    {
        List<Category> ret = new ArrayList<Category>();
        String hotCategoryStr = null;
        for (Configuration config : configList)
        {
            int type = config.getCfgType();
            if (type == 0)
            {
                for (NamedParameter namedParam : config.getExtensionInfo())
                {
                    if (namedParam.getKey().equalsIgnoreCase("hot_recommend"))
                    {
                        hotCategoryStr = namedParam.getValue();
                        break;
                    }
                }
            }
        }
        JSONArray ararys;
        try
        {
            ararys = new JSONArray(hotCategoryStr);
            for (int i = 0; i < ararys.length(); i++)
            {
                JSONObject objectItem = ararys.getJSONObject(i);
                long categoryId = objectItem.getLong("categoryId");
                String name = objectItem.getString("name");
                Category category = new Category();
                category.setCategoryId(String.valueOf(categoryId));
                category.setName(name);
                ret.add(category);
            }
        }
        catch (JSONException e)
        {
            DebugLog.trace(TAG, e);
        }
        
        return ret;
    }
    
    //vod_recommended_
    private List<Category> getCategoryList(String vodRecmmendedId)
    {
        List<Category> ret = new ArrayList<Category>();
        
        String getCategoryDataStr = "<CategoryListReq> <count>-1</count>  <categoryid>" + vodRecmmendedId
            + "</categoryid>  <offset>0</offset>   <type>VOD</type> </CategoryListReq>";//
        String getCategoryCmdStr = EPGHead + "XML/CategoryList";
        String vodListStr = HttpHelper.sendForEPG(getCategoryCmdStr, getCategoryDataStr, null);
        
        DebugLog.info(TAG, "getCategoryList ret:" + vodListStr);
        CategoryListResponse categoryListResponse;
        try
        {
            categoryListResponse = SerializerService.fromXml(CategoryListResponse.class, vodListStr);
            List<Category> categoryList = categoryListResponse.getCategorylist();
            if (categoryList != null)
            {
                DebugLog.info(TAG, "getCategoryList listSize:" + categoryList.size());
                ret.addAll(categoryList);
            }
        }
        catch (PersistenceException e)
        {
            e.printStackTrace();
        }
        
        return ret;
    }
    
    private List<Media> getBatchPlayUrl(Map<String, VodNode> allVods1, final String videoType)
    {
        final List<Media> retList = new ArrayList<Media>();
        DebugLog.info(TAG, "getBatchPlayUrl begin");
        final List<Integer> resultcount = Collections.synchronizedList(new ArrayList<Integer>());
        int totalCount = allVods1.size();
        for (Entry<String, VodNode> entry : allVods1.entrySet())
        {
            final Entry<String, VodNode> oneTry = entry;
            service.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    Media media = downloadOnePlayUrl(videoType, oneTry);
                    if (media != null)
                    {
                        retList.add(media);
                    }
                    resultcount.add(0);
                }
            });
        }
        while (resultcount.size() < totalCount)
        {
            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        DebugLog.info(TAG, "getBatchPlayUrl end:");
        return retList;
    }
    
    private Media downloadOnePlayUrl(String videoType, Entry<String, VodNode> entry)
    {
        VodNode vodNode = entry.getValue();
        PlayRequest sigleRequest = new PlayRequest();
        sigleRequest.setContentId(vodNode.getVodId());
        sigleRequest.setPlayType(1);
        Media retUrl = null;
        String requestStr;
        try
        {
            
            requestStr = SerializerService.toXml(sigleRequest, "UTF-8");
            DebugLog.info(TAG, "playUrl vidoeType:" + videoType + " requestStr:" + requestStr);
            String queryUrlStr = EPGHead + "XML/Play";
            String queryUrlRet = HttpHelper.sendForEPG(queryUrlStr, requestStr, null);
            DebugLog.info(TAG, "playUrl vidoeType:" + videoType + " requestStr:" + requestStr);
            
            PlayResponse oneResponse = SerializerService.fromXml(PlayResponse.class, queryUrlRet);
            if (oneResponse.isMPD() && videoType.equals(DragonApplication.DASH_TYPE))
            {
                DebugLog.info(TAG, "add one dash:" + vodNode.getVodName() + " url:" + oneResponse.getUrl());
                String url = oneResponse.getUrl();
                retUrl = new Media(vodNode.getVodName(), "0", url);
            }
            else if (oneResponse.isHLS() && videoType.equals(DragonApplication.HLS_TYPE))
            {
                DebugLog.info(TAG, "add one hls:" + vodNode.getVodName() + " url:" + oneResponse.getUrl());
                String url = oneResponse.getUrl();
                retUrl = new Media(vodNode.getVodName(), "0", url);
            }
            else
            {
                DebugLog.info(TAG,
                    "response is not dash or hls:" + vodNode.getVodName() + " url:" + oneResponse.getUrl());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return retUrl;
    }
    
    private boolean autenticateCBG()
    {
        boolean retValue = false;
        String authenticateStr = EPGHead + "JSON/CBGAuthenticate";
        
        String autenticateBuffer =
            "{\"Servicetoken\":\"00900086000147938493499957322262234f55216607027e446425fe8a7e6f82ea60d446bf10856beabe\",\"appId\":\"com.huawei.himovie\",\"areaid\":\"1\",\"autoLogin\":1,\"bizdomain\":\"2\",\"cnonce\":\"59510284\",\"locale\":1,\"mac\":\"868227024669996\",\"needLogin\":false,\"osversion\":\"4.4.4\",\"packageid\":\"-1\",\"sso_Parm_Deviceid\":\"0\",\"sso_Parm_Devicetype\":\"0\",\"subnetId\":\"8601\",\"templatename\":\"default\",\"terminalid\":\"868227024669996\",\"terminaltype\":\"AndroidPhone\",\"timezone\":\"\",\"usergroup\":\"-1\",\"userid\":\"900086000147938493\",\"utcEnable\":1}";
        
        DebugLog.info(TAG, "autenticateCBG begin:");
        String authenticateResult = HttpHelper.sendForEPG(authenticateStr, autenticateBuffer, null);
        DebugLog.info(TAG, "authenticate Result:" + authenticateResult);
        AuthenticateResponse authenticate = JsonParser.toObject(AuthenticateResponse.class, authenticateResult);
        if (authenticate != null)
        {
            DebugLog.info(TAG, "autenticateCBG Result:" + authenticate.getRetmsg());
            configList = authenticate.getConfigurations();
            retValue = true;
            DebugLog.info(TAG, "autenticateCBG parse succeed:" + authenticateResult);
        }
        else
        {
            DebugLog.info(TAG, "autenticateCBG parse failed:" + authenticateResult);
        }
        
        return retValue;
    }
    
    private void cetificate()
    {
        DebugLog.info(TAG, "cetificate begin:");
        String certificateStr =
            "https://videouser.hicloud.com/userservice/user/certificateUser?userId=900086000147938493&ver=60002001&appId=104";
        String certificateData =
            "androidVer=4.4.4&packageName=com.huawei.himovie&ts=1501071235465&emuiVer=0&romVer=Che1-CL20V100R001CHNC00B288&deviceType=0&data=%7B%22sso_st%22%3A%2200900086000147938493499957322262234f55216607027e446425fe8a7e6f82ea60d446bf10856beabe%22%7D&terminalType=Che1-CL20&brand=Honor&i18n=zh_CN&upDeviceId=868227024669996&deviceId=868227024669996";
        String authenticateResult = HttpHelper.sendForEPG(certificateStr, certificateData, null);
        DebugLog.info(TAG, "cetificate Result:" + authenticateResult);
    }
}
