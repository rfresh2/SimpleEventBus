package com.github.rfresh2;

/**
 * Base class for events that can be cancelled.
 *
 * If an event is cancelled, it will not be passed to any further handlers.
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
