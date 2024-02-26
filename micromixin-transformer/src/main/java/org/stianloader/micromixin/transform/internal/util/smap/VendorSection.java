package org.stianloader.micromixin.transform.internal.util.smap;

import org.jetbrains.annotations.NotNull;

public class VendorSection extends AbstractSMAPSection {

    @NotNull
    private final String vendorId;

    public VendorSection(@NotNull String vendorId) {
        this.vendorId = vendorId;
    }

    @Override
    @NotNull
    public StringBuilder pushContents(@NotNull StringBuilder sharedBuilder) {
        sharedBuilder.append("*V\r").append(this.vendorId).appendCodePoint('\r');
        return sharedBuilder;
    }

}
