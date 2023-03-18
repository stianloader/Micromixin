package org.spongepowered.asm.mixin.injection.callback;

import org.jetbrains.annotations.NotNull;

public class CallbackInfo {

    @NotNull
    private final String name;
    private final boolean cancellable;
    private boolean cancelled;

    public CallbackInfo(@NotNull String name, boolean cancellable) {
        this.name = name;
        this.cancellable = cancellable;
    }

    @NotNull
    public String getId() {
        return this.name;
    }

    public void cancel() throws CancellationException {
        if (!this.cancellable) {
            throw new CancellationException("The call " + this.name + " is not cancellable.");
        }

        this.cancelled = true;
    }

    public final boolean isCancellable() {
        return this.cancellable;
    }

    public final boolean isCancelled() {
        return this.cancelled;
    }
}
