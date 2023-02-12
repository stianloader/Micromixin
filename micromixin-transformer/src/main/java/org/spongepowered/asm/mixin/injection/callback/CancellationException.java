package org.spongepowered.asm.mixin.injection.callback;

public class CancellationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CancellationException(String message) {
        super(message);
    }
}
