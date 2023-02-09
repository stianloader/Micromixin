package de.geolykt.micromixin;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModularityAttached<T, V> {
    @Nullable
    public final T attachment;
    @NotNull
    public final V value;
    public ModularityAttached(@Nullable T modularityAttachment, @NotNull V value) {
        this.attachment = modularityAttachment;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ModularityAttached) {
            return Objects.equals(((ModularityAttached<?, ?>) obj).attachment, this.attachment)
                    && this.value.equals(((ModularityAttached<?, ?>) obj).value);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.value.hashCode() ^ Objects.hashCode(this.attachment);
    }
}
