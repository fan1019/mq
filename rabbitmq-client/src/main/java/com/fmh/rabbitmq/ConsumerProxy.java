package com.fmh.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsumerProxy implements Consumer {

    private class ConsumeRunner implements Callable<Object> {

        @Override
        public Object call() throws Exception {
            try {
                return channelProxy.invoke(channelProxy, basicConsumeMethod, basicConsumeArgs);
            } catch (Throwable e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Error reinvoking basicConsume", e);
                }
                return e;
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerProxy.class);

    private final Consumer target;
    private final ChannelProxy channelProxy;
    private final Method basicConsumeMethod;
    private final Object[] basicConsumeArgs;
    private final ExecutorService executor;


    public ConsumerProxy(final Consumer target, final ChannelProxy channelProxy, final Method basicConsumeMethod, final Object[] basicConsumeArgs) {
        assert target != null;
        assert channelProxy != null;
        assert basicConsumeMethod != null;
        assert basicConsumeArgs != null;

        this.target = target;
        this.channelProxy = channelProxy;
        this.basicConsumeMethod = basicConsumeMethod;
        this.basicConsumeArgs = basicConsumeArgs;
        this.executor = Executors.newCachedThreadPool();
    }


    @Override
    public void handleConsumeOk(String s){
        target.handleConsumeOk(s);
    }

    @Override
    public void handleCancelOk(String s) {
        target.handleCancelOk(s);
    }

    @Override
    public void handleCancel(String s) throws IOException {
        target.handleCancel(s);
    }

    @Override
    public void handleShutdownSignal(String s, ShutdownSignalException e) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("Consumer asked to handle shutdown signal, reregistering consume.",e.getMessage());
        }
        channelProxy.closeConnectionLatch();
        executor.submit(new ConsumeRunner());
    }

    @Override
    public void handleRecoverOk(String s) {
        target.handleRecoverOk(s);
    }

    @Override
    public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
        target.handleDelivery(s,envelope,basicProperties,bytes);
    }
}
