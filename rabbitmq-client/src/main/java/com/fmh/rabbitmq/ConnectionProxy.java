package com.fmh.rabbitmq;

import com.fmh.rabbitmq.retry.RetryStrategy;
import com.fmh.rabbitmq.util.InvocationHandlerUtils;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
        assert retryStrategy != null;

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


    public void closeConnectionLatch() {
        for (ChannelProxy proxy : channelProxies) {
            proxy.closeConnectionLatch();
        }
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.equals(CREATE_CHANNEL) || method.equals(CREATE_CHANNEL_INT)) {
            return createChannelAndWrapWithProxy(method, args);
        }
        return InvocationHandlerUtils.delegateMethodInvocation(method, args, target);
    }

    public void markAsOpern() {
        synchronized (channelProxies) {
            for (ChannelProxy proxy : channelProxies) {
                proxy.markAsOpen();
            }
        }
    }

    protected Channel createChannelAndWrapWithProxy(final Method method, final Object[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Channel targetChannel = (Channel) method.invoke(target, args);
        ClassLoader classLoader = Connection.class.getClassLoader();
        Class<?>[] interfaces = {Channel.class};
        ChannelProxy proxy = new ChannelProxy(this, targetChannel, retryStrategy);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating channel proxy: " + targetChannel.toString());
        }
        synchronized (channelProxies) {
            channelProxies.add(proxy);
        }
        return (Channel) Proxy.newProxyInstance(classLoader, interfaces, proxy);
    }

    protected void removeCloseChannel(final ChannelProxy channelProxy) {
        synchronized (channelProxies) {
            channelProxies.remove(channelProxy);
        }
    }


    protected void replaceChannelsInProxies() throws IOException {
        synchronized (channelProxies) {
            for (ChannelProxy proxy : channelProxies) {
                int channelNumber = proxy.getTargetChannel().getChannelNumber();
                proxy.setTargetChannel(target.createChannel(channelNumber));
            }
        }
    }

    protected void setTargetConnection(final Connection target) {
        assert target != null;
        this.target = target;
    }

    static {
        try {
            CREATE_CHANNEL = Connection.class.getMethod("createChannel");
            CREATE_CHANNEL_INT = Connection.class.getMethod("createChannel", int.class);
        } catch (Exception r) {
            throw new RuntimeException(r);
        }
    }

}
