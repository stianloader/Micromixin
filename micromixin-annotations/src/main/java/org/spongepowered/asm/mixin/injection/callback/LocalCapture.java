package org.spongepowered.asm.mixin.injection.callback;

public enum LocalCapture {

    /**
     * Throws an {@link Error} during transformation if local capture couldn't occur.
     * Otherwise locals are captured as per standard capture behaviour.
     */
    CAPTURE_FAILHARD,

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
