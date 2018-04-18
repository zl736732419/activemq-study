package com.zheng.mq.queue.ssl;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public class SSLUtils {

    /**
     * 加载证书文件
     *
     * @param trustStore
     * @return
     * @throws java.io.IOException
     * @throws java.security.GeneralSecurityException
     */
    public static TrustManager[] loadTrustManager(String trustStore) throws IOException, GeneralSecurityException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(trustStore), null);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        System.out.println("init TrustManagers finish");
        return tmf.getTrustManagers();
    }

    /**
     * 加载密钥文件
     *
     * @param keyStore
     * @param keyStorePassword
     * @return
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.KeyStoreException
     * @throws java.security.GeneralSecurityException
     * @throws java.security.cert.CertificateException
     * @throws java.io.IOException
     * @throws java.security.UnrecoverableKeyException
     */
    public static KeyManager[] loadKeyManager(String keyStore, String keyStorePassword) 
            throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(keyStore), keyStorePassword.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, keyStorePassword.toCharArray());
        System.out.println("init KeyManager finish");
        return kmf.getKeyManagers();
    }
}