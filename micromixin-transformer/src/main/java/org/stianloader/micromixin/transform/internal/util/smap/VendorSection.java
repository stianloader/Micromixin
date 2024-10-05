package org.stianloader.micromixin.transform.internal.util.smap;

import org.jetbrains.annotations.NotNull;

public class VendorSection extends AbstractSMAPSection {

    @NotNull
    private final String vendorId;

    /**
     * Constructor for a new {@link VendorSection} with a given vendorId string. The vendorInfo
     * attribute is left unspecified.
     *
     * @param vendorId The vendor Id. Must follow the package naming conventions as per JSR-45.
     */
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
