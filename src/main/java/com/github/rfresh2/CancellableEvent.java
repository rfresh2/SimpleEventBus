package com.github.rfresh2;

/**
 * Example base class for events that can be cancelled.
 *
 * Handling cancellations is the responsibility of users, there is no special handling in the event bus.
 * In other words, dispatchers and consumers should check if an event is cancelled and handle it accordingly.
 */
public abstract class CancellableEvent {
    private boolean cancelled = false;
    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
