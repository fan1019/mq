package com.fmh.rabbitmq;

import com.fmh.rabbitmq.client.BooleanReentrantLatch;
import com.fmh.rabbitmq.retry.RetryStrategy;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelProxy implements InvocationHandler {
    private static Logger logger = LoggerFactory.getLogger(ChannelProxy.class);
    private static final String BASIC_CONSUME = "basicConsume";
    private static final String CLOSE = "close";
    private final ConnectionProxy connectionProxy;
    private Channel target;
    private final RetryStrategy retryStrategy;
    private final BooleanReentrantLatch connectionLatch;
    private final ConcurrentHashMap<Consumer, ConsumerProxy> consumerProxies;

    public ChannelProxy(final ConnectionProxy connectionProxy, final Channel target, final RetryStrategy retryStrategy) {
        assert connectionProxy != null;
        assert target != null;
        assert retryStrategy != null;

        this.connectionProxy = connectionProxy;
        this.retryStrategy = retryStrategy;
        this.target = target;

        connectionLatch = new BooleanReentrantLatch();
        consumerProxies = new ConcurrentHashMap<>();
    }

    public void closeConnectionLatch() {
        connectionLatch.close();
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (logger.isDebugEnabled()) {
            logger.debug("Invoke: ", method.getName());
        }
        if (method.getName().equals(CLOSE)) {
            try {
                target.close();
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to close underlying channel, not a problem: ", e.getMessage());
                }
            }
            connectionProxy.removeCloseChannel(this);
            return null;
        }

        Exception lastException = null;
        boolean shutdownRecoverable = true;
        boolean keepOnInvoking = true;

        for (int numOperationInvocations = 1; keepOnInvoking && shutdownRecoverable; numOperationInvocations++) {
            synchronized (target) {
                if (method.getName().equals(BASIC_CONSUME)) {
                    Consumer targetConsumer = (Consumer) args[args.length - 1];
                    if (!(targetConsumer instanceof ConsumerProxy)) {
                        ConsumerProxy consumerProxy = consumerProxies.get(targetConsumer);
                        if (consumerProxy == null) {
                            consumerProxy = new ConsumerProxy(targetConsumer, this, method, args);
                        }
                        ConsumerProxy existingConsumerProxy = consumerProxies.putIfAbsent(targetConsumer, consumerProxy);
                        args[args.length - 1] = existingConsumerProxy == null ? consumerProxy : existingConsumerProxy;
                    }
                }
                return Invoc
            }
        }
        return null;
    }
}
