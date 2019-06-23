package com.baoyihu.dragonplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.baoyihu.common.util.Bytes;
import com.baoyihu.common.util.DebugLog;
import com.baoyihu.common.util.DebugLog.UserLogHandler;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class DragonApplication extends Application
{
    private static Context g_contect = null;
    
    public static String LOG_FILE = "/sdcard/test.log";
    
    private static final String TAG = "DragonApplication";
    
    public static final String ENCODE = "UTF-8";//"GB2312";
    
    public static final String DASH_TYPE = "mpd";
    
    public static final String HLS_TYPE = "m3u8";
    
    public static String downloadType = DASH_TYPE;
    
    public static final boolean useProxy = false;
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        g_contect = this;
        DebugLog.init(DebugLog.LEVEL_DEBUG, LOG_FILE);
        DebugLog.setUserHandler(new UserLogHandler()
        {
            @Override
            public void onWriteLog(int level, String tag, String msg)
            {
                switch (level)
                {
                    case DebugLog.LEVEL_DEBUG:
                        Log.d(tag, msg);
                        break;
                    case DebugLog.LEVEL_INFO:
                        Log.i(tag, msg);
                        break;
                    case DebugLog.LEVEL_ERROR:
                        Log.e(tag, msg);
                        break;
                }
            }
            
            @Override
            public void onTraceLog(String tag, Throwable throwable)
            {
                Log.e(tag, "Fata", throwable);
            }
            
        });
    }
    
    public static byte[] readFile(String name)
    {
        File file = new File(name);
        if (!file.exists())
            return null;
        
        byte[] ret = new byte[] {};
        FileInputStream reader = null;
        try
        {
            reader = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 1024];
            int count = 0;
            while ((count = reader.read(buffer)) > 0)
            {
                ret = Bytes.connect(ret, ret.length, buffer, count);
            }
            return ret;
        }
        catch (IOException e)
        {
            DebugLog.trace(TAG, e);
        }
        finally
        {
            try
            {
                if (reader != null)
                    reader.close();
            }
            catch (IOException e)
            {
                DebugLog.trace(TAG, e);
            }
        }
        return null;
    }
    
    public static Context getContext()
    {
        return g_contect;
    }
}