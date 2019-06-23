package com.baoyihu.mem;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.baoyihu.common.util.DebugLog;

public class TurcellTrustManager implements X509TrustManager
{
    private static final String TAG = "TurcellTrustManager";
    
    private X509Certificate[] trustCerts = null;
    
    public TurcellTrustManager(KeyStore ks)
    {
        
        TrustManagerFactory tmf;
        try
        {
            tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(ks);
            TrustManager tms[] = tmf.getTrustManagers();
            X509TrustManager sunJSSEX509TrustManager = null;
            for (int i = 0; i < tms.length; i++)
            {
                if (tms[i] instanceof X509TrustManager)
                {
                    sunJSSEX509TrustManager = (X509TrustManager)tms[i];
                    break;
                }
            }
            if (sunJSSEX509TrustManager != null)
            {
                trustCerts = sunJSSEX509TrustManager.getAcceptedIssuers();
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            DebugLog.trace(TAG, e);
        }
        catch (KeyStoreException e)
        {
            DebugLog.trace(TAG, e);
        }
        
    }
    
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException
    {
        DebugLog.info(TAG, "checkClientTrusted");
    }
    
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException
    {
        boolean verifyCerEnable = true;
        if (verifyCerEnable)
        {
            try
            {
                checkValid(chain);
                checkSignature(chain);
            }
            catch (Exception e)
            {
                DebugLog.trace(TAG, e);
            }
        }
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers()
    {
        return new X509Certificate[0];
    }
    
    //    /**
    //     * 验证发行服务器证书的CA 是否可靠
    //     * @author  y63586
    //     * @param chain
    //     * @throws CertificateException
    //     * @see [类、类#方法、类#成员]
    //     */
    //    private void checkIssuerDN(X509Certificate[] chain)
    //        throws CertificateException
    //    {
    //        boolean isTrust = false;
    //        if (trustCerts != null)
    //        {
    //            for (X509Certificate cer : chain)
    //            {
    //                for (X509Certificate trustCert : trustCerts)
    //                {
    //                    if (cer.getIssuerDN().equals(trustCert.getIssuerDN()))
    //                    {
    //                        isTrust = true;
    //                        break;
    //                    }
    //                }
    //            }
    //        }
    //        if (!isTrust)
    //        {
    //            throw new CertificateException();
    //        }
    //    }
    
    /**
     * 
     * 验证证书是否过期
     * @author  y63586
     * @param chain
     * @throws CertificateException
     * @see [类、类#方法、类#成员]
     */
    private void checkValid(X509Certificate[] chain)
        throws CertificateException
    {
        Date date = new Date(System.currentTimeMillis());
        for (X509Certificate cer : chain)
        {
            cer.checkValidity(date);
        }
    }
    
    /**
     * 验证发行者证书的公钥能否正确解开服务器证书的发行者的数字签名
     * @author  y63586
     * @param chain
     * @throws CertificateException
     * @see [类、类#方法、类#成员]
     */
    private void checkSignature(X509Certificate[] chain)
        throws CertificateException
    {
        PublicKey pk = null;
        if (trustCerts != null)
        {
            for (X509Certificate cer : chain)
            {
                for (X509Certificate trustCert : trustCerts)
                {
                    if (cer.getIssuerDN().equals(trustCert.getIssuerDN()))
                    {
                        pk = trustCert.getPublicKey();
                        try
                        {
                            cer.verify(pk);
                        }
                        catch (Exception e)
                        {
                            DebugLog.trace(TAG, e);
                            throw new CertificateException(e);
                        }
                        
                        break;
                    }
                }
            }
        }
    }
}
