package org.java.netty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by msamoylych on 12.07.2017.
 */
@Service
public class NettyFactory {

    @Autowired
    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    protected Netty netty;
}
