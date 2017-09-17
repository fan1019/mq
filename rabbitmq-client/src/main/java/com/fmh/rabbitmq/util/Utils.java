package com.fmh.rabbitmq.util;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.impl.AMQImpl;

import java.io.EOFException;
import java.io.IOException;

public class Utils {

    private Utils(){

    }

    public static boolean isShutdownRecoverable(final IOException ioe){
        if (ioe.getCause() instanceof ShutdownSignalException){
            return isShutdownRecoverable((ShutdownSignalException) ioe.getCause());
        }
        return true;
    }

    public static boolean isShutdownRecoverable(final ShutdownSignalException s){
        if (s != null){
            int retryCode = 0;

            if (s.getReason() instanceof AMQImpl.Connection.Close){
                retryCode = ((AMQImpl.Connection.Close)s.getReason()).getReplyCode();
            }

            if (s.isInitiatedByApplication()){
                return retryCode == AMQP.CONNECTION_FORCED || retryCode == AMQP.INTERNAL_ERROR
                        || s.getCause() instanceof EOFException || s instanceof AlreadyClosedException;

            }
        }
        return false;
    }
}
