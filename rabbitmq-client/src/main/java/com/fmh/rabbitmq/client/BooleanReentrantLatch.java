package com.fmh.rabbitmq.client;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class BooleanReentrantLatch {

    private static final class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 6818161533627815789L;

        protected Sync(final boolean open) {
            setState(open ? 0 : 1);
        }

        @Override
        protected boolean tryReleaseShared(final int arg) {
            return compareAndSetState(1, 0);
        }

        protected boolean isOpen() {
            return getState() == 0;
        }

        @Override
        protected int tryAcquireShared(int arg) {
            if (arg == 0) {
                return isOpen() ? 1 : -1;
            }
            setState(1);
            return 1;
        }
    }

    private final Sync sync;

    public BooleanReentrantLatch() {
        this(true);
    }

    public BooleanReentrantLatch(final boolean open) {
        sync = new Sync(open);
    }

    public void close() {
        sync.acquireShared(1);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BooleanReentrantLatch)) {
            return false;
        }
        BooleanReentrantLatch rhs = (BooleanReentrantLatch) obj;
        return isOpen() == rhs.isOpen();
    }

    public boolean isOpen() {
        return sync.isOpen();
    }

    public boolean isClosed() {
        return !isOpen();
    }

    public void open() {
        sync.releaseShared(1);
    }

    @Override
    public String toString() {
        return super.toString() + "[" + (isOpen() ? "open" : "closed") + "]";
    }

    public void waitUntilOpen() throws InterruptedException {
        sync.acquireSharedInterruptibly(0);
    }

    public boolean waitUntilOpen(final long timeout, final TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(0, unit.toNanos(timeout));
    }

}
