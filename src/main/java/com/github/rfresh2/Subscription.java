package com.github.rfresh2;

record Subscription(SimpleEventBus eventBus, EventConsumer<?>[] eventConsumers) {
    public void unsubscribe() {
        for (int i = 0; i < eventConsumers.length; i++) {
            EventConsumer<?> eventConsumer = eventConsumers[i];
            eventBus.removeEventConsumer(eventConsumer);
        }
    }
}
