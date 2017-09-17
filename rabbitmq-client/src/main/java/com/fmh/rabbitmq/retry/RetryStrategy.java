package com.fmh.rabbitmq.retry;

import com.fmh.rabbitmq.client.BooleanReentrantLatch;

public interface RetryStrategy {

    public boolean shouldRetry(Exception e, int numOperationInvocations, BooleanReentrantLatch connectionGate);
}
