package com.fmh.rabbitmq;

import com.fmh.rabbitmq.retry.RetryStrategy;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class ConnectionProxy implements InvocationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionProxy.class);

    private static Method CREATE_CHANNEL;
    private static Method CREATE_CHANNEL_INT;

    private final Address[] addresses;
    private Connection target;
    private final Set<ChannelProxy> channelProxies;
    private final RetryStrategy retryStrategy;


    public ConnectionProxy(final Address[] addresses, final Connection target, final RetryStrategy retryStrategy) {

        assert addresses != null;
        assert addresses.length > 0;
        assert retryStrategy !=null;

        this.target = target;
        this.retryStrategy = retryStrategy;
        this.addresses = addresses;
        channelProxies = new HashSet<>();
    }

    public Address[] getAddresses() {
        return addresses;
    }

    public Connection getTarget() {
        return target;
    }



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
