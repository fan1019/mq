package com.fmh.rabbitmq;

import com.rabbitmq.client.ShutdownSignalException;

public class AbstractConnectionListener implements ConnectionListener{
    @Override
    public void onConnectFailure(ConnectionProxy connectionProxy, Exception exception) {

    }

    @Override
    public void onConnection(ConnectionProxy connectionProxy) {

    }

    @Override
    public void onDisconnect(ConnectionProxy connectionProxy, ShutdownSignalException shutdownSignalException) {

    }


    @Override
    public void ReConnectFailure(ConnectionProxy connectionProxy, Exception exception) {

    }

    @Override
    public void Reconnection(ConnectionProxy connectionProxy) {

    }
}
