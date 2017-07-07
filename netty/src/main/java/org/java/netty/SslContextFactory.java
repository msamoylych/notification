package org.java.netty;

import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.ssl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;

import static io.netty.handler.ssl.ApplicationProtocolConfig.*;

/**
 * Created by msamoylych on 07.04.2017.
 */
public class SslContextFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SslContextFactory.class);

    private static final SslProvider SSL_PROVIDER;

    static {
        if (OpenSsl.isAvailable()) {
            if (OpenSsl.isAlpnSupported()) {
                LOGGER.info("Native SSL provider is available and supports ALPN; will use native provider.");
                SSL_PROVIDER = SslProvider.OPENSSL;
            } else {
                LOGGER.warn("Native SSL provider is available, but does not support ALPN; will use JDK SSL provider.");
                SSL_PROVIDER = SslProvider.JDK;
            }
        } else {
            LOGGER.warn("Native SSL provider not available; will use JDK SSL provider.");
            SSL_PROVIDER = SslProvider.JDK;
        }
    }

    public static SslContext buildSSLContext() {
        try {
            return clientSslContextBuilder().build();
        } catch (SSLException ex) {
            throw new IllegalStateException("Build SSL context error", ex);
        }
    }

    public static SslContext buildHttp2SslContext() {
        try {
            return clientSslContextBuilder()
                    .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                    .applicationProtocolConfig(new ApplicationProtocolConfig(
                            Protocol.ALPN, SelectorFailureBehavior.NO_ADVERTISE, SelectedListenerFailureBehavior.ACCEPT,
                            ApplicationProtocolNames.HTTP_2))
                    .build();
        } catch (SSLException ex) {
            throw new IllegalStateException("Build SSL context error", ex);
        }
    }

    private static SslContextBuilder clientSslContextBuilder() {
        return SslContextBuilder.forClient().sslProvider(SSL_PROVIDER);
    }
}
