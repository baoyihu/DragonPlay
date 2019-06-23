package com.baoyihu.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.baoyihu.common.util.DebugLog;
import com.baoyihu.dragonplayer.DragonApplication;

public class NetworkController
{
    private static final String TAG = "NetworkController";
    
    private static final int TIME_OUT = 60000;
    
    private static void addCommonPara(HttpURLConnection connection)
    {
        // HTTP connection reuse which was buggy pre-froyo
        System.setProperty("http.keepAlive", "false");
        connection.setReadTimeout(TIME_OUT);
        connection.setConnectTimeout(TIME_OUT);
        
        connection.setRequestProperty("Charset", "UTF-8");
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("User-Agent", "Android/1.0");
        connection.setRequestProperty("Accept-Encoding", "");
        connection.setUseCaches(false);
        connection.setDefaultUseCaches(false);
    }
    
    public static boolean postFileBuffer(final String urlString, final String fileName, final boolean delete,
        final boolean zip)
    {
        boolean ret = false;
        if (fileName == null)
        {
            return ret;
        }
        final String reportFile;
        if (zip)
        {
            reportFile = "/sdcard/test.rar";
            try
            {
                ZipUtil.ZipFolderOrFile(DragonApplication.LOG_FILE, reportFile);
            }
            catch (Exception e)
            {
                DebugLog.trace("TAG", e);
                return ret;
            }
        }
        else
        {
            reportFile = fileName;
        }
        NetworkExecuter execute = new NetworkExecuter(new Runnable()
        {
            @Override
            public void run()
            {
                HttpURLConnection connection = null;
                FileInputStream fileIn = null;
                OutputStream output = null;
                InputStream in = null;
                File fileHandler = new File(fileName);
                if (!fileHandler.exists())
                {
                    return;
                }
                try
                {
                    fileIn = new FileInputStream(fileHandler);
                    URL url = new URL(urlString);
                    
                    connection = (HttpURLConnection)url.openConnection();
                    addCommonPara(connection);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    //   connection.setRequestProperty("Content-Length", String.valueOf(stringBuffer.getBytes().length));
                    output = connection.getOutputStream();
                    byte[] buffer = new byte[1024 * 1024 * 5];
                    int readed = 0;
                    
                    while ((readed = fileIn.read(buffer, 0, 1024 * 1024 * 5)) > 0)
                    {
                        output.write(buffer, 0, readed);
                        output.flush();
                    }
                    in = connection.getInputStream();
                    int size = in.read(buffer);
                    String writedFile = new String(buffer, 0, size);
                }
                catch (IOException e)
                {
                    DebugLog.trace(TAG, e);
                }
                finally
                {
                    if (in != null)
                    {
                        try
                        {
                            in.close();
                        }
                        catch (IOException e)
                        {
                            DebugLog.trace(TAG, e);
                        }
                    }
                    if (output != null)
                    {
                        try
                        {
                            output.close();
                        }
                        catch (IOException e)
                        {
                            DebugLog.trace(TAG, e);
                        }
                    }
                    if (connection != null)
                    {
                        connection.disconnect();
                    }
                    if (delete)
                    {
                        if (zip)
                        {
                            new File(reportFile).delete();
                        }
                        fileHandler.delete();
                    }
                }
                
            }
        });
        execute.doWork();
        return ret;
    }
    
    public static void postBuffer(final String urlString, final String stringBuffer)
    {
        if (stringBuffer == null)
        {
            return;
        }
        NetworkExecuter execute = new NetworkExecuter(new Runnable()
        {
            @Override
            public void run()
            {
                HttpURLConnection connection = null;
                OutputStreamWriter output = null;
                InputStream in = null;
                
                try
                {
                    URL url = new URL(urlString);
                    connection = (HttpURLConnection)url.openConnection();
                    addCommonPara(connection);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Length", String.valueOf(stringBuffer.getBytes().length));
                    output = new OutputStreamWriter(connection.getOutputStream());
                    output.write(stringBuffer);
                    output.flush();
                    byte[] buffer = new byte[1024 * 10];
                    in = connection.getInputStream();
                    int size = in.read(buffer);
                    String writedFile = new String(buffer, 0, size);
                }
                catch (IOException e)
                {
                    DebugLog.trace(TAG, e);
                }
                finally
                {
                    if (in != null)
                    {
                        try
                        {
                            in.close();
                        }
                        catch (IOException e)
                        {
                            DebugLog.trace(TAG, e);
                        }
                    }
                    if (output != null)
                    {
                        try
                        {
                            output.close();
                        }
                        catch (IOException e)
                        {
                            DebugLog.trace(TAG, e);
                        }
                    }
                    if (connection != null)
                    {
                        connection.disconnect();
                    }
                }
                
            }
        });
        execute.doWork();
    }
}
