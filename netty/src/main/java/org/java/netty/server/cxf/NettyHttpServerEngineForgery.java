package org.java.netty.server.cxf;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.configuration.jsse.TLSServerParameters;
import org.apache.cxf.transport.http.netty.server.NettyHttpServerEngine;
import org.apache.cxf.transport.http.netty.server.NettyHttpServerEngineFactory;
import org.apache.cxf.transport.http.netty.server.ThreadingParameters;
import org.java.netty.Netty;
import org.java.utils.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by msamoylych on 26.05.2017.
 */
@Component
public class NettyHttpServerEngineForgery implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        Bus bus = BusFactory.getDefaultBus();
        NettyHttpServerEngineFactory engineFactory = bus.getExtension(NettyHttpServerEngineFactory.class);
        bus.setExtension(new NettyHttpServerEngineFactoryDelegate(engineFactory), NettyHttpServerEngineFactory.class);
    }

    private class NettyHttpServerEngineFactoryDelegate extends NettyHttpServerEngineFactory {
        private NettyHttpServerEngineFactory engineFactory;

        private NettyHttpServerEngineFactoryDelegate(NettyHttpServerEngineFactory engineFactory) {
            assert engineFactory != null;
            this.engineFactory = engineFactory;
        }

        @Override
        public NettyHttpServerEngine createNettyHttpServerEngine(String host, int port, String protocol) throws IOException {
            Netty netty = BeanUtils.bean(Netty.class);
            NettyHttpServerEngine engine = new NettyHttpServerEngine(host, port);
            engine.setBossGroup(netty.acceptor());
            engine.setWorkerGroup(netty.server());
            engineFactory.setEnginesList(Collections.singletonList(engine));
            return engine;
        }

        @Override
        public Bus getBus() {
            return engineFactory.getBus();
        }

        @Override
        public Map<String, TLSServerParameters> getTlsServerParametersMap() {
            return engineFactory.getTlsServerParametersMap();
        }

        @Override
        public void setTlsServerParameters(Map<String, TLSServerParameters> tlsParametersMap) {
            engineFactory.setTlsServerParameters(tlsParametersMap);
        }

        @Override
        public Map<String, ThreadingParameters> getThreadingParametersMap() {
            return engineFactory.getThreadingParametersMap();
        }

        @Override
        public void setThreadingParametersMap(Map<String, ThreadingParameters> parameterMap) {
            engineFactory.setThreadingParametersMap(parameterMap);
        }

        @Override
        public void setEnginesList(List<NettyHttpServerEngine> enginesList) {
            engineFactory.setEnginesList(enginesList);
        }

        @Override
        public void initComplete() {
            engineFactory.initComplete();
        }

        @Override
        public void postShutdown() {
            engineFactory.postShutdown();
        }

        @Override
        public void preShutdown() {
            engineFactory.preShutdown();
        }

        @Override
        public NettyHttpServerEngine retrieveNettyHttpServerEngine(int port) {
            return engineFactory.retrieveNettyHttpServerEngine(port);
        }

        @Override
        public NettyHttpServerEngine createNettyHttpServerEngine(int port, String protocol) throws IOException {
            return createNettyHttpServerEngine(null, port, protocol);
        }
    }
}
