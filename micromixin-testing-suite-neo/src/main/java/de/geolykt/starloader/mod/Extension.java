package de.geolykt.starloader.mod;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.VisibleForTesting;

@ApiStatus.Internal
@VisibleForTesting
public abstract class Extension {
    public abstract void postInitialize();
}
