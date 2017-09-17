package com.fmh.rabbitmq;

import com.rabbitmq.client.ShutdownSignalException;

public interface ConnectionListener {

    void onConnectFailure(final ConnectionProxy connectionProxy, final Exception exception);

    void onConnection(final ConnectionProxy connectionProxy);

    void onDisconnect(final ConnectionProxy connectionProxy, final ShutdownSignalException shutdownSignalException);

    void ReConnectFailure(final ConnectionProxy connectionProxy, final Exception exception);

    void Reconnection(final ConnectionProxy connectionProxy);
}
