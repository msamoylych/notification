package org.java.netty.server.http.cxf;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.configuration.jsse.TLSServerParameters;
import org.apache.cxf.transport.http.netty.server.NettyHttpServerEngine;
import org.apache.cxf.transport.http.netty.server.NettyHttpServerEngineFactory;
import org.apache.cxf.transport.http.netty.server.ThreadingParameters;
import org.java.netty.NettyFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by msamoylych on 26.05.2017.
 */
@Component
public class NettyHttpServerEngineForgery extends NettyFactory implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        Bus bus = BusFactory.getThreadDefaultBus();
        NettyHttpServerEngineFactory engineFactory = bus.getExtension(NettyHttpServerEngineFactory.class);
        bus.setExtension(new NettyHttpServerEngineFactoryDelegate(engineFactory), NettyHttpServerEngineFactory.class);
    }

    private class NettyHttpServerEngineFactoryDelegate extends NettyHttpServerEngineFactory {
        private final NettyHttpServerEngineFactory engineFactory;

        private NettyHttpServerEngineFactoryDelegate(NettyHttpServerEngineFactory engineFactory) {
            this.engineFactory = engineFactory;
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
        public NettyHttpServerEngine createNettyHttpServerEngine(String host, int port, String protocol) throws IOException {
            NettyHttpServerEngine engine = engineFactory.createNettyHttpServerEngine(host, port, protocol);
            if (engine.getBossGroup() == null) {
                engine.setBossGroup(netty.acceptor());
            }
            if (engine.getWorkerGroup() == null) {
                engine.setWorkerGroup(netty.server());
            }
            if (engine.getApplicationExecutor() == null) {
                engine.setApplicationExecutor(netty.executor());
            }
            return engine;
        }

        @Override
        public NettyHttpServerEngine createNettyHttpServerEngine(int port, String protocol) throws IOException {
            return createNettyHttpServerEngine(null, port, protocol);
        }
    }
}
