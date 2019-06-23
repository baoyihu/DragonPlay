package com.baoyihu.mem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.baoyihu.common.util.Bytes;
import com.baoyihu.common.util.DebugLog;
import com.baoyihu.common.util.Pair;
import com.baoyihu.dragonplayer.DragonApplication;

import android.text.TextUtils;

public class HttpHelper
{
    private static final String TAG = "HttpHelper";
    
    private static String global_SessionId = null;
    
    private static String global_C_SessionId = null;
    
    private static final int TIME_OUT = 120000;
    
    private static final int HTTP_BUFFER_SIZE = 1024 * 1024;
    
    public static void setCSessionId(String value)
    {
        DebugLog.info(TAG, "setCSessionId :" + value);
        global_C_SessionId = value;
    }
    
    public static void setSessionId(String value)
    {
        DebugLog.info(TAG, "setSessionId :" + value);
        global_SessionId = value;
    }
    
    public static byte[] sendDirect(String path, String buffer, List<Pair<String, String>> propertyList)
        throws Exception
    {
        HttpURLConnection connection = null;
        connection = generateConnection(path);
        InputStream inputStream = null;
        byte[] ret = new byte[0];
        int retCode = 0;
        //     DebugLog.info(TAG, "sendDirect: :" + path);
        try
        {
            while (true)
            {
                addCommonPara(connection, buffer, propertyList);
                if (!path.contains("/EPG/XML/Login"))
                {
                    addSessionId(connection);
                }
                if (path.contains("/EPG"))
                {
                    connection.setRequestProperty("Content-Type", "application/xml");
                }
                
                retCode = readResponse(connection, buffer);
                if (retCode == HttpURLConnection.HTTP_MOVED_TEMP || retCode == HttpURLConnection.HTTP_MOVED_PERM)
                {
                    //    DebugLog.info(TAG, "Http Redirection 302");
                    String urlString = connection.getHeaderField("Location");
                    URL url = new URL(urlString);
                    connection = getProxyConnection(url);
                }
                else if (retCode == HttpURLConnection.HTTP_SEE_OTHER)
                {
                    //     DebugLog.info(TAG, "Http Redirection 303");
                    String urlString = connection.getHeaderField("Location");
                    URL url = new URL(urlString);
                    connection = getProxyConnection(url);
                    throw new RuntimeException("Session time out");
                }
                else
                {
                    break;
                }
                
            }
            
            setParaForLogInOut(connection, path);
            checkResponseCode(retCode);
            String encoding = connection.getContentEncoding();
            inputStream = connection.getInputStream();
            if (encoding != null && encoding.contains("gzip"))
            {
                DebugLog.info(TAG, "GZIP Stream:");
                inputStream = new GZIPInputStream(inputStream);
            }
            int len = 0;
            byte[] dd = new byte[HTTP_BUFFER_SIZE];
            
            while ((len = inputStream.read(dd)) > 0)
            {
                ret = Bytes.connect(ret, ret.length, dd, len);
            }
            //   DebugLog.info(TAG, "sendDirect: :" + path);
            return ret;
        }
        finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
            if (connection != null)
            {
                connection.disconnect();
            }
            
        }
    }
    
    // fomateType =0:xml;1 json
    public static String sendForEPG(String path, String buffer, List<Pair<String, String>> propertyLis)
    {
        HttpURLConnection connection = null;
        connection = generateConnection(path);
        InputStreamReader inputStream = null;
        
        int retCode = 0;
        try
        {
            try
            {
                while (true)
                {
                    if (path.contains("/XML/"))
                    {
                        addCommonPara(connection, buffer, propertyLis);
                        if (!path.contains("/Login"))
                        {
                            addSessionId(connection);
                        }
                        connection.setRequestProperty("Content-Type", "application/xml");
                    }
                    else if (path.contains("/JSON/"))
                    {
                        if (!path.contains("/Login"))
                        {
                            addSessionId(connection);
                        }
                        connection.addRequestProperty("Connection", "Close");
                        connection.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        
                        if (!TextUtils.isEmpty(global_C_SessionId))
                        {
                            connection.addRequestProperty("Cookie", global_C_SessionId);
                        }
                        
                        if (!TextUtils.isEmpty(global_SessionId))
                        {
                            connection.addRequestProperty("Cookie", global_SessionId);
                        }
                    }
                    else
                    {
                        addSessionId(connection);
                        
                        connection.addRequestProperty("Connection", "Close");
                        //connection.addRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        
                        if (!TextUtils.isEmpty(global_C_SessionId))
                        {
                            connection.addRequestProperty("Cookie", global_C_SessionId);
                        }
                        
                        if (!TextUtils.isEmpty(global_SessionId))
                        {
                            connection.addRequestProperty("Cookie", global_SessionId);
                        }
                    }
                    
                    retCode = readResponse(connection, buffer);
                    if (retCode == HttpURLConnection.HTTP_MOVED_TEMP || retCode == HttpURLConnection.HTTP_MOVED_PERM)
                    {
                        DebugLog.info(TAG, "Http Redirection 302");
                        String urlString = connection.getHeaderField("Location");
                        URL url = new URL(urlString);
                        connection = getProxyConnection(url);
                    }
                    else if (retCode == HttpURLConnection.HTTP_SEE_OTHER)
                    {
                        DebugLog.info(TAG, "Http Redirection 303");
                        String urlString = connection.getHeaderField("Location");
                        URL url = new URL(urlString);
                        connection = getProxyConnection(url);
                        throw new RuntimeException("Session time out");
                    }
                    else
                    {
                        break;
                    }
                    
                }
                
                setParaForLogInOut(connection, path);
                checkResponseCode(retCode);
                inputStream = new InputStreamReader(connection.getInputStream(), "UTF-8");
                StringBuilder builder = new StringBuilder();
                int len = 0;
                char[] dd = new char[1024 * 1024];
                while ((len = inputStream.read(dd)) > 0)
                {
                    builder.append(dd, 0, len);
                }
                
                return builder.toString();
            }
            finally
            {
                if (connection != null)
                {
                    connection.disconnect();
                }
                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
        }
        catch (
        
        Exception e)
        {
            DebugLog.trace(TAG, e);
        }
        return "";
    }
    
    private static int readResponse(HttpURLConnection connection, String buffer)
        throws IOException
    {
        int ret = 0;
        
        if (buffer.isEmpty())
        { // GET
            ret = connection.getResponseCode();
        }
        else
        { // POST, PUT, DELETE
            ret = postXML(connection, buffer);
        }
        
        return ret;
    }
    
    private static int postXML(HttpURLConnection connection, String buffer)
        throws IOException
    {
        OutputStream out = null;
        int responseCode = 200;
        
        try
        {
            byte[] payloadData = buffer.getBytes(DragonApplication.ENCODE);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Length", String.valueOf(payloadData.length));
            out = connection.getOutputStream();
            out.write(payloadData);
            out.flush();
            // Now we can look at response code
            responseCode = connection.getResponseCode();
            return responseCode;
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
        }
        
    }
    
    private static void addCommonPara(HttpURLConnection connection, String buffer,
        List<Pair<String, String>> propertyList)
    {
        System.setProperty("http.keepAlive", "false");
        connection.setReadTimeout(TIME_OUT);
        connection.setConnectTimeout(TIME_OUT);
        String method = buffer.isEmpty() ? "GET" : "POST";
        try
        {
            connection.setRequestMethod(method);
        }
        catch (ProtocolException e)
        {
            DebugLog.trace(TAG, e);
        }
        connection.setDoInput(true);
        connection.setRequestProperty("Charset", DragonApplication.ENCODE);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("User-Agent", "Android/1.0");
        connection.setRequestProperty("Accept-Encoding", "");
        connection.setUseCaches(false);
        connection.setDefaultUseCaches(false);
        if (propertyList != null)
        {
            for (Pair<String, String> oneProperty : propertyList)
            {
                connection.setRequestProperty(oneProperty.first, oneProperty.second);
            }
        }
    }
    
    private static void setParaForLogInOut(HttpURLConnection connection, String url)
    {
        if (url.contains("/Login"))
        {
            String tmpSessionId = connection.getHeaderField("Set-Cookie");
            if (tmpSessionId != null)
            {
                String sessionId = tmpSessionId.split("\\;")[0];
                setSessionId(sessionId);
            }
        }
        else if (url.contains("/CBGAuthenticate") || url.contains("/Authenticate"))
        {
            List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
            if (cookies != null && cookies.size() > 0)
            {
                for (String cookie : cookies)
                {
                    String sessionId = cookie.split("\\;")[0];
                    if (cookie.indexOf("JSESSIONID=") >= 0)
                    {
                        setSessionId(sessionId);
                    }
                    else if (cookie.indexOf("CSESSIONID=") >= 0)
                    {
                        setCSessionId(sessionId);
                    }
                }
            }
        }
        
    }
    
    private static HttpURLConnection generateConnection(String urlPath)
    {
        boolean isHttps = false;
        HttpURLConnection connection = null;
        if (urlPath.startsWith("https"))
        {
            isHttps = true;
        }
        
        try
        {
            if (isHttps)
            {
                URL url = new URL(urlPath);
                connection = HttpUtils.getHttpsURLConnection(url);
            }
            else
            {
                URL url = new URL(urlPath);
                connection = getProxyConnection(url);
            }
        }
        
        catch (IOException e)
        {
            //
        }
        return connection;
    }
    
    private static HttpURLConnection getProxyConnection(URL url)
    {
        HttpURLConnection connection = null;
        try
        {
            connection = (HttpURLConnection)(url.openConnection());
        }
        catch (IOException e)
        {
            DebugLog.trace(TAG, e);
        }
        
        return connection;
    }
    
    private static void checkResponseCode(int responseCode)
        throws IOException
    {
        
    }
    
    private static void addSessionId(HttpURLConnection connection)
    {
        if (global_SessionId != null && !global_SessionId.isEmpty())
        {
            connection.addRequestProperty("Cookie", global_SessionId);
        }
        
        if (global_C_SessionId != null && !global_C_SessionId.isEmpty())
        {
            connection.addRequestProperty("Cookie", global_C_SessionId);
        }
        
    }
}
