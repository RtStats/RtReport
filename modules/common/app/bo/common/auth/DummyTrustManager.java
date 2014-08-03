package bo.common.auth;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class DummyTrustManager implements X509TrustManager {

    private final static X509Certificate[] ACCEPTED_ISSUERS = new X509Certificate[0];

    @Override
    public void checkClientTrusted(X509Certificate[] xcs, String algorithm)
            throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] xcs, String algorithm)
            throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return ACCEPTED_ISSUERS;
    }
}
