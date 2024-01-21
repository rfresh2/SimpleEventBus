package com.github.rfresh2;

class Subscription {
    private final Runnable unsubscribeCallback;

    public Subscription(Runnable unsubscribeCallback) {
        this.unsubscribeCallback = unsubscribeCallback;
    }

    public void unsubscribe() {
        unsubscribeCallback.run();
    }
}
