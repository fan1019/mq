package com.fmh.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ConnectionFactory extends com.rabbitmq.client.ConnectionFactory {
    private static Logger log = LoggerFactory.getLogger(ConnectionFactory.class);
    private Set<ConnectionListener> listeners;



    private class ConnecttionSet{

        private final Connection wrapped;
        private final ConsumerProxy consumerProxy;
    }

    private class ShutDownListener implements ShutdownListener{

        private final ConnectionProxy connectionProxy;

        public ShutDownListener(final ConnectionProxy connectionProxy){
            assert connectionProxy != null;
            this.connectionProxy = connectionProxy;
        }

        @Override
        public void shutdownCompleted(final ShutdownSignalException e) {
            if (log.isDebugEnabled()){
                log.debug("Shutdown signal caught: "+e.getMessage());
            }
            for (ConnectionListener listener : listeners){
                listener.onDisconnect(connectionProxy,e);
            }
            if (!e.isInitiatedByApplication()){

            }
        }
    }
}
