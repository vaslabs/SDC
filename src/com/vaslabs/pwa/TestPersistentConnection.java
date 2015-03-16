package com.vaslabs.pwa;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TestPersistentConnection {
    private static SSLSocketFactory sslSocketFactory = null;

    protected static void setAcceptAllVerifier( HttpsURLConnection connection )
            throws NoSuchAlgorithmException, KeyManagementException {

        // Create the socket factory.
        // Reusing the same socket factory allows sockets to be
        // reused, supporting persistent connections.
        if ( null == sslSocketFactory ) {
            SSLContext sc = SSLContext.getInstance( "SSL" );
            sc.init( null, ALL_TRUSTING_TRUST_MANAGER,
                    new java.security.SecureRandom() );
            sslSocketFactory = sc.getSocketFactory();
        }

        connection.setSSLSocketFactory( sslSocketFactory );

        // Since we may be using a cert with a different name, we need to ignore
        // the hostname as well.
        connection.setHostnameVerifier( ALL_TRUSTING_HOSTNAME_VERIFIER );
    }

    private static final TrustManager[] ALL_TRUSTING_TRUST_MANAGER = new TrustManager[] { new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted( X509Certificate[] certs, String authType ) {
        }

        public void checkServerTrusted( X509Certificate[] certs, String authType ) {
        }
    } };

    private static final HostnameVerifier ALL_TRUSTING_HOSTNAME_VERIFIER = new HostnameVerifier() {
        public boolean verify( String hostname, SSLSession session ) {
            return true;
        }
    };

}