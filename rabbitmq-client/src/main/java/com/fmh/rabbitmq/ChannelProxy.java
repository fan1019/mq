package com.fmh.rabbitmq;

import com.fmh.rabbitmq.retry.RetryStrategy;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ChannelProxy implements InvocationHandler{
    private static Logger logger = LoggerFactory.getLogger(ChannelProxy.class);
    private static final String BASIC_CONSUME = "basicConsume";
    private static final String CLOSE = "close";
    private final ConnectionProxy connectionProxy;
    private Channel target;
    private final RetryStrategy retryStrategy;






    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
