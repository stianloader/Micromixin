package org.spongepowered.asm.mixin.injection.callback;

public enum LocalCapture {

    /**
     * The default state of the {@link LocalCapture} flag:
     * no locals are captured (however, argument capture can still occur).
     */
    NO_CAPTURE,

    /**
     * Aborts transformation (however within the Micromixin implementation, the handler is still copied into the target
     * class) and prints the locals that can be captured at the injection points.
     */
    PRINT;
}
