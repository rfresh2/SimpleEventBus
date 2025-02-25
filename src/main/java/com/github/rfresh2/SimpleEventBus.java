package com.github.rfresh2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

/**
 * A simple event bus without reflection.
 * <p>
 * Event priority is ordered by larger priority values being called first.
 * The default priority is 0.
 */
public class SimpleEventBus {
    private final Logger logger;
    private final ExecutorService asyncEventExecutor;
    private final IdentityHashMap<Class<?>, EventConsumer<?>[]> eventConsumersMap = new IdentityHashMap<>();
    private final IdentityHashMap<Object, Subscription> subscribersMap = new IdentityHashMap<>();

    public SimpleEventBus() {
        this(ForkJoinPool.commonPool(), LoggerFactory.getLogger("SimpleEventBus"));
    }

    public SimpleEventBus(final ExecutorService asyncEventExecutor) {
        this(asyncEventExecutor, LoggerFactory.getLogger("SimpleEventBus"));
    }

    public SimpleEventBus(final Logger logger) {
        this(ForkJoinPool.commonPool(), logger);
    }

    public SimpleEventBus(final ExecutorService asyncEventExecutor, final Logger logger) {
        this.asyncEventExecutor = asyncEventExecutor;
        this.logger = logger;
    }

    public <T> void subscribe(Object subscriber, Class<T> eventType, Consumer<T> handler) {
        var existingSub = subscribersMap.remove(subscriber);
        if (existingSub != null) existingSub.unsubscribe();
        var sub = subscribe(EventConsumer.of(eventType, handler));
        subscribersMap.put(subscriber, sub);
    }

    public <T> void subscribe(Object subscriber, EventConsumer<T> eventConsumer) {
        var existingSub = subscribersMap.remove(subscriber);
        if (existingSub != null) existingSub.unsubscribe();
        var sub = subscribe(eventConsumer);
        subscribersMap.put(subscriber, sub);
    }

    public final void subscribe(Object subscriber, EventConsumer<?>... eventConsumers) {
        var existingSub = subscribersMap.remove(subscriber);
        if (existingSub != null) existingSub.unsubscribe();
        var sub = subscribe(eventConsumers);
        subscribersMap.put(subscriber, sub);
    }

    public boolean isSubscribed(Object subscriber) {
        return subscribersMap.containsKey(subscriber);
    }

    public void unsubscribe(Object subscriber) {
        var sub = subscribersMap.remove(subscriber);
        if (sub != null) sub.unsubscribe();
    }

    public <T> void post(T event) {
        var consumers = eventConsumersMap.get(event.getClass());
        postInternal(event, consumers);
    }

    public <T> void postAsync(T event) {
        postAsync(event, asyncEventExecutor);
    }

    public <T> void postAsync(T event, Executor executor) {
        var consumers = eventConsumersMap.get(event.getClass());
        if (consumers != null)
            executor.execute(() -> this.postInternal(event, consumers));
    }


    //////////////////////////////////////////////////////////////////////
    // Internal API
    //////////////////////////////////////////////////////////////////////


    private <T> void postInternal(final T event, final EventConsumer<?>[] consumers) {
        var isCancellableEvent = event instanceof CancellableEvent;
        if (consumers != null) {
            for (int i = 0; i < consumers.length; i++) {
                var consumer = consumers[i];
                try {
                    ((Consumer<T>) consumer.handler()).accept(event);
                    if (isCancellableEvent && ((CancellableEvent) event).isCancelled())
                        break;
                } catch (final Throwable e) {
                    logger.error("Caught exception while handling event: {}", event.getClass(), e);
                }
            }
        }
    }

    synchronized void removeEventConsumer(EventConsumer<?> eventConsumer) {
        var consumers = eventConsumersMap.get(eventConsumer.eventClass());
        if (consumers != null) {
            int index = -1;
            for (int i = 0; i < consumers.length; i++) {
                if (consumers[i].handler() == eventConsumer.handler()) {
                    index = i;
                    break;
                }
            }
            if (index == -1) return;
            if (consumers.length == 1) {
                eventConsumersMap.remove(eventConsumer.eventClass());
                return;
            }
            final EventConsumer<?>[] newConsumers = new EventConsumer[consumers.length - 1];
            System.arraycopy(consumers, 0, newConsumers, 0, index);
            System.arraycopy(consumers, index + 1, newConsumers, index, consumers.length - index - 1);
            eventConsumersMap.put(eventConsumer.eventClass(), newConsumers);
        }
    }

    private synchronized Subscription subscribe(EventConsumer<?>... eventConsumers) {
        for (int i = 0; i < eventConsumers.length; i++) {
            var eventConsumer = eventConsumers[i];
            this.eventConsumersMap.compute(eventConsumer.eventClass(), (key, consumers) -> {
                if (consumers == null) {
                    return new EventConsumer[]{eventConsumer};
                } else {
                    final EventConsumer<?>[] newConsumers = new EventConsumer[consumers.length + 1];
                    System.arraycopy(consumers, 0, newConsumers, 0, consumers.length);
                    newConsumers[consumers.length] = eventConsumer;
                    Arrays.sort(newConsumers);
                    return newConsumers;
                }
            });
        }
        return new Subscription(this, eventConsumers);
    }
}
