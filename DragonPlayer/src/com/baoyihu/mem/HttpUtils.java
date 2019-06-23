package com.baoyihu.mem;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.baoyihu.common.util.DebugLog;
import com.baoyihu.dragonplayer.DragonApplication;

public final class HttpUtils
{
    private static final String TAG = "Steal_HttpUtils";
    
    private static KeyManagerFactory kmf = null;
    
    private static final String CACERTSPATH = "cacerts.bks";
    
    private static TrustManager[] trustAllCerts = null;
    
    private static HostnameVerifier hostnameVerifier = null;
    
    private HttpUtils()
    {
        
    }
    
    private static HttpsURLConnection getProxyConnection(URL url)
    {
        //   Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.huawei.com", 8080));
        HttpsURLConnection connection = null;
        
        try
        {
            connection = (HttpsURLConnection)(url.openConnection());
        }
        catch (IOException e)
        {
            DebugLog.trace(TAG, e);
        }
        
        return connection;
    }
    
    public static synchronized HttpsURLConnection getHttpsURLConnection(URL httpsUrl)
    {
        HttpsURLConnection httpsURLConnection = null;
        
        if (kmf == null)
        {
            kmf = initKeyManagerFactory();
        }
        
        try
        {
            httpsURLConnection = getProxyConnection(httpsUrl);
            if (httpsURLConnection == null)
            {
                System.err.println("HttpUtils httpsURLConnection is null:");
                throw new RuntimeException("httpsURLConnection is null in getHttpsURLConnection");
            }
            SSLContext sslContext;
            
            sslContext = SSLContext.getInstance("TLS");
            
            sslContext.init(kmf.getKeyManagers(), trustAllCerts, new SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            httpsURLConnection.setHostnameVerifier(getHostVerifier());
            httpsURLConnection.setSSLSocketFactory(ssf);
        }
        catch (Exception e)
        {
            System.err.println("HttpUtils CA error:" + e.getMessage());
            //throw new HuaweiClientException(e);
        }
        return httpsURLConnection;
    }
    
    private static synchronized KeyManagerFactory initKeyManagerFactory()
    {
        KeyManagerFactory theKey = null;
        InputStream caCertFis = null;
        InputStream ksFis = null;
        try
        {
            try
            {
                
                ksFis = DragonApplication.getContext().getAssets().open(CACERTSPATH);
                KeyStore ks = KeyStore.getInstance("BKS");//("BKS", "BC");
                ks.load(ksFis, null);
                //                ks.setCertificateEntry("iptvapi", clientCert);
                theKey = KeyManagerFactory.getInstance("X509");
                theKey.init(ks, null);
                //                hostnameVerifier = getHostVerifier();
                trustAllCerts = new TrustManager[] {new TurcellTrustManager(ks)};
                
            }
            finally
            {
                closeStream(caCertFis);
                closeStream(ksFis);
            }
        }
        
        catch (Exception e)
        {
            DebugLog.trace(TAG, e);
        }
        return theKey;
    }
    
    private static HostnameVerifier getHostVerifier()
    {
        return new HostnameVerifier()
        {
            
            @Override
            public boolean verify(String hostname, SSLSession sslSession)
            {
                boolean verifyCerEnable = true;// SimpleApplication.getContext().isVerifyCerEnable();
                if (verifyCerEnable)
                {
                    if (!hostname.equals(sslSession.getPeerHost()))
                    {
                        DebugLog.error(TAG, "not support");
                    }
                }
                return true;
            }
            
        };
        
    }
    
    private static void closeStream(InputStream stream)
    {
        if (stream != null)
        {
            try
            {
                stream.close();
            }
            catch (Exception e)
            {
                //  DebugLog.printException("IPTV Error", e);
            }
        }
    }
}
